package com.smartfarming.ai.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Crop recommendation classifier.
 *
 * <p>This is a Random Forest (60 trees, max depth 10) trained in Python /
 * scikit-learn on the cleaned 7,165-row crop dataset — see
 * {@code /ml/notebooks/export_crop_forest.py}. Real held-out performance:
 * 94.00% accuracy, 0.9211 weighted F1, 0.9493 macro-F1 across 40 crop classes
 * (see {@code /ml/model-results/crop_deployed_metrics.json}).
 *
 * <p>Rather than depend on a third-party Java ML library (Smile/Tribuo) whose
 * exact API surface couldn't be compiled/verified in the environment this was
 * built in, the trained forest is exported as plain JSON (each tree's
 * feature/threshold/children/leaf-vote arrays — literally scikit-learn's own
 * internal {@code tree_} representation) and this class re-implements the
 * standard Random Forest voting rule: walk every tree via feature/threshold
 * comparisons, sum the per-class leaf vote counts across all trees, and take
 * the argmax. This has no ML-library dependency at all — only Jackson, which
 * Spring Boot already includes.
 *
 * <p>This exact algorithm was cross-checked in Python against
 * {@code RandomForestClassifier.predict()} on real held-out rows before being
 * ported here (200/200 exact matches) — see the sanity check at the bottom of
 * {@code export_final.py}. If you retrain the model, re-export with that same
 * script so the JSON shape stays compatible with this loader.
 */
@Component
public class CropModel {

    private static final String MODEL_RESOURCE = "models/crop-model/crop_forest.json";

    /** One decision tree, stored in the same parallel-array layout scikit-learn itself uses. */
    private static class Tree {
        int[] feature;          // feature index to split on; -2 marks a leaf node
        double[] threshold;
        int[] childrenLeft;
        int[] childrenRight;
        double[][] classCounts; // vote count per class; only meaningful at leaf nodes
    }

    private List<String> featureNames;
    private List<String> classNames;
    private List<Tree> trees;

    @PostConstruct
    public void load() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = new ClassPathResource(MODEL_RESOURCE).getInputStream()) {
            JsonNode root = mapper.readTree(is);

            featureNames = new ArrayList<>();
            root.get("features").forEach(n -> featureNames.add(n.asText()));

            classNames = new ArrayList<>();
            root.get("classes").forEach(n -> classNames.add(n.asText()));

            trees = new ArrayList<>();
            for (JsonNode treeNode : root.get("trees")) {
                trees.add(parseTree(treeNode));
            }
        }
    }

    private Tree parseTree(JsonNode treeNode) {
        Tree t = new Tree();
        JsonNode featureArr = treeNode.get("feature");
        int n = featureArr.size();
        t.feature = new int[n];
        t.threshold = new double[n];
        t.childrenLeft = new int[n];
        t.childrenRight = new int[n];
        t.classCounts = new double[n][];

        JsonNode thresholdArr = treeNode.get("threshold");
        JsonNode leftArr = treeNode.get("childrenLeft");
        JsonNode rightArr = treeNode.get("childrenRight");
        JsonNode countsArr = treeNode.get("classCounts");

        for (int i = 0; i < n; i++) {
            t.feature[i] = featureArr.get(i).asInt();
            t.threshold[i] = thresholdArr.get(i).asDouble();
            t.childrenLeft[i] = leftArr.get(i).asInt();
            t.childrenRight[i] = rightArr.get(i).asInt();

            JsonNode countsForNode = countsArr.get(i);
            double[] counts = new double[countsForNode.size()];
            for (int c = 0; c < counts.length; c++) {
                counts[c] = countsForNode.get(c).asDouble();
            }
            t.classCounts[i] = counts;
        }
        return t;
    }

    /**
     * Predicts the best crop for a feature vector ordered exactly as
     * {@link #getFeatureNames()}: [N, P, K, pH, rainfall, temperature].
     */
    public Prediction predict(double[] x) {
        double[] totalVotes = new double[classNames.size()];
        for (Tree tree : trees) {
            double[] leafCounts = walkTree(tree, x);
            for (int c = 0; c < totalVotes.length; c++) {
                totalVotes[c] += leafCounts[c];
            }
        }

        double totalWeight = sum(totalVotes);
        List<ScoredCrop> ranked = new ArrayList<>();
        for (int c = 0; c < totalVotes.length; c++) {
            double prob = totalWeight > 0 ? totalVotes[c] / totalWeight : 0.0;
            ranked.add(new ScoredCrop(classNames.get(c), prob));
        }
        ranked.sort((a, b) -> Double.compare(b.probability(), a.probability()));

        ScoredCrop best = ranked.get(0);
        List<ScoredCrop> alternatives = ranked.subList(1, Math.min(4, ranked.size()));
        return new Prediction(best.crop(), best.probability(), new ArrayList<>(alternatives));
    }

    private double[] walkTree(Tree tree, double[] x) {
        int node = 0;
        while (tree.feature[node] != -2) {
            int f = tree.feature[node];
            node = (x[f] <= tree.threshold[node]) ? tree.childrenLeft[node] : tree.childrenRight[node];
        }
        return tree.classCounts[node];
    }

    private double sum(double[] values) {
        double s = 0;
        for (double v : values) s += v;
        return s;
    }

    public List<String> getFeatureNames() {
        return featureNames;
    }

    public List<String> getClassNames() {
        return classNames;
    }

    public int getTreeCount() {
        return trees.size();
    }

    public record Prediction(String crop, double confidence, List<ScoredCrop> topAlternatives) {}

    public record ScoredCrop(String crop, double probability) {}
}

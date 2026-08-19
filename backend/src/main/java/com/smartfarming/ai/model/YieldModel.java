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
 * Satellite-index yield regressor, trained on {@code yield_prediction_dataset.csv}
 * (NDVI/GNDVI/NDWI/SAVI + soil moisture + weather, 90 distinct fields).
 *
 * <p>Real held-out performance — <b>evaluated by splitting on {@code field_id}, not
 * randomly by row</b>, so no field's readings leak between train and test —
 * R&sup2; = 0.876, MAE = 1.39, RMSE = 3.05 (see
 * {@code /ml/model-results/yield_deployed_metrics.json}). This is a regression
 * model: report R&sup2;/MAE/RMSE, never an "accuracy percentage" for it.
 *
 * <p>Not currently wired to the main Recommendation form, since that form
 * doesn't collect satellite vegetation indices — it's exposed as its own
 * capability ({@code POST /api/farm-analysis/yield-estimate}) for a future
 * "satellite yield estimate" feature once a field-imagery input exists.
 *
 * <p>Same JSON-tree-export approach as {@link CropModel}, generalized to
 * regression: each leaf stores a single value (the mean target of training
 * rows that landed there) and the forest's prediction is the mean across all
 * trees, not a vote.
 */
@Component
public class YieldModel {

    private static final String MODEL_RESOURCE = "models/yield-model/yield_forest.json";

    private static class Tree {
        int[] feature;
        double[] threshold;
        int[] childrenLeft;
        int[] childrenRight;
        double[] value; // regression value at each node; only meaningful at leaves
    }

    private List<String> featureNames;
    private List<Tree> trees;

    @PostConstruct
    public void load() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = new ClassPathResource(MODEL_RESOURCE).getInputStream()) {
            JsonNode root = mapper.readTree(is);

            featureNames = new ArrayList<>();
            root.get("features").forEach(n -> featureNames.add(n.asText()));

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
        t.value = new double[n];

        JsonNode thresholdArr = treeNode.get("threshold");
        JsonNode leftArr = treeNode.get("childrenLeft");
        JsonNode rightArr = treeNode.get("childrenRight");
        JsonNode valueArr = treeNode.get("value");

        for (int i = 0; i < n; i++) {
            t.feature[i] = featureArr.get(i).asInt();
            t.threshold[i] = thresholdArr.get(i).asDouble();
            t.childrenLeft[i] = leftArr.get(i).asInt();
            t.childrenRight[i] = rightArr.get(i).asInt();
            t.value[i] = valueArr.get(i).asDouble();
        }
        return t;
    }

    /**
     * Predicts yield for a feature vector ordered exactly as
     * {@link #getFeatureNames()}: [NDVI, GNDVI, NDWI, SAVI, soil_moisture, temperature, rainfall].
     */
    public double predict(double[] x) {
        double total = 0;
        for (Tree tree : trees) {
            total += walkTree(tree, x);
        }
        return total / trees.size();
    }

    private double walkTree(Tree tree, double[] x) {
        int node = 0;
        while (tree.feature[node] != -2) {
            int f = tree.feature[node];
            node = (x[f] <= tree.threshold[node]) ? tree.childrenLeft[node] : tree.childrenRight[node];
        }
        return tree.value[node];
    }

    public List<String> getFeatureNames() {
        return featureNames;
    }
}

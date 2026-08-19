package com.smartfarming.ai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Central place for the classpath/filesystem locations services use to find
 * reference datasets and trained model files, so no path strings are hardcoded
 * throughout the service layer.
 */
@Configuration
public class ModelConfig {

    @Value("${app.datasets.path}")
    private String datasetsPath;

    @Value("${app.models.classpath-path}")
    private String modelsClasspathPath;

    @Value("${app.models.external-path}")
    private String modelsExternalPath;

    public String getDatasetsPath() {
        return datasetsPath;
    }

    public String getModelsClasspathPath() {
        return modelsClasspathPath;
    }

    public String getModelsExternalPath() {
        return modelsExternalPath;
    }
}

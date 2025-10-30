package com.springcore.ai.scai_platform.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Maps the properties under the 'app.finetune' prefix from application.yaml.
 * This ensures properties are loaded correctly and avoids direct @Value dependency errors.
 */
@Configuration
@ConfigurationProperties(prefix = "app.finetune")
@Data
public class FineTuneProperties {

    // Corresponds to app.finetune.dataset-file
    private String datasetFile;

    // Corresponds to app.finetune.python-file
    private String pythonFile;
}
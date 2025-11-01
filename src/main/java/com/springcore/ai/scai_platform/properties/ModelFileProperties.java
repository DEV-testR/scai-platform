package com.springcore.ai.scai_platform.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Maps the properties under the 'app.finetune' prefix from application.yaml.
 * This ensures properties are loaded correctly and avoids direct @Value dependency errors.
 */
@Configuration
@ConfigurationProperties(prefix = "spring.modelfile")
@Data
public class ModelFileProperties {
    private String modelfilepath;
    private String modelname;
}
package com.springcore.ai.scai_platform.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.application")
public class ApplicationProperties {
    private Upload upload;

    @Data
    public static class Upload {
        private String path;
    }
}

package bio.terra.cloudtransparency.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cloudtransparency.sam")
public record SamConfiguration(String basePath) {}

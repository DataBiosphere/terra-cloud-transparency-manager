package bio.terra.cloudtransparency.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cloudtransparency.google-directory-api")
public record GoogleDirectoryApiConfiguration(
    String[] adminServiceAccountPaths, String[] directoryAdminAccountEmails) {}

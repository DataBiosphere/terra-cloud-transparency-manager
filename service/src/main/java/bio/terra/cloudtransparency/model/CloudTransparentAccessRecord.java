package bio.terra.cloudtransparency.model;

import java.util.Date;
import java.util.Objects;

public record CloudTransparentAccessRecord(
    String samUserId,
    String resourceTypeName,
    String resourceName,
    Boolean enabled,
    Date createdAt,
    Date updatedAt) {
  public CloudTransparentAccessRecord {
    Objects.requireNonNull(samUserId);
    Objects.requireNonNull(resourceTypeName);
    Objects.requireNonNull(resourceName);
    Objects.requireNonNull(enabled);
    Objects.requireNonNull(createdAt);
    Objects.requireNonNull(updatedAt);
  }

  public CloudTransparentAccess toCloudTransparentAccess() {
    return new CloudTransparentAccess()
        .samUserId(samUserId)
        .resourceType(resourceTypeName)
        .resourceId(resourceName)
        .enabled(enabled);
  }
}

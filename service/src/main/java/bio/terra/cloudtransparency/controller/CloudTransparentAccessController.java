package bio.terra.cloudtransparency.controller;

import bio.terra.cloudtransparency.api.CloudTransparentAccessApi;
import bio.terra.cloudtransparency.config.SamConfiguration;
import bio.terra.cloudtransparency.iam.SamService;
import bio.terra.cloudtransparency.model.CloudTransparentAccess;
import bio.terra.cloudtransparency.service.CloudTransparentAccessService;
import bio.terra.common.iam.BearerTokenFactory;
import bio.terra.common.iam.SamUser;
import bio.terra.common.iam.SamUserFactory;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class CloudTransparentAccessController implements CloudTransparentAccessApi {

  public static final String EXAMPLE_COUNTER_TAG = "tag";
  public static final String EXAMPLE_COUNTER_NAME = "example.counter";

  private final CloudTransparentAccessService cloudTransparentAccessService;
  private final BearerTokenFactory bearerTokenFactory;
  private final SamUserFactory samUserFactory;
  private final SamConfiguration samConfiguration;
  private final HttpServletRequest request;

  private final SamService samService;

  public CloudTransparentAccessController(
      CloudTransparentAccessService cloudTransparentAccessService,
      BearerTokenFactory bearerTokenFactory,
      SamUserFactory samUserFactory,
      SamConfiguration samConfiguration,
      HttpServletRequest request,
      SamService samService) {
    this.cloudTransparentAccessService = cloudTransparentAccessService;
    this.bearerTokenFactory = bearerTokenFactory;
    this.samUserFactory = samUserFactory;
    this.samConfiguration = samConfiguration;
    this.request = request;
    this.samService = samService;
  }

  private SamUser getUser() {
    // this automatically checks if the user is enabled
    return this.samUserFactory.from(request, samConfiguration.basePath());
  }

  /** Example of getting user information from sam. */
  @Override
  public ResponseEntity<List<CloudTransparentAccess>> getCloudTransparentAccesses() {
    var user = getUser();
    var cloudTransparentAccesses =
        this.cloudTransparentAccessService.getCloudTransparentAccessesForUser(user.getSubjectId());
    return ResponseEntity.ok(cloudTransparentAccesses);
  }

  @Override
  public ResponseEntity<Void> putCloudTransparentAccess(
      String resourceTypeName, String resourceId) {
    var user = getUser();
    this.cloudTransparentAccessService.saveCloudTransparentAccess(
        new CloudTransparentAccess()
            .samUserId(user.getSubjectId())
            .resourceType(resourceTypeName)
            .resourceId(resourceId)
            .enabled(true));
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> deleteCloudTransparentAccess(
      String resourceTypeName, String resourceId) {
    var user = getUser();
    this.cloudTransparentAccessService.saveCloudTransparentAccess(
        new CloudTransparentAccess()
            .samUserId(user.getSubjectId())
            .resourceType(resourceTypeName)
            .resourceId(resourceId)
            .enabled(false));
    return ResponseEntity.noContent().build();
  }

  /** Example of getting the bearer token and using it to make a Sam (or other service) api call */
  //  @Override
  //  public ResponseEntity<Boolean> getAction(String resourceType, String resourceId, String
  // action) {
  //    var bearerToken = bearerTokenFactory.from(request);
  //    return ResponseEntity.ok(samService.getAction(resourceType, resourceId, action,
  // bearerToken));
  //  }

  //  @Override
  //  public ResponseEntity<Void> incrementCounter(String tag) {
  //    Metrics.globalRegistry
  //        .counter(EXAMPLE_COUNTER_NAME, List.of(Tag.of(EXAMPLE_COUNTER_TAG, tag)))
  //        .increment();
  //    return ResponseEntity.noContent().build();
  //  }
}

package bio.terra.cloudtransparency.util;

import bio.terra.common.iam.BearerToken;
import bio.terra.common.iam.SamUser;
import java.util.Random;
import java.util.UUID;

public class TestUtils {

  private static Random random = new Random();

  public static SamUser testSamUser() {
    var userId = Long.toString(random.nextLong(0L, Long.MAX_VALUE));
    return new SamUser(
        "test-" + userId + "@email", userId, new BearerToken(UUID.randomUUID().toString()));
  }
}

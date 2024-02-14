package bio.terra.cloudtransparency.dao;

import static org.junit.jupiter.api.Assertions.*;

import bio.terra.cloudtransparency.model.CloudTransparentAccess;
import bio.terra.cloudtransparency.model.CloudTransparentAccessRecord;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CloudTransparentAccessDaoTest extends BaseDaoTest {
  @Autowired CloudTransparentAccessDao cloudTransparentAccessDao;

  @Test
  void testDifferentUserUpsert() {
    var random = new Random();
    var access =
        new CloudTransparentAccess()
            .samUserId(Long.toString(random.nextLong()))
            .resourceType("resourceTypeName")
            .resourceId("resourceType")
            .enabled(true);
    cloudTransparentAccessDao.upsertCloudTransparentAccess(access);
    List<CloudTransparentAccessRecord> user1Actual =
        cloudTransparentAccessDao.getCloudTransparentAccessesForUser(access.getSamUserId());

    var access2 =
        new CloudTransparentAccess()
            .samUserId(Long.toString(random.nextLong()))
            .resourceType("resourceTypeName")
            .resourceId("resourceType")
            .enabled(true);
    cloudTransparentAccessDao.upsertCloudTransparentAccess(access2);
    List<CloudTransparentAccessRecord> access2Actual =
        cloudTransparentAccessDao.getCloudTransparentAccessesForUser(access2.getSamUserId());

    assertFalse(user1Actual.isEmpty());
    assertEquals(access.getSamUserId(), user1Actual.get(0).samUserId());
    assertEquals(access.isEnabled(), user1Actual.get(0).enabled());

    assertFalse(access2Actual.isEmpty());
    assertEquals(access2.getSamUserId(), access2Actual.get(0).samUserId());
    assertEquals(access2.isEnabled(), access2Actual.get(0).enabled());
  }

  @Test
  void testRepeatedUpsert() {
    var samUserId = Long.toString(new Random().nextLong());
    var access1 =
        new CloudTransparentAccess()
            .samUserId(samUserId)
            .resourceType("resourceTypeName")
            .resourceId("resourceType")
            .enabled(true);
    cloudTransparentAccessDao.upsertCloudTransparentAccess(access1);
    List<CloudTransparentAccessRecord> firstSave =
        cloudTransparentAccessDao.getCloudTransparentAccessesForUser(access1.getSamUserId());
    assertFalse(firstSave.isEmpty());
    assertEquals(access1.getSamUserId(), firstSave.get(0).samUserId());
    assertEquals(access1.isEnabled(), firstSave.get(0).enabled());

    var access2 =
        new CloudTransparentAccess()
            .samUserId(samUserId)
            .resourceType("resourceTypeName")
            .resourceId("resourceType")
            .enabled(false);
    cloudTransparentAccessDao.upsertCloudTransparentAccess(access2);
    List<CloudTransparentAccessRecord> secondSave =
        cloudTransparentAccessDao.getCloudTransparentAccessesForUser(access2.getSamUserId());

    assertFalse(secondSave.isEmpty());
    assertEquals(firstSave.get(0).samUserId(), secondSave.get(0).samUserId());
    assertEquals(access2.getSamUserId(), secondSave.get(0).samUserId());
    assertEquals(access2.isEnabled(), secondSave.get(0).enabled());
  }

  @Test
  void testNotFound() {
    assertTrue(
        cloudTransparentAccessDao
            .getCloudTransparentAccessesForUser(Long.toString(new Random().nextLong()))
            .isEmpty());
  }
}

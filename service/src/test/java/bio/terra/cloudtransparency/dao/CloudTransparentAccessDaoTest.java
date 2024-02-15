package bio.terra.cloudtransparency.dao;

import static org.junit.jupiter.api.Assertions.*;

import bio.terra.cloudtransparency.model.CloudTransparentAccess;
import bio.terra.cloudtransparency.model.CloudTransparentAccessRecord;
import bio.terra.cloudtransparency.util.TestUtils;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CloudTransparentAccessDaoTest extends BaseDaoTest {
  @Autowired CloudTransparentAccessDao cloudTransparentAccessDao;

  @Test
  void testDifferentUserUpsert() {

    var testUser1 = TestUtils.testSamUser();
    var testUser2 = TestUtils.testSamUser();
    var access =
        new CloudTransparentAccess()
            .samUserId(testUser1.getSubjectId())
            .resourceType("resourceTypeName")
            .resourceId("resourceType")
            .enabled(true);
    cloudTransparentAccessDao.upsertCloudTransparentAccess(access);
    List<CloudTransparentAccessRecord> user1Actual =
        cloudTransparentAccessDao.getCloudTransparentAccessesForUser(access.getSamUserId());

    var access2 =
        new CloudTransparentAccess()
            .samUserId(testUser2.getSubjectId())
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
    var testUser = TestUtils.testSamUser();
    var access1 =
        new CloudTransparentAccess()
            .samUserId(testUser.getSubjectId())
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
            .samUserId(testUser.getSubjectId())
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
    var testUser = TestUtils.testSamUser();
    assertTrue(
        cloudTransparentAccessDao
            .getCloudTransparentAccessesForUser(testUser.getSubjectId())
            .isEmpty());
  }
}

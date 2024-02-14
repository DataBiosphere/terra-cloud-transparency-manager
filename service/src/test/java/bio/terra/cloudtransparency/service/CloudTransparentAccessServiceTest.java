package bio.terra.cloudtransparency.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import bio.terra.cloudtransparency.dao.CloudTransparentAccessDao;
import bio.terra.cloudtransparency.model.CloudTransparentAccess;
import bio.terra.cloudtransparency.model.CloudTransparentAccessRecord;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class CloudTransparentAccessServiceTest {

  @MockBean CloudTransparentAccessDao cloudTransparentAccessDao;
  @Autowired CloudTransparentAccessService cloudTransparentAccessService;

  @Test
  void saveCloudTransparentAccess() {
    doNothing().when(cloudTransparentAccessDao).upsertCloudTransparentAccess(any());
    var testAccess =
        new CloudTransparentAccess()
            .samUserId(Long.toString(new Random().nextLong()))
            .resourceType("resourceTypeName")
            .resourceId("resourceId")
            .enabled(true);
    cloudTransparentAccessService.saveCloudTransparentAccess(testAccess);
    verify(cloudTransparentAccessDao)
        .upsertCloudTransparentAccess(
            argThat(cloudTransparentAccess -> cloudTransparentAccess.equals(testAccess)));
  }

  @Test
  void getCloudTransparentAccessesForUser() {
    var testAccessRecord =
        new CloudTransparentAccessRecord(
            Long.toString(new Random().nextLong()),
            "resourceTypeName",
            "resourceId",
            true,
            new Date(),
            new Date());
    var expectedAccess =
        new CloudTransparentAccess()
            .samUserId(testAccessRecord.samUserId())
            .resourceType("resourceTypeName")
            .resourceId("resourceId")
            .enabled(true);
    when(cloudTransparentAccessDao.getCloudTransparentAccessesForUser(any()))
        .thenReturn(List.of(testAccessRecord));

    var result =
        cloudTransparentAccessService.getCloudTransparentAccessesForUser(
            expectedAccess.getSamUserId());
    assertEquals(List.of(expectedAccess), result);
  }
}

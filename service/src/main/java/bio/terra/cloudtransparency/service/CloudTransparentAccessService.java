package bio.terra.cloudtransparency.service;

import static java.util.stream.Collectors.toList;

import bio.terra.cloudtransparency.dao.CloudTransparentAccessDao;
import bio.terra.cloudtransparency.model.CloudTransparentAccess;
import bio.terra.cloudtransparency.model.CloudTransparentAccessRecord;
import bio.terra.common.db.ReadTransaction;
import bio.terra.common.db.WriteTransaction;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CloudTransparentAccessService {
  private final CloudTransparentAccessDao cloudTransparentAccessDao;

  public CloudTransparentAccessService(CloudTransparentAccessDao cloudTransparentAccessDao) {
    this.cloudTransparentAccessDao = cloudTransparentAccessDao;
  }

  // README docs/transactions.md
  @WriteTransaction
  public void saveCloudTransparentAccess(CloudTransparentAccess cloudTransparentAccess) {
    cloudTransparentAccessDao.upsertCloudTransparentAccess(cloudTransparentAccess);
  }

  @ReadTransaction
  public List<CloudTransparentAccess> getCloudTransparentAccessesForUser(String samUserId) {
    return cloudTransparentAccessDao.getCloudTransparentAccessesForUser(samUserId).stream()
        .map(CloudTransparentAccessRecord::toCloudTransparentAccess)
        .collect(toList());
  }
}

package bio.terra.cloudtransparency.dao;

import bio.terra.cloudtransparency.model.CloudTransparentAccess;
import bio.terra.cloudtransparency.model.CloudTransparentAccessRecord;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CloudTransparentAccessDao {
  private static final RowMapper<CloudTransparentAccessRecord> CLOUD_TRANSPARENT_ACCESS_ROW_MAPPER =
      (rs, rowNum) ->
          new CloudTransparentAccessRecord(
              rs.getString("sam_user_id"),
              rs.getString("resource_type_name"),
              rs.getString("resource_id"),
              rs.getBoolean("enabled"),
              rs.getDate("created_at"),
              rs.getDate("updated_at"));

  private final NamedParameterJdbcTemplate jdbcTemplate;

  public CloudTransparentAccessDao(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @WithSpan
  public void upsertCloudTransparentAccess(CloudTransparentAccess cloudTransparentAccess) {
    var query =
        "INSERT INTO cloud_transparent_access (sam_user_id, resource_type_name, resource_id, enabled, updated_at)"
            + " VALUES (:samUserId, :resourceTypeName, :resourceId, :enabled, current_timestamp)"
            + " ON CONFLICT (sam_user_id, resource_type_name, resource_id) DO UPDATE SET"
            + " enabled = excluded.enabled, "
            + " updated_at = current_timestamp";

    var namedParameters =
        new MapSqlParameterSource()
            .addValue("samUserId", cloudTransparentAccess.getSamUserId())
            .addValue("resourceTypeName", cloudTransparentAccess.getResourceType())
            .addValue("resourceId", cloudTransparentAccess.getResourceType())
            .addValue("enabled", cloudTransparentAccess.isEnabled());

    jdbcTemplate.update(query, namedParameters);
  }

  @WithSpan
  public List<CloudTransparentAccessRecord> getCloudTransparentAccessesForUser(String samUserId) {
    var namedParameters = new MapSqlParameterSource().addValue("samUserId", samUserId);
    var selectSql = "SELECT * FROM cloud_transparent_access WHERE sam_user_id = :samUserId";
    return jdbcTemplate.query(selectSql, namedParameters, CLOUD_TRANSPARENT_ACCESS_ROW_MAPPER);
  }
}

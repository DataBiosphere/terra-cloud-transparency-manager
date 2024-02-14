package bio.terra.cloudtransparency.api;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import bio.terra.cloudtransparency.config.SamConfiguration;
import bio.terra.cloudtransparency.controller.CloudTransparentAccessController;
import bio.terra.cloudtransparency.iam.SamService;
import bio.terra.cloudtransparency.model.CloudTransparentAccess;
import bio.terra.cloudtransparency.service.CloudTransparentAccessService;
import bio.terra.common.iam.BearerToken;
import bio.terra.common.iam.BearerTokenFactory;
import bio.terra.common.iam.SamUser;
import bio.terra.common.iam.SamUserFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@ContextConfiguration(classes = CloudTransparentAccessController.class)
@WebMvcTest
public class CloudTransparentAccessControllerTest {
  @MockBean CloudTransparentAccessService serviceMock;
  @MockBean SamUserFactory samUserFactoryMock;
  @MockBean BearerTokenFactory bearerTokenFactory;
  @MockBean SamConfiguration samConfiguration;
  @MockBean SamService samService;

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper mapper;

  protected <T> T fromJson(MvcResult result, Class<T> valueType)
      throws UnsupportedEncodingException, JsonProcessingException {
    return mapper.readValue(result.getResponse().getContentAsString(), valueType);
  }

  private SamUser testUser =
      new SamUser(
          "test@email",
          Long.toString(new Random().nextLong()),
          new BearerToken(UUID.randomUUID().toString()));

  @BeforeEach
  void beforeEach() {
    when(samUserFactoryMock.from(any(HttpServletRequest.class), any())).thenReturn(testUser);
  }

  @Test
  void testGetCloudTransparentAccessesOk() throws Exception {
    var access =
        new CloudTransparentAccess()
            .samUserId(testUser.getSubjectId())
            .resourceType("resourceType")
            .resourceId("resourceId")
            .enabled(true);
    when(serviceMock.getCloudTransparentAccessesForUser(testUser.getSubjectId()))
        .thenReturn(List.of(access));
    when(serviceMock.getCloudTransparentAccessesForUser(testUser.getSubjectId()))
        .thenReturn(List.of(access));

    var result =
        mockMvc
            .perform(get("/api/cloudTransparentAccess/v1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    List<Map<String, Object>> accesses = assertDoesNotThrow(() -> fromJson(result, List.class));
    var head = accesses.get(0);
    var testAccess =
        new CloudTransparentAccess()
            .samUserId(head.get("samUserId").toString())
            .resourceType(head.get("resourceType").toString())
            .resourceId(head.get("resourceId").toString())
            .enabled((Boolean) head.get("enabled"));
    assertEquals(access, testAccess);
  }

  @Test
  void testGetNoAccessesOk() throws Exception {
    when(serviceMock.getCloudTransparentAccessesForUser(testUser.getSubjectId()))
        .thenReturn(List.of());

    var result =
        mockMvc
            .perform(get("/api/cloudTransparentAccess/v1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    List<CloudTransparentAccess> accesses = assertDoesNotThrow(() -> fromJson(result, List.class));
    assertTrue(accesses.isEmpty());
  }

  @Test
  void testPutAccessOk() throws Exception {
    var access =
        new CloudTransparentAccess()
            .samUserId(testUser.getSubjectId())
            .resourceType("resourceType")
            .resourceId("resourceId")
            .enabled(true);
    doNothing().when(serviceMock).saveCloudTransparentAccess(access);

    mockMvc
        .perform(put("/api/cloudTransparentAccess/v1/resourceType/resourceId"))
        .andExpect(status().isNoContent());
  }

  @Test
  void testDeleteAccessOk() throws Exception {
    var access =
        new CloudTransparentAccess()
            .samUserId(testUser.getSubjectId())
            .resourceType("resourceType")
            .resourceId("resourceId")
            .enabled(true);
    doNothing().when(serviceMock).saveCloudTransparentAccess(access);

    mockMvc
        .perform(delete("/api/cloudTransparentAccess/v1/resourceType/resourceId"))
        .andExpect(status().isNoContent());
  }
}

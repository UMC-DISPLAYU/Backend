package com.example.demo.domain.user.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest(properties = "springdoc.api-docs.enabled=true")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserArtistOpenApiTest {

  @Autowired private MockMvc mvc;

  private final JsonMapper mapper = JsonMapper.builder().build();

  @Test
  void documentsConcreteSuccessExamplesForEveryUserAndArtistJsonApi() throws Exception {
    JsonNode document = document();

    for (OperationRef ref :
        List.of(
            new OperationRef("/api/v1/agreements", "get", "200"),
            new OperationRef("/api/v1/schools", "get", "200"),
            new OperationRef("/api/v1/users/me/verification/email/send", "post", "200"),
            new OperationRef("/api/v1/users/me/verification/email/resend", "post", "200"),
            new OperationRef("/api/v1/users/me/verification/email/confirm", "post", "200"),
            new OperationRef("/api/v1/users/me", "get", "200"),
            new OperationRef("/api/v1/users/me", "patch", "200"),
            new OperationRef("/api/v1/users/me", "delete", "200"),
            new OperationRef("/api/v1/users/me/nickname", "patch", "200"),
            new OperationRef("/api/v1/users/nickname/check", "get", "200"),
            new OperationRef("/api/v1/users/search", "get", "200"),
            new OperationRef("/api/v1/auth/signup", "post", "200"),
            new OperationRef("/api/v1/auth/refresh", "post", "200"),
            new OperationRef("/api/v1/auth/logout", "post", "200"),
            new OperationRef("/api/v1/auth/kakao/login-url", "get", "200"),
            new OperationRef("/api/v1/auth/google/login-url", "get", "200"),
            new OperationRef("/api/v1/artists/me/artist-profile", "post", "201"),
            new OperationRef("/api/v1/artists/me/artist-profile", "get", "200"),
            new OperationRef("/api/v1/artists/me/artist-profile", "patch", "200"),
            new OperationRef("/api/v1/artists/{userId}/artist-profile", "get", "200"))) {
      JsonNode example = firstExample(document, ref);
      assertThat(example.path("resultType").asText()).as(ref.toString()).isEqualTo("SUCCESS");
      assertThat(example.path("success").has("data")).as(ref.toString()).isTrue();
      assertThat(example.path("error").isNull()).as(ref.toString()).isTrue();
      assertThat(example.path("meta").path("path").asText())
          .as(ref.toString())
          .startsWith("/api/v1/")
          .doesNotContain("string", "{");
      assertNoPlaceholder(example, ref.toString());
    }
  }

  @Test
  void documentsRuntimeErrorEnvelopesAndStatusCodes() throws Exception {
    JsonNode document = document();

    assertStatuses(document, "/api/v1/auth/signup", "post", "200", "400", "401", "409");
    assertStatuses(document, "/api/v1/auth/refresh", "post", "200", "401", "403");
    assertStatuses(document, "/api/v1/auth/logout", "post", "200", "401", "403");
    assertStatuses(document, "/api/v1/users/me", "patch", "200", "400", "401", "404", "409");
    assertStatuses(
        document,
        "/api/v1/users/me/verification/email/send",
        "post",
        "200",
        "400",
        "401",
        "404",
        "409",
        "429",
        "500");
    assertStatuses(
        document,
        "/api/v1/users/me/verification/email/resend",
        "post",
        "200",
        "400",
        "401",
        "404",
        "429");
    assertStatuses(
        document,
        "/api/v1/users/me/verification/email/confirm",
        "post",
        "200",
        "400",
        "401",
        "404",
        "409");
    assertStatuses(
        document,
        "/api/v1/artists/me/artist-profile",
        "post",
        "201",
        "400",
        "401",
        "403",
        "404",
        "409");
    assertStatuses(
        document,
        "/api/v1/artists/me/artist-profile",
        "patch",
        "200",
        "400",
        "401",
        "403",
        "404",
        "409");

    for (String path : List.of("/api/v1/auth/kakao/callback", "/api/v1/auth/google/callback")) {
      assertStatuses(document, path, "get", "302", "400", "401");
    }
  }

  private JsonNode document() throws Exception {
    return mapper.readTree(
        mvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString());
  }

  private JsonNode firstExample(JsonNode document, OperationRef ref) throws Exception {
    JsonNode examples =
        operation(document, ref.path(), ref.method())
            .path("responses")
            .path(ref.status())
            .path("content")
            .path("application/json")
            .path("examples");
    assertThat(examples.isEmpty()).as(ref.toString()).isFalse();
    JsonNode value = examples.iterator().next().path("value");
    return value.isTextual() ? mapper.readTree(value.asText()) : value;
  }

  private void assertStatuses(
      JsonNode document, String path, String method, String... expectedStatuses) {
    JsonNode responses = operation(document, path, method).path("responses");
    for (String expectedStatus : expectedStatuses) {
      assertThat(responses.has(expectedStatus))
          .as("%s %s %s", method, path, expectedStatus)
          .isTrue();
    }
  }

  private JsonNode operation(JsonNode document, String path, String method) {
    JsonNode operation = document.path("paths").path(path).path(method);
    assertThat(operation.isMissingNode()).as("%s %s", method, path).isFalse();
    return operation;
  }

  private void assertNoPlaceholder(JsonNode node, String operation) {
    if (node.isTextual()) {
      assertThat(node.asText()).as(operation).isNotEqualTo("string");
    }
    for (JsonNode child : node) {
      assertNoPlaceholder(child, operation);
    }
  }

  private record OperationRef(String path, String method, String status) {}
}

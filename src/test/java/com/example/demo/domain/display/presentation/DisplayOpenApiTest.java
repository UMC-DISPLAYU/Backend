package com.example.demo.domain.display.presentation;

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
class DisplayOpenApiTest {

  @Autowired private MockMvc mvc;

  @Test
  void documentsFrontendListArtworkAndCancellationContracts() throws Exception {
    JsonNode document =
        JsonMapper.builder()
            .build()
            .readTree(
                mvc.perform(get("/v3/api-docs"))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString());

    for (String path :
        List.of(
            "/api/v1/display/popular",
            "/api/v1/display/graduation",
            "/api/v1/display/search",
            "/api/v1/display/closing-soon")) {
      JsonNode data = responseData(document, path, "get");
      JsonNode card = resolve(document, property(document, data, "exhibitions").path("items"));
      assertThat(property(document, card, "placeName").isMissingNode()).as(path).isFalse();
      assertCrop(document, property(document, card, "posterCrop"));
    }

    JsonNode preview = responseData(document, "/api/v1/artworks/preview", "get");
    JsonNode artwork = resolve(document, property(document, preview, "artworks").path("items"));
    JsonNode artist = resolve(document, property(document, artwork, "artists").path("items"));
    assertThat(property(document, artist, "name").isMissingNode()).isFalse();
    assertNullable(property(document, artist, "userId"));

    assertDetailFields(document, responseData(document, "/api/v1/display", "post", "201"));
    assertDetailFields(document, responseData(document, "/api/v1/display", "patch"));
    assertDetailFields(document, responseData(document, "/api/v1/display/{displayId}", "get"));
    assertDetailFields(document, responseData(document, "/api/v1/display/publish", "patch"));

    JsonNode request =
        resolve(
            document,
            operation(document, "/api/v1/display", "post")
                .path("requestBody")
                .path("content")
                .path("application/json")
                .path("schema"));
    for (String field : List.of("instagramUrl", "websiteUrl")) {
      assertThat(property(document, request, field).path("maxLength").asInt()).isEqualTo(255);
      assertThat(request.path("required").toString()).doesNotContain('"' + field + '"');
    }

    for (String method : List.of("post", "patch")) {
      JsonNode writeRequest =
          resolve(
              document,
              operation(document, "/api/v1/display", method)
                  .path("requestBody")
                  .path("content")
                  .path("application/json")
                  .path("schema"));
      assertCrop(document, property(document, writeRequest, "posterCrop"));
      JsonNode fields = resolve(document, property(document, writeRequest, "fields").path("items"));
      java.util.ArrayList<String> values = new java.util.ArrayList<>();
      fields.path("enum").forEach(value -> values.add(value.asText()));
      assertThat(values)
          .containsExactlyInAnyOrder(
              "PAINTING",
              "SCULPTURE",
              "PHOTOGRAPHY",
              "GRAPHIC",
              "BRANDING",
              "PRODUCT",
              "VIDEO",
              "SPACE",
              "ARCHITECTURE",
              "UXUI",
              "CRAFTS",
              "OTHERS");
    }

    JsonNode cancel =
        operation(document, "/api/v1/display/{displayId}/screenings/{screeningId}/cancel", "post");
    assertThat(cancel.has("requestBody")).isFalse();
    for (String code : List.of("200", "401", "403", "404", "409")) {
      assertThat(cancel.path("responses").has(code)).as(code).isTrue();
    }
    JsonNode cancellation =
        responseData(
            document, "/api/v1/display/{displayId}/screenings/{screeningId}/cancel", "post");
    for (String field : List.of("displayId", "screeningId", "status", "screeningStatus")) {
      assertThat(property(document, cancellation, field).isMissingNode()).as(field).isFalse();
    }
  }

  private void assertCrop(JsonNode document, JsonNode crop) {
    assertNullable(crop);
    JsonNode schema = resolve(document, crop);
    for (String coordinate : List.of("x", "y", "w", "h")) {
      assertThat(property(document, schema, coordinate).isMissingNode()).isFalse();
      assertThat(schema.path("required").toString()).contains('"' + coordinate + '"');
    }
  }

  private void assertDetailFields(JsonNode document, JsonNode data) {
    assertCrop(document, property(document, data, "posterCrop"));
    for (String field : List.of("instagramUrl", "websiteUrl", "currentScreeningId")) {
      JsonNode schema = property(document, data, field);
      assertThat(schema.isMissingNode()).as(field).isFalse();
      assertNullable(schema);
    }
  }

  private void assertNullable(JsonNode schema) {
    assertThat(
            schema.path("nullable").asBoolean() || schema.path("type").toString().contains("null"))
        .as("nullable schema: %s", schema)
        .isTrue();
  }

  private JsonNode responseData(JsonNode document, String path, String method) {
    return responseData(document, path, method, "200");
  }

  private JsonNode responseData(JsonNode document, String path, String method, String code) {
    JsonNode envelope =
        resolve(
            document,
            operation(document, path, method)
                .path("responses")
                .path(code)
                .path("content")
                .path("application/json")
                .path("schema"));
    return resolve(document, property(document, property(document, envelope, "success"), "data"));
  }

  private JsonNode operation(JsonNode document, String path, String method) {
    JsonNode operation = document.path("paths").path(path).path(method);
    assertThat(operation.isMissingNode()).as("%s %s", method, path).isFalse();
    return operation;
  }

  private JsonNode property(JsonNode document, JsonNode schema, String name) {
    return resolve(document, resolve(document, schema).path("properties").path(name));
  }

  private JsonNode resolve(JsonNode document, JsonNode schema) {
    return schema.has("$ref") ? document.at(schema.path("$ref").asText().substring(1)) : schema;
  }
}

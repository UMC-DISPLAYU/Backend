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

@SpringBootTest(properties = {"springdoc.api-docs.enabled=true", "admin.review.enabled=true"})
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
      assertThat(property(document, writeRequest, "region").path("enum").toString())
          .doesNotContain("ALL");
      for (String helper : List.of("fieldsValid", "regionValid", "departmentOrClubValid")) {
        assertThat(writeRequest.path("properties").has(helper)).as(helper).isFalse();
      }
      assertCrop(document, property(document, writeRequest, "posterCrop"));
      for (String dimension : List.of("w", "h")) {
        assertThat(
                property(document, property(document, writeRequest, "posterCrop"), dimension)
                    .path("minimum")
                    .decimalValue())
            .isEqualByComparingTo("0.0000000001");
      }
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

  @Test
  void documentsPatchFieldConstraintsAndEveryPosterCard() throws Exception {
    JsonNode document = document();
    JsonNode patch =
        resolve(
            document,
            operation(document, "/api/v1/display", "patch")
                .path("requestBody")
                .path("content")
                .path("application/json")
                .path("schema"));
    for (String name :
        List.of(
            "instagramUrl",
            "websiteUrl",
            "roadAddress",
            "latitude",
            "longitude",
            "region",
            "displayImageUrl",
            "posterCrop")) {
      JsonNode field = property(document, patch, name);
      assertThat(field.isMissingNode()).as(name).isFalse();
      assertNullable(field);
      assertThat(patch.path("required").toString()).doesNotContain('"' + name + '"');
    }
    for (String name : List.of("instagramUrl", "websiteUrl")) {
      JsonNode field = property(document, patch, name);
      assertThat(field.path("maxLength").asInt()).isEqualTo(255);
      assertThat(field.path("description").asText()).contains("빈 문자열", "삭제");
    }
    assertThat(property(document, patch, "latitude").path("minimum").asDouble()).isEqualTo(-90);
    assertThat(property(document, patch, "latitude").path("maximum").asDouble()).isEqualTo(90);
    assertThat(property(document, patch, "longitude").path("minimum").asDouble()).isEqualTo(-180);
    assertThat(property(document, patch, "longitude").path("maximum").asDouble()).isEqualTo(180);
    assertThat(property(document, patch, "region").path("enum").toString())
        .contains("SEOUL", "GYEONGGI_INCHEON", "OTHERS")
        .doesNotContain("ALL");
    JsonNode images = property(document, patch, "displayImageUrl");
    assertThat(images.path("maxItems").asInt()).isEqualTo(4);
    assertThat(property(document, patch, "fields").path("minItems").asInt()).isEqualTo(1);
    assertThat(images.path("items").path("type").asText()).isEqualTo("string");
    assertThat(images.path("description").asText()).contains("빈 배열", "삭제");

    for (List<String> route :
        List.of(
            List.of("/api/v1/display/map", "markers"),
            List.of("/api/v1/display/me", "createdDisplays"),
            List.of("/api/v1/display/me", "participatedDisplays"),
            List.of("/api/v1/display/artists/{userId}", "createdDisplays"),
            List.of("/api/v1/display/artists/{userId}", "participatedDisplays"),
            List.of("/api/v1/archives/exhibitions", "displays"),
            List.of("/api/v1/display-invitations", "exhibitions"),
            List.of("/api/v1/display-invitations/me", "invitations"),
            List.of("/api/v1/admin/displays", "items"))) {
      JsonNode data = responseData(document, route.get(0), "get");
      JsonNode card = resolve(document, property(document, data, route.get(1)).path("items"));
      assertThat(property(document, card, "posterCrop").isMissingNode())
          .as("%s schema: %s", route, card)
          .isFalse();
      assertCrop(document, property(document, card, "posterCrop"));
    }
  }

  @Test
  void posterExamplesMatchTheDocumentedCropContract() throws Exception {
    JsonNode document = document();
    for (String path :
        List.of(
            "/api/v1/display",
            "/api/v1/display/{displayId}",
            "/api/v1/display/map",
            "/api/v1/display/popular",
            "/api/v1/display/graduation",
            "/api/v1/display/search",
            "/api/v1/display/closing-soon",
            "/api/v1/display/me",
            "/api/v1/display/artists/{userId}",
            "/api/v1/archives/exhibitions",
            "/api/v1/display-invitations",
            "/api/v1/display-invitations/me")) {
      JsonNode operations = document.path("paths").path(path);
      for (JsonNode operation : operations) {
        assertMediaExamples(operation.path("requestBody").path("content").path("application/json"));
        for (JsonNode response : operation.path("responses")) {
          assertMediaExamples(response.path("content").path("application/json"));
        }
      }
    }
    JsonNode patchExamples =
        operation(document, "/api/v1/display", "patch")
            .path("requestBody")
            .path("content")
            .path("application/json")
            .path("examples");
    assertThat(patchExamples.isEmpty()).isFalse();
    for (JsonNode example : patchExamples) {
      JsonNode value = exampleValue(example.path("value"));
      for (String name :
          List.of(
              "instagramUrl",
              "websiteUrl",
              "roadAddress",
              "latitude",
              "longitude",
              "region",
              "displayImageUrl",
              "posterCrop")) {
        assertThat(value.has(name)).as(name).isTrue();
      }
    }
  }

  private JsonNode document() throws Exception {
    return JsonMapper.builder()
        .build()
        .readTree(
            mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString());
  }

  private void assertMediaExamples(JsonNode media) throws Exception {
    assertExamples(media.path("examples"));
    if (media.has("example")) {
      assertPosterExample(exampleValue(media.path("example")));
    }
  }

  private void assertExamples(JsonNode examples) throws Exception {
    for (JsonNode example : examples) {
      assertPosterExample(exampleValue(example.path("value")));
    }
  }

  private JsonNode exampleValue(JsonNode value) throws Exception {
    return value.isTextual() ? JsonMapper.builder().build().readTree(value.asText()) : value;
  }

  private void assertPosterExample(JsonNode value) {
    if (value.isObject()
        && (value.has("posterImageUrl")
            || value.has("postImageUrl")
            || value.has("thumbnailUrl")
            || value.has("displayFields"))) {
      assertThat(value.has("posterCrop")).as("poster example: %s", value).isTrue();
      JsonNode crop = value.path("posterCrop");
      if (!crop.isNull()) {
        for (String coordinate : List.of("x", "y", "w", "h")) {
          assertThat(crop.path(coordinate).isNumber()).isTrue();
        }
        assertThat(crop.path("x").asDouble()).isBetween(0.0, 1.0);
        assertThat(crop.path("y").asDouble()).isBetween(0.0, 1.0);
        assertThat(crop.path("w").asDouble()).isGreaterThan(0.0);
        assertThat(crop.path("h").asDouble()).isGreaterThan(0.0);
        assertThat(crop.path("x").asDouble() + crop.path("w").asDouble()).isLessThanOrEqualTo(1.0);
        assertThat(crop.path("y").asDouble() + crop.path("h").asDouble()).isLessThanOrEqualTo(1.0);
      }
    }
    for (JsonNode child : value) {
      assertPosterExample(child);
    }
  }

  private void assertCrop(JsonNode document, JsonNode crop) {
    assertNullable(crop);
    JsonNode schema = resolve(document, crop);
    assertThat(schema.path("properties").has("contained")).isFalse();
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
    JsonNode content =
        operation(document, path, method).path("responses").path(code).path("content");
    JsonNode media = content.path("application/json");
    JsonNode envelope = resolve(document, media.path("schema"));
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

package com.example.demo.domain.health.presentation;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.micrometer.metrics.test.autoconfigure.AutoConfigureMetrics;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalManagementPort;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureMetrics
class HealthControllerTest {

  private final HttpClient httpClient = HttpClient.newHttpClient();

  @Autowired private MockMvc mockMvc;

  @LocalServerPort private int serverPort;

  @LocalManagementPort private int managementPort;

  @Value("${management.server.port}")
  private int configuredManagementPort;

  @Test
  void healthReturnsCommonSuccessResponse() throws Exception {
    mockMvc
        .perform(get("/api/v1/health"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.status").value("UP"))
        .andExpect(jsonPath("$.success.data.checkedAt", notNullValue()))
        .andExpect(jsonPath("$.error").doesNotExist())
        .andExpect(jsonPath("$.meta.timestamp", notNullValue()))
        .andExpect(jsonPath("$.meta.path").value("/api/v1/health"));
  }

  @Test
  void actuatorEndpointsExposePrometheusMetricsOnDynamicManagementPort() throws Exception {
    HttpResponse<String> apiHealth = httpGet(serverPort, "/api/v1/health");
    HttpResponse<String> actuatorHealth = httpGet(managementPort, "/actuator/health");
    HttpResponse<String> prometheus = httpGet(managementPort, "/actuator/prometheus");

    assertAll(
        () -> assertEquals(0, configuredManagementPort),
        () -> assertNotEquals(serverPort, managementPort),
        () -> assertEquals(200, apiHealth.statusCode()),
        () -> assertTrue(apiHealth.body().contains("\"status\":\"UP\"")),
        () -> assertEquals(200, actuatorHealth.statusCode()),
        () -> assertTrue(actuatorHealth.body().contains("\"status\":\"UP\"")),
        () -> assertEquals(200, prometheus.statusCode()),
        () -> assertTrue(prometheus.body().contains("application=\"displayu-backend\"")),
        () -> assertTrue(prometheus.body().contains("environment=\"test\"")),
        () -> assertTrue(prometheus.body().contains("http_server_requests_seconds_bucket")),
        () -> assertTrue(prometheus.body().contains("le=\"0.1\"")));
  }

  private HttpResponse<String> httpGet(int port, String path) throws Exception {
    HttpRequest request =
        HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).GET().build();
    return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
  }
}

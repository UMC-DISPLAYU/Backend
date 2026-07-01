package com.example.demo.domain.health.presentation;

import com.example.demo.global.response.ApiResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

  @GetMapping("/api/v1/health")
  public ApiResponseBody<HealthResponse> health(HttpServletRequest request) {
    return ApiResponseBody.success(new HealthResponse("UP", LocalDateTime.now()), request);
  }

  public record HealthResponse(String status, LocalDateTime checkedAt) {}
}

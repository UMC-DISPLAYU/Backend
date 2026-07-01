package com.example.demo.domain.health.presentation;

import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Health", description = "서버 상태 확인 API")
public class HealthController {

  @GetMapping("/api/v1/health")
  @Operation(summary = "Health check", description = "서버가 정상적으로 실행 중인지 확인합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "서버 상태 확인 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Health check success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "status": "UP",
                                "checkedAt": "2026-07-02T02:50:00"
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-07-02T02:50:00",
                              "path": "/api/v1/health"
                            }
                          }
                          """)))
  public ApiResponseBody<HealthResponse> health(HttpServletRequest request) {
    return ApiResponseBody.success(new HealthResponse("UP", LocalDateTime.now()), request);
  }

  public record HealthResponse(String status, LocalDateTime checkedAt) {}
}

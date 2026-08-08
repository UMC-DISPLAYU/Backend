package com.example.demo.global.error;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ExtendWith(OutputCaptureExtension.class)
class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

  @Test
  void logsBadRequestAtWarn(CapturedOutput output) {
    MockHttpServletRequest request = request("POST", "/api/v1/displays");

    var response =
        exceptionHandler.handleBusinessException(
            new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE), request);

    assertThat(response.getStatusCode().value()).isEqualTo(400);
    assertThat(output)
        .contains("WARN")
        .contains("status=400")
        .contains("code=INVALID_INPUT_VALUE")
        .contains("method=POST")
        .contains("uri=/api/v1/displays");
  }

  @Test
  void logsMissingResourceAtInfoAndReturns404(CapturedOutput output) {
    MockHttpServletRequest request = request("GET", "/phpunit/eval-stdin.php");

    var response =
        exceptionHandler.handleNoResourceFoundException(
            new NoResourceFoundException(
                HttpMethod.GET, request.getRequestURI(), "classpath:/static"),
            request);

    assertThat(response.getStatusCode().value()).isEqualTo(404);
    assertThat(output)
        .contains("INFO")
        .contains("status=404")
        .contains("code=NOT_FOUND")
        .contains("method=GET")
        .contains("uri=/phpunit/eval-stdin.php");
  }

  @Test
  void logsUnhandledExceptionAtError(CapturedOutput output) {
    MockHttpServletRequest request = request("GET", "/api/v1/displays");

    var response = exceptionHandler.handleException(new RuntimeException("test"), request);

    assertThat(response.getStatusCode().value()).isEqualTo(500);
    assertThat(output)
        .contains("ERROR")
        .contains("Unhandled exception occurred")
        .contains("uri=/api/v1/displays");
  }

  private MockHttpServletRequest request(String method, String uri) {
    return new MockHttpServletRequest(method, uri);
  }
}

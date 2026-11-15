package com.example.demo.global.security;

import com.example.demo.global.error.BaseErrorCode;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.response.ApiResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  public static final String AUTH_ERROR_ATTRIBUTE =
      CustomAuthenticationEntryPoint.class.getName() + ".AUTH_ERROR";

  private final ObjectMapper objectMapper;

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {

    BaseErrorCode errorCode = resolveErrorCode(request);

    log.warn(
        "HTTP error response. status={} code={} method={} uri={}",
        errorCode.getStatus().value(),
        errorCode.getCode(),
        request.getMethod(),
        request.getRequestURI());

    response.setStatus(errorCode.getStatus().value());

    response.setContentType("application/json;charset=UTF-8");

    ApiResponseBody<Void> body =
        ApiResponseBody.fail(
            new ApiResponseBody.ErrorBody(errorCode.getCode(), errorCode.getMessage(), null),
            request);

    response.getWriter().write(objectMapper.writeValueAsString(body));
  }

  private BaseErrorCode resolveErrorCode(HttpServletRequest request) {
    Object errorCode = request.getAttribute(AUTH_ERROR_ATTRIBUTE);
    return errorCode instanceof BaseErrorCode baseErrorCode
        ? baseErrorCode
        : GlobalErrorCode.UNAUTHORIZED;
  }
}

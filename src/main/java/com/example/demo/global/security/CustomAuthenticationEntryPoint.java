package com.example.demo.global.security;

import com.example.demo.domain.user.exception.AuthErrorCode;
import com.example.demo.global.response.ApiResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    response.setContentType("application/json;charset=UTF-8");

    ApiResponseBody<Void> body =
        ApiResponseBody.fail(
            new ApiResponseBody.ErrorBody(
                AuthErrorCode.INVALID_ACCESS_TOKEN.getCode(),
                AuthErrorCode.INVALID_ACCESS_TOKEN.getMessage(),
                null),
            request);

    response.getWriter().write(objectMapper.writeValueAsString(body));
  }
}

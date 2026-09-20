package com.example.demo.global.security;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final TokenProvider tokenProvider;
  private final AdminAccessPort adminAccessPort;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String authorization = request.getHeader("Authorization");

    if (authorization == null || !authorization.startsWith("Bearer ")) {

      filterChain.doFilter(request, response);
      return;
    }

    String token = authorization.substring(7);

    try {

      tokenProvider.validateAccessTokenOrThrow(token);

      Long userId = tokenProvider.getUserId(token);

      AuthUser authUser = new AuthUser(userId);

      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(
              authUser,
              null,
              adminAccessPort.isAdmin(userId)
                  ? List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                  : List.of());

      SecurityContextHolder.getContext().setAuthentication(authentication);

    } catch (RuntimeException e) {

      SecurityContextHolder.clearContext();
    }

    filterChain.doFilter(request, response);
  }
}

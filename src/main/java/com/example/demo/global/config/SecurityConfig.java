package com.example.demo.global.config;

import com.example.demo.global.security.CustomAuthenticationEntryPoint;
import com.example.demo.global.security.JwtAuthenticationFilter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    return http.csrf(csrf -> csrf.disable())
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(HttpMethod.GET, "/api/v1/agreements")
                    .permitAll()
                    .requestMatchers(
                        "/api/v1/users/me/**", "/api/v1/artists/me/**", "/api/v1/archives/**")
                    .authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/v1/users/search")
                    .authenticated()
                    .requestMatchers(HttpMethod.HEAD, "/api/v1/users/search")
                    .authenticated()
                    .requestMatchers(HttpMethod.POST, "/api/v1/lounge/**")
                    .authenticated()
                    .requestMatchers(HttpMethod.PATCH, "/api/v1/lounge/**")
                    .authenticated()
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/lounge/**")
                    .authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/v1/lounge/me/**")
                    .authenticated()
                    .requestMatchers(HttpMethod.HEAD, "/api/v1/lounge/me/**")
                    .authenticated()
                    .requestMatchers(HttpMethod.POST, "/api/v1/display/**")
                    .authenticated()
                    .requestMatchers(HttpMethod.PUT, "/api/v1/display/**")
                    .authenticated()
                    .requestMatchers(HttpMethod.PATCH, "/api/v1/display/**")
                    .authenticated()
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/display/**")
                    .authenticated()
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/v1/display/me",
                        "/api/v1/display-invitations",
                        "/api/v1/display-invitations/me")
                    .authenticated()
                    .requestMatchers(
                        HttpMethod.HEAD,
                        "/api/v1/display/me",
                        "/api/v1/display-invitations",
                        "/api/v1/display-invitations/me")
                    .authenticated()
                    .requestMatchers(HttpMethod.POST, "/api/v1/display-invitations/**")
                    .authenticated()
                    .requestMatchers(HttpMethod.PATCH, "/api/v1/display-invitations/**")
                    .authenticated()
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/display-invitations/**")
                    .authenticated()
                    .requestMatchers(HttpMethod.POST, "/api/v1/artworks/**")
                    .authenticated()
                    .requestMatchers(HttpMethod.PUT, "/api/v1/artworks/**")
                    .authenticated()
                    .requestMatchers(HttpMethod.PATCH, "/api/v1/artworks/**")
                    .authenticated()
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/artworks/**")
                    .authenticated()
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/v1/artworks/*/edit",
                        "/api/v1/artworks/questions/me",
                        "/api/v1/artworks/questions/received",
                        "/api/v1/artworks/feelings/me")
                    .authenticated()
                    .requestMatchers(
                        HttpMethod.HEAD,
                        "/api/v1/artworks/*/edit",
                        "/api/v1/artworks/questions/me",
                        "/api/v1/artworks/questions/received",
                        "/api/v1/artworks/feelings/me")
                    .authenticated()
                    .requestMatchers(HttpMethod.POST, "/api/v1/personal-artworks/**")
                    .authenticated()
                    .requestMatchers(HttpMethod.PUT, "/api/v1/personal-artworks/**")
                    .authenticated()
                    .requestMatchers(HttpMethod.PATCH, "/api/v1/personal-artworks/**")
                    .authenticated()
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/personal-artworks/**")
                    .authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/v1/display/reviews/me")
                    .authenticated()
                    .requestMatchers(HttpMethod.HEAD, "/api/v1/display/reviews/me")
                    .authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/v1/display/*/reviews/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.HEAD, "/api/v1/display/*/reviews/**")
                    .permitAll()
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/v1/auth/kakao/login-url",
                        "/api/v1/auth/kakao/callback",
                        "/api/v1/auth/google/login-url",
                        "/api/v1/auth/google/callback")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/v1/auth/logout")
                    .authenticated()
                    .anyRequest()
                    .permitAll())
        .exceptionHandling(
            exception -> exception.authenticationEntryPoint(customAuthenticationEntryPoint))
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOriginPatterns(List.of("*"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setExposedHeaders(List.of("Authorization"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}

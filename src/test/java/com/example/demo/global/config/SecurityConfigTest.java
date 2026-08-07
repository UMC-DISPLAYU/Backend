package com.example.demo.global.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigTest {

  @Autowired private MockMvc mockMvc;

  @ParameterizedTest
  @ValueSource(
      strings = {
        "/api/v1/users/search",
        "/api/v1/lounge/me/posts",
        "/api/v1/display/me",
        "/api/v1/display/1/members",
        "/api/v1/display-invitations",
        "/api/v1/display-invitations/me",
        "/api/v1/artworks/1/edit",
        "/api/v1/artworks/questions/me",
        "/api/v1/artworks/questions/received",
        "/api/v1/artworks/feelings/me",
        "/api/v1/display/reviews/me",
        "/api/v1/display/1/reviews/1"
      })
  void rejectsHeadRequestWithoutAuthentication(String path) throws Exception {
    mockMvc.perform(head(path)).andExpect(status().isUnauthorized());
  }
}

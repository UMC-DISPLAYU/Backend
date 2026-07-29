package com.example.demo.domain.user.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MeAuthenticationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void getMeReturnsUnauthorizedWithoutAuthentication() throws Exception {
    mockMvc
        .perform(get("/api/v1/users/me"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"))
        .andExpect(jsonPath("$.meta.path").value("/api/v1/users/me"));
  }

  @Test
  void updateMeReturnsUnauthorizedWithoutAuthentication() throws Exception {
    assertUnauthorized(
        patch("/api/v1/users/me")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"nickname\":\"User2\"}"),
        "/api/v1/users/me");
  }

  @Test
  void withdrawReturnsUnauthorizedWithoutAuthentication() throws Exception {
    assertUnauthorized(delete("/api/v1/users/me"), "/api/v1/users/me");
  }

  @Test
  void changeNicknameReturnsUnauthorizedWithoutAuthentication() throws Exception {
    assertUnauthorized(
        patch("/api/v1/users/me/nickname")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"nickname\":\"User2\"}"),
        "/api/v1/users/me/nickname");
  }

  @Test
  void getMyArtistProfileReturnsUnauthorizedWithoutAuthentication() throws Exception {
    assertUnauthorized(get("/api/v1/users/me/artist-profile"), "/api/v1/users/me/artist-profile");
  }

  @Test
  void updateMyArtistProfileReturnsUnauthorizedWithoutAuthentication() throws Exception {
    assertUnauthorized(
        patch("/api/v1/users/me/artist-profile")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                """
                {
                  "artistName": "Artist",
                  "fields": ["PAINTING"]
                }
                """),
        "/api/v1/users/me/artist-profile");
  }

  @Test
  void sendSchoolEmailVerificationReturnsUnauthorizedWithoutAuthentication() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/users/me/verification/email/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "schoolEmail": "user@university.ac.kr",
                      "univName": "University"
                    }
                    """))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"))
        .andExpect(jsonPath("$.meta.path").value("/api/v1/users/me/verification/email/send"));
  }

  @Test
  void createArtistProfileReturnsUnauthorizedWithoutAuthentication() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/artists/me/artist-profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "artistName": "Artist",
                      "activityFields": ["PAINTING"]
                    }
                    """))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"))
        .andExpect(jsonPath("$.meta.path").value("/api/v1/artists/me/artist-profile"));
  }

  private void assertUnauthorized(
      org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder request,
      String path)
      throws Exception {
    mockMvc
        .perform(request)
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"))
        .andExpect(jsonPath("$.meta.path").value(path));
  }
}

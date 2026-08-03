package com.example.demo.domain.displayartwork.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
class DisplayArtworkControllerTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void likeDisplayArtworkReturnsUnauthorizedWithoutAuthentication() throws Exception {
    mockMvc
        .perform(post("/api/v1/artworks/{artworkId}/like", 1L))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  @Test
  void cancelDisplayArtworkLikeReturnsUnauthorizedWithoutAuthentication() throws Exception {
    mockMvc
        .perform(delete("/api/v1/artworks/{artworkId}/like", 1L))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  @Test
  void deleteDisplayArtworkReturnsUnauthorizedWithoutAuthentication() throws Exception {
    mockMvc
        .perform(delete("/api/v1/artworks/{artworkId}", 1L))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  @Test
  void reorderDisplayArtworksReturnsUnauthorizedWithoutAuthentication() throws Exception {
    mockMvc
        .perform(
            put("/api/v1/artworks/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "displayId": 1,
                      "orderedArtworkIds": [1]
                    }
                    """))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  @Test
  void getArtworksByArtistReturnsEmptyListWhenUserHasNoArtwork() throws Exception {
    mockMvc
        .perform(get("/api/v1/artworks").param("userId", "999999"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.artworks").isArray())
        .andExpect(jsonPath("$.success.data.artworks").isEmpty());
  }

  @Test
  void getArtworksReturnsBadRequestWhenNoFilterParameterGiven() throws Exception {
    mockMvc
        .perform(get("/api/v1/artworks"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("MISSING_REQUIRED_VALUE"));
  }

  @Test
  void getArtworksReturnsBadRequestWhenBothFilterParametersGiven() throws Exception {
    mockMvc
        .perform(get("/api/v1/artworks").param("displayId", "1").param("userId", "1"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("MISSING_REQUIRED_VALUE"));
  }

  @Test
  void getArtworksByArtistReturnsBadRequestWhenUserIdIsNotPositive() throws Exception {
    mockMvc
        .perform(get("/api/v1/artworks").param("userId", "-1"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
  }
}

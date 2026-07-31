package com.example.demo.domain.displayartwork.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
}

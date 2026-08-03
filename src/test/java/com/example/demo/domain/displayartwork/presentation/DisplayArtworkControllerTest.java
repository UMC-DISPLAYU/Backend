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
  void createDisplayArtworkReturnsBadRequestWhenQaHandlerListIsEmpty() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/artworks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "displayId": 1,
                      "artworkName": "작품",
                      "content": "설명",
                      "type": "PAINTING",
                      "productionYear": 2026,
                      "materialMedia": "캔버스",
                      "size": "100x100",
                      "point": "감상 포인트",
                      "images": [
                        {
                          "imageUrl": "https://example.com/a.jpg",
                          "isThumbnail": true,
                          "imageType": "ARTWORK",
                          "sortOrder": 0,
                          "width": 800,
                          "height": 600
                        }
                      ],
                      "artistName": "작가",
                      "artistUserId": 1,
                      "coAuthors": { "userIds": [], "rawNames": [] },
                      "qaHandlerUserIds": []
                    }
                    """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"))
        .andExpect(jsonPath("$.error.details[0].field").value("qaHandlerUserIds"));
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

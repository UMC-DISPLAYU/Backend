package com.example.demo.domain.displayartwork.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.global.security.JwtFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
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

  @Autowired private JwtFactory jwtFactory;

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
                .header(HttpHeaders.AUTHORIZATION, bearer(1L))
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
  void createDisplayArtworkAcceptsRequestWithoutOptionalFields() throws Exception {
    // 작품설명/규격/감상 포인트는 디자인상 선택 항목이므로 생략해도 검증에 걸리지 않아야 한다.
    // 인증이 없어 401까지는 가더라도, 필드 검증 실패(400)는 나오지 않아야 한다.
    mockMvc
        .perform(
            post("/api/v1/artworks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "displayId": 1,
                      "artworkName": "작품",
                      "type": "PAINTING",
                      "productionYear": 2026,
                      "materialMedia": "캔버스",
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
                      "qaHandlerUserIds": [1]
                    }
                    """))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  @Test
  void createDisplayArtworkReturnsBadRequestWhenRequiredFieldIsMissing() throws Exception {
    // 작품명/작품분야/제작 연도/재료·매체는 필수이므로 누락 시 검증에서 막혀야 한다.
    mockMvc
        .perform(
            post("/api/v1/artworks")
                .header(HttpHeaders.AUTHORIZATION, bearer(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "displayId": 1,
                      "type": "PAINTING",
                      "productionYear": 2026,
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
                      "qaHandlerUserIds": [1]
                    }
                    """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
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

  private String bearer(Long userId) {
    return "Bearer " + jwtFactory.create(userId.toString(), 3_600_000L, "ACCESS");
  }
}

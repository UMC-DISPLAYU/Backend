package com.example.demo.domain.personalartwork.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.personalartwork.infrastructure.persistence.SpringDataPersonalArtworkJpaRepository;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.type.Provider;
import com.example.demo.domain.user.infrastructure.persistence.UserJpaRepository;
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
class PersonalArtworkControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private SpringDataPersonalArtworkJpaRepository personalArtworkJpaRepository;

  @Autowired private JwtFactory jwtFactory;

  @Autowired private UserJpaRepository userJpaRepository;

  @Test
  void createPersonalArtworkReturnsUnauthorizedWithoutAuthentication() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/personal-artworks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequest()))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));

    assertThat(personalArtworkJpaRepository.findAllByOwnerUserIdOrderByCreatedAtAsc(42L)).isEmpty();
  }

  @Test
  void createPersonalArtworkRecordsAuthenticatedUserId() throws Exception {
    Long requesterUserId = insertUser(true);

    mockMvc
        .perform(
            post("/api/v1/personal-artworks")
                .header(HttpHeaders.AUTHORIZATION, bearer(requesterUserId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequest()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success.data.personalArtworkId").isNumber());

    assertThat(
            personalArtworkJpaRepository.findAllByOwnerUserIdOrderByCreatedAtAsc(requesterUserId))
        .hasSize(1);
  }

  @Test
  void createPersonalArtworkIsForbiddenForUnverifiedUser() throws Exception {
    Long requesterUserId = insertUser(false);

    mockMvc
        .perform(
            post("/api/v1/personal-artworks")
                .header(HttpHeaders.AUTHORIZATION, bearer(requesterUserId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequest()))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("NOT_VERIFIED_ARTIST"));

    assertThat(
            personalArtworkJpaRepository.findAllByOwnerUserIdOrderByCreatedAtAsc(requesterUserId))
        .isEmpty();
  }

  /** 개인 작품 등록은 작가 인증 여부를 User 테이블에서 확인하므로, 테스트에서도 실제 사용자 행이 필요하다. */
  private Long insertUser(boolean verified) {
    String uniqueValue = "personal-artwork-" + verified;
    User user =
        userJpaRepository.saveAndFlush(
            User.builder()
                .provider(Provider.Google)
                .providerId(uniqueValue)
                .name("테스트 사용자")
                .nickname(uniqueValue)
                .isVerified(verified)
                .socialEmail(uniqueValue + "@example.com")
                .build());
    return user.getId();
  }

  private static String createRequest() {
    return """
        {
          "artworkName": "머문 자리의 온기",
          "content": "작품 설명",
          "type": "PAINTING",
          "productionYear": 2026,
          "materialMedia": "캔버스에 유화",
          "size": "100x100cm",
          "point": "작품 포인트",
          "images": [
            {
              "imageUrl": "https://cdn.displayu.com/works/main.png",
              "isThumbnail": true,
              "imageType": "ARTWORK",
              "sortOrder": 0,
              "caption": null,
              "width": 800,
              "height": 600
            }
          ]
        }
        """;
  }

  private String bearer(Long userId) {
    return "Bearer " + jwtFactory.create(userId.toString(), 3_600_000L, "ACCESS");
  }
}

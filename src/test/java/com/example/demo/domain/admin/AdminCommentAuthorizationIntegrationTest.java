package com.example.demo.domain.admin;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.domain.admin.application.port.CommentQueryPort;
import com.example.demo.domain.admin.application.result.CommentDetail;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.type.Provider;
import com.example.demo.domain.user.domain.type.UserRole;
import com.example.demo.domain.user.infrastructure.persistence.UserJpaRepository;
import com.example.demo.global.security.TokenProvider;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = "admin.comments.enabled=true")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminCommentAuthorizationIntegrationTest {
  private static final String PATH = "/api/v1/admin/comments/COMMENT_KIND/41";
  @Autowired private MockMvc mvc;
  @Autowired private TokenProvider tokens;
  @Autowired private UserJpaRepository users;
  @Autowired private EntityManager entityManager;
  @MockitoBean private CommentQueryPort comments;

  @Test
  void administratorCanReadCommentUsingRealAuthorization() throws Exception {
    String token = tokens.createAccessToken(user(UserRole.ADMIN));
    when(comments.getDetail("COMMENT_KIND", 41L))
        .thenReturn(
            new CommentDetail(
                new CommentDetail.Comment(
                    "COMMENT_KIND", 41L, null, null, null, List.of(), null, null),
                null,
                null,
                null));

    mvc.perform(get(PATH).header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.comment.commentId").value(41));
    verify(comments).getDetail("COMMENT_KIND", 41L);
  }

  @Test
  void anonymousAndOrdinaryUsersCannotReadComment() throws Exception {
    String token = tokens.createAccessToken(user(UserRole.USER));
    mvc.perform(get(PATH)).andExpect(status().isUnauthorized());
    mvc.perform(get(PATH).header("Authorization", "Bearer " + token))
        .andExpect(status().isForbidden());
    verifyNoInteractions(comments);
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void revokedOrWithdrawnAdministratorsCannotUsePreviouslyIssuedTokens(boolean withdraw)
      throws Exception {
    User admin = user(UserRole.ADMIN);
    String token = tokens.createAccessToken(admin);
    if (withdraw) {
      admin.withdraw(LocalDateTime.now(ZoneOffset.UTC));
      users.flush();
    } else {
      entityManager
          .createQuery("update User u set u.role = :role where u.id = :id")
          .setParameter("role", UserRole.USER)
          .setParameter("id", admin.getId())
          .executeUpdate();
    }
    entityManager.clear();

    mvc.perform(get(PATH).header("Authorization", "Bearer " + token))
        .andExpect(status().is(withdraw ? 401 : 403));
    verifyNoInteractions(comments);
  }

  @Test
  void invalidAndRefreshTokensCannotReadComment() throws Exception {
    User admin = user(UserRole.ADMIN);
    for (String token : List.of("invalid", tokens.createRefreshToken(admin))) {
      mvc.perform(get(PATH).header("Authorization", "Bearer " + token))
          .andExpect(status().isUnauthorized());
    }
    verifyNoInteractions(comments);
  }

  private User user(UserRole role) {
    String id = UUID.randomUUID().toString();
    return users.saveAndFlush(
        User.builder()
            .provider(Provider.Kakao)
            .providerId(id)
            .name("사용자")
            .nickname(id)
            .socialEmail(id + "@example.com")
            .role(role)
            .build());
  }
}

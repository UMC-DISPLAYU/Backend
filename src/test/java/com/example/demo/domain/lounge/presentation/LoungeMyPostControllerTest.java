package com.example.demo.domain.lounge.presentation;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import com.example.demo.domain.lounge.domain.entity.LoungePostScrap;
import com.example.demo.domain.lounge.domain.repository.LoungeWriterRepository;
import com.example.demo.domain.lounge.domain.type.LoungePostCategory;
import com.example.demo.domain.lounge.domain.vo.UserId;
import com.example.demo.domain.lounge.infrastructure.persistence.SpringDataLoungePostJpaRepository;
import com.example.demo.domain.lounge.infrastructure.persistence.SpringDataLoungePostScrapJpaRepository;
import com.example.demo.global.security.TokenProvider;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class LoungeMyPostControllerTest {

  private static final long USER_ID = 101L;
  private static final String ACCESS_TOKEN = "test-token";

  @Autowired private MockMvc mockMvc;
  @Autowired private SpringDataLoungePostJpaRepository postRepository;
  @Autowired private SpringDataLoungePostScrapJpaRepository scrapRepository;

  @MockitoBean private LoungeWriterRepository writerRepository;
  @MockitoBean private TokenProvider tokenProvider;

  private LoungePost oldMyPost;
  private LoungePost recentMyPost;
  private LoungePostScrap recentScrap;

  @BeforeEach
  void setUp() {
    when(tokenProvider.getUserId(ACCESS_TOKEN)).thenReturn(USER_ID);
    when(writerRepository.findByUserIds(anyList())).thenReturn(Map.of());

    oldMyPost = savePost(USER_ID, "예전 내 글");
    recentMyPost = savePost(USER_ID, "최근 내 글");
    LoungePost oldScrappedPost = savePost(202L, "예전에 스크랩한 글");
    LoungePost recentScrappedPost = savePost(203L, "최근에 스크랩한 글");

    LoungePost deletedMyPost = savePost(USER_ID, "삭제한 내 글");
    deletedMyPost.delete();
    LoungePost deletedScrappedPost = savePost(204L, "삭제된 스크랩 글");
    deletedScrappedPost.delete();
    postRepository.flush();

    scrapRepository.saveAndFlush(
        LoungePostScrap.create(oldScrappedPost.getId(), new UserId(USER_ID)));
    recentScrap =
        scrapRepository.saveAndFlush(
            LoungePostScrap.create(recentScrappedPost.getId(), new UserId(USER_ID)));
    scrapRepository.saveAndFlush(
        LoungePostScrap.create(deletedScrappedPost.getId(), new UserId(USER_ID)));
  }

  @Test
  void returnsMyActivePostsWithCursor() throws Exception {
    mockMvc
        .perform(authenticatedGet("/api/v1/lounge/me/posts").param("size", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.posts.length()").value(1))
        .andExpect(jsonPath("$.success.data.posts[0].loungePostId").value(recentMyPost.getId()))
        .andExpect(jsonPath("$.success.data.posts[0].isMyPost").value(true))
        .andExpect(jsonPath("$.success.data.nextCursorId").value(recentMyPost.getId()))
        .andExpect(jsonPath("$.success.data.hasNext").value(true));

    mockMvc
        .perform(
            authenticatedGet("/api/v1/lounge/me/posts")
                .param("cursorId", recentMyPost.getId().toString())
                .param("size", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.posts.length()").value(1))
        .andExpect(jsonPath("$.success.data.posts[0].loungePostId").value(oldMyPost.getId()))
        .andExpect(jsonPath("$.success.data.nextCursorId").doesNotExist())
        .andExpect(jsonPath("$.success.data.hasNext").value(false));
  }

  @Test
  void returnsActiveScrappedPostsWithScrapCursor() throws Exception {
    mockMvc
        .perform(authenticatedGet("/api/v1/lounge/me/scraps").param("size", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.posts.length()").value(1))
        .andExpect(jsonPath("$.success.data.posts[0].title").value("최근에 스크랩한 글"))
        .andExpect(jsonPath("$.success.data.posts[0].isMyPost").value(false))
        .andExpect(jsonPath("$.success.data.nextCursorId").value(recentScrap.getId()))
        .andExpect(jsonPath("$.success.data.hasNext").value(true));

    mockMvc
        .perform(
            authenticatedGet("/api/v1/lounge/me/scraps")
                .param("cursorId", recentScrap.getId().toString())
                .param("size", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.posts.length()").value(1))
        .andExpect(jsonPath("$.success.data.posts[0].title").value("예전에 스크랩한 글"))
        .andExpect(jsonPath("$.success.data.nextCursorId").doesNotExist())
        .andExpect(jsonPath("$.success.data.hasNext").value(false));
  }

  @Test
  void rejectsAnonymousMyPostRequests() throws Exception {
    mockMvc
        .perform(get("/api/v1/lounge/me/posts"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));

    mockMvc
        .perform(get("/api/v1/lounge/me/scraps"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  private LoungePost savePost(long userId, String title) {
    return postRepository.saveAndFlush(
        LoungePost.create(new UserId(userId), title, title + " 내용", LoungePostCategory.WORK_TIP));
  }

  private MockHttpServletRequestBuilder authenticatedGet(String url) {
    return get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN);
  }
}

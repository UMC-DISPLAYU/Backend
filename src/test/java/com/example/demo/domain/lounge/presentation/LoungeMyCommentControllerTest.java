package com.example.demo.domain.lounge.presentation;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import com.example.demo.domain.lounge.domain.entity.LoungeComment;
import com.example.demo.domain.lounge.domain.repository.LoungeWriterRepository;
import com.example.demo.domain.lounge.domain.type.LoungePostCategory;
import com.example.demo.domain.lounge.domain.vo.UserId;
import com.example.demo.domain.lounge.infrastructure.persistence.SpringDataLoungeCommentJpaRepository;
import com.example.demo.domain.lounge.infrastructure.persistence.SpringDataLoungePostJpaRepository;
import com.example.demo.global.security.TokenProvider;
import java.util.List;
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
class LoungeMyCommentControllerTest {

  private static final long USER_ID = 101L;
  private static final String ACCESS_TOKEN = "test-token";

  @Autowired private MockMvc mockMvc;
  @Autowired private SpringDataLoungePostJpaRepository postRepository;
  @Autowired private SpringDataLoungeCommentJpaRepository commentRepository;

  @MockitoBean private LoungeWriterRepository writerRepository;
  @MockitoBean private TokenProvider tokenProvider;

  private LoungePost activePost;
  private LoungePost oldPost;
  private LoungeComment rootComment;
  private LoungeComment reply;

  @BeforeEach
  void setUp() {
    when(tokenProvider.getUserId(ACCESS_TOKEN)).thenReturn(USER_ID);
    when(writerRepository.findByUserIds(anyList())).thenReturn(Map.of());

    oldPost = savePost("예전에 댓글 단 게시글");
    commentRepository.saveAndFlush(
        LoungeComment.createComment(oldPost.getId(), new UserId(USER_ID), "예전 내 댓글"));

    activePost = savePost("최근에 댓글 단 게시글");
    rootComment =
        commentRepository.saveAndFlush(
            LoungeComment.createComment(
                activePost.getId(), new UserId(USER_ID), "내 댓글", List.of("comment-image")));
    reply =
        commentRepository.saveAndFlush(
            LoungeComment.createReply(
                activePost.getId(),
                rootComment.getId(),
                new UserId(USER_ID),
                "내 답글",
                List.of("reply-image")));
    commentRepository.saveAndFlush(
        LoungeComment.createComment(activePost.getId(), new UserId(202L), "다른 사용자 댓글"));

    LoungePost deletedCommentPost = savePost("삭제된 댓글만 있는 게시글");
    LoungeComment deletedComment =
        commentRepository.saveAndFlush(
            LoungeComment.createComment(deletedCommentPost.getId(), new UserId(USER_ID), "삭제한 댓글"));
    deletedComment.delete();

    LoungePost deletedPost = savePost("삭제된 게시글");
    commentRepository.saveAndFlush(
        LoungeComment.createComment(deletedPost.getId(), new UserId(USER_ID), "삭제된 글의 댓글"));
    deletedPost.delete();
    commentRepository.flush();
    postRepository.flush();
  }

  @Test
  void returnsCommentedPostsOnceWithLatestCommentCursor() throws Exception {
    mockMvc
        .perform(authenticatedGet("/api/v1/lounge/me/comments").param("size", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.comments").doesNotExist())
        .andExpect(jsonPath("$.success.data.posts.length()").value(1))
        .andExpect(jsonPath("$.success.data.posts[0].loungePostId").value(activePost.getId()))
        .andExpect(jsonPath("$.success.data.posts[0].title").value("최근에 댓글 단 게시글"))
        .andExpect(jsonPath("$.success.data.posts[0].postImageUrls[0]").value("최근에 댓글 단 게시글 이미지"))
        .andExpect(jsonPath("$.success.data.posts[0].isMyPost").value(false))
        .andExpect(jsonPath("$.success.data.nextCursorId").value(reply.getId()))
        .andExpect(jsonPath("$.success.data.hasNext").value(true));

    mockMvc
        .perform(
            authenticatedGet("/api/v1/lounge/me/comments")
                .param("cursorId", reply.getId().toString())
                .param("size", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.posts.length()").value(1))
        .andExpect(jsonPath("$.success.data.posts[0].loungePostId").value(oldPost.getId()))
        .andExpect(jsonPath("$.success.data.posts[0].title").value("예전에 댓글 단 게시글"))
        .andExpect(jsonPath("$.success.data.nextCursorId").doesNotExist())
        .andExpect(jsonPath("$.success.data.hasNext").value(false));
  }

  @Test
  void rejectsAnonymousMyCommentRequest() throws Exception {
    mockMvc
        .perform(get("/api/v1/lounge/me/comments"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  private LoungePost savePost(String title) {
    return postRepository.saveAndFlush(
        LoungePost.create(
            new UserId(303L),
            title,
            List.of(title + " 이미지"),
            title + " 내용",
            LoungePostCategory.DISPLAY_REVIEW));
  }

  private MockHttpServletRequestBuilder authenticatedGet(String url) {
    return get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN);
  }
}

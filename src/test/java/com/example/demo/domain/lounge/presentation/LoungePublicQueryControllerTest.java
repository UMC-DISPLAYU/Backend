package com.example.demo.domain.lounge.presentation;

import static org.assertj.core.api.Assertions.assertThat;
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
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class LoungePublicQueryControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private SpringDataLoungePostJpaRepository postRepository;

  @Autowired private SpringDataLoungeCommentJpaRepository commentRepository;

  @Autowired private EntityManager entityManager;

  @MockitoBean private LoungeWriterRepository writerRepository;

  private LoungePost post;
  private LoungeComment comment;

  @BeforeEach
  void setUp() {
    when(writerRepository.findByUserIds(anyList())).thenReturn(Map.of());
    post =
        postRepository.saveAndFlush(
            LoungePost.create(
                new UserId(101L),
                "전시 후기",
                List.of("image-1", "image-2", "image-3", "image-4"),
                "분위기가 좋았어요.",
                LoungePostCategory.DISPLAY_REVIEW));
    comment =
        commentRepository.saveAndFlush(
            LoungeComment.createComment(
                post.getId(),
                new UserId(102L),
                "저도 다녀왔어요.",
                List.of("comment-image-1", "comment-image-2")));
    commentRepository.saveAndFlush(
        LoungeComment.createReply(
            post.getId(),
            comment.getId(),
            new UserId(103L),
            "저도 같은 생각이에요.",
            List.of("reply-image-1", "reply-image-2")));
  }

  @Test
  void anonymousRequestsReturnPublicLoungeResponses() throws Exception {
    mockMvc
        .perform(get("/api/v1/lounge/posts"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.posts[0].postImageUrls.length()").value(4))
        .andExpect(jsonPath("$.success.data.posts[0].postImageUrls[0]").value("image-1"))
        .andExpect(jsonPath("$.success.data.posts[0].postImageUrls[3]").value("image-4"))
        .andExpect(jsonPath("$.success.data.posts[0].isLiked").value(false))
        .andExpect(jsonPath("$.success.data.posts[0].isMyPost").value(false));

    mockMvc
        .perform(get("/api/v1/lounge/posts/{loungePostId}", post.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.postImageUrls.length()").value(4))
        .andExpect(jsonPath("$.success.data.postImageUrls[0]").value("image-1"))
        .andExpect(jsonPath("$.success.data.postImageUrls[3]").value("image-4"))
        .andExpect(jsonPath("$.success.data.isLiked").value(false))
        .andExpect(jsonPath("$.success.data.isScrapped").value(false))
        .andExpect(jsonPath("$.success.data.isMyPost").value(false));

    mockMvc
        .perform(get("/api/v1/lounge/posts/{loungePostId}/comments", post.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.comments[0].imageUrls.length()").value(2))
        .andExpect(jsonPath("$.success.data.comments[0].imageUrls[0]").value("comment-image-1"))
        .andExpect(jsonPath("$.success.data.comments[0].imageUrls[1]").value("comment-image-2"))
        .andExpect(jsonPath("$.success.data.comments[0].replyCount").value(1))
        .andExpect(jsonPath("$.success.data.comments[0].isLiked").value(false))
        .andExpect(jsonPath("$.success.data.comments[0].isMyComment").value(false));

    mockMvc
        .perform(get("/api/v1/lounge/comments/{parentCommentId}/replies", comment.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.replies[0].imageUrls.length()").value(2))
        .andExpect(jsonPath("$.success.data.replies[0].imageUrls[0]").value("reply-image-1"))
        .andExpect(jsonPath("$.success.data.replies[0].imageUrls[1]").value("reply-image-2"))
        .andExpect(jsonPath("$.success.data.replies[0].replyCount").doesNotExist())
        .andExpect(jsonPath("$.success.data.replies[0].isLiked").value(false))
        .andExpect(jsonPath("$.success.data.replies[0].isMyComment").value(false));
  }

  @Test
  void commentWithoutImagesReturnsEmptyArray() throws Exception {
    commentRepository.saveAndFlush(
        LoungeComment.createComment(post.getId(), new UserId(104L), "이미지 없는 댓글"));

    mockMvc
        .perform(get("/api/v1/lounge/posts/{loungePostId}/comments", post.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.comments[1].imageUrls").isArray())
        .andExpect(jsonPath("$.success.data.comments[1].imageUrls").isEmpty());
  }

  @Test
  void anonymousCommentRequestReturnsBadRequestWhenSizeIsInvalid() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/lounge/posts/{loungePostId}/comments", post.getId()).param("size", "51"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
  }

  @Test
  void repliesReturnNotFoundWhenPostIsDeleted() throws Exception {
    post.delete();
    postRepository.flush();

    mockMvc
        .perform(get("/api/v1/lounge/comments/{parentCommentId}/replies", comment.getId()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("LOUNGE_POST_NOT_FOUND"));
  }

  @Test
  void repliesReturnNotFoundWhenPostIsHidden() throws Exception {
    post.hide();
    postRepository.flush();

    mockMvc
        .perform(get("/api/v1/lounge/comments/{parentCommentId}/replies", comment.getId()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("LOUNGE_POST_NOT_FOUND"));
  }

  @Test
  void replacesPostImagesInRequestOrder() {
    post.replaceImages(List.of("replacement-2", "replacement-1"));
    postRepository.flush();
    entityManager.clear();

    LoungePost reloaded = postRepository.findById(post.getId()).orElseThrow();

    assertThat(reloaded.getPostImageUrls()).containsExactly("replacement-2", "replacement-1");
  }
}

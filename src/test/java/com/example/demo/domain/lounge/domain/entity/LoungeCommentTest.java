package com.example.demo.domain.lounge.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.domain.lounge.domain.vo.UserId;
import java.util.List;
import org.junit.jupiter.api.Test;

class LoungeCommentTest {

  @Test
  void createsCommentWithFiveImagesInOrder() {
    List<String> imageUrls =
        List.of(
            "https://image/1",
            "https://image/2",
            "https://image/3",
            "https://image/4",
            "https://image/5");

    LoungeComment comment = LoungeComment.createComment(1L, new UserId(1L), "댓글 내용", imageUrls);

    assertThat(comment.getImageUrls()).containsExactlyElementsOf(imageUrls);
  }

  @Test
  void rejectsMoreThanFiveImages() {
    List<String> imageUrls =
        List.of(
            "https://image/1",
            "https://image/2",
            "https://image/3",
            "https://image/4",
            "https://image/5",
            "https://image/6");

    assertThatThrownBy(() -> LoungeComment.createComment(1L, new UserId(1L), "댓글 내용", imageUrls))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("imageUrls must contain at most 5 images");
  }
}

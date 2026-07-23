package com.example.demo.domain.lounge.domain.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.domain.lounge.domain.type.LoungePostCategory;
import com.example.demo.domain.lounge.domain.vo.UserId;
import java.util.List;
import org.junit.jupiter.api.Test;

class LoungePostTest {

  @Test
  void allowsNoImagesAndReplacesImagesInRequestOrder() {
    LoungePost post =
        LoungePost.create(new UserId(1L), "제목", List.of(), "내용", LoungePostCategory.DISPLAY_REVIEW);

    assertThat(post.getPostImageUrls()).isEmpty();

    post.replaceImages(List.of("image-2", "image-1"));

    assertThat(post.getPostImageUrls()).containsExactly("image-2", "image-1");

    post.replaceImages(List.of());

    assertThat(post.getPostImageUrls()).isEmpty();
  }

  @Test
  void allowsExactlyFiveImagesInRequestOrder() {
    LoungePost post =
        LoungePost.create(
            new UserId(1L),
            "제목",
            List.of("1", "2", "3", "4", "5"),
            "내용",
            LoungePostCategory.DISPLAY_REVIEW);

    assertThat(post.getPostImageUrls()).containsExactly("1", "2", "3", "4", "5");
  }

  @Test
  void rejectsMoreThanFiveImages() {
    assertThatThrownBy(
            () ->
                LoungePost.create(
                    new UserId(1L),
                    "제목",
                    List.of("1", "2", "3", "4", "5", "6"),
                    "내용",
                    LoungePostCategory.DISPLAY_REVIEW))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("postImageUrls must contain at most 5 images");
  }
}

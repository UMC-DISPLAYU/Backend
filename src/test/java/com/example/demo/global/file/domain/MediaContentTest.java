package com.example.demo.global.file.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class MediaContentTest {

  @Test
  void imageUsesOriginalForCardAndHasNoVideo() {
    MediaContent image = MediaContent.image("https://external.example/image.png");

    assertThat(image.cardImageUrl()).isEqualTo(image.imageUrl());
    assertThat(image.videoUrl()).isNull();
    assertThat(image.fileUrls()).containsExactly(image.imageUrl());
  }

  @Test
  void videoKeepsOriginalAndCardThumbnailSeparate() {
    MediaContent video = MediaContent.video("video.mp4", "cover.jpg");

    assertThat(video.imageUrl()).isEqualTo("video.mp4");
    assertThat(video.cardImageUrl()).isEqualTo("cover.jpg");
    assertThat(video.videoUrl()).isEqualTo("video.mp4");
    assertThat(video.fileUrls()).containsExactly("video.mp4", "cover.jpg");
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" ", "\t"})
  void rejectsMissingOriginal(String url) {
    assertInvalid(() -> MediaContent.image(url));
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" ", "\t"})
  void rejectsMissingVideoThumbnail(String thumbnail) {
    assertInvalid(() -> MediaContent.video("video.mp4", thumbnail));
  }

  @Test
  void rejectsMissingType() {
    assertInvalid(() -> new MediaContent("image.png", null, null));
  }

  @ParameterizedTest
  @ValueSource(strings = {"cover.jpg", "", " "})
  void rejectsThumbnailOnImage(String thumbnail) {
    assertInvalid(() -> new MediaContent("image.png", MediaType.IMAGE, thumbnail));
  }

  private void assertInvalid(Runnable action) {
    assertThatThrownBy(action::run)
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                assertThat(exception.errorCode()).isEqualTo(GlobalErrorCode.INVALID_INPUT_VALUE));
  }
}

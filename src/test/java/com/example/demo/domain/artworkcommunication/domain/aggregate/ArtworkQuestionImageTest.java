package com.example.demo.domain.artworkcommunication.domain.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;

class ArtworkQuestionImageTest {

  @Test
  void questionAndReplyImagesPreserveRequestOrder() {
    ArtworkQuestion question =
        ArtworkQuestion.create(
            1L,
            2L,
            "이미지가 포함된 질문",
            true,
            List.of(
                new ArtworkQuestion.ImageInfo("https://image.test/question-1.jpg", 800, 600),
                new ArtworkQuestion.ImageInfo("https://image.test/question-2.jpg", 600, 800)));
    ArtworkQuestionReply reply =
        ArtworkQuestionReply.create(
            10L,
            "이미지가 포함된 답변",
            3L,
            List.of(
                new ArtworkQuestionReply.ImageInfo("https://image.test/reply-1.jpg", 1200, 900),
                new ArtworkQuestionReply.ImageInfo("https://image.test/reply-2.jpg", 900, 1200)));

    assertThat(question.getImages())
        .extracting(image -> image.getImageUrl(), image -> image.getSortOrder())
        .containsExactly(
            Tuple.tuple("https://image.test/question-1.jpg", 0),
            Tuple.tuple("https://image.test/question-2.jpg", 1));
    assertThat(reply.getImages())
        .extracting(image -> image.getImageUrl(), image -> image.getSortOrder())
        .containsExactly(
            Tuple.tuple("https://image.test/reply-1.jpg", 0),
            Tuple.tuple("https://image.test/reply-2.jpg", 1));
  }

  @Test
  void questionAndReplyAllowUpToFiveImages() {
    List<ArtworkQuestion.ImageInfo> questionImages =
        java.util.stream.IntStream.rangeClosed(1, 5)
            .mapToObj(
                index ->
                    new ArtworkQuestion.ImageInfo(
                        "https://image.test/question-" + index + ".jpg", 800, 600))
            .toList();
    List<ArtworkQuestionReply.ImageInfo> replyImages =
        java.util.stream.IntStream.rangeClosed(1, 5)
            .mapToObj(
                index ->
                    new ArtworkQuestionReply.ImageInfo(
                        "https://image.test/reply-" + index + ".jpg", 800, 600))
            .toList();

    assertThat(ArtworkQuestion.create(1L, 2L, "질문", true, questionImages).getImages()).hasSize(5);
    assertThat(ArtworkQuestionReply.create(10L, "답변", 3L, replyImages).getImages()).hasSize(5);
  }

  @Test
  void questionAndReplyRejectMoreThanFiveImages() {
    List<ArtworkQuestion.ImageInfo> questionImages =
        java.util.stream.IntStream.rangeClosed(1, 6)
            .mapToObj(
                index ->
                    new ArtworkQuestion.ImageInfo(
                        "https://image.test/question-" + index + ".jpg", 800, 600))
            .toList();
    List<ArtworkQuestionReply.ImageInfo> replyImages =
        java.util.stream.IntStream.rangeClosed(1, 6)
            .mapToObj(
                index ->
                    new ArtworkQuestionReply.ImageInfo(
                        "https://image.test/reply-" + index + ".jpg", 800, 600))
            .toList();

    assertThatThrownBy(() -> ArtworkQuestion.create(1L, 2L, "질문", true, questionImages))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> ArtworkQuestionReply.create(10L, "답변", 3L, replyImages))
        .isInstanceOf(IllegalArgumentException.class);
  }
}

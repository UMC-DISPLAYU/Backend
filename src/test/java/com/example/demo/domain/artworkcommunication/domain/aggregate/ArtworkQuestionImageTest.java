package com.example.demo.domain.artworkcommunication.domain.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

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
            List.of(new ArtworkQuestionReply.ImageInfo("https://image.test/reply.jpg", 1200, 900)));

    assertThat(question.getImages())
        .extracting(image -> image.getImageUrl(), image -> image.getSortOrder())
        .containsExactly(
            Tuple.tuple("https://image.test/question-1.jpg", 0),
            Tuple.tuple("https://image.test/question-2.jpg", 1));
    assertThat(reply.getImages()).hasSize(1);
    assertThat(reply.getImages().get(0).getSortOrder()).isZero();
  }
}

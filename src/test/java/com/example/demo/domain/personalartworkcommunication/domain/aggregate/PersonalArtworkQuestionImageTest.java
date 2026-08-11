package com.example.demo.domain.personalartworkcommunication.domain.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class PersonalArtworkQuestionImageTest {

  @Test
  void questionAndReplyImagesPreserveRequestOrder() {
    PersonalArtworkQuestion question =
        PersonalArtworkQuestion.create(
            1L,
            2L,
            "이미지가 포함된 질문",
            true,
            List.of(
                new PersonalArtworkQuestion.ImageInfo(
                    "https://image.test/question-1.jpg", 800, 600),
                new PersonalArtworkQuestion.ImageInfo(
                    "https://image.test/question-2.jpg", 600, 800)));

    PersonalArtworkQuestionReply reply =
        question.answer(
            3L,
            "이미지가 포함된 답변",
            List.of(
                new PersonalArtworkQuestionReply.ImageInfo(
                    "https://image.test/reply.jpg", 1200, 900)));

    assertThat(question.getImages())
        .extracting(image -> image.getImageUrl(), image -> image.getSortOrder())
        .containsExactly(
            org.assertj.core.groups.Tuple.tuple("https://image.test/question-1.jpg", 0),
            org.assertj.core.groups.Tuple.tuple("https://image.test/question-2.jpg", 1));
    assertThat(reply.getImages()).hasSize(1);
    assertThat(reply.getImages().get(0).getSortOrder()).isZero();
  }
}

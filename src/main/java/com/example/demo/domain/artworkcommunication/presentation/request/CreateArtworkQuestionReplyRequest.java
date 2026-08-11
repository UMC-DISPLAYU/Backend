package com.example.demo.domain.artworkcommunication.presentation.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateArtworkQuestionReplyRequest(
    @NotBlank(message = "답변 내용은 필수입니다.") @Size(max = 300, message = "답변은 300자 이하로 입력해주세요.") String content,
    @Size(max = 5, message = "사진은 최대 5개까지 첨부할 수 있습니다.") List<@NotNull @Valid ArtworkQuestionReplyImageRequest> images) {

  public record ArtworkQuestionReplyImageRequest(
      @NotBlank @Size(max = 2048) String imageUrl, @Positive int width, @Positive int height) {}
}

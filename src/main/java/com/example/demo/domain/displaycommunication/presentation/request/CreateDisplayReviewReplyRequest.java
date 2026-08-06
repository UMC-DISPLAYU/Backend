package com.example.demo.domain.displaycommunication.presentation.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateDisplayReviewReplyRequest(
    @NotBlank(message = "답글 내용은 필수입니다.") @Size(max = 300, message = "후기 답글은 300자 이하로 작성해주세요.") String content,
    @Size(max = 5, message = "사진은 최대 5개까지 첨부할 수 있습니다.") List<@NotNull @Valid DisplayReviewReplyImageRequest> images) {

  public record DisplayReviewReplyImageRequest(
      @NotBlank @Size(max = 2048) String imageUrl, @Positive int width, @Positive int height) {}
}

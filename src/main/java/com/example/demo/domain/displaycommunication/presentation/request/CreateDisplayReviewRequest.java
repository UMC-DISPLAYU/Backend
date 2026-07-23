package com.example.demo.domain.displaycommunication.presentation.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record CreateDisplayReviewRequest(
    @NotBlank(message = "후기 내용은 필수입니다.") @Size(max = 300, message = "전시 후기는 300자 이내로 입력해주세요.") String content,
    @Size(max = 5, message = "사진은 최대 5개까지 첨부할 수 있습니다.") List<@Valid DisplayReviewImageRequest> images) {
  public record DisplayReviewImageRequest(
      @NotBlank @Size(max = 2048) String imageUrl, @Positive int width, @Positive int height) {}
}

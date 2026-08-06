package com.example.demo.domain.artworkcommunication.presentation.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateArtworkFeelingRequest(
    @NotBlank(message = "감상평 내용은 필수입니다.") @Size(min = 1, max = 300, message = "감상평은 1자 이상 300자 이하로 입력해주세요.") String content,
    @Size(max = 5, message = "사진은 최대 5개까지 첨부할 수 있습니다.") List<@NotNull @Valid ArtworkFeelingImageRequest> images) {

  public record ArtworkFeelingImageRequest(
      @NotBlank @Size(max = 2048) String imageUrl, @Positive int width, @Positive int height) {}
}

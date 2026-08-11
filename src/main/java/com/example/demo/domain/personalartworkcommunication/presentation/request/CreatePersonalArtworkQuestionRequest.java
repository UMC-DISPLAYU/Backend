package com.example.demo.domain.personalartworkcommunication.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreatePersonalArtworkQuestionRequest(
    @NotBlank(message = "질문 내용은 필수입니다.") @Size(max = 300, message = "질문은 300자 이하로 입력해주세요.") String content,
    @Schema(description = "질문 공개 여부. 생략하면 공개로 등록됩니다.", defaultValue = "true", example = "true")
        Boolean isPublic,
    @Size(max = 5, message = "사진은 최대 5개까지 첨부할 수 있습니다.") List<@NotNull @Valid PersonalArtworkQuestionImageRequest> images) {

  public record PersonalArtworkQuestionImageRequest(
      @NotBlank @Size(max = 2048) String imageUrl, @Positive int width, @Positive int height) {}
}

package com.example.demo.domain.personalartworkcommunication.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePersonalArtworkQuestionRequest(
    @NotBlank(message = "질문 내용은 필수입니다.") @Size(max = 300, message = "질문은 300자 이하로 입력해주세요.") String content,
    @Schema(description = "질문 공개 여부. 생략하면 공개로 등록됩니다.", defaultValue = "true", example = "true")
        Boolean isPublic) {}

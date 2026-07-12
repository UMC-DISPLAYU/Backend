package com.example.demo.domain.artworkcommunication.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateArtworkQuestionRequest(
    @NotBlank(message = "질문 내용은 필수입니다.") String content,
    @NotNull(message = "질문 공개 여부는 필수입니다.") Boolean isPublic) {}

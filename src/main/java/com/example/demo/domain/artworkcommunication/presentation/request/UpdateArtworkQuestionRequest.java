package com.example.demo.domain.artworkcommunication.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateArtworkQuestionRequest(
    @NotBlank(message = "질문 내용은 필수입니다.") @Size(max = 300, message = "질문은 300자 이하로 입력해주세요.") String content,
    @NotNull(message = "질문 공개 여부는 필수입니다.") Boolean isPublic) {}

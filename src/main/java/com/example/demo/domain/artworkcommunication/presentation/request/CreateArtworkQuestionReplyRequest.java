package com.example.demo.domain.artworkcommunication.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateArtworkQuestionReplyRequest(
    @NotBlank(message = "답변 내용은 필수입니다.") @Size(max = 300, message = "답변은 300자 이하로 입력해주세요.") String content) {}

package com.example.demo.domain.personalartworkcommunication.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePersonalArtworkQuestionReplyRequest(
    @NotBlank(message = "답변 내용은 필수입니다.") @Size(max = 300, message = "질문 답변은 300자 이하로 작성해주세요.") String content) {}

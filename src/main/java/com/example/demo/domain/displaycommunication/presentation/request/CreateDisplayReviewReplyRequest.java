package com.example.demo.domain.displaycommunication.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateDisplayReviewReplyRequest(
    @NotBlank(message = "답글 내용은 필수입니다.") @Size(max = 300, message = "후기 답글은 300자 이하로 작성해주세요.") String content) {}

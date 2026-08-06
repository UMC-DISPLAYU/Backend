package com.example.demo.domain.personalartworkcommunication.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePersonalArtworkFeelingReplyRequest(
    @NotBlank(message = "답변 내용은 필수입니다.") @Size(min = 1, max = 300, message = "감상평 답변은 1자 이상 300자 이하로 작성해주세요.") String content) {}

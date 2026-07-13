package com.example.demo.domain.artworkcommunication.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateArtworkFeelingRequest(
    @NotBlank(message = "감상평 내용은 필수입니다.") @Size(max = 300, message = "감상평은 300자 이하로 입력해주세요.") String content) {}

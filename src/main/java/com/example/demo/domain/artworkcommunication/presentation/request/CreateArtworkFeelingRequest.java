package com.example.demo.domain.artworkcommunication.presentation.request;

import jakarta.validation.constraints.NotBlank;

public record CreateArtworkFeelingRequest(@NotBlank(message = "감상평 내용은 필수입니다.") String content) {}

package com.example.demo.domain.display.presentation.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DisplayLikeStatusResponse(@JsonProperty("isLiked") boolean isLiked) {}

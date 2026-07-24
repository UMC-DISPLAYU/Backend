package com.example.demo.domain.displaycommunication.presentation.response;

import java.time.LocalDateTime;

public record DeletedDisplayReviewResponse(Long displayReviewId, LocalDateTime deletedAt) {}

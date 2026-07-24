package com.example.demo.domain.displaycommunication.application.result;

import java.time.LocalDateTime;

public record DeletedDisplayReviewResult(Long displayReviewId, LocalDateTime deletedAt) {}

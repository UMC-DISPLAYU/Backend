package com.example.demo.domain.admin.application.result;

import java.time.Instant;

public record CommentDeletionResult(Long commentId, Instant processedAt) {}

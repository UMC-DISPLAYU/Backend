package com.example.demo.domain.admin.presentation.response;

import java.time.Instant;

public record CommentDeletionResponse(Long commentId, Instant processedAt) {}

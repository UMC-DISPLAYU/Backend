package com.example.demo.domain.admin.presentation.response;

import java.time.Instant;

public record ParentDeletionResponse(Long parentId, Instant processedAt) {}

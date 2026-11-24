package com.example.demo.domain.admin.application.result;

import java.time.Instant;

public record ParentDeletionResult(Long parentId, Instant processedAt) {}

package com.example.demo.domain.artworkcommunication.presentation.response;

import java.time.LocalDateTime;

public record DeletedArtworkFeelingReplyResponse(Long feelingReplyId, LocalDateTime deletedAt) {}

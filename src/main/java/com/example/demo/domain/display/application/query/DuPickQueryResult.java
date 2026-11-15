package com.example.demo.domain.display.application.query;

import com.example.demo.domain.display.domain.vo.PosterCrop;
import java.time.LocalDateTime;

public record DuPickQueryResult(
    Long duPickId,
    String title,
    String subtitle,
    String bannerImageUrl,
    LocalDateTime createdAt,
    PosterCrop posterCrop) {}

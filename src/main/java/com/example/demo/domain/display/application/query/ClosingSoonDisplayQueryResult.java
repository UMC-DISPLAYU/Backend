package com.example.demo.domain.display.application.query;

import com.example.demo.domain.display.domain.vo.PosterCrop;
import java.time.LocalDate;

public record ClosingSoonDisplayQueryResult(
    Long displayId,
    String title,
    String posterImageUrl,
    String organization,
    String department,
    LocalDate startedAt,
    LocalDate endedAt,
    String placeName,
    PosterCrop posterCrop) {}

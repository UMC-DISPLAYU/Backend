package com.example.demo.domain.display.application.query;

import com.example.demo.domain.display.domain.type.PosterFit;
import com.example.demo.domain.display.domain.vo.PosterCrop;
import java.time.LocalDate;

public record DisplaySummaryQueryResult(
    Long displayId,
    String title,
    String organization,
    String department,
    String placeName,
    LocalDate startDate,
    LocalDate endDate,
    String posterImageUrl,
    PosterCrop posterCrop,
    PosterFit posterFit,
    String posterEdgeColor) {}

package com.example.demo.domain.display.application.result;

import com.example.demo.domain.display.application.query.SearchDisplayQueryResult;
import com.example.demo.domain.display.domain.type.PosterFit;
import com.example.demo.domain.display.domain.vo.PosterCrop;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public record SearchDisplayResult(List<ExhibitionResult> exhibitions, PaginationResult pagination) {

  public record ExhibitionResult(
      Long displayId,
      String title,
      String posterImageUrl,
      String organization,
      String department,
      LocalDate startedAt,
      LocalDate endedAt,
      long dayLeft,
      boolean isArchived,
      String placeName,
      PosterCrop posterCrop,
      PosterFit posterFit,
      String posterEdgeColor,
      String posterVideoUrl) {

    public static ExhibitionResult from(SearchDisplayQueryResult queryResult, LocalDate today) {
      return new ExhibitionResult(
          queryResult.displayId(),
          queryResult.title(),
          queryResult.posterImageUrl(),
          queryResult.organization(),
          queryResult.department(),
          queryResult.startedAt(),
          queryResult.endedAt(),
          ChronoUnit.DAYS.between(today, queryResult.endedAt()),
          false,
          queryResult.placeName(),
          queryResult.posterCrop(),
          queryResult.posterFit(),
          queryResult.posterEdgeColor(),
          queryResult.posterVideoUrl());
    }

    public ExhibitionResult withArchived(boolean isArchived) {
      return new ExhibitionResult(
          displayId,
          title,
          posterImageUrl,
          organization,
          department,
          startedAt,
          endedAt,
          dayLeft,
          isArchived,
          placeName,
          posterCrop,
          posterFit,
          posterEdgeColor,
          posterVideoUrl);
    }
  }

  public record PaginationResult(
      Long nextCursor, int size, boolean hasNext, Long nextCursorLikeCount) {}
}

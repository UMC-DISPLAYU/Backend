package com.example.demo.domain.display.application.result;

import com.example.demo.domain.display.application.query.DuPickQueryResult;
import com.example.demo.domain.display.domain.vo.PosterCrop;
import java.time.LocalDate;
import java.util.List;

public record DuPickResult(List<DuPickItemResult> duPicks, PaginationResult pagination) {

  public record DuPickItemResult(
      Long duPickId,
      String title,
      String subtitle,
      String bannerImageUrl,
      LocalDate createdAt,
      PosterCrop posterCrop) {

    public static DuPickItemResult from(DuPickQueryResult queryResult) {
      return new DuPickItemResult(
          queryResult.duPickId(),
          queryResult.title(),
          queryResult.subtitle(),
          queryResult.bannerImageUrl(),
          queryResult.createdAt().toLocalDate(),
          queryResult.posterCrop());
    }
  }

  public record PaginationResult(Long nextCursor, int size, boolean hasNext) {}
}

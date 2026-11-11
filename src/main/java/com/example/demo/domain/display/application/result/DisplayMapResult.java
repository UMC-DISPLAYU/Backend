package com.example.demo.domain.display.application.result;

import com.example.demo.domain.display.application.query.DisplayMapQueryResult;
import com.example.demo.domain.display.domain.vo.PosterCrop;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DisplayMapResult(List<MarkerResult> markers, PaginationResult pagination) {

  public record MarkerResult(
      Long displayId,
      String title,
      LocalDate startDate,
      LocalDate endDate,
      String locationName,
      String posterImageUrl,
      String organization,
      String department,
      BigDecimal latitude,
      BigDecimal longitude,
      boolean isArchived,
      PosterCrop posterCrop) {

    public static MarkerResult from(DisplayMapQueryResult queryResult) {
      return new MarkerResult(
          queryResult.displayId(),
          queryResult.title(),
          queryResult.startDate(),
          queryResult.endDate(),
          queryResult.locationName(),
          queryResult.posterImageUrl(),
          queryResult.organization(),
          queryResult.department(),
          queryResult.latitude(),
          queryResult.longitude(),
          false,
          queryResult.posterCrop());
    }

    public MarkerResult withArchived(boolean isArchived) {
      return new MarkerResult(
          displayId,
          title,
          startDate,
          endDate,
          locationName,
          posterImageUrl,
          organization,
          department,
          latitude,
          longitude,
          isArchived,
          posterCrop);
    }
  }

  public record PaginationResult(Long nextCursor, int size, boolean hasNext) {}
}

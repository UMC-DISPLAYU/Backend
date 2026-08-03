package com.example.demo.domain.display.application.result;

import com.example.demo.domain.display.application.query.ClosingSoonDisplayQueryResult;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayImage;
import com.example.demo.domain.display.domain.entity.DisplayInvitation;
import com.example.demo.domain.display.domain.type.DisplayImageType;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public record GraduationDisplayResult(List<ExhibitionResult> exhibitions) {

  public record ExhibitionResult(
      Long displayId,
      String title,
      String posterImageUrl,
      String organization,
      String department,
      LocalDate startedAt,
      LocalDate endedAt,
      long dayLeft,
      boolean isBookmarked) {

    public static ExhibitionResult from(
        ClosingSoonDisplayQueryResult queryResult, LocalDate today) {
      return new ExhibitionResult(
          queryResult.displayId(),
          queryResult.title(),
          queryResult.posterImageUrl(),
          queryResult.organization(),
          queryResult.department(),
          queryResult.startedAt(),
          queryResult.endedAt(),
          ChronoUnit.DAYS.between(today, queryResult.endedAt()),
          false);
    }

    public static ExhibitionResult from(DisplayInvitation invitation, LocalDate today) {
      Display display = invitation.getDisplay();
      return new ExhibitionResult(
          display.getId(),
          display.getTitle(),
          posterImageUrl(display),
          display.getOrganization(),
          display.getDepartment(),
          display.getPeriod().startDate(),
          display.getPeriod().endDate(),
          ChronoUnit.DAYS.between(today, display.getPeriod().endDate()),
          false);
    }

    public ExhibitionResult withBookmarked(boolean isBookmarked) {
      return new ExhibitionResult(
          displayId,
          title,
          posterImageUrl,
          organization,
          department,
          startedAt,
          endedAt,
          dayLeft,
          isBookmarked);
    }

    private static String posterImageUrl(Display display) {
      return display.getImages().stream()
          .filter(image -> image.getImageType() == DisplayImageType.MAIN)
          .filter(image -> !image.isDeleted())
          .findFirst()
          .map(DisplayImage::getImageUrl)
          .orElse(null);
    }
  }
}

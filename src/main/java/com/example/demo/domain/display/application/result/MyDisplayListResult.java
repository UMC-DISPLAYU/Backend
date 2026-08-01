package com.example.demo.domain.display.application.result;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayImage;
import com.example.demo.domain.display.domain.type.DisplayImageType;
import java.time.LocalDate;
import java.util.List;

public record MyDisplayListResult(
    List<MyDisplayResult> createdDisplays, List<MyDisplayResult> participatedDisplays) {

  public static MyDisplayListResult from(
      List<Display> createdDisplays, List<Display> participatedDisplays, LocalDate today) {
    return new MyDisplayListResult(
        createdDisplays.stream().map(display -> MyDisplayResult.from(display, today)).toList(),
        participatedDisplays.stream()
            .map(display -> MyDisplayResult.from(display, today))
            .toList());
  }

  public record MyDisplayResult(
      Long displayId,
      String title,
      boolean isDisplaying,
      LocalDate startDate,
      LocalDate endDate,
      String school,
      String department,
      String placeName,
      String postImageUrl) {

    private static MyDisplayResult from(Display display, LocalDate today) {
      LocalDate startDate = display.getPeriod().startDate();
      LocalDate endDate = display.getPeriod().endDate();
      return new MyDisplayResult(
          display.getId(),
          display.getTitle(),
          isDisplaying(startDate, endDate, today),
          startDate,
          endDate,
          display.getOrganization(),
          display.getDepartment(),
          display.getLocation().placeName(),
          postImageUrl(display));
    }

    private static boolean isDisplaying(LocalDate startDate, LocalDate endDate, LocalDate today) {
      return !today.isBefore(startDate) && !today.isAfter(endDate);
    }

    private static String postImageUrl(Display display) {
      return display.getImages().stream()
          .filter(image -> image.getImageType() == DisplayImageType.MAIN)
          .filter(image -> !image.isDeleted())
          .findFirst()
          .map(DisplayImage::getImageUrl)
          .orElse(null);
    }
  }
}

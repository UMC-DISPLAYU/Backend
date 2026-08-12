package com.example.demo.domain.display.application.result;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayImage;
import com.example.demo.domain.display.domain.type.DisplayImageType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record MyDisplayListResult(
    List<MyDisplayResult> createdDisplays, List<MyDisplayResult> participatedDisplays) {

  public static MyDisplayListResult from(
      List<Display> createdDisplays, List<Display> participatedDisplays, LocalDateTime now) {
    return new MyDisplayListResult(
        createdDisplays.stream().map(display -> MyDisplayResult.from(display, now)).toList(),
        participatedDisplays.stream().map(display -> MyDisplayResult.from(display, now)).toList());
  }

  public record MyDisplayResult(
      Long displayId,
      String title,
      String displayStatus,
      LocalDate startDate,
      LocalDate endDate,
      String school,
      String department,
      String placeName,
      String postImageUrl) {

    private static MyDisplayResult from(Display display, LocalDateTime now) {
      LocalDate startDate = display.getPeriod().startDate();
      LocalDate endDate = display.getPeriod().endDate();
      return new MyDisplayResult(
          display.getId(),
          display.getTitle(),
          display.getPeriod().statusAt(now).name(),
          startDate,
          endDate,
          display.getOrganization(),
          display.getDepartment(),
          display.getLocation().placeName(),
          postImageUrl(display));
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

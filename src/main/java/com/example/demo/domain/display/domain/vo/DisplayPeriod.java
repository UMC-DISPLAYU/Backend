package com.example.demo.domain.display.domain.vo;

import com.example.demo.domain.display.domain.type.DisplayPeriodStatus;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import lombok.EqualsAndHashCode;

@Embeddable
@EqualsAndHashCode
public class DisplayPeriod {

  private LocalDate startDate;
  private LocalDate endDate;
  private LocalTime startTime;
  private LocalTime endTime;

  protected DisplayPeriod() {}

  public DisplayPeriod(
      LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
    Objects.requireNonNull(startDate, "startDate must not be null.");
    Objects.requireNonNull(endDate, "endDate must not be null.");
    Objects.requireNonNull(startTime, "startTime must not be null.");
    Objects.requireNonNull(endTime, "endTime must not be null.");

    if (endDate.isBefore(startDate)) {
      throw new IllegalArgumentException("endDate must not be before startDate.");
    }

    this.startDate = startDate;
    this.endDate = endDate;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public LocalDate startDate() {
    return startDate;
  }

  public LocalDate endDate() {
    return endDate;
  }

  public LocalTime startTime() {
    return startTime;
  }

  public LocalTime endTime() {
    return endTime;
  }

  public DisplayPeriodStatus statusAt(LocalDateTime now) {
    Objects.requireNonNull(now, "now must not be null.");

    LocalDateTime startedAt = LocalDateTime.of(startDate, startTime);
    LocalDateTime endedAt = LocalDateTime.of(endDate, endTime);
    if (now.isBefore(startedAt)) {
      return DisplayPeriodStatus.UPCOMING;
    }
    if (now.isAfter(endedAt)) {
      return DisplayPeriodStatus.ENDED;
    }
    return DisplayPeriodStatus.DISPLAYING;
  }
}

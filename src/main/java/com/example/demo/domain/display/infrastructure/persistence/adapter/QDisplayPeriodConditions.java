package com.example.demo.domain.display.infrastructure.persistence.adapter;

import com.example.demo.domain.display.domain.aggregate.QDisplay;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.time.LocalDate;

final class QDisplayPeriodConditions {
  private QDisplayPeriodConditions() {}

  static BooleanExpression closingSoon(QDisplay display, LocalDate today) {
    return display
        .period
        .startDate
        .loe(today)
        .and(display.period.endDate.between(today, today.plusDays(3)));
  }
}

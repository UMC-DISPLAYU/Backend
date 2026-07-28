package com.example.demo.domain.display.infrastructure.persistence.adapter;

import com.example.demo.domain.display.domain.entity.QDisplayImage;
import com.example.demo.domain.display.domain.type.DisplayImageType;
import com.querydsl.core.types.dsl.BooleanExpression;

final class QDisplayImageConditions {

  private QDisplayImageConditions() {}

  static BooleanExpression mainImage(QDisplayImage image) {
    return image
        .imageType
        .eq(DisplayImageType.MAIN)
        .and(image.deletedAt.isNull())
        .and(image.sortOrder.eq(0));
  }
}

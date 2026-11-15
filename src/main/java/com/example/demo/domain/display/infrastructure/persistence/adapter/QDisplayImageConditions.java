package com.example.demo.domain.display.infrastructure.persistence.adapter;

import com.example.demo.domain.display.domain.entity.QDisplayImage;
import com.example.demo.domain.display.domain.type.DisplayImageType;
import com.example.demo.global.file.domain.MediaType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.StringExpression;

final class QDisplayImageConditions {

  private QDisplayImageConditions() {}

  static StringExpression cardImageUrl(QDisplayImage image) {
    return new CaseBuilder()
        .when(image.mediaType.eq(MediaType.VIDEO))
        .then(image.thumbnailUrl)
        .otherwise(image.imageUrl);
  }

  static StringExpression videoUrl(QDisplayImage image) {
    return new CaseBuilder()
        .when(image.mediaType.eq(MediaType.VIDEO))
        .then(image.imageUrl)
        .otherwise((String) null);
  }

  static BooleanExpression mainImage(QDisplayImage image) {
    return image
        .imageType
        .eq(DisplayImageType.MAIN)
        .and(image.deletedAt.isNull())
        .and(image.sortOrder.eq(0));
  }
}

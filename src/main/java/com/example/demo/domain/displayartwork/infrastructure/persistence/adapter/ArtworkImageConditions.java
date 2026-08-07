package com.example.demo.domain.displayartwork.infrastructure.persistence.adapter;

import com.example.demo.domain.displayartwork.domain.entity.QArtworkImage;
import com.querydsl.core.types.dsl.BooleanExpression;

final class ArtworkImageConditions {

  private ArtworkImageConditions() {}

  static BooleanExpression thumbnailImage(QArtworkImage image) {
    return image.isThumbnail.isTrue();
  }
}

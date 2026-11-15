package com.example.demo.domain.displayartwork.infrastructure.persistence.adapter;

import com.example.demo.domain.displayartwork.domain.entity.QArtworkImage;
import com.example.demo.global.file.domain.MediaType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.StringExpression;

final class ArtworkImageConditions {

  private ArtworkImageConditions() {}

  static StringExpression cardImageUrl(QArtworkImage image) {
    return new CaseBuilder()
        .when(image.mediaType.eq(MediaType.VIDEO))
        .then(image.thumbnailUrl)
        .otherwise(image.imageUrl);
  }

  static BooleanExpression thumbnailImage(QArtworkImage image) {
    return image.isThumbnail.isTrue();
  }
}

package com.example.demo.domain.display.infrastructure.persistence.adapter;

import com.example.demo.domain.display.application.query.SearchDisplayQuery;
import com.example.demo.domain.display.application.query.SearchDisplayQueryRepository;
import com.example.demo.domain.display.application.query.SearchDisplayQueryResult;
import com.example.demo.domain.display.domain.aggregate.QDisplay;
import com.example.demo.domain.display.domain.entity.QDisplayFieldSelection;
import com.example.demo.domain.display.domain.entity.QDisplayImage;
import com.example.demo.domain.display.domain.entity.QDisplayLike;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplaySearchSort;
import com.example.demo.domain.display.domain.type.DisplayStatus;
import com.example.demo.domain.display.domain.type.SearchDisplayStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Repository;

@Repository
public class JpaSearchDisplayQueryRepositoryAdapter implements SearchDisplayQueryRepository {

  private static final QDisplay display = QDisplay.display;
  private static final QDisplayImage image = QDisplayImage.displayImage;
  private static final QDisplayFieldSelection fieldSelection =
      QDisplayFieldSelection.displayFieldSelection;

  private static final QDisplayLike like = QDisplayLike.displayLike;

  private final JPAQueryFactory queryFactory;

  public JpaSearchDisplayQueryRepositoryAdapter(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Override
  public List<SearchDisplayQueryResult> searchDisplays(
      SearchDisplayQuery query, LocalDate today, int limit) {
    return queryFactory
        .select(
            Projections.constructor(
                SearchDisplayQueryResult.class,
                display.id,
                display.title,
                QDisplayImageConditions.cardImageUrl(image),
                display.organization,
                display.department,
                display.period.startDate,
                display.period.endDate,
                display.location.placeName,
                display.posterCrop,
                QDisplayImageConditions.videoUrl(image),
                like.id.count()))
        .from(display)
        .leftJoin(display.images, image)
        .on(QDisplayImageConditions.mainImage(image))
        .leftJoin(like)
        .on(like.displayId.eq(display.id), like.deletedAt.isNull())
        .where(
            display.status.eq(DisplayStatus.PUBLISHED),
            display.deletedAt.isNull(),
            query.sort() == DisplaySearchSort.LIKES ? null : cursorBefore(query.cursor()),
            searchWordContains(query.searchWord()),
            regionEq(query),
            query.type() == null ? null : display.displayType.eq(query.type()),
            fieldExists(query),
            statusCondition(query.status(), today))
        .groupBy(
            display.id,
            display.title,
            display.organization,
            display.department,
            display.period.startDate,
            display.period.endDate,
            display.location.placeName,
            display.posterCrop.x,
            display.posterCrop.y,
            display.posterCrop.w,
            display.posterCrop.h,
            image.imageUrl,
            image.thumbnailUrl,
            image.mediaType)
        .having(likesBefore(query))
        .orderBy(
            query.sort() == DisplaySearchSort.LIKES ? like.id.count().desc() : display.id.desc(),
            display.id.desc())
        .limit(limit)
        .fetch();
  }

  private BooleanExpression likesBefore(SearchDisplayQuery query) {
    if (query.sort() != DisplaySearchSort.LIKES || query.cursor() == null || query.cursor() == 0) {
      return null;
    }
    return like.id
        .count()
        .lt(query.cursorLikeCount())
        .or(like.id.count().eq(query.cursorLikeCount()).and(display.id.lt(query.cursor())));
  }

  private BooleanExpression cursorBefore(Long cursor) {
    return cursor == null || cursor == 0 ? null : display.id.lt(cursor);
  }

  private BooleanExpression searchWordContains(String searchWord) {
    if (!hasText(searchWord)) {
      return null;
    }
    String normalized = searchWord.toLowerCase(Locale.ROOT);
    return display
        .title
        .lower()
        .contains(normalized)
        .or(display.organization.lower().contains(normalized))
        .or(display.department.lower().contains(normalized));
  }

  private BooleanExpression regionEq(SearchDisplayQuery query) {
    if (query.region() == null || query.region() == DisplayRegion.ALL) {
      return null;
    }
    return display.region.eq(query.region());
  }

  private BooleanExpression fieldExists(SearchDisplayQuery query) {
    if (query.field() == null) {
      return null;
    }
    return JPAExpressions.selectOne()
        .from(fieldSelection)
        .where(fieldSelection.display.eq(display), fieldSelection.field.eq(query.field()))
        .exists();
  }

  private BooleanExpression statusCondition(SearchDisplayStatus status, LocalDate today) {
    if (status == null) {
      return null;
    }
    return switch (status) {
      case UPCOMING -> display.period.startDate.gt(today);
      case ONGOING -> display.period.startDate.loe(today).and(display.period.endDate.goe(today));
      case ENDED -> display.period.endDate.lt(today);
      case CLOSING_SOON -> QDisplayPeriodConditions.closingSoon(display, today);
    };
  }

  private boolean hasText(String value) {
    return value != null && !value.isBlank();
  }
}

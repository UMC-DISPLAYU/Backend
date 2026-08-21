package com.example.demo.domain.display.infrastructure.persistence.adapter;

import com.example.demo.domain.display.application.query.SearchDisplayQuery;
import com.example.demo.domain.display.application.query.SearchDisplayQueryRepository;
import com.example.demo.domain.display.application.query.SearchDisplayQueryResult;
import com.example.demo.domain.display.domain.aggregate.QDisplay;
import com.example.demo.domain.display.domain.entity.QDisplayFieldSelection;
import com.example.demo.domain.display.domain.entity.QDisplayImage;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayStatus;
import com.example.demo.domain.display.domain.type.SearchDisplayStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class JpaSearchDisplayQueryRepositoryAdapter implements SearchDisplayQueryRepository {

  private static final QDisplay display = QDisplay.display;
  private static final QDisplayImage image = QDisplayImage.displayImage;
  private static final QDisplayFieldSelection fieldSelection =
      QDisplayFieldSelection.displayFieldSelection;

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
                image.imageUrl,
                display.organization,
                display.department,
                display.period.startDate,
                display.period.endDate))
        .from(display)
        .leftJoin(display.images, image)
        .on(QDisplayImageConditions.mainImage(image))
        .where(
            display.status.eq(DisplayStatus.PUBLISHED),
            display.deletedAt.isNull(),
            cursorBefore(query.cursor()),
            searchWordContains(query.searchWord()),
            regionEq(query),
            query.type() == null ? null : display.displayType.eq(query.type()),
            fieldExists(query),
            statusCondition(query.status(), today))
        .orderBy(display.id.desc())
        .limit(limit)
        .fetch();
  }

  private BooleanExpression cursorBefore(Long cursor) {
    return cursor == null || cursor == 0 ? null : display.id.lt(cursor);
  }

  private BooleanExpression searchWordContains(String searchWord) {
    if (!hasText(searchWord)) {
      return null;
    }
    return display.title.lower().contains(searchWord.toLowerCase());
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
      case CLOSING_SOON ->
          display
              .period
              .startDate
              .loe(today)
              .and(display.period.endDate.between(today, today.plusDays(3)));
    };
  }

  private boolean hasText(String value) {
    return value != null && !value.isBlank();
  }
}

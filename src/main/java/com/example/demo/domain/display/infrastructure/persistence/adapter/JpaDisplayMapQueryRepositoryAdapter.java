package com.example.demo.domain.display.infrastructure.persistence.adapter;

import com.example.demo.domain.display.application.query.DisplayMapQuery;
import com.example.demo.domain.display.application.query.DisplayMapQueryRepository;
import com.example.demo.domain.display.application.query.DisplayMapQueryResult;
import com.example.demo.domain.display.domain.aggregate.QDisplay;
import com.example.demo.domain.display.domain.entity.QDisplayImage;
import com.example.demo.domain.display.domain.type.DisplayStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class JpaDisplayMapQueryRepositoryAdapter implements DisplayMapQueryRepository {

  private static final QDisplay display = QDisplay.display;
  private static final QDisplayImage image = QDisplayImage.displayImage;

  private final JPAQueryFactory queryFactory;

  public JpaDisplayMapQueryRepositoryAdapter(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Override
  public List<DisplayMapQueryResult> findMarkers(DisplayMapQuery query, int limit) {
    return queryFactory
        .select(
            Projections.constructor(
                DisplayMapQueryResult.class,
                display.id,
                display.title,
                display.period.startDate,
                display.period.endDate,
                display.location.placeName,
                image.imageUrl,
                display.location.latitude,
                display.location.longitude))
        .from(display)
        .leftJoin(display.images, image)
        .on(QDisplayImageConditions.mainImage(image))
        .where(
            display.status.eq(DisplayStatus.PUBLISHED),
            display.location.latitude.between(query.southLatitude(), query.northLatitude()),
            display.location.longitude.between(query.westLongitude(), query.eastLongitude()),
            cursorBefore(query),
            searchWordContains(query.searchWord()))
        .orderBy(display.id.desc())
        .limit(limit)
        .fetch();
  }

  private BooleanExpression cursorBefore(DisplayMapQuery query) {
    return query.cursor() == null ? null : display.id.lt(query.cursor());
  }

  private BooleanExpression searchWordContains(String searchWord) {
    if (!hasText(searchWord)) {
      return null;
    }
    String normalizedSearchWord = searchWord.toLowerCase();
    return display
        .title
        .lower()
        .contains(normalizedSearchWord)
        .or(display.location.placeName.lower().contains(normalizedSearchWord));
  }

  private boolean hasText(String value) {
    return value != null && !value.isBlank();
  }
}

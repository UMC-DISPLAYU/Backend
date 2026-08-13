package com.example.demo.domain.display.infrastructure.persistence.adapter;

import com.example.demo.domain.display.application.query.ClosingSoonDisplayQuery;
import com.example.demo.domain.display.application.query.ClosingSoonDisplayQueryRepository;
import com.example.demo.domain.display.application.query.ClosingSoonDisplayQueryResult;
import com.example.demo.domain.display.domain.aggregate.QDisplay;
import com.example.demo.domain.display.domain.entity.QDisplayImage;
import com.example.demo.domain.display.domain.type.DisplayImageType;
import com.example.demo.domain.display.domain.type.DisplayStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class JpaClosingSoonDisplayQueryRepositoryAdapter
    implements ClosingSoonDisplayQueryRepository {

  private static final QDisplay display = QDisplay.display;
  private static final QDisplayImage image = QDisplayImage.displayImage;

  private final JPAQueryFactory queryFactory;

  public JpaClosingSoonDisplayQueryRepositoryAdapter(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Override
  public List<ClosingSoonDisplayQueryResult> findClosingSoonDisplays(
      ClosingSoonDisplayQuery query, LocalDate today, int limit) {
    return queryFactory
        .select(
            Projections.constructor(
                ClosingSoonDisplayQueryResult.class,
                display.id,
                display.title,
                image.imageUrl,
                display.organization,
                display.department,
                display.period.startDate,
                display.period.endDate))
        .from(display)
        .leftJoin(display.images, image)
        .on(
            image.imageType.eq(DisplayImageType.MAIN),
            image.deletedAt.isNull(),
            image.sortOrder.eq(0))
        .where(
            display.status.eq(DisplayStatus.PUBLISHED),
            display.deletedAt.isNull(),
            display.period.endDate.goe(today),
            cursorAfter(query.cursor()))
        .orderBy(display.period.endDate.asc(), display.id.asc())
        .limit(limit)
        .fetch();
  }

  private BooleanExpression cursorAfter(ClosingSoonDisplayQuery.Cursor cursor) {
    if (cursor == null) {
      return null;
    }
    return display
        .period
        .endDate
        .gt(cursor.endedAt())
        .or(display.period.endDate.eq(cursor.endedAt()).and(display.id.gt(cursor.displayId())));
  }
}

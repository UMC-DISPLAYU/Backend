package com.example.demo.domain.display.infrastructure.persistence.adapter;

import com.example.demo.domain.display.application.query.DisplaySummaryQueryRepository;
import com.example.demo.domain.display.application.query.DisplaySummaryQueryResult;
import com.example.demo.domain.display.domain.aggregate.QDisplay;
import com.example.demo.domain.display.domain.entity.QDisplayImage;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class JpaDisplaySummaryQueryRepositoryAdapter implements DisplaySummaryQueryRepository {

  private static final QDisplay display = QDisplay.display;
  private static final QDisplayImage image = QDisplayImage.displayImage;

  private final JPAQueryFactory queryFactory;

  public JpaDisplaySummaryQueryRepositoryAdapter(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Override
  public List<DisplaySummaryQueryResult> findByDisplayIdIn(List<Long> displayIds) {
    if (displayIds.isEmpty()) {
      return List.of();
    }

    return queryFactory
        .select(
            Projections.constructor(
                DisplaySummaryQueryResult.class,
                display.id,
                display.title,
                display.organization,
                display.department,
                display.location.placeName,
                display.period.startDate,
                display.period.endDate,
                image.imageUrl))
        .from(display)
        .leftJoin(display.images, image)
        .on(QDisplayImageConditions.mainImage(image))
        .where(display.id.in(displayIds))
        .fetch();
  }
}

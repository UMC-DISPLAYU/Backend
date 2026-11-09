package com.example.demo.domain.display.infrastructure.persistence.adapter;

import com.example.demo.domain.display.application.query.ClosingSoonDisplayQueryResult;
import com.example.demo.domain.display.application.query.PopularDisplayQueryRepository;
import com.example.demo.domain.display.domain.aggregate.QDisplay;
import com.example.demo.domain.display.domain.entity.QDisplayImage;
import com.example.demo.domain.display.domain.entity.QDisplayLike;
import com.example.demo.domain.display.domain.type.DisplayImageType;
import com.example.demo.domain.display.domain.type.DisplayStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class JpaPopularDisplayQueryRepositoryAdapter implements PopularDisplayQueryRepository {

  private static final QDisplay display = QDisplay.display;
  private static final QDisplayImage image = QDisplayImage.displayImage;

  private static final QDisplayLike like = QDisplayLike.displayLike;

  private final JPAQueryFactory queryFactory;

  public JpaPopularDisplayQueryRepositoryAdapter(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Override
  public List<ClosingSoonDisplayQueryResult> findPopularDisplays(LocalDate today) {
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
                display.period.endDate,
                display.location.placeName))
        .from(display)
        .leftJoin(display.images, image)
        .on(
            image.imageType.eq(DisplayImageType.MAIN),
            image.deletedAt.isNull(),
            image.sortOrder.eq(0))
        .leftJoin(like)
        .on(like.displayId.eq(display.id))
        .where(
            display.status.eq(DisplayStatus.PUBLISHED),
            display.deletedAt.isNull(),
            display.period.endDate.goe(today))
        .groupBy(display.id, image.imageUrl)
        .orderBy(like.id.countDistinct().desc(), display.id.desc())
        .limit(10)
        .fetch();
  }
}

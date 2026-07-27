package com.example.demo.domain.display.infrastructure.persistence.adapter;

import com.example.demo.domain.display.application.query.ClosingSoonDisplayQueryResult;
import com.example.demo.domain.display.application.query.GraduationDisplayQueryRepository;
import com.example.demo.domain.display.domain.aggregate.QDisplay;
import com.example.demo.domain.display.domain.entity.QDisplayImage;
import com.example.demo.domain.display.domain.type.DisplayImageType;
import com.example.demo.domain.display.domain.type.DisplayStatus;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class JpaGraduationDisplayQueryRepositoryAdapter
    implements GraduationDisplayQueryRepository {

  private static final QDisplay display = QDisplay.display;
  private static final QDisplayImage image = QDisplayImage.displayImage;
  private static final QDisplayImage mainImage = new QDisplayImage("mainImage");

  private final JPAQueryFactory queryFactory;

  public JpaGraduationDisplayQueryRepositoryAdapter(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Override
  public List<ClosingSoonDisplayQueryResult> findRandomGraduationDisplays(int size) {
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
            image.sortOrder.eq(
                JPAExpressions.select(mainImage.sortOrder.min())
                    .from(mainImage)
                    .where(
                        mainImage.display.eq(display),
                        mainImage.imageType.eq(DisplayImageType.MAIN),
                        mainImage.deletedAt.isNull())))
        .where(
            display.status.eq(DisplayStatus.PUBLISHED),
            display.displayType.eq(DisplayType.GRADUATION))
        .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
        .limit(size)
        .fetch();
  }
}

package com.example.demo.domain.display.infrastructure.persistence.adapter;

import com.example.demo.domain.display.application.query.ClosingSoonDisplayQueryResult;
import com.example.demo.domain.display.application.query.GraduationDisplayQueryRepository;
import com.example.demo.domain.display.domain.aggregate.QDisplay;
import com.example.demo.domain.display.domain.entity.QDisplayImage;
import com.example.demo.domain.display.domain.type.DisplayStatus;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Repository;

@Repository
public class JpaGraduationDisplayQueryRepositoryAdapter
    implements GraduationDisplayQueryRepository {

  private static final QDisplay display = QDisplay.display;
  private static final QDisplayImage image = QDisplayImage.displayImage;

  private final JPAQueryFactory queryFactory;

  public JpaGraduationDisplayQueryRepositoryAdapter(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Override
  public List<ClosingSoonDisplayQueryResult> findRandomGraduationDisplays(int size) {
    if (size <= 0) {
      return List.of();
    }

    List<Long> selectedDisplayIds = selectRandomDisplayIds(size);
    if (selectedDisplayIds.isEmpty()) {
      return List.of();
    }

    Map<Long, Integer> displayIdOrder = displayIdOrder(selectedDisplayIds);
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
        .on(QDisplayImageConditions.mainImage(image))
        .where(publishedGraduationDisplay(), display.id.in(selectedDisplayIds))
        .fetch()
        .stream()
        .sorted(
            Comparator.comparingInt(
                result -> displayIdOrder.getOrDefault(result.displayId(), Integer.MAX_VALUE)))
        .toList();
  }

  private List<Long> selectRandomDisplayIds(int size) {
    NumberExpression<Long> minDisplayId = display.id.min();
    NumberExpression<Long> maxDisplayId = display.id.max();
    Tuple idRange =
        queryFactory
            .select(minDisplayId, maxDisplayId)
            .from(display)
            .where(publishedGraduationDisplay())
            .fetchOne();
    if (idRange == null || idRange.get(minDisplayId) == null || idRange.get(maxDisplayId) == null) {
      return List.of();
    }

    long pivot = randomPivot(idRange.get(minDisplayId), idRange.get(maxDisplayId));
    List<Long> selectedDisplayIds =
        new ArrayList<>(
            queryFactory
                .select(display.id)
                .from(display)
                .where(publishedGraduationDisplay(), display.id.goe(pivot))
                .orderBy(display.id.asc())
                .limit(size)
                .fetch());
    if (selectedDisplayIds.size() < size) {
      selectedDisplayIds.addAll(
          queryFactory
              .select(display.id)
              .from(display)
              .where(publishedGraduationDisplay(), display.id.lt(pivot))
              .orderBy(display.id.asc())
              .limit(size - selectedDisplayIds.size())
              .fetch());
    }
    Collections.shuffle(selectedDisplayIds);
    return selectedDisplayIds;
  }

  private long randomPivot(long minId, long maxId) {
    if (minId == maxId) {
      return minId;
    }
    return ThreadLocalRandom.current().nextLong(minId, maxId + 1);
  }

  private Map<Long, Integer> displayIdOrder(List<Long> displayIds) {
    Map<Long, Integer> displayIdOrder = new HashMap<>();
    for (int index = 0; index < displayIds.size(); index++) {
      displayIdOrder.put(displayIds.get(index), index);
    }
    return displayIdOrder;
  }

  private BooleanExpression publishedGraduationDisplay() {
    return display
        .status
        .eq(DisplayStatus.PUBLISHED)
        .and(display.deletedAt.isNull())
        .and(display.displayType.eq(DisplayType.GRADUATION));
  }
}

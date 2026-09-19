package com.example.demo.domain.display.infrastructure.persistence.adapter;

import com.example.demo.domain.display.application.query.DisplayScreeningQueryRepository;
import com.example.demo.domain.display.application.query.DisplayScreeningSearchQuery;
import com.example.demo.domain.display.application.query.DisplayScreeningSummaryQueryResult;
import com.example.demo.domain.display.domain.aggregate.QDisplay;
import com.example.demo.domain.display.domain.entity.QDisplayImage;
import com.example.demo.domain.display.domain.entity.QDisplayScreening;
import com.example.demo.domain.display.domain.repository.TeamMemberRepository;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class JpaDisplayScreeningQueryRepositoryAdapter implements DisplayScreeningQueryRepository {

  private static final QDisplay display = QDisplay.display;
  private static final QDisplayImage image = QDisplayImage.displayImage;
  private static final QDisplayScreening screening = QDisplayScreening.displayScreening;
  private static final QDisplayScreening latestScreening =
      new QDisplayScreening("latestDisplayScreening");

  private final JPAQueryFactory queryFactory;
  private final TeamMemberRepository teamMemberRepository;

  public JpaDisplayScreeningQueryRepositoryAdapter(
      JPAQueryFactory queryFactory, TeamMemberRepository teamMemberRepository) {
    this.queryFactory = queryFactory;
    this.teamMemberRepository = teamMemberRepository;
  }

  @Override
  public List<DisplayScreeningSummaryQueryResult> search(
      DisplayScreeningSearchQuery query, int limit) {
    List<Tuple> rows =
        queryFactory
            .select(
                display.id,
                display.title,
                screening.requesterId,
                display.status,
                screening.requestedAt,
                display.organization,
                display.period.startDate,
                display.period.endDate,
                image.imageUrl)
            .from(display)
            .leftJoin(screening)
            .on(
                screening.display.eq(display),
                screening.id.eq(
                    JPAExpressions.select(latestScreening.id.max())
                        .from(latestScreening)
                        .where(latestScreening.display.eq(display))))
            .leftJoin(display.images, image)
            .on(QDisplayImageConditions.mainImage(image))
            .where(
                display.status.eq(query.status()),
                display.deletedAt.isNull(),
                cursorBefore(query.cursor()))
            .orderBy(display.id.desc())
            .limit(limit)
            .fetch();

    Map<Long, String> leaderNames = uniqueLeaderNames(displayIds(rows));
    return rows.stream().map(row -> toResult(row, leaderNames)).toList();
  }

  private BooleanExpression cursorBefore(Long cursor) {
    return cursor == null ? null : display.id.lt(cursor);
  }

  private List<Long> displayIds(List<Tuple> rows) {
    return rows.stream().map(row -> row.get(display.id)).toList();
  }

  private Map<Long, String> uniqueLeaderNames(List<Long> displayIds) {
    return teamMemberRepository.findAcceptedLeadersByDisplayIds(displayIds).stream()
        .collect(Collectors.groupingBy(member -> member.getDisplay().getId()))
        .entrySet()
        .stream()
        .filter(entry -> entry.getValue().size() == 1)
        .collect(
            Collectors.toMap(
                Map.Entry::getKey, entry -> entry.getValue().getFirst().getDisplayNickname()));
  }

  private DisplayScreeningSummaryQueryResult toResult(Tuple row, Map<Long, String> leaderNames) {
    Long displayId = row.get(display.id);
    return new DisplayScreeningSummaryQueryResult(
        displayId,
        row.get(display.title),
        row.get(screening.requesterId),
        row.get(display.status),
        row.get(screening.requestedAt),
        row.get(display.organization),
        leaderNames.get(displayId),
        row.get(display.period.startDate),
        row.get(display.period.endDate),
        row.get(image.imageUrl));
  }
}

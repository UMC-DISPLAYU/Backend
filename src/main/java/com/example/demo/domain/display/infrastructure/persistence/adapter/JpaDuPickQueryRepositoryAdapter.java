package com.example.demo.domain.display.infrastructure.persistence.adapter;

import com.example.demo.domain.display.application.query.DuPickQuery;
import com.example.demo.domain.display.application.query.DuPickQueryRepository;
import com.example.demo.domain.display.application.query.DuPickQueryResult;
import com.example.demo.domain.display.infrastructure.persistence.QDuPickColumnJpaEntity;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class JpaDuPickQueryRepositoryAdapter implements DuPickQueryRepository {

  private static final QDuPickColumnJpaEntity column = QDuPickColumnJpaEntity.duPickColumnJpaEntity;

  private final JPAQueryFactory queryFactory;

  public JpaDuPickQueryRepositoryAdapter(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Override
  public List<DuPickQueryResult> findDuPicks(DuPickQuery query, int limit) {
    return queryFactory
        .select(
            Projections.constructor(
                DuPickQueryResult.class,
                column.id,
                column.name,
                column.content,
                column.columnImageUrl,
                column.createdAt))
        .from(column)
        .where(cursorAfter(query))
        .orderBy(column.id.asc())
        .limit(limit)
        .fetch();
  }

  private BooleanExpression cursorAfter(DuPickQuery query) {
    return query.cursor() == null ? null : column.id.gt(query.cursor());
  }
}

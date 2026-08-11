package com.example.demo.domain.displayartwork.infrastructure.persistence.adapter;

import com.example.demo.domain.display.domain.type.DisplayStatus;
import com.example.demo.domain.displayartwork.application.query.ArtistWorkStatsQueryRepository;
import com.example.demo.domain.displayartwork.application.query.ArtistWorkStatsQueryResult;
import com.example.demo.domain.displayartwork.domain.aggregate.QDisplayArtwork;
import com.example.demo.domain.displayartwork.domain.entity.QCreator;
import com.example.demo.domain.displayartwork.domain.type.DisplayArtworkStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class JpaArtistWorkStatsQueryRepositoryAdapter implements ArtistWorkStatsQueryRepository {

  private static final QCreator creator = QCreator.creator;
  private static final QDisplayArtwork displayArtwork = QDisplayArtwork.displayArtwork;

  private final JPAQueryFactory queryFactory;

  public JpaArtistWorkStatsQueryRepositoryAdapter(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Override
  public List<ArtistWorkStatsQueryResult> findByUserIdIn(List<Long> userIds) {
    if (userIds.isEmpty()) {
      return List.of();
    }

    return queryFactory
        .select(
            Projections.constructor(
                ArtistWorkStatsQueryResult.class,
                creator.userId,
                displayArtwork.id.countDistinct(),
                displayArtwork.display.id.countDistinct()))
        .from(creator)
        .join(displayArtwork)
        .on(displayArtwork.id.eq(creator.displayArtworkId))
        .where(
            creator.userId.in(userIds),
            displayArtwork.deletedAt.isNull(),
            displayArtwork.status.eq(DisplayArtworkStatus.PUBLISHED),
            displayArtwork.display.status.eq(DisplayStatus.PUBLISHED))
        .groupBy(creator.userId)
        .fetch();
  }
}

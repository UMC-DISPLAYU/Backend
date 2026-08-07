package com.example.demo.domain.displayartwork.infrastructure.persistence.adapter;

import com.example.demo.domain.displayartwork.application.query.ArtworkSummaryQueryRepository;
import com.example.demo.domain.displayartwork.application.query.ArtworkSummaryQueryResult;
import com.example.demo.domain.displayartwork.domain.aggregate.QDisplayArtwork;
import com.example.demo.domain.displayartwork.domain.entity.QArtworkImage;
import com.example.demo.domain.displayartwork.domain.entity.QCreator;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class JpaArtworkSummaryQueryRepositoryAdapter implements ArtworkSummaryQueryRepository {

  private static final QDisplayArtwork displayArtwork = QDisplayArtwork.displayArtwork;
  private static final QArtworkImage image = QArtworkImage.artworkImage;
  private static final QCreator creator = QCreator.creator;

  private final JPAQueryFactory queryFactory;

  public JpaArtworkSummaryQueryRepositoryAdapter(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Override
  public List<ArtworkSummaryQueryResult> findByDisplayArtworkIdIn(List<Long> displayArtworkIds) {
    if (displayArtworkIds.isEmpty()) {
      return List.of();
    }

    return queryFactory
        .select(
            Projections.constructor(
                ArtworkSummaryQueryResult.class,
                displayArtwork.id,
                displayArtwork.artworkName,
                creator.creatorName,
                image.imageUrl))
        .from(displayArtwork)
        .leftJoin(displayArtwork.images, image)
        .on(ArtworkImageConditions.thumbnailImage(image))
        .leftJoin(creator)
        .on(creator.displayArtworkId.eq(displayArtwork.id).and(creator.isLeader.isTrue()))
        .where(displayArtwork.id.in(displayArtworkIds))
        .fetch();
  }
}

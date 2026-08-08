package com.example.demo.domain.displayartwork.infrastructure.persistence.adapter;

import com.example.demo.domain.displayartwork.application.query.ArtworkSummaryQueryRepository;
import com.example.demo.domain.displayartwork.application.query.ArtworkSummaryQueryResult;
import com.example.demo.domain.displayartwork.domain.aggregate.QDisplayArtwork;
import com.example.demo.domain.displayartwork.domain.entity.QArtworkImage;
import com.example.demo.domain.displayartwork.domain.entity.QCreator;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

    // 대표 이미지/리더 작가를 한 쿼리에서 조인하면, 작품 하나에 조건을 만족하는 행이
    // 2개 이상 있을 때 결과가 중복되어 Collectors.toMap이 예외를 던질 수 있다.
    // 그래서 작품별로 최대 한 건만 남도록 각각 따로 조회한 뒤 자바에서 병합한다.
    Map<Long, String> artworkImageUrlByArtworkId =
        findThumbnailImageUrlByArtworkId(displayArtworkIds);
    Map<Long, String> artistNameByArtworkId = findLeaderCreatorNameByArtworkId(displayArtworkIds);

    return queryFactory
        .select(displayArtwork.id, displayArtwork.artworkName)
        .from(displayArtwork)
        .where(displayArtwork.id.in(displayArtworkIds))
        .fetch()
        .stream()
        .map(
            tuple -> {
              Long displayArtworkId = tuple.get(displayArtwork.id);
              return new ArtworkSummaryQueryResult(
                  displayArtworkId,
                  tuple.get(displayArtwork.artworkName),
                  artistNameByArtworkId.get(displayArtworkId),
                  artworkImageUrlByArtworkId.get(displayArtworkId));
            })
        .toList();
  }

  private Map<Long, String> findThumbnailImageUrlByArtworkId(List<Long> displayArtworkIds) {
    List<Tuple> rows =
        queryFactory
            .select(image.displayArtwork.id, image.imageUrl)
            .from(image)
            .where(
                image.displayArtwork.id.in(displayArtworkIds),
                ArtworkImageConditions.thumbnailImage(image))
            .fetch();
    return rows.stream()
        .collect(
            Collectors.toMap(
                tuple -> tuple.get(image.displayArtwork.id),
                tuple -> tuple.get(image.imageUrl),
                (existing, duplicate) -> existing));
  }

  private Map<Long, String> findLeaderCreatorNameByArtworkId(List<Long> displayArtworkIds) {
    List<Tuple> rows =
        queryFactory
            .select(creator.displayArtworkId, creator.creatorName)
            .from(creator)
            .where(creator.displayArtworkId.in(displayArtworkIds), creator.isLeader.isTrue())
            .fetch();
    return rows.stream()
        .collect(
            Collectors.toMap(
                tuple -> tuple.get(creator.displayArtworkId),
                tuple -> tuple.get(creator.creatorName),
                (existing, duplicate) -> existing));
  }
}

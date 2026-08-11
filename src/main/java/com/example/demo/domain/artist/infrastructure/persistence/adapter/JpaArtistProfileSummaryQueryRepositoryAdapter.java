package com.example.demo.domain.artist.infrastructure.persistence.adapter;

import com.example.demo.domain.artist.application.query.ArtistProfileSummaryQueryRepository;
import com.example.demo.domain.artist.application.query.ArtistProfileSummaryQueryResult;
import com.example.demo.domain.artist.domain.aggregate.QArtistProfile;
import com.example.demo.domain.artist.domain.entity.QAreaOfActivity;
import com.example.demo.domain.artist.domain.type.ActivityCategory;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class JpaArtistProfileSummaryQueryRepositoryAdapter
    implements ArtistProfileSummaryQueryRepository {

  private static final QArtistProfile artistProfile = QArtistProfile.artistProfile;
  private static final QAreaOfActivity areaOfActivity = QAreaOfActivity.areaOfActivity;

  private final JPAQueryFactory queryFactory;

  public JpaArtistProfileSummaryQueryRepositoryAdapter(JPAQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Override
  public List<ArtistProfileSummaryQueryResult> findByArtistProfileIdIn(
      List<Long> artistProfileIds) {
    if (artistProfileIds.isEmpty()) {
      return List.of();
    }

    Map<Long, List<ActivityCategory>> fieldsByArtistProfileId =
        findFieldsByArtistProfileId(artistProfileIds);

    return queryFactory
        .select(
            artistProfile.id,
            artistProfile.user.id,
            artistProfile.artistName,
            artistProfile.profileImageUrl)
        .from(artistProfile)
        .where(artistProfile.id.in(artistProfileIds))
        .fetch()
        .stream()
        .map(
            tuple -> {
              Long id = tuple.get(artistProfile.id);
              return new ArtistProfileSummaryQueryResult(
                  id,
                  tuple.get(artistProfile.user.id),
                  tuple.get(artistProfile.artistName),
                  tuple.get(artistProfile.profileImageUrl),
                  fieldsByArtistProfileId.getOrDefault(id, List.of()));
            })
        .toList();
  }

  @Override
  public List<ArtistProfileSummaryQueryResult> findByUserIdIn(List<Long> userIds) {
    if (userIds.isEmpty()) {
      return List.of();
    }

    List<Tuple> profiles =
        queryFactory
            .select(
                artistProfile.id,
                artistProfile.user.id,
                artistProfile.artistName,
                artistProfile.profileImageUrl)
            .from(artistProfile)
            .where(artistProfile.user.id.in(userIds))
            .fetch();

    List<Long> artistProfileIds =
        profiles.stream().map(tuple -> tuple.get(artistProfile.id)).toList();
    Map<Long, List<ActivityCategory>> fieldsByArtistProfileId =
        findFieldsByArtistProfileId(artistProfileIds);

    return profiles.stream()
        .map(
            tuple -> {
              Long id = tuple.get(artistProfile.id);
              return new ArtistProfileSummaryQueryResult(
                  id,
                  tuple.get(artistProfile.user.id),
                  tuple.get(artistProfile.artistName),
                  tuple.get(artistProfile.profileImageUrl),
                  fieldsByArtistProfileId.getOrDefault(id, List.of()));
            })
        .toList();
  }

  private Map<Long, List<ActivityCategory>> findFieldsByArtistProfileId(
      List<Long> artistProfileIds) {
    List<Tuple> rows =
        queryFactory
            .select(areaOfActivity.artistProfile.id, areaOfActivity.field)
            .from(areaOfActivity)
            .where(areaOfActivity.artistProfile.id.in(artistProfileIds))
            .fetch();
    return rows.stream()
        .collect(
            Collectors.groupingBy(
                tuple -> tuple.get(areaOfActivity.artistProfile.id),
                Collectors.mapping(tuple -> tuple.get(areaOfActivity.field), Collectors.toList())));
  }
}

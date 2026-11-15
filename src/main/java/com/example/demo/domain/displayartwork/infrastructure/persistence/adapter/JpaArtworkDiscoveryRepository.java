package com.example.demo.domain.displayartwork.infrastructure.persistence.adapter;

import com.example.demo.domain.display.domain.type.SearchDisplayStatus;
import com.example.demo.domain.displayartwork.application.query.ArtworkDiscoveryRepository;
import com.example.demo.domain.displayartwork.application.query.SearchArtworksQuery;
import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class JpaArtworkDiscoveryRepository implements ArtworkDiscoveryRepository {
  private final EntityManager entityManager;

  public JpaArtworkDiscoveryRepository(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public List<DisplayArtwork> search(SearchArtworksQuery request, LocalDate today) {
    StringBuilder hql =
        new StringBuilder(
            """
      SELECT a FROM DisplayArtwork a JOIN FETCH a.display d
      WHERE a.deletedAt IS NULL AND d.deletedAt IS NULL
      AND a.status = com.example.demo.domain.displayartwork.domain.type.DisplayArtworkStatus.PUBLISHED
      AND d.status = com.example.demo.domain.display.domain.type.DisplayStatus.PUBLISHED
      """);
    boolean keyword = request.searchWord() != null && !request.searchWord().isBlank();
    if (keyword)
      hql.append(
          """
      AND (LOWER(a.artworkName) LIKE :keyword ESCAPE '!'
        OR EXISTS (SELECT c.id FROM Creator c WHERE c.displayArtworkId=a.id
          AND c.role <> com.example.demo.domain.displayartwork.domain.type.CreatorRole.QA_ONLY
          AND LOWER(c.creatorName) LIKE :keyword ESCAPE '!'))
      """);
    if (request.type() != null) hql.append(" AND d.displayType=:type");
    if (request.status() != null)
      hql.append(
          switch (request.status()) {
            case UPCOMING -> " AND d.period.startDate>:today";
            case ONGOING -> " AND d.period.startDate<=:today AND d.period.endDate>=:today";
            case ENDED -> " AND d.period.endDate<:today";
            case CLOSING_SOON ->
                " AND d.period.startDate<=:today AND d.period.endDate BETWEEN :today AND :soon";
          });
    var query =
        entityManager.createQuery(
            hql.append(" ORDER BY a.id DESC").toString(), DisplayArtwork.class);
    if (keyword)
      query.setParameter(
          "keyword",
          "%"
              + request
                  .searchWord()
                  .strip()
                  .toLowerCase(Locale.ROOT)
                  .replace("!", "!!")
                  .replace("%", "!%")
                  .replace("_", "!_")
              + "%");
    if (request.type() != null) query.setParameter("type", request.type());
    if (request.status() != null) query.setParameter("today", today);
    if (request.status() == SearchDisplayStatus.CLOSING_SOON)
      query.setParameter("soon", today.plusDays(3));
    return query
        .setFirstResult(Math.toIntExact((long) request.page() * request.size()))
        .setMaxResults(request.size() + 1)
        .getResultList();
  }

  @Override
  public List<DisplayArtwork> recommend(List<ArtworkType> fields, String school) {
    List<Long> ids = findRecommendedArtworkIds(fields, school);
    if (ids.isEmpty()) return List.of();
    var artworks =
        entityManager
            .createQuery(
                "SELECT a FROM DisplayArtwork a JOIN FETCH a.display WHERE a.id IN :ids",
                DisplayArtwork.class)
            .setParameter("ids", ids)
            .getResultList()
            .stream()
            .collect(Collectors.toMap(DisplayArtwork::getId, Function.identity()));
    return ids.stream().map(artworks::get).toList();
  }

  public List<Long> findRecommendedArtworkIds(List<ArtworkType> fields, String school) {
    var selected =
        fields == null
            ? List.<ArtworkType>of()
            : fields.stream().map(ArtworkType::canonical).distinct().toList();
    StringBuilder filter = new StringBuilder();
    for (int index = 0; index < selected.size(); index++) {
      filter
          .append(
              " AND EXISTS (SELECT 1 FROM ArtworkField f WHERE f.displayArtworkId=a.displayArtworkId AND f.field IN (:fields")
          .append(index)
          .append("))");
    }
    boolean hasSchool = school != null && !school.isBlank();
    if (hasSchool) filter.append(" AND d.organization=:school");
    String sql =
        """
      WITH eligible AS (
        SELECT a.displayArtworkId AS artworkId, a.displayId, a.firstPublishedAt
        FROM DisplayArtwork a JOIN Display d ON d.displayId=a.displayId
        WHERE a.status='PUBLISHED' AND a.deletedAt IS NULL
          AND d.status='PUBLISHED' AND d.deletedAt IS NULL
      """
            + filter
            + """
      ), ranked AS (
        SELECT artworkId, displayId,
          ROW_NUMBER() OVER (PARTITION BY displayId ORDER BY firstPublishedAt DESC, artworkId DESC) AS artworkRank,
          MAX(firstPublishedAt) OVER (PARTITION BY displayId) AS latestPublication
        FROM eligible
      )
      SELECT artworkId FROM ranked
      ORDER BY artworkRank, latestPublication DESC, displayId DESC
      LIMIT 10
      """;
    var query = entityManager.createNativeQuery(sql, Long.class);
    for (int index = 0; index < selected.size(); index++)
      query.setParameter(
          "fields" + index, selected.get(index).storedAliases().stream().map(Enum::name).toList());
    if (hasSchool) query.setParameter("school", school.strip());
    List<?> ids = query.getResultList();
    return ids.stream().map(Long.class::cast).toList();
  }
}

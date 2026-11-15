package com.example.demo.domain.displayartwork.infrastructure.persistence.adapter;

import com.example.demo.domain.displayartwork.application.query.ArtworkDiscoveryRepository;
import com.example.demo.domain.displayartwork.application.query.SearchArtworksQuery;
import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
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
    if (request.status()
        == com.example.demo.domain.display.domain.type.SearchDisplayStatus.CLOSING_SOON)
      query.setParameter("soon", today.plusDays(3));
    return query
        .setFirstResult(Math.toIntExact((long) request.page() * request.size()))
        .setMaxResults(request.size() + 1)
        .getResultList();
  }
}

package com.example.demo.domain.artworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.artworkcommunication.application.query.GetMyArtworkFeelingsQuery.Cursor;
import com.example.demo.domain.artworkcommunication.application.query.MyArtworkFeelingQueryItem;
import com.example.demo.domain.artworkcommunication.application.query.MyArtworkFeelingQueryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaMyArtworkFeelingQueryRepositoryAdapter implements MyArtworkFeelingQueryRepository {

  private final EntityManager entityManager;

  @Override
  public List<MyArtworkFeelingQueryItem> findByUserIdWithCursor(
      Long userId, Cursor cursor, int limit) {
    String cursorCondition =
        cursor == null
            ? ""
            : """
              WHERE items.created_at < :cursorCreatedAt
                 OR (
                      items.created_at = :cursorCreatedAt
                      AND (
                          items.source_order > :cursorSourceOrder
                          OR (
                              items.source_order = :cursorSourceOrder
                              AND items.item_id < :cursorItemId
                          )
                      )
                 )
              """;
    String sql =
        """
        SELECT item_id, source_order, artwork_id, personal_artwork_id, artwork_name, content, created_at
        FROM (
            SELECT
                feeling.feelingId AS item_id,
                0 AS source_order,
                feeling.displayArtworkId AS artwork_id,
                NULL AS personal_artwork_id,
                artwork.artworkName AS artwork_name,
                feeling.content AS content,
                feeling.createdAt AS created_at
            FROM ArtworkFeeling feeling
            JOIN DisplayArtwork artwork
              ON artwork.displayArtworkId = feeling.displayArtworkId
            WHERE feeling.userId = :userId
              AND feeling.deletedAt IS NULL

            UNION ALL

            SELECT
                feeling.personalFeelingId AS item_id,
                1 AS source_order,
                NULL AS artwork_id,
                feeling.personalArtworkId AS personal_artwork_id,
                artwork.artworkName AS artwork_name,
                feeling.content AS content,
                feeling.createdAt AS created_at
            FROM PersonalArtworkFeeling feeling
            JOIN PersonalArtwork artwork
              ON artwork.personalArtworkId = feeling.personalArtworkId
            WHERE feeling.userId = :userId
              AND feeling.deletedAt IS NULL
        ) items
        """
            + cursorCondition
            + """
        ORDER BY items.created_at DESC, items.source_order ASC, items.item_id DESC
        LIMIT :limit
        """;

    Query nativeQuery = entityManager.createNativeQuery(sql).setParameter("userId", userId);
    if (cursor != null) {
      nativeQuery
          .setParameter("cursorCreatedAt", cursor.createdAt())
          .setParameter("cursorSourceOrder", cursor.sourceOrder())
          .setParameter("cursorItemId", cursor.itemId());
    }

    @SuppressWarnings("unchecked")
    List<Object[]> rows = nativeQuery.setParameter("limit", limit).getResultList();

    return rows.stream().map(this::toItem).toList();
  }

  private MyArtworkFeelingQueryItem toItem(Object[] row) {
    return new MyArtworkFeelingQueryItem(
        ((Number) row[0]).longValue(),
        ((Number) row[1]).intValue(),
        toLong(row[2]),
        toLong(row[3]),
        (String) row[4],
        (String) row[5],
        toLocalDateTime(row[6]));
  }

  private Long toLong(Object value) {
    return value == null ? null : ((Number) value).longValue();
  }

  private LocalDateTime toLocalDateTime(Object value) {
    if (value instanceof LocalDateTime localDateTime) {
      return localDateTime;
    }
    return ((Timestamp) value).toLocalDateTime();
  }
}

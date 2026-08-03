package com.example.demo.domain.artworkcommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.artworkcommunication.application.query.GetReceivedArtworkQuestionsQuery.Cursor;
import com.example.demo.domain.artworkcommunication.application.query.ReceivedArtworkQuestionQueryItem;
import com.example.demo.domain.artworkcommunication.application.query.ReceivedArtworkQuestionQueryRepository;
import com.example.demo.domain.artworkcommunication.domain.type.AnswerStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaReceivedArtworkQuestionQueryRepositoryAdapter
    implements ReceivedArtworkQuestionQueryRepository {

  private final EntityManager entityManager;

  @Override
  public List<ReceivedArtworkQuestionQueryItem> findReceivedByUserIdAndAnswerStatusWithCursor(
      Long userId, AnswerStatus answerStatus, Cursor cursor, int limit) {
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
        SELECT
            item_id,
            source_order,
            question_id,
            personal_question_id,
            artwork_id,
            personal_artwork_id,
            artwork_name,
            content,
            is_public,
            answer_status,
            questioner_id,
            questioner_nickname,
            created_at
        FROM (
            SELECT
                question.questionId AS item_id,
                0 AS source_order,
                question.questionId AS question_id,
                NULL AS personal_question_id,
                question.displayArtworkId AS artwork_id,
                NULL AS personal_artwork_id,
                artwork.artworkName AS artwork_name,
                question.content AS content,
                question.isPublic AS is_public,
                question.answerStatus AS answer_status,
                question.userId AS questioner_id,
                questioner.nickname AS questioner_nickname,
                question.createdAt AS created_at
            FROM ArtworkQuestion question
            JOIN DisplayArtwork artwork
              ON artwork.displayArtworkId = question.displayArtworkId
            JOIN Creator creator
              ON creator.displayArtworkId = question.displayArtworkId
             AND creator.userId = :userId
             AND creator.isContact = TRUE
            JOIN User questioner
              ON questioner.userId = question.userId
            WHERE question.answerStatus = :answerStatus
              AND question.deletedAt IS NULL

            UNION ALL

            SELECT
                question.personalQuestionId AS item_id,
                1 AS source_order,
                NULL AS question_id,
                question.personalQuestionId AS personal_question_id,
                NULL AS artwork_id,
                question.personalArtworkId AS personal_artwork_id,
                artwork.artworkName AS artwork_name,
                question.content AS content,
                question.isPublic AS is_public,
                question.answerStatus AS answer_status,
                question.userId AS questioner_id,
                questioner.nickname AS questioner_nickname,
                question.createdAt AS created_at
            FROM PersonalArtworkQuestion question
            JOIN PersonalArtwork artwork
              ON artwork.personalArtworkId = question.personalArtworkId
             AND artwork.userId = :userId
            JOIN User questioner
              ON questioner.userId = question.userId
            WHERE question.answerStatus = :answerStatus
              AND question.deletedAt IS NULL
        ) items
        """
            + cursorCondition
            + """
        ORDER BY items.created_at DESC, items.source_order ASC, items.item_id DESC
        LIMIT :limit
        """;

    Query nativeQuery =
        entityManager
            .createNativeQuery(sql)
            .setParameter("userId", userId)
            .setParameter("answerStatus", answerStatus.name());
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

  private ReceivedArtworkQuestionQueryItem toItem(Object[] row) {
    return new ReceivedArtworkQuestionQueryItem(
        ((Number) row[0]).longValue(),
        ((Number) row[1]).intValue(),
        toLong(row[2]),
        toLong(row[3]),
        toLong(row[4]),
        toLong(row[5]),
        (String) row[6],
        (String) row[7],
        toBoolean(row[8]),
        AnswerStatus.valueOf((String) row[9]),
        ((Number) row[10]).longValue(),
        (String) row[11],
        toLocalDateTime(row[12]));
  }

  private Long toLong(Object value) {
    return value == null ? null : ((Number) value).longValue();
  }

  private Boolean toBoolean(Object value) {
    if (value instanceof Boolean booleanValue) {
      return booleanValue;
    }
    return ((Number) value).intValue() == 1;
  }

  private LocalDateTime toLocalDateTime(Object value) {
    if (value instanceof LocalDateTime localDateTime) {
      return localDateTime;
    }
    return ((Timestamp) value).toLocalDateTime();
  }
}

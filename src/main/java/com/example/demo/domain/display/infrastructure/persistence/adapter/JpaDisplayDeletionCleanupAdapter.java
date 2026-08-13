package com.example.demo.domain.display.infrastructure.persistence.adapter;

import com.example.demo.domain.display.application.port.DisplayDeletionCleanupPort;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JpaDisplayDeletionCleanupAdapter implements DisplayDeletionCleanupPort {

  private final EntityManager entityManager;

  public JpaDisplayDeletionCleanupAdapter(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void cleanupDisplayChildren(Long displayId, LocalDateTime deletedAt) {
    List<Long> displayArtworkIds = findDisplayArtworkIds(displayId);

    cleanupDisplayArchives(displayId, deletedAt);
    cleanupDisplayLikes(displayId);
    cleanupDisplayReviews(displayId, deletedAt);

    if (displayArtworkIds.isEmpty()) {
      return;
    }

    cleanupArtworkArchives(displayArtworkIds, deletedAt);
    cleanupArtworkLikes(displayArtworkIds);
    cleanupArtworkCommunications(displayArtworkIds, deletedAt);
    softDeleteDisplayArtworks(displayArtworkIds, deletedAt);
  }

  private List<Long> findDisplayArtworkIds(Long displayId) {
    return entityManager
        .createQuery(
            """
            SELECT artwork.id
            FROM DisplayArtwork artwork
            WHERE artwork.display.id = :displayId
              AND artwork.deletedAt IS NULL
            """,
            Long.class)
        .setParameter("displayId", displayId)
        .getResultList();
  }

  private void cleanupDisplayArchives(Long displayId, LocalDateTime deletedAt) {
    entityManager
        .createQuery(
            """
            UPDATE Memo memo
            SET memo.deletedAt = :deletedAt
            WHERE memo.archiveDisplayId IN (
              SELECT archive.id
              FROM ArchiveDisplay archive
              WHERE archive.displayId = :displayId
                AND archive.deletedAt IS NULL
            )
              AND memo.deletedAt IS NULL
            """)
        .setParameter("deletedAt", deletedAt)
        .setParameter("displayId", displayId)
        .executeUpdate();

    entityManager
        .createQuery(
            """
            UPDATE ArchiveDisplay archive
            SET archive.deletedAt = :deletedAt
            WHERE archive.displayId = :displayId
              AND archive.deletedAt IS NULL
            """)
        .setParameter("deletedAt", deletedAt)
        .setParameter("displayId", displayId)
        .executeUpdate();
  }

  private void cleanupArtworkArchives(List<Long> displayArtworkIds, LocalDateTime deletedAt) {
    entityManager
        .createQuery(
            """
            UPDATE Memo memo
            SET memo.deletedAt = :deletedAt
            WHERE memo.archiveWorkId IN (
              SELECT archive.id
              FROM ArchiveWork archive
              WHERE archive.displayArtworkId IN :displayArtworkIds
                AND archive.deletedAt IS NULL
            )
              AND memo.deletedAt IS NULL
            """)
        .setParameter("deletedAt", deletedAt)
        .setParameter("displayArtworkIds", displayArtworkIds)
        .executeUpdate();

    entityManager
        .createQuery(
            """
            UPDATE ArchiveWork archive
            SET archive.deletedAt = :deletedAt
            WHERE archive.displayArtworkId IN :displayArtworkIds
              AND archive.deletedAt IS NULL
            """)
        .setParameter("deletedAt", deletedAt)
        .setParameter("displayArtworkIds", displayArtworkIds)
        .executeUpdate();
  }

  private void cleanupDisplayLikes(Long displayId) {
    entityManager
        .createQuery("DELETE FROM DisplayLike displayLike WHERE displayLike.displayId = :displayId")
        .setParameter("displayId", displayId)
        .executeUpdate();
  }

  private void cleanupArtworkLikes(List<Long> displayArtworkIds) {
    entityManager
        .createQuery(
            """
            DELETE FROM DisplayArtworkLike artworkLike
            WHERE artworkLike.displayArtworkId IN :displayArtworkIds
            """)
        .setParameter("displayArtworkIds", displayArtworkIds)
        .executeUpdate();
  }

  private void cleanupDisplayReviews(Long displayId, LocalDateTime deletedAt) {
    entityManager
        .createQuery(
            """
            UPDATE DisplayReviewReplyLike replyLike
            SET replyLike.deletedAt = :deletedAt
            WHERE replyLike.deletedAt IS NULL
              AND replyLike.displayReviewReplyId IN (
                SELECT reply.displayReviewReplyId
                FROM DisplayReviewReply reply
                WHERE reply.displayReviewId IN (
                  SELECT review.displayReviewId
                  FROM DisplayReview review
                  WHERE review.displayId = :displayId
                )
              )
            """)
        .setParameter("deletedAt", deletedAt)
        .setParameter("displayId", displayId)
        .executeUpdate();

    entityManager
        .createQuery(
            """
            UPDATE DisplayReviewReply reply
            SET reply.deletedAt = :deletedAt
            WHERE reply.deletedAt IS NULL
              AND reply.displayReviewId IN (
                SELECT review.displayReviewId
                FROM DisplayReview review
                WHERE review.displayId = :displayId
              )
            """)
        .setParameter("deletedAt", deletedAt)
        .setParameter("displayId", displayId)
        .executeUpdate();

    entityManager
        .createQuery(
            """
            UPDATE DisplayReviewLike reviewLike
            SET reviewLike.deletedAt = :deletedAt
            WHERE reviewLike.deletedAt IS NULL
              AND reviewLike.displayReviewId IN (
                SELECT review.displayReviewId
                FROM DisplayReview review
                WHERE review.displayId = :displayId
              )
            """)
        .setParameter("deletedAt", deletedAt)
        .setParameter("displayId", displayId)
        .executeUpdate();

    entityManager
        .createQuery(
            """
            UPDATE DisplayReview review
            SET review.deletedAt = :deletedAt
            WHERE review.deletedAt IS NULL
              AND review.displayId = :displayId
            """)
        .setParameter("deletedAt", deletedAt)
        .setParameter("displayId", displayId)
        .executeUpdate();
  }

  private void cleanupArtworkCommunications(List<Long> displayArtworkIds, LocalDateTime deletedAt) {
    cleanupArtworkQuestions(displayArtworkIds, deletedAt);
    cleanupArtworkFeelings(displayArtworkIds, deletedAt);
  }

  private void cleanupArtworkQuestions(List<Long> displayArtworkIds, LocalDateTime deletedAt) {
    entityManager
        .createQuery(
            """
            UPDATE ArtworkQuestionReply reply
            SET reply.deletedAt = :deletedAt
            WHERE reply.deletedAt IS NULL
              AND reply.questionId IN (
                SELECT question.questionId
                FROM ArtworkQuestion question
                WHERE question.displayArtworkId IN :displayArtworkIds
              )
            """)
        .setParameter("deletedAt", deletedAt)
        .setParameter("displayArtworkIds", displayArtworkIds)
        .executeUpdate();

    entityManager
        .createQuery(
            """
            UPDATE ArtworkQuestion question
            SET question.deletedAt = :deletedAt
            WHERE question.deletedAt IS NULL
              AND question.displayArtworkId IN :displayArtworkIds
            """)
        .setParameter("deletedAt", deletedAt)
        .setParameter("displayArtworkIds", displayArtworkIds)
        .executeUpdate();
  }

  private void cleanupArtworkFeelings(List<Long> displayArtworkIds, LocalDateTime deletedAt) {
    entityManager
        .createQuery(
            """
            UPDATE ArtworkFeelingReplyLike replyLike
            SET replyLike.deletedAt = :deletedAt
            WHERE replyLike.deletedAt IS NULL
              AND replyLike.feelingReplyId IN (
                SELECT reply.feelingReplyId
                FROM ArtworkFeelingReply reply
                WHERE reply.feelingId IN (
                  SELECT feeling.feelingId
                  FROM ArtworkFeeling feeling
                  WHERE feeling.displayArtworkId IN :displayArtworkIds
                )
              )
            """)
        .setParameter("deletedAt", deletedAt)
        .setParameter("displayArtworkIds", displayArtworkIds)
        .executeUpdate();

    entityManager
        .createQuery(
            """
            UPDATE ArtworkFeelingReply reply
            SET reply.deletedAt = :deletedAt
            WHERE reply.deletedAt IS NULL
              AND reply.feelingId IN (
                SELECT feeling.feelingId
                FROM ArtworkFeeling feeling
                WHERE feeling.displayArtworkId IN :displayArtworkIds
              )
            """)
        .setParameter("deletedAt", deletedAt)
        .setParameter("displayArtworkIds", displayArtworkIds)
        .executeUpdate();

    entityManager
        .createQuery(
            """
            UPDATE ArtworkFeelingLike feelingLike
            SET feelingLike.deletedAt = :deletedAt
            WHERE feelingLike.deletedAt IS NULL
              AND feelingLike.feelingId IN (
                SELECT feeling.feelingId
                FROM ArtworkFeeling feeling
                WHERE feeling.displayArtworkId IN :displayArtworkIds
              )
            """)
        .setParameter("deletedAt", deletedAt)
        .setParameter("displayArtworkIds", displayArtworkIds)
        .executeUpdate();

    entityManager
        .createQuery(
            """
            UPDATE ArtworkFeeling feeling
            SET feeling.deletedAt = :deletedAt
            WHERE feeling.deletedAt IS NULL
              AND feeling.displayArtworkId IN :displayArtworkIds
            """)
        .setParameter("deletedAt", deletedAt)
        .setParameter("displayArtworkIds", displayArtworkIds)
        .executeUpdate();
  }

  private void softDeleteDisplayArtworks(List<Long> displayArtworkIds, LocalDateTime deletedAt) {
    entityManager
        .createQuery(
            """
            UPDATE DisplayArtwork artwork
            SET artwork.deletedAt = :deletedAt
            WHERE artwork.deletedAt IS NULL
              AND artwork.id IN :displayArtworkIds
            """)
        .setParameter("deletedAt", deletedAt)
        .setParameter("displayArtworkIds", displayArtworkIds)
        .executeUpdate();
  }
}

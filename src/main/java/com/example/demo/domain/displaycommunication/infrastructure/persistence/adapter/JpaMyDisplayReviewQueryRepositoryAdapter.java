package com.example.demo.domain.displaycommunication.infrastructure.persistence.adapter;

import com.example.demo.domain.displaycommunication.application.query.MyDisplayReviewQueryItem;
import com.example.demo.domain.displaycommunication.application.query.MyDisplayReviewQueryRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaMyDisplayReviewQueryRepositoryAdapter implements MyDisplayReviewQueryRepository {

  private final EntityManager entityManager;

  @Override
  public List<MyDisplayReviewQueryItem> findByUserIdWithCursor(
      Long userId, Long cursorId, int limit) {
    return entityManager
        .createQuery(
            """
            SELECT new com.example.demo.domain.displaycommunication.application.query.MyDisplayReviewQueryItem(
                review.displayReviewId,
                review.displayId,
                review.content,
                review.createdAt
            )
            FROM DisplayReview review
            WHERE review.userId = :userId
              AND review.deletedAt IS NULL
              AND (:cursorId IS NULL OR review.displayReviewId < :cursorId)
            ORDER BY review.displayReviewId DESC
            """,
            MyDisplayReviewQueryItem.class)
        .setParameter("userId", userId)
        .setParameter("cursorId", cursorId)
        .setMaxResults(limit)
        .getResultList();
  }
}

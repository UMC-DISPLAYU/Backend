package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArtworkQuestionLikeJpaRepository extends JpaRepository<ArtworkQuestionLike, Long> {

  Optional<ArtworkQuestionLike> findByQuestionIdAndUserId(Long questionId, Long userId);

  long countByQuestionIdAndDeletedAtIsNull(Long questionId);

  @Query(
      """
      SELECT questionLike.questionId, COUNT(questionLike)
      FROM ArtworkQuestionLike questionLike
      WHERE questionLike.questionId IN :questionIds
        AND questionLike.deletedAt IS NULL
      GROUP BY questionLike.questionId
      """)
  List<Object[]> countByQuestionIds(@Param("questionIds") List<Long> questionIds);
}

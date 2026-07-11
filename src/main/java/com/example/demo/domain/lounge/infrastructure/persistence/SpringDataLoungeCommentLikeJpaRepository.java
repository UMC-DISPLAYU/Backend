package com.example.demo.domain.lounge.infrastructure.persistence;

import com.example.demo.domain.lounge.domain.entity.LoungeCommentLike;
import com.example.demo.domain.lounge.domain.vo.UserId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataLoungeCommentLikeJpaRepository
    extends JpaRepository<LoungeCommentLike, Long> {

  Optional<LoungeCommentLike> findByLoungeCommentIdAndUserId(Long loungeCommentId, UserId userId);

  long countByLoungeCommentId(Long loungeCommentId);
}

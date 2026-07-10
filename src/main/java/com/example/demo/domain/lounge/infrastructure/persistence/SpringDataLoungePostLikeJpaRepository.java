package com.example.demo.domain.lounge.infrastructure.persistence;

import com.example.demo.domain.lounge.domain.entity.LoungePostLike;
import com.example.demo.domain.lounge.domain.vo.UserId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataLoungePostLikeJpaRepository extends JpaRepository<LoungePostLike, Long> {

    Optional<LoungePostLike> findByLoungePostIdAndUserId(Long loungePostId, UserId userId);

    long countByLoungePostId(Long loungePostId);
}

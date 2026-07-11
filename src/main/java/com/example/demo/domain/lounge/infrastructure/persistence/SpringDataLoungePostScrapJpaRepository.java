package com.example.demo.domain.lounge.infrastructure.persistence;

import com.example.demo.domain.lounge.domain.entity.LoungePostScrap;
import com.example.demo.domain.lounge.domain.vo.UserId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataLoungePostScrapJpaRepository
    extends JpaRepository<LoungePostScrap, Long> {

  Optional<LoungePostScrap> findByLoungePostIdAndUserId(Long loungePostId, UserId userId);

  long countByLoungePostId(Long loungePostId);
}

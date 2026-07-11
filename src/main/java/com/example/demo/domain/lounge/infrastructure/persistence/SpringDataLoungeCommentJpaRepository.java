package com.example.demo.domain.lounge.infrastructure.persistence;

import com.example.demo.domain.lounge.domain.entity.LoungeComment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataLoungeCommentJpaRepository extends JpaRepository<LoungeComment, Long> {

  List<LoungeComment> findByLoungePostId(Long loungePostId);
}

package com.example.demo.domain.lounge.domain.repository;

import com.example.demo.domain.lounge.domain.entity.LoungeComment;
import java.util.List;
import java.util.Optional;

public interface LoungeCommentRepository {
  Optional<LoungeComment> findById(Long loungeCommentId);

  List<LoungeComment> findByLoungePostId(Long loungePostId);

  LoungeComment save(LoungeComment loungeComment);

  void delete(LoungeComment loungeComment);
}

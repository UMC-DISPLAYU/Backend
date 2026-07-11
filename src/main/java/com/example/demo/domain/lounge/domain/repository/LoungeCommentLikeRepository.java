package com.example.demo.domain.lounge.domain.repository;

import com.example.demo.domain.lounge.domain.entity.LoungeCommentLike;
import com.example.demo.domain.lounge.domain.vo.UserId;
import java.util.Optional;

public interface LoungeCommentLikeRepository {

  Optional<LoungeCommentLike> findByLoungeCommentIdAndUserId(Long loungeCommentId, UserId userId);

  LoungeCommentLike save(LoungeCommentLike loungeCommentLike);

  void delete(LoungeCommentLike loungeCommentLike);

  long countByLoungeCommentId(Long loungeCommentId);
}

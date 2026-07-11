package com.example.demo.domain.lounge.domain.repository;

import com.example.demo.domain.lounge.domain.entity.LoungePostLike;
import com.example.demo.domain.lounge.domain.vo.UserId;
import java.util.Optional;

public interface LoungePostLikeRepository {

  Optional<LoungePostLike> findByLoungePostIdAndUserId(Long loungePostId, UserId userId);

  LoungePostLike save(LoungePostLike loungePostLike);

  void delete(LoungePostLike loungePostLike);

  long countByLoungePostId(Long loungePostId);
}

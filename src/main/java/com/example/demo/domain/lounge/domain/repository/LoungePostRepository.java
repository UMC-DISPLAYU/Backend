package com.example.demo.domain.lounge.domain.repository;

import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import com.example.demo.domain.lounge.domain.type.LoungePostCategory;
import java.util.List;
import java.util.Optional;

public interface LoungePostRepository {
  Optional<LoungePost> findById(Long loungePostId);

  Optional<LoungePost> findByIdWithOptimisticLock(Long loungePostId);

  List<LoungePost> findActiveByCursor(
      List<LoungePostCategory> categories, Long cursorId, int limit);

  LoungePost save(LoungePost loungePost);
}

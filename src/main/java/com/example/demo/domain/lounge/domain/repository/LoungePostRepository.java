package com.example.demo.domain.lounge.domain.repository;

import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
import com.example.demo.domain.lounge.domain.type.LoungePostCategory;
import com.example.demo.domain.lounge.domain.vo.UserId;
import java.util.List;
import java.util.Optional;

public interface LoungePostRepository {
  Optional<LoungePost> findById(Long loungePostId);

  List<LoungePost> findActiveByCursor(LoungePostCategory category, Long cursorId, int limit);

  List<LoungePost> findActiveByAuthorCursor(UserId authorUserId, Long cursorId, int limit);

  List<LoungePost> findAllByIds(List<Long> loungePostIds);

  LoungePost save(LoungePost loungePost);
}

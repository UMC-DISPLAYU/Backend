package com.example.demo.domain.lounge.infrastructure.persistence.adapter;

import com.example.demo.domain.lounge.application.query.LoungePostQueryRepository;
import com.example.demo.domain.lounge.application.query.LoungePostQueryResult;
import com.example.demo.domain.lounge.domain.entity.LoungePostImage;
import com.example.demo.domain.lounge.domain.type.LoungeCommentStatus;
import com.example.demo.domain.lounge.domain.type.LoungePostCategory;
import com.example.demo.domain.lounge.domain.type.LoungePostStatus;
import com.example.demo.domain.lounge.infrastructure.persistence.SpringDataLoungePostQueryJpaRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
public class JpaLoungePostQueryRepositoryAdapter implements LoungePostQueryRepository {

  private final SpringDataLoungePostQueryJpaRepository jpaRepository;

  public JpaLoungePostQueryRepositoryAdapter(SpringDataLoungePostQueryJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public List<LoungePostQueryResult> findActiveByAuthorCursor(
      Long authorUserId, List<LoungePostCategory> categories, Long cursorId, int limit) {
    return jpaRepository.findActiveByAuthorCursor(
        authorUserId, categories, LoungePostStatus.ACTIVE, cursorId, PageRequest.of(0, limit));
  }

  @Override
  public List<LoungePostQueryResult> findActiveScrappedByUserCursor(
      Long userId, List<LoungePostCategory> categories, Long cursorId, int limit) {
    return jpaRepository.findActiveScrappedByUserCursor(
        userId, categories, LoungePostStatus.ACTIVE, cursorId, PageRequest.of(0, limit));
  }

  @Override
  public List<LoungePostQueryResult> findActiveCommentedByUserCursor(
      Long userId, List<LoungePostCategory> categories, Long cursorId, int limit) {
    return jpaRepository.findActiveCommentedByUserCursor(
        userId,
        categories,
        LoungeCommentStatus.ACTIVE,
        LoungePostStatus.ACTIVE,
        cursorId,
        PageRequest.of(0, limit));
  }

  @Override
  public Map<Long, List<String>> findImageUrlsByLoungePostIds(List<Long> loungePostIds) {
    Map<Long, List<String>> imageUrlsByPostId = new HashMap<>();
    for (LoungePostImage image : jpaRepository.findImagesByLoungePostIds(loungePostIds)) {
      imageUrlsByPostId
          .computeIfAbsent(image.getLoungePost().getId(), ignored -> new ArrayList<>())
          .add(image.getImageUrl());
    }
    return imageUrlsByPostId;
  }
}

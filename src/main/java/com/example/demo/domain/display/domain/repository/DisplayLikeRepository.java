package com.example.demo.domain.display.domain.repository;

import com.example.demo.domain.display.domain.entity.DisplayLike;
import com.example.demo.domain.display.domain.vo.UserId;
import java.util.Optional;

public interface DisplayLikeRepository {

  Optional<DisplayLike> findByDisplayIdAndUserId(Long displayId, UserId userId);

  DisplayLike save(DisplayLike displayLike);

  long countByDisplayIdAndDeletedAtIsNull(Long displayId);
}

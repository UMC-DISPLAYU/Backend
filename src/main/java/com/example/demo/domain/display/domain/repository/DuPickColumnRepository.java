package com.example.demo.domain.display.domain.repository;

import com.example.demo.domain.display.domain.vo.PosterCrop;
import java.time.LocalDateTime;

public interface DuPickColumnRepository {
  boolean updatePosterCrop(Long duPickId, PosterCrop crop, LocalDateTime changedAt);
}

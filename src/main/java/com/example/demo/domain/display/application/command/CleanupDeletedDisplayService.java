package com.example.demo.domain.display.application.command;

import com.example.demo.domain.display.domain.repository.DisplayLikeRepository;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CleanupDeletedDisplayService {

  private final DisplayLikeRepository displayLikeRepository;

  public CleanupDeletedDisplayService(DisplayLikeRepository displayLikeRepository) {
    this.displayLikeRepository = displayLikeRepository;
  }

  @Transactional
  public void cleanup(Long displayId) {
    Objects.requireNonNull(displayId, "displayId must not be null.");
    displayLikeRepository.deleteAllByDisplayId(displayId);
  }
}

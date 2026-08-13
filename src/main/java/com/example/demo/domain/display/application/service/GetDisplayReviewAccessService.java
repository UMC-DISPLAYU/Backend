package com.example.demo.domain.display.application.service;

import com.example.demo.domain.display.application.result.DisplayReviewAccessResult;
import com.example.demo.domain.display.application.usecase.GetDisplayReviewAccessUseCase;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetDisplayReviewAccessService implements GetDisplayReviewAccessUseCase {

  private final DisplayRepository displayRepository;

  @Override
  @Transactional(readOnly = true)
  public Optional<DisplayReviewAccessResult> getDisplayReviewAccess(Long displayId) {
    return displayRepository
        .findById(displayId)
        .filter(display -> !display.isDeleted())
        .map(DisplayReviewAccessResult::from);
  }
}

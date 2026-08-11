package com.example.demo.domain.display.application.usecase;

import com.example.demo.domain.display.application.result.DisplayReviewAccessResult;
import java.util.Optional;

public interface GetDisplayReviewAccessUseCase {

  Optional<DisplayReviewAccessResult> getDisplayReviewAccess(Long displayId);
}

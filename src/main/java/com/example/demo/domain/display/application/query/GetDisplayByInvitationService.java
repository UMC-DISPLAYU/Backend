package com.example.demo.domain.display.application.query;

import com.example.demo.domain.display.application.result.DisplayDetailResult;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.display.infrastructure.DisplayInvitationTokenHasher;
import com.example.demo.global.error.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetDisplayByInvitationService {

  private final DisplayRepository displayRepository;
  private final DisplayInvitationTokenHasher tokenHasher;

  public GetDisplayByInvitationService(
      DisplayRepository displayRepository, DisplayInvitationTokenHasher tokenHasher) {
    this.displayRepository = displayRepository;
    this.tokenHasher = tokenHasher;
  }

  @Transactional(readOnly = true)
  public DisplayDetailResult getDisplay(String rawToken) {
    String tokenHash = tokenHasher.hash(rawToken);
    return displayRepository
        .findByInvitationToken(tokenHash)
        .map(
            display -> {
              display.validateInvitationAccessible();
              return DisplayDetailResult.from(display);
            })
        .orElseThrow(
            () -> new BusinessException(DisplayErrorCode.INVALID_DISPLAY_INVITATION_TOKEN));
  }
}

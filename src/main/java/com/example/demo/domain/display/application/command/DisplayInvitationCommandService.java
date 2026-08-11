package com.example.demo.domain.display.application.command;

import com.example.demo.domain.display.application.permission.DisplayPermissionChecker;
import com.example.demo.domain.display.application.port.DisplayInvitationBaseUrlProvider;
import com.example.demo.domain.display.application.port.DisplayInvitationTokenGenerator;
import com.example.demo.domain.display.application.port.DisplayInvitationTokenHasher;
import com.example.demo.domain.display.application.result.DisplayInvitationDisableResult;
import com.example.demo.domain.display.application.result.DisplayInvitationResult;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.global.error.BusinessException;
import java.time.Clock;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DisplayInvitationCommandService {

  private final DisplayRepository displayRepository;
  private final DisplayInvitationTokenGenerator tokenGenerator;
  private final DisplayInvitationTokenHasher tokenHasher;
  private final DisplayInvitationBaseUrlProvider baseUrlProvider;
  private final Clock clock;
  private final DisplayPermissionChecker displayPermissionChecker;

  public DisplayInvitationCommandService(
      DisplayRepository displayRepository,
      DisplayInvitationTokenGenerator tokenGenerator,
      DisplayInvitationTokenHasher tokenHasher,
      DisplayInvitationBaseUrlProvider baseUrlProvider,
      Clock clock,
      DisplayPermissionChecker displayPermissionChecker) {
    this.displayRepository = displayRepository;
    this.tokenGenerator = tokenGenerator;
    this.tokenHasher = tokenHasher;
    this.baseUrlProvider = baseUrlProvider;
    this.clock = clock;
    this.displayPermissionChecker = displayPermissionChecker;
  }

  @Transactional
  public DisplayInvitationResult issueInvitation(Long requesterUserId, Long displayId) {
    Display display = findDisplay(displayId);
    displayPermissionChecker.requireInvitationTokenManager(display, requesterUserId);
    String rawToken = tokenGenerator.generate();
    display.issueInvitationToken(tokenHasher.hash(rawToken));
    return new DisplayInvitationResult(display.getId(), invitationUrl(rawToken));
  }

  @Transactional
  public DisplayInvitationDisableResult disableInvitation(Long requesterUserId, Long displayId) {
    Display display = findDisplay(displayId);
    displayPermissionChecker.requireInvitationTokenManager(display, requesterUserId);
    display.disableInvitation(LocalDateTime.now(clock));
    return new DisplayInvitationDisableResult(display.getId(), display.getInvitationDisabledAt());
  }

  private Display findDisplay(Long displayId) {
    return displayRepository
        .findById(displayId)
        .orElseThrow(() -> new BusinessException(DisplayErrorCode.DISPLAY_NOT_FOUND));
  }

  private String invitationUrl(String rawToken) {
    String baseUrl = baseUrlProvider.baseUrl();
    if (baseUrl.endsWith("/")) {
      return baseUrl + rawToken;
    }
    return baseUrl + "/" + rawToken;
  }
}

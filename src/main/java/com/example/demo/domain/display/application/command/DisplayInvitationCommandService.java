package com.example.demo.domain.display.application.command;

import com.example.demo.domain.display.application.result.DisplayInvitationDisableResult;
import com.example.demo.domain.display.application.result.DisplayInvitationResult;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.display.infrastructure.DisplayInvitationProperties;
import com.example.demo.domain.display.infrastructure.DisplayInvitationTokenGenerator;
import com.example.demo.domain.display.infrastructure.DisplayInvitationTokenHasher;
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
  private final DisplayInvitationProperties properties;
  private final Clock clock;

  public DisplayInvitationCommandService(
      DisplayRepository displayRepository,
      DisplayInvitationTokenGenerator tokenGenerator,
      DisplayInvitationTokenHasher tokenHasher,
      DisplayInvitationProperties properties,
      Clock clock) {
    this.displayRepository = displayRepository;
    this.tokenGenerator = tokenGenerator;
    this.tokenHasher = tokenHasher;
    this.properties = properties;
    this.clock = clock;
  }

  @Transactional
  public DisplayInvitationResult issueInvitation(Long requesterUserId, Long displayId) {
    Display display = findDisplay(displayId);
    validateRequester(display, requesterUserId);
    String rawToken = tokenGenerator.generate();
    display.issueInvitationToken(tokenHasher.hash(rawToken));
    return new DisplayInvitationResult(display.getId(), invitationUrl(rawToken));
  }

  @Transactional
  public DisplayInvitationDisableResult disableInvitation(Long requesterUserId, Long displayId) {
    Display display = findDisplay(displayId);
    validateRequester(display, requesterUserId);
    display.disableInvitation(LocalDateTime.now(clock));
    return new DisplayInvitationDisableResult(display.getId(), display.getInvitationDisabledAt());
  }

  private Display findDisplay(Long displayId) {
    return displayRepository
        .findById(displayId)
        .orElseThrow(() -> new BusinessException(DisplayErrorCode.DISPLAY_NOT_FOUND));
  }

  private void validateRequester(Display display, Long requesterUserId) {
    if (!display.canInviteMember(requesterUserId)) {
      throw new BusinessException(DisplayErrorCode.DISPLAY_INVITATION_PERMISSION_DENIED);
    }
  }

  private String invitationUrl(String rawToken) {
    String baseUrl = properties.baseUrl();
    if (baseUrl.endsWith("/")) {
      return baseUrl + rawToken;
    }
    return baseUrl + "/" + rawToken;
  }
}

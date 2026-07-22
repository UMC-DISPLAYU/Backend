package com.example.demo.domain.display.application.service;

import com.example.demo.domain.display.application.result.DisplayMemberListResult;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetDisplayMembersService {

  private final DisplayRepository displayRepository;

  public GetDisplayMembersService(DisplayRepository displayRepository) {
    this.displayRepository = displayRepository;
  }

  @Transactional(readOnly = true)
  public DisplayMemberListResult getMembers(Long requesterUserId, Long displayId) {
    Display display =
        displayRepository
            .findById(displayId)
            .orElseThrow(() -> new BusinessException(DisplayErrorCode.DISPLAY_NOT_FOUND));

    if (!display.hasAcceptedTeamMember(requesterUserId)) {
      throw new BusinessException(GlobalErrorCode.FORBIDDEN);
    }

    return DisplayMemberListResult.of(display.getId(), display.getTeamMembers());
  }
}

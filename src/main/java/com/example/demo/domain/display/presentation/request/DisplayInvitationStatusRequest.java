package com.example.demo.domain.display.presentation.request;

import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import jakarta.validation.constraints.NotNull;

public record DisplayInvitationStatusRequest(
    @NotNull(message = "초대 활성화 여부는 필수입니다.") Boolean invitationEnabled) {

  public void validateDisableOnly() {
    if (Boolean.TRUE.equals(invitationEnabled)) {
      throw new BusinessException(GlobalErrorCode.INVALID_REQUEST);
    }
  }
}

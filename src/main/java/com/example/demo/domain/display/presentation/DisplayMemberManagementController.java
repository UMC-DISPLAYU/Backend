package com.example.demo.domain.display.presentation;

import com.example.demo.domain.display.application.result.DisplayLeaderTransferResult;
import com.example.demo.domain.display.application.result.DisplayMemberRoleResult;
import com.example.demo.domain.display.application.service.ChangeDisplayMemberRoleService;
import com.example.demo.domain.display.application.service.TransferDisplayLeaderService;
import com.example.demo.domain.display.presentation.docs.DisplayMemberManagementControllerDocs;
import com.example.demo.domain.display.presentation.mapper.DisplayMemberManagementPresentationMapper;
import com.example.demo.domain.display.presentation.request.ChangeDisplayMemberRoleRequest;
import com.example.demo.domain.display.presentation.request.TransferDisplayLeaderRequest;
import com.example.demo.domain.display.presentation.response.DisplayLeaderTransferResponse;
import com.example.demo.domain.display.presentation.response.DisplayMemberRoleResponse;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/display/{displayId}")
@Tag(name = "DisplayMember")
public class DisplayMemberManagementController implements DisplayMemberManagementControllerDocs {

  private final ChangeDisplayMemberRoleService changeDisplayMemberRoleService;
  private final TransferDisplayLeaderService transferDisplayLeaderService;
  private final DisplayMemberManagementPresentationMapper mapper;

  @Override
  @PatchMapping("/members/{memberId}/role")
  public ApiResponseBody<DisplayMemberRoleResponse> changeRole(
      @PathVariable Long displayId,
      @PathVariable Long memberId,
      @Valid @RequestBody ChangeDisplayMemberRoleRequest request,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    DisplayMemberRoleResult result =
        changeDisplayMemberRoleService.changeRole(
            mapper.toCommand(requireUserId(user), displayId, memberId, request));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @Override
  @PostMapping("/leader-transfer")
  public ApiResponseBody<DisplayLeaderTransferResponse> transferLeader(
      @PathVariable Long displayId,
      @Valid @RequestBody TransferDisplayLeaderRequest request,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    DisplayLeaderTransferResult result =
        transferDisplayLeaderService.transfer(
            mapper.toCommand(requireUserId(user), displayId, request));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  private Long requireUserId(AuthUser user) {
    if (user == null) {
      throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    }
    return user.userId();
  }
}

package com.example.demo.domain.display.presentation;

import com.example.demo.domain.display.application.result.DisplayMemberInvitationResult;
import com.example.demo.domain.display.application.result.DisplayMemberListResult;
import com.example.demo.domain.display.application.result.MyDisplayInvitationListResult;
import com.example.demo.domain.display.application.service.AcceptDisplayInvitationService;
import com.example.demo.domain.display.application.service.GetDisplayMembersService;
import com.example.demo.domain.display.application.service.GetMyDisplayInvitationsService;
import com.example.demo.domain.display.application.service.InviteDisplayMemberService;
import com.example.demo.domain.display.application.service.RejectDisplayInvitationService;
import com.example.demo.domain.display.presentation.docs.DisplayMemberInvitationControllerDocs;
import com.example.demo.domain.display.presentation.mapper.DisplayMemberInvitationPresentationMapper;
import com.example.demo.domain.display.presentation.request.AcceptDisplayInvitationRequest;
import com.example.demo.domain.display.presentation.request.InviteDisplayMemberRequest;
import com.example.demo.domain.display.presentation.response.DisplayMemberInvitationResponse;
import com.example.demo.domain.display.presentation.response.DisplayMemberListResponse;
import com.example.demo.domain.display.presentation.response.MyDisplayInvitationListResponse;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "DisplayMember")
public class DisplayMemberInvitationController implements DisplayMemberInvitationControllerDocs {

  private final InviteDisplayMemberService inviteDisplayMemberService;
  private final AcceptDisplayInvitationService acceptDisplayInvitationService;
  private final RejectDisplayInvitationService rejectDisplayInvitationService;
  private final GetDisplayMembersService getDisplayMembersService;
  private final GetMyDisplayInvitationsService getMyDisplayInvitationsService;
  private final DisplayMemberInvitationPresentationMapper mapper;

  @Override
  @PostMapping("/display-invitations/displays/{displayId}")
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponseBody<DisplayMemberInvitationResponse> invite(
      @PathVariable Long displayId,
      @Valid @RequestBody InviteDisplayMemberRequest request,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    DisplayMemberInvitationResult result =
        inviteDisplayMemberService.invite(
            mapper.toCommand(requireUserId(user), displayId, request));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @Override
  @PostMapping("/display-invitations/{invitationId}/accept")
  public ApiResponseBody<DisplayMemberInvitationResponse> accept(
      @PathVariable Long invitationId,
      @Valid @RequestBody AcceptDisplayInvitationRequest request,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    DisplayMemberInvitationResult result =
        acceptDisplayInvitationService.accept(
            mapper.toAcceptCommand(requireUserId(user), invitationId, request));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @Override
  @PostMapping("/display-invitations/{invitationId}/reject")
  public ApiResponseBody<DisplayMemberInvitationResponse> reject(
      @PathVariable Long invitationId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    DisplayMemberInvitationResult result =
        rejectDisplayInvitationService.reject(
            mapper.toRejectCommand(requireUserId(user), invitationId));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @Override
  @GetMapping("/display/{displayId}/members")
  public ApiResponseBody<DisplayMemberListResponse> getMembers(
      @PathVariable Long displayId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    DisplayMemberListResult result =
        getDisplayMembersService.getMembers(requireUserId(user), displayId);
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @Override
  @GetMapping("/display-invitations/me")
  public ApiResponseBody<MyDisplayInvitationListResponse> getMyInvitations(
      @AuthenticationPrincipal AuthUser user, HttpServletRequest httpRequest) {
    MyDisplayInvitationListResult result =
        getMyDisplayInvitationsService.getInvitations(requireUserId(user));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  private Long requireUserId(AuthUser user) {
    if (user == null) {
      throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    }
    return user.userId();
  }
}

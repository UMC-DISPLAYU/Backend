package com.example.demo.domain.display.presentation.docs;

import com.example.demo.domain.display.presentation.request.InviteDisplayMemberRequest;
import com.example.demo.domain.display.presentation.response.DisplayMemberInvitationResponse;
import com.example.demo.domain.display.presentation.response.DisplayMemberListResponse;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;

public interface DisplayMemberInvitationControllerDocs {

  @Operation(summary = "전시 멤버 초대 생성", description = "전시 팀장이 특정 사용자를 전시 멤버로 초대합니다.")
  @SecurityRequirement(name = "Authorization")
  ApiResponseBody<DisplayMemberInvitationResponse> invite(
      Long displayId,
      InviteDisplayMemberRequest request,
      AuthUser user,
      HttpServletRequest httpRequest);

  @Operation(summary = "전시 멤버 초대 수락", description = "초대 대상자가 본인에게 온 전시 멤버 초대를 수락합니다.")
  @SecurityRequirement(name = "Authorization")
  ApiResponseBody<DisplayMemberInvitationResponse> accept(
      Long invitationId, AuthUser user, HttpServletRequest httpRequest);

  @Operation(summary = "전시 멤버 초대 거절", description = "초대 대상자가 본인에게 온 전시 멤버 초대를 거절합니다.")
  @SecurityRequirement(name = "Authorization")
  ApiResponseBody<DisplayMemberInvitationResponse> reject(
      Long invitationId, AuthUser user, HttpServletRequest httpRequest);

  @Operation(summary = "전시 멤버 목록 조회", description = "해당 전시의 수락된 전시 멤버 목록을 조회합니다.")
  @SecurityRequirement(name = "Authorization")
  ApiResponseBody<DisplayMemberListResponse> getMembers(
      Long displayId, AuthUser user, HttpServletRequest httpRequest);
}

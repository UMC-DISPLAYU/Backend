package com.example.demo.domain.display.presentation.docs;

import com.example.demo.domain.display.presentation.request.AcceptDisplayInvitationRequest;
import com.example.demo.domain.display.presentation.request.InviteDisplayMemberRequest;
import com.example.demo.domain.display.presentation.request.UpdateMyDisplayNicknameRequest;
import com.example.demo.domain.display.presentation.response.DisplayMemberInvitationResponse;
import com.example.demo.domain.display.presentation.response.DisplayMemberListResponse;
import com.example.demo.domain.display.presentation.response.GraduationDisplayResponse;
import com.example.demo.domain.display.presentation.response.MyDisplayInvitationListResponse;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;

public interface DisplayMemberInvitationControllerDocs {

  @Operation(summary = "전시 멤버 초대 생성", description = "전시 팀장이 특정 사용자를 전시 멤버로 초대합니다.")
  @SecurityRequirement(name = "Authorization")
  @ApiResponse(
      responseCode = "201",
      description = "전시 멤버 초대 생성 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Display invitation created",
                      value = INVITATION_SUCCESS_EXAMPLE)))
  ApiResponseBody<DisplayMemberInvitationResponse> invite(
      Long displayId,
      InviteDisplayMemberRequest request,
      AuthUser user,
      HttpServletRequest httpRequest);

  @Operation(summary = "전시 멤버 초대 수락", description = "초대 대상자가 본인에게 온 전시 멤버 초대를 수락합니다.")
  @SecurityRequirement(name = "Authorization")
  @ApiResponse(
      responseCode = "200",
      description = "전시 멤버 초대 수락 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Display invitation accepted",
                      value = INVITATION_ACCEPT_SUCCESS_EXAMPLE)))
  ApiResponseBody<DisplayMemberInvitationResponse> accept(
      Long invitationId,
      AcceptDisplayInvitationRequest request,
      AuthUser user,
      HttpServletRequest httpRequest);

  @Operation(summary = "전시 멤버 초대 거절", description = "초대 대상자가 본인에게 온 전시 멤버 초대를 거절합니다.")
  @SecurityRequirement(name = "Authorization")
  @ApiResponse(
      responseCode = "200",
      description = "전시 멤버 초대 거절 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Display invitation rejected",
                      value = INVITATION_REJECT_SUCCESS_EXAMPLE)))
  ApiResponseBody<DisplayMemberInvitationResponse> reject(
      Long invitationId, AuthUser user, HttpServletRequest httpRequest);

  @Operation(
      summary = "전시 멤버 목록 조회",
      description =
          "해당 전시의 삭제되지 않은 전시 멤버 목록을 조회합니다. 수락된 멤버와 초대 대기 중인 멤버를 함께 반환하며, "
              + "각 멤버의 로그인 가능 상태와 작가 인증 여부를 포함합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "전시 멤버 목록 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Display members success",
                      value = DISPLAY_MEMBERS_SUCCESS_EXAMPLE)))
  ApiResponseBody<DisplayMemberListResponse> getMembers(
      @Parameter(description = "전시 ID", example = "1") Long displayId,
      HttpServletRequest httpRequest);

  @Operation(summary = "내 전시 닉네임 수정", description = "현재 로그인 사용자의 특정 전시 내 닉네임을 수정합니다.")
  @SecurityRequirement(name = "Authorization")
  @ApiResponse(
      responseCode = "200",
      description = "내 전시 닉네임 수정 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Display nickname updated",
                      value = DISPLAY_NICKNAME_SUCCESS_EXAMPLE)))
  ApiResponseBody<DisplayMemberListResponse.TeamMemberResponse> updateMyDisplayNickname(
      UpdateMyDisplayNicknameRequest request, AuthUser user, HttpServletRequest httpRequest);

  @Operation(
      summary = "내 전시 멤버 초대 목록 조회",
      description = "현재 로그인 사용자가 받은 처리 대기 중인 전시 멤버 초대 목록을 조회합니다.")
  @SecurityRequirement(name = "Authorization")
  @ApiResponse(
      responseCode = "200",
      description = "내 전시 멤버 초대 목록 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "My display invitations success",
                      value = MY_INVITATIONS_SUCCESS_EXAMPLE)))
  ApiResponseBody<MyDisplayInvitationListResponse> getMyInvitations(
      AuthUser user, HttpServletRequest httpRequest);

  @Operation(
      summary = "내가 받은 전시 초대 조회",
      description =
          "현재 로그인 사용자가 받은 처리 대기 중인 전시 초대를 전시 카드 형태로 조회합니다. " + "수락/거절/삭제된 초대와 다른 사용자의 초대는 제외합니다.")
  @SecurityRequirement(name = "Authorization")
  @ApiResponse(
      responseCode = "200",
      description = "내가 받은 전시 초대 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Display invitation displays success",
                      value = DISPLAY_INVITATION_DISPLAYS_SUCCESS_EXAMPLE)))
  @ApiResponse(
      responseCode = "401",
      description = "인증 실패",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "Unauthorized", value = UNAUTHORIZED_EXAMPLE)))
  ApiResponseBody<GraduationDisplayResponse> getInvitationDisplays(
      AuthUser user, HttpServletRequest httpRequest);

  String DISPLAY_MEMBERS_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "displayId": 1,
            "memberAccept": [
              {
                "teamMemberId": 1,
                "userId": 10,
                "displayNickname": "도현",
                "loggedIn": true,
                "artistVerified": true,
                "accepted": true,
                "role": "TEAM_LEADER"
              }
            ],
            "memberPending": [
              {
                "teamMemberId": null,
                "userId": 11,
                "displayNickname": "민지",
                "loggedIn": true,
                "artistVerified": false,
                "accepted": false,
                "role": "TEAM_MEM"
              }
            ]
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-08-03T23:00:00",
          "path": "/api/v1/display/1/members"
        }
      }
      """;

  String DISPLAY_INVITATION_DISPLAYS_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "exhibitions": [
              {
                "displayId": 1,
                "title": "FORM 2026",
                "posterImageUrl": "https://cdn.displayu.com/posters/form.png",
                "schoolDepartmentName": "중앙대학교 디자인학부",
                "startedAt": "2026-05-28",
                "endedAt": "2026-06-05",
                "dayLeft": 3,
                "isArchived": false
              }
            ]
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-08-03T23:00:00",
          "path": "/api/v1/display-invitations"
        }
      }
      """;

  String INVITATION_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "invitationId": 1,
            "displayId": 1,
            "inviterUserId": 10,
            "inviteeUserId": 11,
            "status": "PENDING",
            "createdAt": "2026-08-03T23:00:00",
            "respondedAt": null
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-08-03T23:00:00",
          "path": "/api/v1/display-invitations/displays/1"
        }
      }
      """;

  String INVITATION_ACCEPT_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "invitationId": 1,
            "displayId": 1,
            "inviterUserId": 10,
            "inviteeUserId": 11,
            "status": "ACCEPTED",
            "createdAt": "2026-08-03T23:00:00",
            "respondedAt": "2026-08-04T09:00:00"
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-08-04T09:00:00",
          "path": "/api/v1/display-invitations/1/accept"
        }
      }
      """;

  String INVITATION_REJECT_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "invitationId": 1,
            "displayId": 1,
            "inviterUserId": 10,
            "inviteeUserId": 11,
            "status": "REJECTED",
            "createdAt": "2026-08-03T23:00:00",
            "respondedAt": "2026-08-04T09:00:00"
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-08-04T09:00:00",
          "path": "/api/v1/display-invitations/1/reject"
        }
      }
      """;

  String DISPLAY_NICKNAME_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "teamMemberId": 1,
            "userId": 10,
            "displayNickname": "도현",
            "loggedIn": true,
            "artistVerified": true,
            "accepted": true,
            "role": "TEAM_LEADER"
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-08-04T09:00:00",
          "path": "/api/v1/display/me/nickname"
        }
      }
      """;

  String MY_INVITATIONS_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "invitations": [
              {
                "invitationId": 1,
                "displayId": 1,
                "thumbnailUrl": "https://cdn.displayu.com/posters/form.png",
                "startDate": "2026-05-28",
                "endDate": "2026-06-05",
                "location": "서울시 종로구",
                "leaderName": "도현",
                "title": "FORM 2026",
                "placeName": "디유 갤러리"
              }
            ]
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-08-04T09:00:00",
          "path": "/api/v1/display-invitations/me"
        }
      }
      """;

  String UNAUTHORIZED_EXAMPLE =
      """
      {
        "resultType": "FAIL",
        "success": null,
        "error": {
          "code": "UNAUTHORIZED",
          "message": "인증이 필요합니다.",
          "details": null
        },
        "meta": {
          "timestamp": "2026-08-03T23:00:00",
          "path": "/api/v1/display-invitations"
        }
      }
      """;
}

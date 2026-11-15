package com.example.demo.domain.display.presentation.docs;

import com.example.demo.domain.display.presentation.request.AcceptDisplayInvitationRequest;
import com.example.demo.domain.display.presentation.request.InviteDisplayMemberRequest;
import com.example.demo.domain.display.presentation.request.UpdateMyDisplayNicknameRequest;
import com.example.demo.domain.display.presentation.response.DisplayInvitationDisplayResponse;
import com.example.demo.domain.display.presentation.response.DisplayMemberInvitationResponse;
import com.example.demo.domain.display.presentation.response.DisplayMemberListResponse;
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

  @Operation(
      summary = "전시 멤버 초대 취소",
      description = "전시 대표자가 대기 중인 초대를 취소합니다. 이미 취소된 초대의 재요청은 성공하며 수락·거절된 초대는 취소할 수 없습니다.")
  @SecurityRequirement(name = "Authorization")
  @ApiResponse(responseCode = "200", description = "초대 취소 성공", useReturnTypeSchema = true)
  @ApiResponse(responseCode = "403", description = "전시 대표자 권한 없음")
  @ApiResponse(responseCode = "404", description = "초대 또는 전시 없음")
  @ApiResponse(responseCode = "409", description = "수락 또는 거절된 초대")
  ApiResponseBody<DisplayMemberInvitationResponse> cancel(
      Long invitationId, AuthUser user, HttpServletRequest request);

  @Operation(
      summary = "전시 멤버 초대 생성",
      description = "기존 초대 권한을 가진 사용자가 작가 인증을 완료한 사용자를 전시 멤버로 초대합니다. 역할을 생략하면 TEAM_MEM으로 처리합니다.")
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
  @ApiResponse(
      responseCode = "400",
      description = "초대 대상 ID 오류 또는 자기 자신 초대",
      content =
          @Content(
              mediaType = "application/json",
              examples = {
                @ExampleObject(name = "입력값 오류", value = INVITE_BAD_REQUEST_EXAMPLE),
                @ExampleObject(name = "자기 자신 초대", value = INVITE_SELF_EXAMPLE)
              }))
  @ApiResponse(
      responseCode = "401",
      description = "인증 정보 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "인증 필요", value = INVITE_UNAUTHORIZED_EXAMPLE)))
  @ApiResponse(
      responseCode = "403",
      description = "초대 권한 없음 또는 초대 대상의 작가 인증 미완료",
      content =
          @Content(
              mediaType = "application/json",
              examples = {
                @ExampleObject(name = "초대 권한 없음", value = INVITE_FORBIDDEN_EXAMPLE),
                @ExampleObject(
                    name = "작가 인증 필요",
                    value = INVITE_ARTIST_VERIFICATION_REQUIRED_EXAMPLE)
              }))
  @ApiResponse(
      responseCode = "404",
      description = "전시 또는 초대 대상 사용자 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples = {
                @ExampleObject(name = "전시 없음", value = INVITE_DISPLAY_NOT_FOUND_EXAMPLE),
                @ExampleObject(name = "사용자 없음", value = INVITE_USER_NOT_FOUND_EXAMPLE)
              }))
  @ApiResponse(
      responseCode = "409",
      description = "탈퇴한 사용자, 기존 멤버 또는 대기 중인 초대",
      content =
          @Content(
              mediaType = "application/json",
              examples = {
                @ExampleObject(name = "탈퇴한 사용자", value = INVITE_WITHDRAWN_USER_EXAMPLE),
                @ExampleObject(name = "이미 전시 멤버", value = INVITE_ALREADY_MEMBER_EXAMPLE),
                @ExampleObject(name = "대기 중인 초대", value = INVITE_PENDING_EXISTS_EXAMPLE)
              }))
  ApiResponseBody<DisplayMemberInvitationResponse> invite(
      Long displayId,
      InviteDisplayMemberRequest request,
      AuthUser user,
      HttpServletRequest httpRequest);

  @Operation(
      summary = "전시 멤버 초대 수락",
      description = "작가 인증을 완료한 초대 대상자가 전시에서 사용할 닉네임을 입력해 본인에게 온 초대를 수락합니다.")
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
  @ApiResponse(
      responseCode = "400",
      description = "전시 닉네임 입력값 오류",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "닉네임 입력값 오류", value = ACCEPT_BAD_REQUEST_EXAMPLE)))
  @ApiResponse(
      responseCode = "401",
      description = "인증 정보 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "인증 필요", value = ACCEPT_UNAUTHORIZED_EXAMPLE)))
  @ApiResponse(
      responseCode = "403",
      description = "초대 대상 불일치 또는 수락 사용자의 작가 인증 미완료",
      content =
          @Content(
              mediaType = "application/json",
              examples = {
                @ExampleObject(name = "초대 대상 불일치", value = ACCEPT_INVITEE_MISMATCH_EXAMPLE),
                @ExampleObject(
                    name = "작가 인증 필요",
                    value = ACCEPT_ARTIST_VERIFICATION_REQUIRED_EXAMPLE)
              }))
  @ApiResponse(
      responseCode = "404",
      description = "초대 또는 사용자 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples = {
                @ExampleObject(name = "초대 없음", value = ACCEPT_NOT_FOUND_EXAMPLE),
                @ExampleObject(name = "사용자 없음", value = ACCEPT_USER_NOT_FOUND_EXAMPLE)
              }))
  @ApiResponse(
      responseCode = "409",
      description = "이미 처리된 초대, 기존 멤버 또는 동시 요청 충돌",
      content =
          @Content(
              mediaType = "application/json",
              examples = {
                @ExampleObject(name = "이미 수락된 초대", value = ACCEPT_ALREADY_ACCEPTED_EXAMPLE),
                @ExampleObject(name = "이미 거절된 초대", value = ACCEPT_ALREADY_REJECTED_EXAMPLE),
                @ExampleObject(name = "이미 전시 멤버", value = ACCEPT_ALREADY_MEMBER_EXAMPLE),
                @ExampleObject(name = "동시 멤버 생성", value = ACCEPT_CONCURRENT_MEMBER_EXAMPLE)
              }))
  ApiResponseBody<DisplayMemberInvitationResponse> accept(
      Long invitationId,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "전시에서 사용할 팀원 닉네임",
              required = true,
              content =
                  @Content(
                      mediaType = "application/json",
                      examples =
                          @ExampleObject(
                              name = "초대 수락 요청",
                              value = INVITATION_ACCEPT_REQUEST_EXAMPLE)))
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
  @ApiResponse(
      responseCode = "401",
      description = "인증 정보 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "인증 필요", value = REJECT_UNAUTHORIZED_EXAMPLE)))
  @ApiResponse(
      responseCode = "403",
      description = "초대 대상 불일치",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(name = "초대 대상 불일치", value = REJECT_INVITEE_MISMATCH_EXAMPLE)))
  @ApiResponse(
      responseCode = "404",
      description = "초대 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "초대 없음", value = REJECT_NOT_FOUND_EXAMPLE)))
  @ApiResponse(
      responseCode = "409",
      description = "이미 처리되었거나 처리할 수 없는 초대",
      content =
          @Content(
              mediaType = "application/json",
              examples = {
                @ExampleObject(name = "이미 수락된 초대", value = REJECT_ALREADY_ACCEPTED_EXAMPLE),
                @ExampleObject(name = "이미 거절된 초대", value = REJECT_ALREADY_REJECTED_EXAMPLE),
                @ExampleObject(name = "처리할 수 없는 상태", value = REJECT_INVALID_STATUS_EXAMPLE)
              }))
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
  @ApiResponse(
      responseCode = "404",
      description = "전시 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "전시 없음", value = MEMBERS_DISPLAY_NOT_FOUND_EXAMPLE)))
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
  @ApiResponse(
      responseCode = "400",
      description = "전시 ID 또는 닉네임 입력값 오류",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "입력값 오류", value = NICKNAME_BAD_REQUEST_EXAMPLE)))
  @ApiResponse(
      responseCode = "401",
      description = "인증 정보 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "인증 필요", value = NICKNAME_UNAUTHORIZED_EXAMPLE)))
  @ApiResponse(
      responseCode = "404",
      description = "수락된 전시 멤버 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(name = "전시 멤버 없음", value = NICKNAME_MEMBER_NOT_FOUND_EXAMPLE)))
  ApiResponseBody<DisplayMemberListResponse.TeamMemberResponse> updateMyDisplayNickname(
      UpdateMyDisplayNicknameRequest request, AuthUser user, HttpServletRequest httpRequest);

  @Operation(
      summary = "내 전시 멤버 초대 목록 조회",
      description = "현재 로그인 사용자가 받은 처리 대기 중인 전시 멤버 초대 목록을 조회합니다.")
  @SecurityRequirement(name = "Authorization")
  @ApiResponse(
      responseCode = "200",
      useReturnTypeSchema = true,
      description = "내 전시 멤버 초대 목록 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "My display invitations success",
                      value = MY_INVITATIONS_SUCCESS_EXAMPLE)))
  @ApiResponse(
      responseCode = "401",
      description = "인증 정보 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(name = "인증 필요", value = MY_INVITATIONS_UNAUTHORIZED_EXAMPLE)))
  ApiResponseBody<MyDisplayInvitationListResponse> getMyInvitations(
      AuthUser user, HttpServletRequest httpRequest);

  @Operation(
      summary = "내가 받은 전시 초대 조회",
      description =
          "현재 로그인 사용자가 받은 처리 대기 중인 전시 초대를 전시 카드 형태로 조회합니다. " + "수락/거절/삭제된 초대와 다른 사용자의 초대는 제외합니다.")
  @SecurityRequirement(name = "Authorization")
  @ApiResponse(
      responseCode = "200",
      useReturnTypeSchema = true,
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
  ApiResponseBody<DisplayInvitationDisplayResponse> getInvitationDisplays(
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
                "posterCrop": {"x": 0.1, "y": 0.2, "w": 0.8, "h": 0.7},
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

  String INVITATION_ACCEPT_REQUEST_EXAMPLE =
      """
      {
        "displayNickname": "전시용 닉네임"
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

  String ACCEPT_ARTIST_VERIFICATION_REQUIRED_EXAMPLE =
      """
      {
        "resultType": "FAIL",
        "success": null,
        "error": {
          "code": "ARTIST_VERIFICATION_REQUIRED",
          "message": "작가 인증이 완료된 사용자만 이용할 수 있습니다.",
          "details": null
        },
        "meta": {
          "timestamp": "2026-11-01T12:00:00",
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
                "posterCrop": {"x": 0.1, "y": 0.2, "w": 0.8, "h": 0.7},
                "startDate": "2026-05-28",
                "endDate": "2026-06-05",
                "location": "서울시 종로구",
                "userNickname": "도현",
                "leaderName": "전시 리더",
                "title": "FORM 2026",
                "schoolDepartmentName": "중앙대학교 디자인학부",
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

  String INVITE_BAD_REQUEST_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"INVALID_INPUT_VALUE\",\"message\":\"입력값이 올바르지 않습니다.\",\"details\":[{\"field\":\"inviteeUserId\",\"message\":\"0보다 커야 합니다\"}]},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display-invitations/displays/1\"}}";
  String INVITE_SELF_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"SELF_INVITATION_NOT_ALLOWED\",\"message\":\"자기 자신은 초대할 수 없습니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display-invitations/displays/1\"}}";
  String INVITE_UNAUTHORIZED_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"UNAUTHORIZED\",\"message\":\"인증이 필요합니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display-invitations/displays/1\"}}";
  String INVITE_FORBIDDEN_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"DISPLAY_INVITATION_PERMISSION_DENIED\",\"message\":\"전시 멤버를 초대할 권한이 없습니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display-invitations/displays/1\"}}";
  String INVITE_ARTIST_VERIFICATION_REQUIRED_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"ARTIST_VERIFICATION_REQUIRED\",\"message\":\"작가 인증이 완료된 사용자만 이용할 수 있습니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display-invitations/displays/1\"}}";
  String INVITE_DISPLAY_NOT_FOUND_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"DISPLAY_NOT_FOUND\",\"message\":\"전시를 찾을 수 없습니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display-invitations/displays/999\"}}";
  String INVITE_USER_NOT_FOUND_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"USER_NOT_FOUND\",\"message\":\"사용자를 찾을 수 없습니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display-invitations/displays/1\"}}";
  String INVITE_WITHDRAWN_USER_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"ALREADY_WITHDRAWN_USER\",\"message\":\"이미 탈퇴한 사용자입니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display-invitations/displays/1\"}}";
  String INVITE_ALREADY_MEMBER_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"ALREADY_DISPLAY_MEMBER\",\"message\":\"이미 전시 멤버인 사용자입니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display-invitations/displays/1\"}}";
  String INVITE_PENDING_EXISTS_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"PENDING_DISPLAY_INVITATION_EXISTS\",\"message\":\"처리 대기 중인 전시 멤버 초대가 이미 존재합니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display-invitations/displays/1\"}}";

  String ACCEPT_BAD_REQUEST_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"INVALID_INPUT_VALUE\",\"message\":\"입력값이 올바르지 않습니다.\",\"details\":[{\"field\":\"displayNickname\",\"message\":\"공백일 수 없습니다\"}]},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display-invitations/1/accept\"}}";
  String ACCEPT_UNAUTHORIZED_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"UNAUTHORIZED\",\"message\":\"인증이 필요합니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display-invitations/1/accept\"}}";
  String ACCEPT_INVITEE_MISMATCH_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"DISPLAY_INVITATION_INVITEE_MISMATCH\",\"message\":\"해당 초대의 대상 사용자가 아닙니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display-invitations/1/accept\"}}";
  String ACCEPT_NOT_FOUND_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"DISPLAY_MEMBER_INVITATION_NOT_FOUND\",\"message\":\"전시 멤버 초대를 찾을 수 없습니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display-invitations/999/accept\"}}";
  String ACCEPT_USER_NOT_FOUND_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"USER_NOT_FOUND\",\"message\":\"사용자를 찾을 수 없습니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display-invitations/1/accept\"}}";
  String ACCEPT_ALREADY_ACCEPTED_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"DISPLAY_INVITATION_ALREADY_ACCEPTED\",\"message\":\"이미 수락된 전시 멤버 초대입니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display-invitations/1/accept\"}}";
  String ACCEPT_ALREADY_REJECTED_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"DISPLAY_INVITATION_ALREADY_REJECTED\",\"message\":\"이미 거절된 전시 멤버 초대입니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display-invitations/1/accept\"}}";
  String ACCEPT_ALREADY_MEMBER_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"ALREADY_DISPLAY_MEMBER\",\"message\":\"이미 전시 멤버인 사용자입니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display-invitations/1/accept\"}}";
  String ACCEPT_CONCURRENT_MEMBER_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"DISPLAY_MEMBER_CONCURRENTLY_CREATED\",\"message\":\"동시 요청으로 전시 멤버가 이미 생성되었습니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display-invitations/1/accept\"}}";

  String REJECT_UNAUTHORIZED_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"UNAUTHORIZED\",\"message\":\"인증이 필요합니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display-invitations/1/reject\"}}";
  String REJECT_INVITEE_MISMATCH_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"DISPLAY_INVITATION_INVITEE_MISMATCH\",\"message\":\"해당 초대의 대상 사용자가 아닙니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display-invitations/1/reject\"}}";
  String REJECT_NOT_FOUND_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"DISPLAY_MEMBER_INVITATION_NOT_FOUND\",\"message\":\"전시 멤버 초대를 찾을 수 없습니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display-invitations/999/reject\"}}";
  String REJECT_ALREADY_ACCEPTED_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"DISPLAY_INVITATION_ALREADY_ACCEPTED\",\"message\":\"이미 수락된 전시 멤버 초대입니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display-invitations/1/reject\"}}";
  String REJECT_ALREADY_REJECTED_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"DISPLAY_INVITATION_ALREADY_REJECTED\",\"message\":\"이미 거절된 전시 멤버 초대입니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display-invitations/1/reject\"}}";
  String REJECT_INVALID_STATUS_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"INVALID_DISPLAY_INVITATION_STATUS\",\"message\":\"처리할 수 없는 전시 멤버 초대 상태입니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display-invitations/1/reject\"}}";

  String MEMBERS_DISPLAY_NOT_FOUND_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"DISPLAY_NOT_FOUND\",\"message\":\"전시를 찾을 수 없습니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display/999/members\"}}";
  String NICKNAME_BAD_REQUEST_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"INVALID_INPUT_VALUE\",\"message\":\"입력값이 올바르지 않습니다.\",\"details\":[{\"field\":\"displayNickname\",\"message\":\"공백일 수 없습니다\"}]},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display/me/nickname\"}}";
  String NICKNAME_UNAUTHORIZED_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"UNAUTHORIZED\",\"message\":\"인증이 필요합니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display/me/nickname\"}}";
  String NICKNAME_MEMBER_NOT_FOUND_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"DISPLAY_MEMBER_NOT_FOUND\",\"message\":\"전시 멤버를 찾을 수 없습니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display/me/nickname\"}}";
  String MY_INVITATIONS_UNAUTHORIZED_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"UNAUTHORIZED\",\"message\":\"인증이 필요합니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display-invitations/me\"}}";
}

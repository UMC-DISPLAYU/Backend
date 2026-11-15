package com.example.demo.domain.display.presentation.docs;

import com.example.demo.domain.display.presentation.request.ChangeDisplayMemberRoleRequest;
import com.example.demo.domain.display.presentation.request.TransferDisplayLeaderRequest;
import com.example.demo.domain.display.presentation.response.DisplayLeaderTransferResponse;
import com.example.demo.domain.display.presentation.response.DisplayMemberRoleResponse;
import com.example.demo.domain.display.presentation.response.RemoveDisplayMemberResponse;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;

public interface DisplayMemberManagementControllerDocs {

  @Operation(
      summary = "전시 팀원 내보내기",
      description = "전시 대표자가 수락 완료된 일반 팀원 또는 관리자를 내보내고, 해당 사용자가 작가로 연결된 작품을 삭제합니다.")
  @SecurityRequirement(name = "Authorization")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "팀원 내보내기 성공",
        content =
            @Content(
                mediaType = "application/json",
                examples = @ExampleObject(name = "팀원 내보내기 성공", value = MEMBER_REMOVAL_SUCCESS))),
    @ApiResponse(
        responseCode = "401",
        description = "인증 정보 없음",
        content =
            @Content(
                mediaType = "application/json",
                examples = @ExampleObject(name = "인증 필요", value = MEMBER_REMOVAL_UNAUTHORIZED))),
    @ApiResponse(
        responseCode = "403",
        description = "전시 대표자 권한 없음",
        content =
            @Content(
                mediaType = "application/json",
                examples = @ExampleObject(name = "대표자 권한 없음", value = MEMBER_REMOVAL_FORBIDDEN))),
    @ApiResponse(
        responseCode = "404",
        description = "전시 또는 대상 팀원 없음",
        content =
            @Content(
                mediaType = "application/json",
                examples = {
                  @ExampleObject(name = "전시 없음", value = MEMBER_REMOVAL_DISPLAY_NOT_FOUND),
                  @ExampleObject(name = "팀원 없음", value = MEMBER_REMOVAL_MEMBER_NOT_FOUND)
                })),
    @ApiResponse(
        responseCode = "409",
        description = "전시 대표자 내보내기 시도",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(name = "대표자 내보내기 불가", value = MEMBER_REMOVAL_LEADER_CONFLICT)))
  })
  ApiResponseBody<RemoveDisplayMemberResponse> removeMember(
      Long displayId, Long memberId, AuthUser user, HttpServletRequest httpRequest);

  @Operation(
      summary = "전시 관리자 지정·해제",
      description = "전시 대표자가 수락 완료된 일반 팀원과 관리자의 역할을 TEAM_MEM 또는 MANAGER로 변경합니다.")
  @SecurityRequirement(name = "Authorization")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "역할 변경 성공",
        content =
            @Content(
                mediaType = "application/json",
                examples = @ExampleObject(name = "역할 변경 성공", value = ROLE_CHANGE_SUCCESS))),
    @ApiResponse(
        responseCode = "400",
        description = "요청 본문 또는 역할 값 오류",
        content =
            @Content(
                mediaType = "application/json",
                examples = @ExampleObject(name = "역할 값 오류", value = ROLE_CHANGE_BAD_REQUEST))),
    @ApiResponse(
        responseCode = "401",
        description = "인증 정보 없음",
        content =
            @Content(
                mediaType = "application/json",
                examples = @ExampleObject(name = "인증 필요", value = ROLE_CHANGE_UNAUTHORIZED))),
    @ApiResponse(
        responseCode = "403",
        description = "전시 대표자 권한 없음",
        content =
            @Content(
                mediaType = "application/json",
                examples = @ExampleObject(name = "대표자 권한 없음", value = ROLE_CHANGE_FORBIDDEN))),
    @ApiResponse(
        responseCode = "404",
        description = "전시 또는 대상 팀원 없음",
        content =
            @Content(
                mediaType = "application/json",
                examples = {
                  @ExampleObject(name = "전시 없음", value = ROLE_CHANGE_DISPLAY_NOT_FOUND),
                  @ExampleObject(name = "팀원 없음", value = ROLE_CHANGE_MEMBER_NOT_FOUND)
                })),
    @ApiResponse(
        responseCode = "409",
        description = "허용되지 않는 역할 변경 또는 동시 요청 충돌",
        content =
            @Content(
                mediaType = "application/json",
                examples = @ExampleObject(name = "역할 변경 불가", value = ROLE_CHANGE_CONFLICT)))
  })
  ApiResponseBody<DisplayMemberRoleResponse> changeRole(
      Long displayId,
      Long memberId,
      ChangeDisplayMemberRoleRequest request,
      AuthUser user,
      HttpServletRequest httpRequest);

  @Operation(
      summary = "전시 대표자 위임",
      description = "현재 대표자가 수락 완료된 팀원에게 대표자 역할을 즉시 위임하고 본인은 관리자로 변경됩니다.")
  @SecurityRequirement(name = "Authorization")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "대표자 위임 성공",
        content =
            @Content(
                mediaType = "application/json",
                examples = @ExampleObject(name = "대표자 위임 성공", value = LEADER_TRANSFER_SUCCESS))),
    @ApiResponse(
        responseCode = "400",
        description = "요청값 오류 또는 자기 자신에게 위임",
        content =
            @Content(
                mediaType = "application/json",
                examples = {
                  @ExampleObject(name = "입력값 오류", value = LEADER_TRANSFER_BAD_REQUEST),
                  @ExampleObject(name = "자기 자신에게 위임", value = LEADER_TRANSFER_SELF)
                })),
    @ApiResponse(
        responseCode = "401",
        description = "인증 정보 없음",
        content =
            @Content(
                mediaType = "application/json",
                examples = @ExampleObject(name = "인증 필요", value = LEADER_TRANSFER_UNAUTHORIZED))),
    @ApiResponse(
        responseCode = "403",
        description = "전시 대표자 권한 없음",
        content =
            @Content(
                mediaType = "application/json",
                examples = @ExampleObject(name = "대표자 권한 없음", value = LEADER_TRANSFER_FORBIDDEN))),
    @ApiResponse(
        responseCode = "404",
        description = "전시 또는 대상 팀원 없음",
        content =
            @Content(
                mediaType = "application/json",
                examples = {
                  @ExampleObject(name = "전시 없음", value = LEADER_TRANSFER_DISPLAY_NOT_FOUND),
                  @ExampleObject(name = "팀원 없음", value = LEADER_TRANSFER_MEMBER_NOT_FOUND)
                })),
    @ApiResponse(
        responseCode = "409",
        description = "대표자 위임 상태 또는 동시 요청 충돌",
        content =
            @Content(
                mediaType = "application/json",
                examples = @ExampleObject(name = "대표자 위임 충돌", value = LEADER_TRANSFER_CONFLICT)))
  })
  ApiResponseBody<DisplayLeaderTransferResponse> transferLeader(
      Long displayId,
      TransferDisplayLeaderRequest request,
      AuthUser user,
      HttpServletRequest httpRequest);

  String MEMBER_REMOVAL_SUCCESS =
      """
      { "resultType": "SUCCESS", "success": { "data": { "displayId": 1, "removedMemberId": 12, "removedUserId": 103, "deletedArtworkCount": 2 } }, "error": null, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/display/1/members/12" } }
      """;
  String ROLE_CHANGE_SUCCESS =
      """
      { "resultType": "SUCCESS", "success": { "data": { "displayId": 1, "teamMemberId": 11, "userId": 102, "role": "MANAGER" } }, "error": null, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/display/1/members/11/role" } }
      """;
  String LEADER_TRANSFER_SUCCESS =
      """
      { "resultType": "SUCCESS", "success": { "data": { "displayId": 1, "previousLeader": { "teamMemberId": 10, "userId": 101, "role": "MANAGER" }, "newLeader": { "teamMemberId": 12, "userId": 103, "role": "TEAM_LEADER" } } }, "error": null, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/display/1/leader-transfer" } }
      """;

  String MEMBER_REMOVAL_UNAUTHORIZED =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"UNAUTHORIZED\",\"message\":\"인증이 필요합니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display/1/members/12\"}}";
  String MEMBER_REMOVAL_FORBIDDEN =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"FORBIDDEN\",\"message\":\"접근 권한이 없습니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display/1/members/12\"}}";
  String MEMBER_REMOVAL_DISPLAY_NOT_FOUND =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"DISPLAY_NOT_FOUND\",\"message\":\"전시를 찾을 수 없습니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display/1/members/12\"}}";
  String MEMBER_REMOVAL_MEMBER_NOT_FOUND =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"DISPLAY_MEMBER_NOT_FOUND\",\"message\":\"전시 멤버를 찾을 수 없습니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display/1/members/12\"}}";
  String MEMBER_REMOVAL_LEADER_CONFLICT =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"DISPLAY_LEADER_REMOVAL_NOT_ALLOWED\",\"message\":\"전시 대표자는 내보낼 수 없습니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display/1/members/10\"}}";

  String ROLE_CHANGE_BAD_REQUEST =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"MALFORMED_JSON\",\"message\":\"요청 본문 형식이 올바르지 않습니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display/1/members/11/role\"}}";
  String ROLE_CHANGE_UNAUTHORIZED =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"UNAUTHORIZED\",\"message\":\"인증이 필요합니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display/1/members/11/role\"}}";
  String ROLE_CHANGE_FORBIDDEN =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"FORBIDDEN\",\"message\":\"접근 권한이 없습니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display/1/members/11/role\"}}";
  String ROLE_CHANGE_DISPLAY_NOT_FOUND =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"DISPLAY_NOT_FOUND\",\"message\":\"전시를 찾을 수 없습니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display/999/members/11/role\"}}";
  String ROLE_CHANGE_MEMBER_NOT_FOUND =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"DISPLAY_MEMBER_NOT_FOUND\",\"message\":\"전시 멤버를 찾을 수 없습니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display/1/members/999/role\"}}";
  String ROLE_CHANGE_CONFLICT =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"INVALID_DISPLAY_MEMBER_ROLE_CHANGE\",\"message\":\"허용되지 않는 전시 멤버 역할 변경입니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display/1/members/11/role\"}}";

  String LEADER_TRANSFER_BAD_REQUEST =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"INVALID_INPUT_VALUE\",\"message\":\"입력값이 올바르지 않습니다.\",\"details\":[{\"field\":\"targetMemberId\",\"message\":\"0보다 커야 합니다\"}]},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display/1/leader-transfer\"}}";
  String LEADER_TRANSFER_SELF =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"SELF_LEADER_TRANSFER_NOT_ALLOWED\",\"message\":\"대표자 역할을 자기 자신에게 위임할 수 없습니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display/1/leader-transfer\"}}";
  String LEADER_TRANSFER_UNAUTHORIZED =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"UNAUTHORIZED\",\"message\":\"인증이 필요합니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display/1/leader-transfer\"}}";
  String LEADER_TRANSFER_FORBIDDEN =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"FORBIDDEN\",\"message\":\"접근 권한이 없습니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display/1/leader-transfer\"}}";
  String LEADER_TRANSFER_DISPLAY_NOT_FOUND =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"DISPLAY_NOT_FOUND\",\"message\":\"전시를 찾을 수 없습니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display/999/leader-transfer\"}}";
  String LEADER_TRANSFER_MEMBER_NOT_FOUND =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"DISPLAY_MEMBER_NOT_FOUND\",\"message\":\"전시 멤버를 찾을 수 없습니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display/1/leader-transfer\"}}";
  String LEADER_TRANSFER_CONFLICT =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"DISPLAY_LEADER_TRANSFER_CONFLICT\",\"message\":\"대표자 위임 중 충돌이 발생했습니다. 다시 시도해주세요.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/display/1/leader-transfer\"}}";
}

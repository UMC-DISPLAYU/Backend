package com.example.demo.domain.display.presentation.docs;

import com.example.demo.domain.display.presentation.request.ChangeDisplayMemberRoleRequest;
import com.example.demo.domain.display.presentation.request.TransferDisplayLeaderRequest;
import com.example.demo.domain.display.presentation.response.DisplayLeaderTransferResponse;
import com.example.demo.domain.display.presentation.response.DisplayMemberRoleResponse;
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
      summary = "전시 관리자 지정·해제",
      description = "전시 대표자가 수락 완료된 일반 팀원과 관리자의 역할을 TEAM_MEM 또는 MANAGER로 변경합니다.")
  @SecurityRequirement(name = "Authorization")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "역할 변경 성공",
        content = @Content(examples = @ExampleObject(value = ROLE_CHANGE_SUCCESS))),
    @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 역할 값"),
    @ApiResponse(responseCode = "401", description = "인증 실패"),
    @ApiResponse(responseCode = "403", description = "대표자 권한 없음"),
    @ApiResponse(responseCode = "404", description = "전시 또는 대상 팀원 없음"),
    @ApiResponse(responseCode = "409", description = "허용되지 않는 역할 변경")
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
        content = @Content(examples = @ExampleObject(value = LEADER_TRANSFER_SUCCESS))),
    @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 자기 자신에게 위임"),
    @ApiResponse(responseCode = "401", description = "인증 실패"),
    @ApiResponse(responseCode = "403", description = "대표자 권한 없음"),
    @ApiResponse(responseCode = "404", description = "전시 또는 대상 팀원 없음"),
    @ApiResponse(responseCode = "409", description = "허용되지 않는 위임 또는 동시 요청 충돌")
  })
  ApiResponseBody<DisplayLeaderTransferResponse> transferLeader(
      Long displayId,
      TransferDisplayLeaderRequest request,
      AuthUser user,
      HttpServletRequest httpRequest);

  String ROLE_CHANGE_SUCCESS =
      """
      {
        "resultType": "SUCCESS",
        "success": {"data": {"displayId": 1, "teamMemberId": 11, "userId": 12, "role": "MANAGER"}},
        "error": null
      }
      """;

  String LEADER_TRANSFER_SUCCESS =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "displayId": 1,
            "previousLeader": {"teamMemberId": 10, "userId": 1, "role": "MANAGER"},
            "newLeader": {"teamMemberId": 12, "userId": 3, "role": "TEAM_LEADER"}
          }
        },
        "error": null
      }
      """;
}

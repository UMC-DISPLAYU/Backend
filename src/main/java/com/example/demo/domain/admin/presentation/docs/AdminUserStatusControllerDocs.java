package com.example.demo.domain.admin.presentation.docs;

import com.example.demo.domain.admin.presentation.request.ChangeUserStatusRequest;
import com.example.demo.domain.admin.presentation.response.UserAccountStatusResponse;
import com.example.demo.domain.admin.presentation.response.UserStatusResponse;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "Admin User Status", description = "관리자 회원 계정 정지 관리 API")
@SecurityRequirement(name = "Authorization")
public interface AdminUserStatusControllerDocs {

  @Operation(summary = "회원 계정 정지 상태 조회", description = "관리자 인증이 필요합니다. ACTIVE 계정의 정지 정보는 null입니다.")
  @ApiResponse(
      responseCode = "200",
      description = "조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = UserAccountStatusResponse.class),
              examples = {
                @ExampleObject(name = "정지 계정", value = SUSPENDED_STATUS_EXAMPLE),
                @ExampleObject(name = "정상 계정", value = ACTIVE_STATUS_EXAMPLE)
              }))
  @ApiResponse(
      responseCode = "401",
      description = "관리자 인증 필요",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(value = UNAUTHORIZED_EXAMPLE)))
  @ApiResponse(
      responseCode = "403",
      description = "관리자 권한 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(value = FORBIDDEN_EXAMPLE)))
  @ApiResponse(
      responseCode = "404",
      description = "회원 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(value = USER_NOT_FOUND_EXAMPLE)))
  ApiResponseBody<UserAccountStatusResponse> getStatus(
      @Parameter(hidden = true) AuthUser operator,
      @Parameter(description = "조회할 회원 ID", example = "12") Long userId,
      HttpServletRequest request);

  @Operation(
      summary = "회원 계정 정지·해제",
      description =
          "관리자 인증이 필요합니다. SUSPENDED는 계정 정지, ACTIVE는 관리자 수동 해제입니다. "
              + "expectedVersion과 idempotencyKey는 현재 지원하지 않습니다.")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      required = true,
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ChangeUserStatusRequest.class),
              examples = {
                @ExampleObject(name = "계정 정지", value = SUSPEND_REQUEST_EXAMPLE),
                @ExampleObject(name = "정지 해제", value = RELEASE_REQUEST_EXAMPLE)
              }))
  @ApiResponse(
      responseCode = "200",
      description = "처리 성공",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = UserStatusResponse.class),
              examples = {
                @ExampleObject(name = "계정 정지", value = SUSPEND_RESPONSE_EXAMPLE),
                @ExampleObject(name = "정지 해제", value = RELEASE_RESPONSE_EXAMPLE)
              }))
  @ApiResponse(
      responseCode = "400",
      description = "입력값 오류",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(value = INVALID_INPUT_EXAMPLE)))
  @ApiResponse(
      responseCode = "401",
      description = "관리자 인증 필요",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(value = UNAUTHORIZED_EXAMPLE)))
  @ApiResponse(
      responseCode = "403",
      description = "관리자 권한 없음 또는 관리자 계정 변경 시도",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(value = FORBIDDEN_EXAMPLE)))
  @ApiResponse(
      responseCode = "404",
      description = "회원 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(value = USER_NOT_FOUND_EXAMPLE)))
  @ApiResponse(
      responseCode = "409",
      description = "이미 같은 상태인 계정",
      content =
          @Content(
              mediaType = "application/json",
              examples = {
                @ExampleObject(name = "이미 정지", value = ALREADY_SUSPENDED_EXAMPLE),
                @ExampleObject(name = "이미 해제", value = ALREADY_ACTIVE_EXAMPLE)
              }))
  ApiResponseBody<UserStatusResponse> changeStatus(
      @Parameter(hidden = true) AuthUser operator,
      @Parameter(description = "처리할 회원 ID", example = "12") Long userId,
      ChangeUserStatusRequest body,
      HttpServletRequest request);

  String SUSPENDED_STATUS_EXAMPLE =
      """
      {"resultType":"SUCCESS","success":{"data":{"userId":12,"accountStatus":"SUSPENDED","suspendedAt":"2026-11-15T14:30:00Z","operatorId":3,"reason":"운영 정책 위반"}},"error":null,"meta":{"timestamp":"2026-11-15T14:30:00","path":"/api/v1/admin/users/12/account-status"}}
      """;
  String ACTIVE_STATUS_EXAMPLE =
      """
      {"resultType":"SUCCESS","success":{"data":{"userId":12,"accountStatus":"ACTIVE","suspendedAt":null,"operatorId":null,"reason":null}},"error":null,"meta":{"timestamp":"2026-11-15T15:00:00","path":"/api/v1/admin/users/12/account-status"}}
      """;
  String SUSPEND_REQUEST_EXAMPLE =
      """
      {"accountStatus":"SUSPENDED","reason":"운영 정책 위반"}
      """;
  String RELEASE_REQUEST_EXAMPLE =
      """
      {"accountStatus":"ACTIVE","reason":"관리자 검토 완료"}
      """;
  String SUSPEND_RESPONSE_EXAMPLE =
      """
      {"resultType":"SUCCESS","success":{"data":{"userId":12,"accountStatus":"SUSPENDED","processedAt":"2026-11-15T14:30:00Z"}},"error":null,"meta":{"timestamp":"2026-11-15T14:30:00","path":"/api/v1/admin/users/12/account-status"}}
      """;
  String RELEASE_RESPONSE_EXAMPLE =
      """
      {"resultType":"SUCCESS","success":{"data":{"userId":12,"accountStatus":"ACTIVE","processedAt":"2026-11-15T15:00:00Z"}},"error":null,"meta":{"timestamp":"2026-11-15T15:00:00","path":"/api/v1/admin/users/12/account-status"}}
      """;
  String UNAUTHORIZED_EXAMPLE =
      """
      {"resultType":"FAIL","success":null,"error":{"code":"UNAUTHORIZED","message":"인증이 필요합니다.","details":null},"meta":{"timestamp":"2026-11-15T14:30:00","path":"/api/v1/admin/users/12/account-status"}}
      """;
  String FORBIDDEN_EXAMPLE =
      """
      {"resultType":"FAIL","success":null,"error":{"code":"FORBIDDEN","message":"접근 권한이 없습니다.","details":null},"meta":{"timestamp":"2026-11-15T14:30:00","path":"/api/v1/admin/users/12/account-status"}}
      """;
  String USER_NOT_FOUND_EXAMPLE =
      """
      {"resultType":"FAIL","success":null,"error":{"code":"USER_NOT_FOUND","message":"사용자를 찾을 수 없습니다.","details":null},"meta":{"timestamp":"2026-11-15T14:30:00","path":"/api/v1/admin/users/12/account-status"}}
      """;
  String INVALID_INPUT_EXAMPLE =
      """
      {"resultType":"FAIL","success":null,"error":{"code":"INVALID_INPUT_VALUE","message":"입력값이 올바르지 않습니다.","details":null},"meta":{"timestamp":"2026-11-15T14:30:00","path":"/api/v1/admin/users/12/account-status"}}
      """;
  String ALREADY_SUSPENDED_EXAMPLE =
      """
      {"resultType":"FAIL","success":null,"error":{"code":"ALREADY_SUSPENDED_USER","message":"이미 정지된 사용자입니다.","details":null},"meta":{"timestamp":"2026-11-15T14:30:00","path":"/api/v1/admin/users/12/account-status"}}
      """;
  String ALREADY_ACTIVE_EXAMPLE =
      """
      {"resultType":"FAIL","success":null,"error":{"code":"ALREADY_ACTIVE_USER","message":"이미 정지 해제된 사용자입니다.","details":null},"meta":{"timestamp":"2026-11-15T14:30:00","path":"/api/v1/admin/users/12/account-status"}}
      """;
}

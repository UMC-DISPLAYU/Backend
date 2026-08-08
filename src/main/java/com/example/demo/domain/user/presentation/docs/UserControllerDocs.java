package com.example.demo.domain.user.presentation.docs;

import static com.example.demo.domain.user.presentation.docs.UserApiDocs.ALREADY_WITHDRAWN_USER_EXAMPLE;
import static com.example.demo.domain.user.presentation.docs.UserApiDocs.CHANGE_NICKNAME_SUCCESS_EXAMPLE;
import static com.example.demo.domain.user.presentation.docs.UserApiDocs.DUPLICATE_NICKNAME_EXAMPLE;
import static com.example.demo.domain.user.presentation.docs.UserApiDocs.EXPIRED_ACCESS_TOKEN_EXAMPLE;
import static com.example.demo.domain.user.presentation.docs.UserApiDocs.INVALID_ACCESS_TOKEN_EXAMPLE;
import static com.example.demo.domain.user.presentation.docs.UserApiDocs.INVALID_NICKNAME_FORMAT_EXAMPLE;
import static com.example.demo.domain.user.presentation.docs.UserApiDocs.MY_ARTIST_PROFILE_NOT_FOUND_EXAMPLE;
import static com.example.demo.domain.user.presentation.docs.UserApiDocs.MY_ARTIST_PROFILE_SUCCESS_EXAMPLE;
import static com.example.demo.domain.user.presentation.docs.UserApiDocs.MY_USER_SUCCESS_EXAMPLE;
import static com.example.demo.domain.user.presentation.docs.UserApiDocs.NICKNAME_CHANGE_NOT_ALLOWED_EXAMPLE;
import static com.example.demo.domain.user.presentation.docs.UserApiDocs.NICKNAME_CHECK_SUCCESS_EXAMPLE;
import static com.example.demo.domain.user.presentation.docs.UserApiDocs.NICKNAME_EXPIRED_ACCESS_TOKEN_EXAMPLE;
import static com.example.demo.domain.user.presentation.docs.UserApiDocs.NICKNAME_INVALID_ACCESS_TOKEN_EXAMPLE;
import static com.example.demo.domain.user.presentation.docs.UserApiDocs.NICKNAME_USER_NOT_FOUND_EXAMPLE;
import static com.example.demo.domain.user.presentation.docs.UserApiDocs.USER_ARTIST_PROFILE_NOT_FOUND_EXAMPLE;
import static com.example.demo.domain.user.presentation.docs.UserApiDocs.USER_ARTIST_PROFILE_SUCCESS_EXAMPLE;
import static com.example.demo.domain.user.presentation.docs.UserApiDocs.USER_NOT_FOUND_EXAMPLE;
import static com.example.demo.domain.user.presentation.docs.UserApiDocs.WITHDRAW_USER_SUCCESS_EXAMPLE;

import com.example.demo.domain.artist.presentation.request.UpdateArtistProfileRequest;
import com.example.demo.domain.artist.presentation.response.MyArtistProfileResponse;
import com.example.demo.domain.artist.presentation.response.UpdateArtistProfileResponse;
import com.example.demo.domain.artist.presentation.response.UserArtistProfileResponse;
import com.example.demo.domain.user.presentation.request.ChangeNicknameRequest;
import com.example.demo.domain.user.presentation.request.UpdateMyProfileRequest;
import com.example.demo.domain.user.presentation.request.UserSearchRequest;
import com.example.demo.domain.user.presentation.response.ChangeNicknameResponse;
import com.example.demo.domain.user.presentation.response.MyUserResponse;
import com.example.demo.domain.user.presentation.response.NicknameCheckResponse;
import com.example.demo.domain.user.presentation.response.UpdateMyProfileResponse;
import com.example.demo.domain.user.presentation.response.UserSearchResponse;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Tag(name = "User", description = "사용자 인증 API")
public interface UserControllerDocs {

  @Operation(
      summary = "닉네임 기반 사용자 검색",
      description =
          """
          닉네임에 검색어가 포함된 활성 사용자를 조회합니다.

          - 검색어의 앞뒤 공백은 제거합니다.
          - 영문 검색은 대소문자를 구분하지 않습니다.
          - 작가 인증 여부와 관계없이 조회합니다.
          - 탈퇴한 사용자는 제외합니다.
          - 닉네임 오름차순, 사용자 ID 오름차순으로 정렬합니다.
          - 검색 결과는 최대 20명까지 반환합니다.
          """)
  @ApiResponse(responseCode = "200", description = "사용자 검색 성공")
  @ApiResponse(responseCode = "400", description = "닉네임 검색어 누락 또는 공백")
  @ApiResponse(responseCode = "401", description = "Access Token 검증 실패")
  @ApiResponse(responseCode = "404", description = "해당 닉네임을 가진 사용자 없음")
  @SecurityRequirement(name = "Authorization")
  ApiResponseBody<List<UserSearchResponse>> searchUsers(
      UserSearchRequest request, AuthUser user, HttpServletRequest httpRequest);

  @Operation(summary = "내 사용자 정보 조회", description = "로그인한 사용자의 기본 정보를 조회합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "내 사용자 정보 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "조회 성공", value = MY_USER_SUCCESS_EXAMPLE)))
  @ApiResponse(responseCode = "401", description = "Access Token verification failed")
  @SecurityRequirement(name = "Authorization")
  ApiResponseBody<MyUserResponse> getMe(AuthUser user, HttpServletRequest httpRequest);

  @Operation(summary = "내 프로필 수정", description = "프로필 이미지와 닉네임을 한 번에 수정합니다.")
  @ApiResponse(responseCode = "200", description = "내 프로필 수정 성공")
  @ApiResponse(responseCode = "400", description = "프로필 이미지 URL 또는 닉네임 형식 오류")
  @ApiResponse(responseCode = "401", description = "Access Token 검증 실패")
  @ApiResponse(responseCode = "404", description = "사용자 없음")
  @ApiResponse(responseCode = "409", description = "닉네임 중복 또는 변경 주기 제한")
  @SecurityRequirement(name = "Authorization")
  ApiResponseBody<UpdateMyProfileResponse> updateMe(
      AuthUser user, UpdateMyProfileRequest request, HttpServletRequest httpRequest);

  @Operation(
      summary = "회원 탈퇴",
      description =
          """
          로그인한 사용자를 즉시 탈퇴 처리합니다.

          - 이미 탈퇴한 사용자는 `ALREADY_WITHDRAWN_USER`를 반환합니다.
          - 존재하지 않는 사용자는 `USER_NOT_FOUND`를 반환합니다.
          """)
  @ApiResponse(
      responseCode = "200",
      description = "회원 탈퇴 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "탈퇴 성공", value = WITHDRAW_USER_SUCCESS_EXAMPLE)))
  @ApiResponse(
      responseCode = "409",
      description = "이미 탈퇴한 사용자",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(name = "이미 탈퇴한 사용자", value = ALREADY_WITHDRAWN_USER_EXAMPLE)))
  @ApiResponse(
      responseCode = "404",
      description = "사용자 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "사용자 없음", value = USER_NOT_FOUND_EXAMPLE)))
  @ApiResponse(
      responseCode = "401",
      description = "Access Token 검증 실패",
      content =
          @Content(
              mediaType = "application/json",
              examples = {
                @ExampleObject(name = "유효하지 않은 토큰", value = INVALID_ACCESS_TOKEN_EXAMPLE),
                @ExampleObject(name = "만료된 토큰", value = EXPIRED_ACCESS_TOKEN_EXAMPLE)
              }))
  @SecurityRequirement(name = "Authorization")
  ApiResponseBody<Void> withdraw(AuthUser user, HttpServletRequest httpRequest);

  @Operation(
      summary = "닉네임 변경",
      description =
          """
          로그인한 사용자의 닉네임을 변경합니다.

          - 닉네임은 한글, 영문, 숫자로 2~15자여야 합니다.
          - 공백과 특수문자는 사용할 수 없습니다.
          - 마지막 닉네임 변경 후 30일이 지나야 다시 변경할 수 있습니다.
          """)
  @ApiResponse(
      responseCode = "200",
      description = "닉네임 변경 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "변경 성공", value = CHANGE_NICKNAME_SUCCESS_EXAMPLE)))
  @ApiResponse(
      responseCode = "400",
      description = "닉네임 형식 오류",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "형식 오류", value = INVALID_NICKNAME_FORMAT_EXAMPLE)))
  @ApiResponse(
      responseCode = "409",
      description = "닉네임 중복 또는 변경 주기 제한",
      content =
          @Content(
              mediaType = "application/json",
              examples = {
                @ExampleObject(name = "닉네임 중복", value = DUPLICATE_NICKNAME_EXAMPLE),
                @ExampleObject(name = "변경 주기 제한", value = NICKNAME_CHANGE_NOT_ALLOWED_EXAMPLE)
              }))
  @ApiResponse(
      responseCode = "404",
      description = "사용자 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "사용자 없음", value = NICKNAME_USER_NOT_FOUND_EXAMPLE)))
  @ApiResponse(
      responseCode = "401",
      description = "Access Token 검증 실패",
      content =
          @Content(
              mediaType = "application/json",
              examples = {
                @ExampleObject(name = "유효하지 않은 토큰", value = NICKNAME_INVALID_ACCESS_TOKEN_EXAMPLE),
                @ExampleObject(name = "만료된 토큰", value = NICKNAME_EXPIRED_ACCESS_TOKEN_EXAMPLE)
              }))
  @SecurityRequirement(name = "Authorization")
  ApiResponseBody<ChangeNicknameResponse> changeNickname(
      AuthUser user, ChangeNicknameRequest request, HttpServletRequest httpRequest);

  @Operation(summary = "내 작가 프로필 조회", description = "로그인한 사용자의 작가 프로필을 조회합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "내 작가 프로필 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "조회 성공", value = MY_ARTIST_PROFILE_SUCCESS_EXAMPLE)))
  @ApiResponse(
      responseCode = "404",
      description = "작가 프로필 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(name = "프로필 없음", value = MY_ARTIST_PROFILE_NOT_FOUND_EXAMPLE)))
  @ApiResponse(responseCode = "401", description = "Access Token verification failed")
  @SecurityRequirement(name = "Authorization")
  ApiResponseBody<MyArtistProfileResponse> getMyArtistProfile(
      AuthUser user, HttpServletRequest httpRequest);

  @Operation(summary = "본인 작가 프로필 수정", description = "작가 인증이 완료된 사용자의 작가 프로필을 수정합니다.")
  @ApiResponse(responseCode = "200", description = "작가 프로필 수정 성공")
  @ApiResponse(responseCode = "400", description = "요청 필드 검증 실패")
  @ApiResponse(responseCode = "401", description = "Access Token 검증 실패")
  @ApiResponse(responseCode = "403", description = "작가 인증이 완료되지 않은 사용자")
  @ApiResponse(responseCode = "404", description = "사용자 또는 작가 프로필이 존재하지 않음")
  @ApiResponse(responseCode = "409", description = "작가명 중복")
  @SecurityRequirement(name = "Authorization")
  ApiResponseBody<UpdateArtistProfileResponse> updateMyArtistProfile(
      AuthUser user, UpdateArtistProfileRequest request, HttpServletRequest httpRequest);

  @Operation(summary = "특정 사용자 작가 프로필 조회", description = "특정 사용자의 작가 프로필을 조회합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "특정 사용자 작가 프로필 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(name = "조회 성공", value = USER_ARTIST_PROFILE_SUCCESS_EXAMPLE)))
  @ApiResponse(
      responseCode = "404",
      description = "작가 프로필 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(name = "프로필 없음", value = USER_ARTIST_PROFILE_NOT_FOUND_EXAMPLE)))
  ApiResponseBody<UserArtistProfileResponse> getUserArtistProfile(
      @Parameter(description = "조회할 사용자 ID", required = true, example = "15") Long userId,
      HttpServletRequest httpRequest);

  @Operation(
      summary = "닉네임 중복 확인",
      description =
          """
                  닉네임의 사용 가능 여부를 확인합니다.

                  - 닉네임은 한글, 영문, 숫자로 2~15자여야 합니다.
                  - 공백과 특수문자는 사용할 수 없습니다.
                  - 이미 사용 중인 닉네임은 isAvailable=false를 반환합니다.
                  """)
  @Parameter(name = "nickname", description = "중복 확인할 닉네임", required = true, example = "maya041225")
  @ApiResponse(
      responseCode = "200",
      description = "닉네임 중복 확인 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "사용 가능", value = NICKNAME_CHECK_SUCCESS_EXAMPLE)))
  ApiResponseBody<NicknameCheckResponse> checkNickname(
      String nickname, HttpServletRequest httpRequest);
}

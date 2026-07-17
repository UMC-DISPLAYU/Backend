package com.example.demo.domain.user.presentation.docs;

import static com.example.demo.domain.user.presentation.docs.UserApiDocs.MY_ARTIST_PROFILE_NOT_FOUND_EXAMPLE;
import static com.example.demo.domain.user.presentation.docs.UserApiDocs.MY_ARTIST_PROFILE_SUCCESS_EXAMPLE;
import static com.example.demo.domain.user.presentation.docs.UserApiDocs.MY_USER_SUCCESS_EXAMPLE;
import static com.example.demo.domain.user.presentation.docs.UserApiDocs.USER_ARTIST_PROFILE_NOT_FOUND_EXAMPLE;
import static com.example.demo.domain.user.presentation.docs.UserApiDocs.USER_ARTIST_PROFILE_SUCCESS_EXAMPLE;

import com.example.demo.domain.artist.presentation.response.MyArtistProfileResponse;
import com.example.demo.domain.artist.presentation.response.UserArtistProfileResponse;
import com.example.demo.domain.user.presentation.response.MyUserResponse;
import com.example.demo.domain.user.presentation.response.NicknameCheckResponse;
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

@Tag(name = "User", description = "사용자 인증 API")
public interface UserControllerDocs {

  @Operation(summary = "내 사용자 정보 조회", description = "로그인한 사용자의 기본 정보를 조회합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "내 사용자 정보 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "조회 성공", value = MY_USER_SUCCESS_EXAMPLE)))
  @SecurityRequirement(name = "Authorization")
  ApiResponseBody<MyUserResponse> getMe(AuthUser user, HttpServletRequest httpRequest);

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
  @SecurityRequirement(name = "Authorization")
  ApiResponseBody<MyArtistProfileResponse> getMyArtistProfile(
      AuthUser user, HttpServletRequest httpRequest);

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
                    회원가입 또는 닉네임 변경 과정에서 닉네임 사용 가능 여부를 확인합니다.

                    - 닉네임이 사용 가능하면 isAvailable=true를 반환합니다.
                    - 이미 사용 중인 닉네임이면 isAvailable=false를 반환합니다.
                    - 닉네임 형식이 올바르지 않은 경우 실패 응답을 반환합니다.
                    """)
  @Parameter(name = "nickname", description = "중복 확인할 닉네임", required = true, example = "maya041225")
  ApiResponseBody<NicknameCheckResponse> checkNickname(
      String nickname, HttpServletRequest httpRequest);
}

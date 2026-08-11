package com.example.demo.domain.artist.presentation.docs;

import com.example.demo.domain.artist.presentation.request.UpdateArtistProfileRequest;
import com.example.demo.domain.artist.presentation.response.MyArtistProfileResponse;
import com.example.demo.domain.artist.presentation.response.UpdateArtistProfileResponse;
import com.example.demo.domain.artist.presentation.response.UserArtistProfileResponse;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;

public interface ArtistProfileControllerDocs {

  public static final String TAG_NAME = "ArtistProfile";
  public static final String TAG_DESCRIPTION = "작가 프로필 API";
  public static final String CREATE_SUMMARY = "작가 프로필 생성";
  public static final String CREATE_DESCRIPTION =
      "학교 이메일 인증을 완료한 사용자가 작가명과 활동 분야를 설정합니다. 활동 분야는 중복 없이 최대 2개까지 입력할 수 있습니다.";
  public static final String CREATE_REQUEST_DESCRIPTION = "작가 프로필 생성 요청";
  public static final String CREATE_REQUEST_EXAMPLE_NAME = "Artist profile create request";
  public static final String CREATE_SUCCESS_DESCRIPTION = "작가 프로필 생성 성공";
  public static final String CREATE_SUCCESS_EXAMPLE_NAME = "Artist profile create success";

  public static final String CREATE_REQUEST_EXAMPLE =
      """
      {
        "artistName": "홍길동",
        "activityFields": ["PAINTING", "ILLUSTRATION"]
      }
      """;

  public static final String CREATE_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "artistProfileId": 1,
            "artistName": "홍길동",
            "schoolEmail": "user@university.ac.kr",
            "univName": "덕성여자대학교",
            "activityFields": ["PAINTING", "ILLUSTRATION"]
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-07-16T12:00:00",
          "path": "/api/v1/artists/me/artist-profile"
        }
      }
      """;

  String MY_ARTIST_PROFILE_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "artistName": "김지원",
            "profileImageUrl": "https://cdn.example.com/images/user/profile.jpg",
            "introduction": "시각과 공간의 관계를 탐구하는 작가입니다.",
            "status": "VERIFIED",
            "schoolName": "중앙대학교 서울캠퍼스",
            "externalLink": "https://portfolio.maya.com",
            "fields": ["PAINTING", "ILLUSTRATION"]
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-07-16T20:00:00",
          "path": "/api/v1/artists/me/artist-profile"
        }
      }
      """;

  String USER_ARTIST_PROFILE_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "artistName": "김지원",
            "schoolName": "중앙대학교 서울캠퍼스",
            "profileImageUrl": "https://cdn.example.com/images/user/profile.jpg",
            "introduction": "시각과 공간의 관계를 탐구하는 작가입니다.",
            "externalLink": "https://portfolio.maya.com",
            "fields": ["PAINTING", "ILLUSTRATION"]
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-07-16T20:00:00",
          "path": "/api/v1/artists/15/artist-profile"
        }
      }
      """;

  String MY_ARTIST_PROFILE_NOT_FOUND_EXAMPLE =
      """
      {
        "resultType": "FAIL",
        "success": null,
        "error": {
          "code": "ARTIST_PROFILE_NOT_FOUND",
          "message": "등록된 작가 프로필이 없습니다.",
          "details": null
        },
        "meta": {
          "timestamp": "2026-07-16T20:00:00",
          "path": "/api/v1/artists/me/artist-profile"
        }
      }
      """;

  String USER_ARTIST_PROFILE_NOT_FOUND_EXAMPLE =
      """
      {
        "resultType": "FAIL",
        "success": null,
        "error": {
          "code": "ARTIST_PROFILE_NOT_FOUND",
          "message": "해당 사용자의 작가 프로필이 존재하지 않습니다.",
          "details": null
        },
        "meta": {
          "timestamp": "2026-07-16T20:00:00",
          "path": "/api/v1/artists/15/artist-profile"
        }
      }
      """;

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
}

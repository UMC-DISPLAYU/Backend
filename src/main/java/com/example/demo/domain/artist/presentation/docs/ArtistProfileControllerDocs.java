package com.example.demo.domain.artist.presentation.docs;

import com.example.demo.domain.artist.presentation.request.CreateArtistProfileRequest;
import com.example.demo.domain.artist.presentation.request.UpdateArtistProfileRequest;
import com.example.demo.domain.artist.presentation.response.CreateArtistProfileResponse;
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

  String TAG_NAME = "ArtistProfile";
  String TAG_DESCRIPTION = "작가 프로필 API";

  @Operation(summary = "작가 프로필 생성", description = "학교 이메일 인증을 완료한 사용자가 작가명과 활동 분야를 설정합니다.")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      required = true,
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "생성 요청", value = CREATE_REQUEST_EXAMPLE)))
  @ApiResponse(
      responseCode = "201",
      description = "작가 프로필 생성 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "생성 성공", value = CREATE_SUCCESS_EXAMPLE)))
  @ApiResponse(
      responseCode = "400",
      description = "요청 형식 또는 활동 분야 오류",
      content =
          @Content(
              mediaType = "application/json",
              examples = {
                @ExampleObject(name = "입력값 검증 실패", value = CREATE_INVALID_INPUT_EXAMPLE),
                @ExampleObject(name = "활동 분야 오류", value = INVALID_ACTIVITY_FIELDS_EXAMPLE)
              }))
  @ApiResponse(
      responseCode = "401",
      description = "인증 정보 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "인증 필요", value = UNAUTHORIZED_EXAMPLE)))
  @ApiResponse(
      responseCode = "403",
      description = "학교 이메일 인증 미완료",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(name = "학교 인증 필요", value = PROFILE_CREATION_FORBIDDEN_EXAMPLE)))
  @ApiResponse(
      responseCode = "404",
      description = "사용자 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "사용자 없음", value = USER_NOT_FOUND_EXAMPLE)))
  @ApiResponse(
      responseCode = "409",
      description = "작가 프로필 또는 작가명 중복",
      content =
          @Content(
              mediaType = "application/json",
              examples = {
                @ExampleObject(name = "프로필 중복", value = PROFILE_ALREADY_EXISTS_EXAMPLE),
                @ExampleObject(name = "작가명 중복", value = DUPLICATE_ARTIST_NAME_EXAMPLE)
              }))
  @SecurityRequirement(name = "Authorization")
  ApiResponseBody<CreateArtistProfileResponse> create(
      CreateArtistProfileRequest request, AuthUser user, HttpServletRequest httpRequest);

  @Operation(summary = "내 작가 프로필 조회", description = "로그인한 사용자의 작가 프로필을 조회합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "내 작가 프로필 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "조회 성공", value = MY_PROFILE_SUCCESS_EXAMPLE)))
  @ApiResponse(
      responseCode = "401",
      description = "인증 정보 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "인증 필요", value = UNAUTHORIZED_EXAMPLE)))
  @ApiResponse(
      responseCode = "404",
      description = "작가 프로필 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "프로필 없음", value = MY_PROFILE_NOT_FOUND_EXAMPLE)))
  @SecurityRequirement(name = "Authorization")
  ApiResponseBody<MyArtistProfileResponse> getMyArtistProfile(
      AuthUser user, HttpServletRequest httpRequest);

  @Operation(summary = "내 작가 프로필 수정", description = "인증된 사용자의 작가 프로필을 수정합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "작가 프로필 수정 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "수정 성공", value = UPDATE_SUCCESS_EXAMPLE)))
  @ApiResponse(
      responseCode = "400",
      description = "입력값, 활동 분야, URL 또는 학교 정보 오류",
      content =
          @Content(
              mediaType = "application/json",
              examples = {
                @ExampleObject(name = "입력값 검증 실패", value = UPDATE_INVALID_INPUT_EXAMPLE),
                @ExampleObject(name = "활동 분야 오류", value = INVALID_ACTIVITY_FIELDS_EXAMPLE),
                @ExampleObject(name = "지원하지 않는 학교", value = UNSUPPORTED_UNIVERSITY_EXAMPLE)
              }))
  @ApiResponse(
      responseCode = "401",
      description = "인증 정보 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "인증 필요", value = UNAUTHORIZED_EXAMPLE)))
  @ApiResponse(
      responseCode = "403",
      description = "작가 인증 미완료",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(name = "작가 인증 필요", value = ARTIST_VERIFICATION_REQUIRED_EXAMPLE)))
  @ApiResponse(
      responseCode = "404",
      description = "사용자 또는 작가 프로필 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples = {
                @ExampleObject(name = "사용자 없음", value = USER_NOT_FOUND_EXAMPLE),
                @ExampleObject(name = "프로필 없음", value = UPDATE_PROFILE_NOT_FOUND_EXAMPLE)
              }))
  @ApiResponse(
      responseCode = "409",
      description = "작가명 중복",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "작가명 중복", value = DUPLICATE_ARTIST_NAME_EXAMPLE)))
  @SecurityRequirement(name = "Authorization")
  ApiResponseBody<UpdateArtistProfileResponse> updateMyArtistProfile(
      AuthUser user, UpdateArtistProfileRequest request, HttpServletRequest httpRequest);

  @Operation(summary = "특정 사용자 작가 프로필 조회", description = "사용자 ID로 공개 작가 프로필을 조회합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "작가 프로필 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "조회 성공", value = USER_PROFILE_SUCCESS_EXAMPLE)))
  @ApiResponse(
      responseCode = "404",
      description = "작가 프로필 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "프로필 없음", value = USER_PROFILE_NOT_FOUND_EXAMPLE)))
  ApiResponseBody<UserArtistProfileResponse> getUserArtistProfile(
      @Parameter(description = "조회할 사용자 ID", required = true, example = "15") Long userId,
      HttpServletRequest httpRequest);

  String CREATE_REQUEST_EXAMPLE =
      """
      { "artistName": "김디유", "activityFields": ["PAINTING", "ILLUSTRATION"] }
      """;

  String CREATE_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": { "data": {
          "artistProfileId": 21,
          "artistName": "김디유",
          "schoolEmail": "diyu@dongguk.edu",
          "univName": "동국대학교",
          "activityFields": ["PAINTING", "ILLUSTRATION"],
          "isVerified": true
        }},
        "error": null,
        "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/artists/me/artist-profile" }
      }
      """;

  String MY_PROFILE_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": { "data": {
          "profileImageUrl": "https://cdn.displayu.co.kr/artists/21/profile.jpg",
          "artistName": "김디유",
          "introduction": "회화와 디지털 아트를 작업하는 작가입니다.",
          "status": "VERIFIED",
          "schoolName": "동국대학교",
          "externalLink": "https://portfolio.example.com/diyu",
          "instagramLink": "https://www.instagram.com/diyu.artist",
          "fields": ["PAINTING", "ILLUSTRATION"],
          "isVerified": true
        }},
        "error": null,
        "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/artists/me/artist-profile" }
      }
      """;

  String UPDATE_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": { "data": {
          "profileImageUrl": "https://cdn.displayu.co.kr/artists/21/profile.jpg",
          "artistName": "김디유",
          "introduction": "회화와 디지털 아트를 작업하는 작가입니다.",
          "fields": ["PAINTING", "ILLUSTRATION"],
          "externalLink": "https://portfolio.example.com/diyu",
          "instagramLink": "https://www.instagram.com/diyu.artist",
          "univName": "동국대학교"
        }},
        "error": null,
        "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/artists/me/artist-profile" }
      }
      """;

  String USER_PROFILE_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": { "data": {
          "profileImageUrl": "https://cdn.displayu.co.kr/artists/21/profile.jpg",
          "artistName": "김디유",
          "introduction": "회화와 디지털 아트를 작업하는 작가입니다.",
          "schoolName": "동국대학교",
          "externalLink": "https://portfolio.example.com/diyu",
          "instagramLink": "https://www.instagram.com/diyu.artist",
          "fields": ["PAINTING", "ILLUSTRATION"]
        }},
        "error": null,
        "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/artists/15/artist-profile" }
      }
      """;

  String CREATE_INVALID_INPUT_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "INVALID_INPUT_VALUE", "message": "입력값이 올바르지 않습니다.", "details": [{ "field": "artistName", "message": "작가명은 한글, 영문, 숫자로 2~15자여야 하며 공백과 특수문자는 사용할 수 없습니다." }] }, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/artists/me/artist-profile" } }
      """;
  String UPDATE_INVALID_INPUT_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "INVALID_INPUT_VALUE", "message": "입력값이 올바르지 않습니다.", "details": [{ "field": "artistName", "message": "작가명은 한글, 영문, 숫자로 2~15자여야 하며 공백과 특수문자는 사용할 수 없습니다." }] }, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/artists/me/artist-profile" } }
      """;
  String UNAUTHORIZED_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "UNAUTHORIZED", "message": "인증이 필요합니다.", "details": null }, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/artists/me/artist-profile" } }
      """;
  String PROFILE_CREATION_FORBIDDEN_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "ARTIST_PROFILE_REQUIRES_VERIFIED_USER", "message": "학교 이메일 인증을 완료한 사용자만 작가 프로필을 생성할 수 있습니다.", "details": null }, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/artists/me/artist-profile" } }
      """;
  String ARTIST_VERIFICATION_REQUIRED_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "ARTIST_VERIFICATION_REQUIRED", "message": "작가 인증이 완료된 사용자만 이용할 수 있습니다.", "details": null }, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/artists/me/artist-profile" } }
      """;
  String USER_NOT_FOUND_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "USER_NOT_FOUND", "message": "사용자를 찾을 수 없습니다.", "details": null }, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/artists/me/artist-profile" } }
      """;
  String INVALID_ACTIVITY_FIELDS_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "INVALID_ACTIVITY_FIELDS", "message": "활동 분야는 중복 없이 최대 2개까지 선택할 수 있습니다.", "details": null }, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/artists/me/artist-profile" } }
      """;
  String PROFILE_ALREADY_EXISTS_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "ARTIST_PROFILE_ALREADY_EXISTS", "message": "이미 작가 프로필이 존재합니다.", "details": null }, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/artists/me/artist-profile" } }
      """;
  String DUPLICATE_ARTIST_NAME_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "DUPLICATE_ARTIST_NAME", "message": "이미 사용 중인 작가명입니다.", "details": null }, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/artists/me/artist-profile" } }
      """;
  String MY_PROFILE_NOT_FOUND_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "ARTIST_PROFILE_NOT_FOUND", "message": "등록된 작가 프로필이 없습니다.", "details": null }, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/artists/me/artist-profile" } }
      """;
  String UPDATE_PROFILE_NOT_FOUND_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "ARTIST_PROFILE_NOT_FOUND", "message": "작가 프로필을 찾을 수 없습니다.", "details": null }, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/artists/me/artist-profile" } }
      """;
  String UNSUPPORTED_UNIVERSITY_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "UNSUPPORTED_UNIVERSITY", "message": "지원하지 않는 대학교입니다.", "details": null }, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/artists/me/artist-profile" } }
      """;
  String USER_PROFILE_NOT_FOUND_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "ARTIST_PROFILE_NOT_FOUND", "message": "해당 사용자의 작가 프로필이 존재하지 않습니다.", "details": null }, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/artists/15/artist-profile" } }
      """;
}

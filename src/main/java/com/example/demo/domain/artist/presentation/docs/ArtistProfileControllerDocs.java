package com.example.demo.domain.artist.presentation.docs;

public final class ArtistProfileControllerDocs {

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
          "path": "/api/v1/artists/me/profile"
        }
      }
      """;

  private ArtistProfileControllerDocs() {}
}

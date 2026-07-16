package com.example.demo.domain.user.presentation.docs;

public final class UserApiDocs {

  public static final String SIGNUP_REQUEST_EXAMPLE =
      """
      {
        "nickname": "maya",
        "agreements": [
          { "agreeId": 1, "isAgreed": true },
          { "agreeId": 2, "isAgreed": true },
          { "agreeId": 3, "isAgreed": false }
        ]
      }
      """;

  public static final String SIGNUP_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "user": {
              "id": 1,
              "provider": "Kakao",
              "name": "홍길동",
              "nickname": "maya",
              "socialEmail": "user@gmail.com",
              "schoolEmail": null,
              "isVerified": false
            },
            "accessToken": "eyJhbGciOi...",
            "refreshToken": "eyJhbGciOi..."
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-07-16T20:00:00",
          "path": "/api/v1/auth/signup"
        }
      }
      """;

  public static final String MY_USER_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "id": 1,
            "provider": "Kakao",
            "name": "홍길동",
            "nickname": "User1",
            "isVerified": false,
            "socialEmail": "user@gmail.com",
            "schoolEmail": null
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-07-16T20:00:00",
          "path": "/api/v1/users/me"
        }
      }
      """;

  public static final String MY_ARTIST_PROFILE_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "artistName": "김지원",
            "status": "VERIFIED",
            "schoolName": "중앙대학교 서울캠퍼스",
            "portfolioUrl": "https://portfolio.example.com",
            "fields": ["PAINTING", "ILLUSTRATION"]
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-07-16T20:00:00",
          "path": "/api/v1/users/me/artist-profile"
        }
      }
      """;

  public static final String USER_ARTIST_PROFILE_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "artistName": "김지원",
            "schoolName": "중앙대학교 서울캠퍼스",
            "portfolioUrl": "https://portfolio.example.com",
            "fields": ["PAINTING", "ILLUSTRATION"]
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-07-16T20:00:00",
          "path": "/api/v1/users/15/artist-profile"
        }
      }
      """;

  public static final String MY_ARTIST_PROFILE_NOT_FOUND_EXAMPLE =
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
          "path": "/api/v1/users/me/artist-profile"
        }
      }
      """;

  public static final String USER_ARTIST_PROFILE_NOT_FOUND_EXAMPLE =
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
          "path": "/api/v1/users/15/artist-profile"
        }
      }
      """;

  private UserApiDocs() {}
}

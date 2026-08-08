package com.example.demo.domain.user.presentation.docs;

public final class UserApiDocs {

  public static final String SIGNUP_REQUEST_EXAMPLE =
      """
      {
        "nickname": "displayu01",
        "agreements": [
          {
            "code": "TERMS_OF_SERVICE",
            "version": "1.0"
          },
          {
            "code": "PRIVACY_COLLECTION_USE",
            "version": "1.0"
          }
        ],
        "isOver14": true
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
              "nickname": "maya01",
              "socialEmail": "user@gmail.com",
              "schoolEmail": null,
              "isVerified": false
            },
            "accessToken": "eyJhbGciOi..."
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

  public static final String WITHDRAW_USER_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": { "data": null },
        "error": null,
        "meta": {
          "timestamp": "2026-07-19T20:00:00",
          "path": "/api/v1/users/me"
        }
      }
      """;

  public static final String CHANGE_NICKNAME_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "nickname": "User2",
            "nextNicknameChangeAvailableAt": "2026-08-18T20:00:00"
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-07-19T20:00:00",
          "path": "/api/v1/users/me/nickname"
        }
      }
      """;

  public static final String ALREADY_WITHDRAWN_USER_EXAMPLE =
      """
      {
        "resultType": "FAIL", "success": null,
        "error": { "code": "ALREADY_WITHDRAWN_USER", "message": "이미 탈퇴한 사용자입니다.", "details": null },
        "meta": { "timestamp": "2026-07-19T20:00:00", "path": "/api/v1/users/me" }
      }
      """;

  public static final String USER_NOT_FOUND_EXAMPLE =
      """
      {
        "resultType": "FAIL", "success": null,
        "error": { "code": "USER_NOT_FOUND", "message": "사용자를 찾을 수 없습니다.", "details": null },
        "meta": { "timestamp": "2026-07-19T20:00:00", "path": "/api/v1/users/me" }
      }
      """;

  public static final String INVALID_ACCESS_TOKEN_EXAMPLE =
      """
      {
        "resultType": "FAIL", "success": null,
        "error": { "code": "INVALID_ACCESS_TOKEN", "message": "유효하지 않은 액세스 토큰입니다.", "details": null },
        "meta": { "timestamp": "2026-07-19T20:00:00", "path": "/api/v1/users/me" }
      }
      """;

  public static final String EXPIRED_ACCESS_TOKEN_EXAMPLE =
      """
      {
        "resultType": "FAIL", "success": null,
        "error": { "code": "EXPIRED_ACCESS_TOKEN", "message": "만료된 액세스 토큰입니다.", "details": null },
        "meta": { "timestamp": "2026-07-19T20:00:00", "path": "/api/v1/users/me" }
      }
      """;

  public static final String NICKNAME_USER_NOT_FOUND_EXAMPLE =
      """
      {
        "resultType": "FAIL", "success": null,
        "error": { "code": "USER_NOT_FOUND", "message": "사용자를 찾을 수 없습니다.", "details": null },
        "meta": { "timestamp": "2026-07-19T20:00:00", "path": "/api/v1/users/me/nickname" }
      }
      """;

  public static final String NICKNAME_INVALID_ACCESS_TOKEN_EXAMPLE =
      """
      {
        "resultType": "FAIL", "success": null,
        "error": { "code": "INVALID_ACCESS_TOKEN", "message": "유효하지 않은 액세스 토큰입니다.", "details": null },
        "meta": { "timestamp": "2026-07-19T20:00:00", "path": "/api/v1/users/me/nickname" }
      }
      """;

  public static final String NICKNAME_EXPIRED_ACCESS_TOKEN_EXAMPLE =
      """
      {
        "resultType": "FAIL", "success": null,
        "error": { "code": "EXPIRED_ACCESS_TOKEN", "message": "만료된 액세스 토큰입니다.", "details": null },
        "meta": { "timestamp": "2026-07-19T20:00:00", "path": "/api/v1/users/me/nickname" }
      }
      """;

  public static final String INVALID_NICKNAME_FORMAT_EXAMPLE =
      """
      {
        "resultType": "FAIL", "success": null,
        "error": { "code": "INVALID_NICKNAME_FORMAT", "message": "닉네임은 한글, 영문, 숫자로 2~15자여야 하며 공백과 특수문자는 사용할 수 없습니다.", "details": null },
        "meta": { "timestamp": "2026-07-19T20:00:00", "path": "/api/v1/users/me/nickname" }
      }
      """;

  public static final String DUPLICATE_NICKNAME_EXAMPLE =
      """
      {
        "resultType": "FAIL", "success": null,
        "error": { "code": "DUPLICATE_NICKNAME", "message": "이미 사용 중인 닉네임입니다.", "details": null },
        "meta": { "timestamp": "2026-07-19T20:00:00", "path": "/api/v1/users/me/nickname" }
      }
      """;

  public static final String NICKNAME_CHANGE_NOT_ALLOWED_EXAMPLE =
      """
      {
        "resultType": "FAIL", "success": null,
        "error": { "code": "NICKNAME_CHANGE_NOT_ALLOWED", "message": "마지막 닉네임 변경 후 30일이 지나야 다시 변경할 수 있습니다.", "details": null },
        "meta": { "timestamp": "2026-07-19T20:00:00", "path": "/api/v1/users/me/nickname" }
      }
      """;

  public static final String MY_ARTIST_PROFILE_SUCCESS_EXAMPLE =
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
            "profileImageUrl": "https://cdn.example.com/images/user/profile.jpg",
            "introduction": "시각과 공간의 관계를 탐구하는 작가입니다.",
            "externalLink": "https://portfolio.maya.com",
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

  public static final String NICKNAME_CHECK_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "nickname": "maya041225",
            "isAvailable": true
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-07-16T20:00:00",
          "path": "/api/v1/users/nickname/check"
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

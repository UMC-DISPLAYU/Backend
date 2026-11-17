package com.example.demo.domain.user.presentation.docs;

public final class UserApiDocs {

  public static final String SIGNUP_REQUEST_EXAMPLE =
      """
      {
        "nickname": "displayu01",
        "agreements": [
          {
            "code": "TERMS_OF_SERVICE",
            "version": "2.0"
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
            "profileImageUrl": "https://cdn.displayu.co.kr/users/1/profile.jpg",
            "role": "USER",
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

  public static final String UPDATE_MY_PROFILE_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": { "data": {
          "profileImageUrl": "https://cdn.displayu.co.kr/users/1/profile.jpg",
          "nickname": "김디유"
        }},
        "error": null,
        "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/users/me" }
      }
      """;

  public static final String UPDATE_MY_PROFILE_INVALID_INPUT_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "INVALID_PROFILE_IMAGE_URL", "message": "프로필 이미지 URL은 올바른 HTTP 또는 HTTPS URL이어야 합니다.", "details": null }, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/users/me" } }
      """;

  public static final String UPDATE_MY_PROFILE_INVALID_NICKNAME_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "INVALID_NICKNAME_FORMAT", "message": "닉네임은 한글, 영문, 숫자로 2~15자여야 하며 공백과 특수문자는 사용할 수 없습니다.", "details": null }, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/users/me" } }
      """;

  public static final String UPDATE_MY_PROFILE_DUPLICATE_NICKNAME_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "DUPLICATE_NICKNAME", "message": "이미 사용 중인 닉네임입니다.", "details": null }, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/users/me" } }
      """;

  public static final String SEARCH_INVALID_INPUT_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "INVALID_INPUT_VALUE", "message": "입력값이 올바르지 않습니다.", "details": [{ "field": "nickname", "message": "공백일 수 없습니다" }] }, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/users/search" } }
      """;

  public static final String SEARCH_UNAUTHORIZED_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "UNAUTHORIZED", "message": "인증이 필요합니다.", "details": null }, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/users/search" } }
      """;

  public static final String MY_USER_UNAUTHORIZED_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "UNAUTHORIZED", "message": "인증이 필요합니다.", "details": null }, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/users/me" } }
      """;

  public static final String MISSING_NICKNAME_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "MISSING_NICKNAME", "message": "닉네임을 입력해주세요.", "details": null }, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/users/nickname/check" } }
      """;

  public static final String CHECK_INVALID_NICKNAME_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "INVALID_NICKNAME_FORMAT", "message": "닉네임은 한글, 영문, 숫자로 2~15자여야 하며 공백과 특수문자는 사용할 수 없습니다.", "details": null }, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/users/nickname/check" } }
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

  public static final String DISPLAY_LEADER_WITHDRAWAL_BLOCKED_EXAMPLE =
      """
      {
        "resultType": "FAIL",
        "success": null,
        "error": {
          "code": "DISPLAY_LEADER_REMOVAL_NOT_ALLOWED",
          "message": "전시 대표자는 내보낼 수 없습니다.",
          "details": null
        },
        "meta": {
          "timestamp": "2026-11-14T20:00:00",
          "path": "/api/v1/users/me"
        }
      }
      """;

  public static final String WITHDRAW_UNAUTHORIZED_EXAMPLE =
      """
      {
        "resultType": "FAIL",
        "success": null,
        "error": {
          "code": "UNAUTHORIZED",
          "message": "인증이 필요합니다.",
          "details": null
        },
        "meta": {
          "timestamp": "2026-11-14T20:00:00",
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
            "nickname": "User2"
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

  public static final String USER_SEARCH_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "userId": 12,
            "name": "홍길동",
            "nickname": "displayArtist",
            "profileImageUrl": "https://cdn.displayu.co.kr/users/12/profile.jpg"
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-11-01T12:00:00",
          "path": "/api/v1/users/search"
        }
      }
      """;

  public static final String USER_NICKNAME_NOT_FOUND_EXAMPLE =
      """
      {
        "resultType": "FAIL",
        "success": null,
        "error": {
          "code": "USER_NICKNAME_NOT_FOUND",
          "message": "해당 닉네임을 가진 사용자를 찾을 수 없습니다.",
          "details": null
        },
        "meta": {
          "timestamp": "2026-11-01T12:00:00",
          "path": "/api/v1/users/search"
        }
      }
      """;

  private UserApiDocs() {}
}

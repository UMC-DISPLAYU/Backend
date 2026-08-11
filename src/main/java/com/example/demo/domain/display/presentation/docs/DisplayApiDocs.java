package com.example.demo.domain.display.presentation.docs;

public final class DisplayApiDocs {

  public static final String TAG_NAME = "Display";
  public static final String TAG_DESCRIPTION = "전시 API";

  public static final String CREATE_SUMMARY = "전시 생성";
  public static final String CREATE_DESCRIPTION =
      "인증된 사용자가 전시 기본 정보와 일정/장소 정보를 생성합니다. region은 SEOUL, GYEONGGI_INCHEON, OTHERS 중 하나를 사용합니다. "
          + "posterImageUrl은 전시 목록/지도/큐레이션에서 사용하는 대표 이미지로 저장되며, 서버는 imageType=MAIN, sortOrder=0으로 생성합니다. "
          + "displayImageUrl은 최대 4개까지 전달할 수 있고, 전달 순서대로 imageType=DETAIL, sortOrder=0부터 저장됩니다.";
  public static final String CREATE_REQUEST_DESCRIPTION = "전시 생성 요청";
  public static final String CREATE_REQUEST_EXAMPLE_NAME = "Display create request";
  public static final String CREATE_SUCCESS_DESCRIPTION = "전시 생성 성공";
  public static final String CREATE_SUCCESS_EXAMPLE_NAME = "Display create success";

  public static final String UPDATE_SUMMARY = "전시 수정";
  public static final String UPDATE_DESCRIPTION =
      "전시 팀장 권한을 가진 사용자가 전시 정보를 수정합니다. displayId는 필수이고, 나머지 필드는 전달한 값만 수정합니다. "
          + "posterImageUrl을 전달하면 imageType=MAIN, sortOrder=0 대표 이미지 URL을 교체합니다.";
  public static final String UPDATE_REQUEST_DESCRIPTION = "전시 수정 요청";
  public static final String UPDATE_REQUEST_EXAMPLE_NAME = "Display update request";
  public static final String UPDATE_SUCCESS_DESCRIPTION = "전시 수정 성공";
  public static final String UPDATE_SUCCESS_EXAMPLE_NAME = "Display update success";

  public static final String PUBLISH_SUMMARY = "전시 등록";
  public static final String PUBLISH_DESCRIPTION =
      "전시 팀장 권한을 가진 사용자가 초안 상태의 전시를 발행 상태로 변경합니다. 이미 발행된 전시는 성공 응답을 반환합니다.";
  public static final String PUBLISH_REQUEST_DESCRIPTION = "전시 등록 요청";
  public static final String PUBLISH_REQUEST_EXAMPLE_NAME = "Display publish request";
  public static final String PUBLISH_SUCCESS_DESCRIPTION = "전시 등록 성공";
  public static final String PUBLISH_SUCCESS_EXAMPLE_NAME = "Display publish success";

  public static final String DELETE_SUMMARY = "전시 삭제";
  public static final String DELETE_DESCRIPTION =
      "전시 팀장이 삭제 기능으로 전시를 초안(DRAFT) 상태로 변경해 목록/지도/졸업/마감임박 조회에서 노출되지 않게 합니다. 물리 삭제는 하지 않으며, 이미 초안 상태인 전시는 성공 응답을 반환합니다.";
  public static final String DELETE_SUCCESS_DESCRIPTION = "전시 삭제 성공";
  public static final String DELETE_SUCCESS_EXAMPLE_NAME = "Display delete success";

  public static final String RESERVATION_SUMMARY = "전시 콘텐츠 공개 예약 수정";
  public static final String RESERVATION_DESCRIPTION =
      "전시 팀장 권한을 가진 사용자가 작품 콘텐츠와 전시 콘텐츠의 공개 시점 정책을 수정합니다.";
  public static final String RESERVATION_REQUEST_DESCRIPTION = "전시 콘텐츠 공개 예약 수정 요청";
  public static final String RESERVATION_REQUEST_EXAMPLE_NAME =
      "Display reservation update request";
  public static final String RESERVATION_SUCCESS_DESCRIPTION = "전시 콘텐츠 공개 예약 수정 성공";
  public static final String RESERVATION_SUCCESS_EXAMPLE_NAME =
      "Display reservation update success";

  public static final String LIKE_SUMMARY = "전시 좋아요";
  public static final String LIKE_DESCRIPTION = "인증된 사용자가 전시에 좋아요를 추가합니다.";
  public static final String LIKE_CANCEL_SUMMARY = "전시 좋아요 취소";
  public static final String LIKE_CANCEL_DESCRIPTION =
      "인증된 사용자가 전시 좋아요를 취소합니다. 물리 삭제하지 않고 deletedAt을 기록합니다.";
  public static final String LIKE_STATUS_SUMMARY = "전시 좋아요 여부 조회";
  public static final String LIKE_STATUS_DESCRIPTION = "인증된 사용자 기준으로 해당 전시에 활성 좋아요를 눌렀는지 조회합니다.";
  public static final String LIKE_REQUEST_DESCRIPTION = "전시 좋아요 요청";
  public static final String LIKE_REQUEST_EXAMPLE_NAME = "Display like request";
  public static final String LIKE_SUCCESS_DESCRIPTION = "전시 좋아요 처리 성공";
  public static final String LIKE_SUCCESS_EXAMPLE_NAME = "Display like success";
  public static final String LIKE_CANCEL_SUCCESS_EXAMPLE_NAME = "Display like cancel success";
  public static final String LIKE_STATUS_SUCCESS_DESCRIPTION = "전시 좋아요 여부 조회 성공";
  public static final String LIKE_STATUS_SUCCESS_EXAMPLE_NAME = "Display like status success";

  public static final String INVITATION_ISSUE_SUMMARY = "전시 초대 링크 생성";
  public static final String INVITATION_ISSUE_DESCRIPTION =
      "전시 팀장이 초대 링크를 생성하거나 재발급합니다. 기존 활성 링크가 있으면 새 토큰으로 교체되어 이전 링크는 즉시 사용할 수 없습니다.";
  public static final String INVITATION_ISSUE_SUCCESS_DESCRIPTION = "전시 초대 링크 생성 성공";
  public static final String INVITATION_ISSUE_SUCCESS_EXAMPLE_NAME =
      "Display invitation issue success";

  public static final String INVITATION_DISABLE_SUMMARY = "전시 초대 링크 비활성화";
  public static final String INVITATION_DISABLE_DESCRIPTION =
      "전시 팀장이 초대 링크를 비활성화합니다. 토큰은 삭제하지 않고 비활성화 시각만 기록하며, 이미 비활성화된 링크도 성공 응답을 반환합니다.";
  public static final String INVITATION_DISABLE_SUCCESS_DESCRIPTION = "전시 초대 링크 비활성화 성공";
  public static final String INVITATION_DISABLE_SUCCESS_EXAMPLE_NAME =
      "Display invitation disable success";

  public static final String INVITATION_DETAIL_SUMMARY = "초대 토큰으로 전시 조회";
  public static final String INVITATION_DETAIL_DESCRIPTION =
      "인증된 사용자가 초대 URL의 원본 토큰으로 전시 상세 정보를 조회합니다. "
          + "조회 시 요청 사용자에게 대기 중인 전시 초대가 생성될 수 있습니다. "
          + "유효하지 않거나 비활성화된 초대 링크는 실패 응답을 반환합니다.";
  public static final String INVITATION_DETAIL_SUCCESS_DESCRIPTION = "초대 토큰 전시 조회 성공";
  public static final String INVITATION_DETAIL_SUCCESS_EXAMPLE_NAME =
      "Display invitation detail success";
  public static final String INVITATION_DISPLAY_ID_DESCRIPTION = "전시 ID";
  public static final String INVITATION_DISPLAY_ID_EXAMPLE = "12";
  public static final String INVITATION_TOKEN_DESCRIPTION = "초대 URL에 포함된 원본 토큰";
  public static final String INVITATION_TOKEN_EXAMPLE =
      "xgpwkMqz7OJfcavJT4IswaxJv6xPYO2knbq4CrNUYW4";

  public static final String MAP_SUMMARY = "지도 영역 전시 조회";
  public static final String MAP_DESCRIPTION = "현재 지도 화면 영역에 포함된 발행 전시 마커 목록을 조회합니다.";
  public static final String MAP_SUCCESS_DESCRIPTION = "지도 영역 전시 조회 성공";
  public static final String MAP_SUCCESS_EXAMPLE_NAME = "Display map success";

  public static final String SEARCH_SUMMARY = "전시 검색";
  public static final String SEARCH_DESCRIPTION =
      "발행된 전시를 displayId 오름차순 커서 방식으로 검색합니다. "
          + "searchWord는 title 부분 일치 필터로만 사용하고, region=ALL이면 지역 조건을 적용하지 않습니다. "
          + "status=CLOSING_SOON은 현재 날짜 기준 3일 이내 종료되는 전시를 조회합니다.";
  public static final String SEARCH_SUCCESS_DESCRIPTION = "전시 검색 성공";
  public static final String SEARCH_SUCCESS_EXAMPLE_NAME = "Display search success";

  public static final String CLOSING_SOON_SUMMARY = "마감 임박 전시 조회";
  public static final String CLOSING_SOON_DESCRIPTION =
      "현재 날짜 기준 종료되지 않은 발행 전시를 종료일 오름차순, 같은 종료일은 displayId 오름차순 커서 방식으로 조회합니다. "
          + "cursor는 마지막으로 조회한 전시의 endedAt과 displayId를 ':'로 연결한 값입니다. 예: 2026-07-15:5";
  public static final String CLOSING_SOON_SUCCESS_DESCRIPTION = "마감 임박 전시 조회 성공";
  public static final String CLOSING_SOON_SUCCESS_EXAMPLE_NAME = "Closing soon display success";

  public static final String GRADUATION_SUMMARY = "졸업 전시 랜덤 추천 조회";
  public static final String GRADUATION_DESCRIPTION = "발행된 졸업 전시 중 요청한 개수만큼 랜덤 추천 목록을 조회합니다.";
  public static final String GRADUATION_SUCCESS_DESCRIPTION = "졸업 전시 랜덤 추천 조회 성공";
  public static final String GRADUATION_SUCCESS_EXAMPLE_NAME = "Graduation display success";

  public static final String DU_PICKS_SUMMARY = "DU Picks 조회";
  public static final String DU_PICKS_DESCRIPTION = "DU Picks 칼럼 목록을 columnId 오름차순 커서 방식으로 조회합니다.";
  public static final String DU_PICKS_SUCCESS_DESCRIPTION = "DU Picks 조회 성공";
  public static final String DU_PICKS_SUCCESS_EXAMPLE_NAME = "DU Picks success";

  public static final String MY_DISPLAY_SUMMARY = "내 전시 목록 조회";
  public static final String MY_DISPLAY_DESCRIPTION =
      "인증된 사용자가 직접 만든 전시와 참여 중인 전시 목록을 조회합니다. 초안 상태의 전시도 포함합니다.";
  public static final String MY_DISPLAY_SUCCESS_DESCRIPTION = "내 전시 목록 조회 성공";
  public static final String MY_DISPLAY_SUCCESS_EXAMPLE_NAME = "My display success";

  public static final String DETAIL_SUMMARY = "전시 상세 조회";
  public static final String DETAIL_DESCRIPTION = "displayId에 해당하는 전시의 전체 상세 데이터를 조회합니다.";
  public static final String DETAIL_SUCCESS_DESCRIPTION = "전시 상세 조회 성공";
  public static final String DETAIL_SUCCESS_EXAMPLE_NAME = "Display detail success";
  public static final String DETAIL_DISPLAY_ID_DESCRIPTION = "전시 ID";
  public static final String DETAIL_DISPLAY_ID_EXAMPLE = "1";

  public static final String EXIT_SUMMARY = "전시 멤버 나가기";
  public static final String EXIT_DESCRIPTION =
      "인증된 사용자가 특정 전시에서 나갑니다. 수락된 활성 전시 멤버만 나갈 수 있으며, TEAM_LEADER는 이 API로 나갈 수 없습니다. "
          + "나가기는 TeamMember.deletedAt을 기록하는 soft delete 방식으로 처리합니다.";
  public static final String EXIT_SUCCESS_DESCRIPTION = "전시 멤버 나가기 성공";
  public static final String EXIT_SUCCESS_EXAMPLE_NAME = "Display exit success";
  public static final String EXIT_UNAUTHORIZED_EXAMPLE_NAME = "Display exit unauthorized";
  public static final String EXIT_FORBIDDEN_EXAMPLE_NAME = "Display exit forbidden";
  public static final String EXIT_MEMBER_NOT_FOUND_EXAMPLE_NAME = "Display member not found";

  public static final String CREATE_REQUEST_EXAMPLE =
      """
      {
        "title": "FORM 2026",
        "posterImageUrl": "https://cdn...",
        "_posterImageUrlRule": "대표 이미지는 서버에서 imageType=MAIN, sortOrder=0으로 저장됩니다.",
        "displayImageUrl": [
          "https://cdn.../detail-1.jpg",
          "https://cdn.../detail-2.jpg"
        ],
        "_displayImageUrlRule": "최대 4개까지 전달할 수 있으며 imageType=DETAIL, sortOrder=0부터 순서대로 저장됩니다.",
        "type": "GRADUATION",
        "fields": ["DESIGN", "MEDIA"],
        "region": "SEOUL",
        "schoolOrOrganization": "중앙대학교",
        "departmentOrClub": "디자인학부",
        "qnaAccount": "@displayu",
        "displayNickname": "전시 리더",
        "contract": "Instagram DM",
        "subtitle": "중앙대학교 디자인학부 졸업전시",
        "description": "디자인학부 학생들의...",
        "startDate": "2026-05-28",
        "endDate": "2026-06-05",
        "openTime": "10:00",
        "closeTime": "18:00",
        "locationName": "중앙대학교 안성캠퍼스 301관 대전시실 2층",
        "latitude": 37.0063,
        "longitude": 127.2267,
        "roadAddress": "경기도 안성시 대덕면 서동대로 4726",
        "precautions": "전시장 내 음료 반입 금지"
      }
      """;

  public static final String CREATE_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "displayId": 1,
            "ownerUserId": 1,
            "title": "FORM 2026",
            "subtitle": "중앙대학교 디자인학부 졸업전시",
            "content": "디자인학부 학생들의...",
            "location": {
              "placeName": "중앙대학교 안성캠퍼스 301관 대전시실 2층",
              "latitude": 37.0063,
              "longitude": 127.2267,
              "roadAddress": "경기도 안성시 대덕면 서동대로 4726"
            },
            "qnaAccount": "@displayu",
            "contract": "Instagram DM",
            "note": "전시장 내 음료 반입 금지",
            "organization": "중앙대학교",
            "department": "디자인학부",
            "displayType": "GRADUATION",
            "displayFields": ["DESIGN", "VIDEO"],
            "region": "SEOUL",
            "likeCount": 0,
            "isArchived": false,
            "period": {
              "startDate": "2026-05-28",
              "endDate": "2026-06-05",
              "startTime": "10:00:00",
              "endTime": "18:00:00"
            },
            "artworkContentOpen": "IMMEDIATELY",
            "exhibitionContentOpen": "ON_EXHIBITION",
            "status": "DRAFT",
            "invitationToken": null,
            "invitationDisabledAt": null,
            "images": [
              {
                "imageId": 1,
                "imageUrl": "https://cdn...",
                "imageType": "MAIN",
                "sortOrder": 0
              }
            ],
            "contentCategories": [],
            "teamMembers": [
              {
                "teamMemberId": 1,
                "userId": 1,
                "displayNickname": "전시 리더",
                "role": "TEAM_LEADER",
                "accepted": true
              }
            ],
            "invitations": []
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-07-09T19:55:00",
          "path": "/api/v1/display"
        }
      }
      """;

  public static final String EXIT_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": null
        },
        "error": null,
        "meta": {
          "timestamp": "2026-08-03T23:00:00",
          "path": "/api/v1/display/1/exit"
        }
      }
      """;

  public static final String EXIT_FORBIDDEN_EXAMPLE =
      """
      {
        "resultType": "FAIL",
        "success": null,
        "error": {
          "code": "FORBIDDEN",
          "message": "접근 권한이 없습니다.",
          "details": null
        },
        "meta": {
          "timestamp": "2026-08-03T23:00:00",
          "path": "/api/v1/display/1/exit"
        }
      }
      """;

  public static final String EXIT_UNAUTHORIZED_EXAMPLE =
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
          "timestamp": "2026-08-03T23:00:00",
          "path": "/api/v1/display/1/exit"
        }
      }
      """;

  public static final String EXIT_MEMBER_NOT_FOUND_EXAMPLE =
      """
      {
        "resultType": "FAIL",
        "success": null,
        "error": {
          "code": "DISPLAY_MEMBER_NOT_FOUND",
          "message": "전시 멤버를 찾을 수 없습니다.",
          "details": null
        },
        "meta": {
          "timestamp": "2026-08-03T23:00:00",
          "path": "/api/v1/display/1/exit"
        }
      }
      """;

  public static final String UPDATE_REQUEST_EXAMPLE =
      """
      {
        "displayId": 12,
        "title": "FORM 2026 (수정본)",
        "posterImageUrl": "https://cdn...",
        "_posterImageUrlRule": "posterImageUrl을 전달하면 imageType=MAIN, sortOrder=0 대표 이미지 URL을 교체합니다.",
        "type": "GRADUATION",
        "fields": ["DESIGN", "MEDIA"],
        "schoolOrOrganization": "중앙대학교",
        "departmentOrClub": "디자인학부 시각디자인",
        "hostOrganizationName": null,
        "contract": "Email",
        "subtitle": "변경된 전시 부제목입니다.",
        "description": "변경된 전시 소개글입니다.",
        "startDate": "2026-05-29",
        "endDate": "2026-06-06",
        "openTime": "09:00",
        "closeTime": "19:00",
        "placeName": "중앙대학교 301관 갤러리 3층 전시장",
        "precautions": "물품 보관소를 운영하지 않습니다."
      }
      """;

  public static final String UPDATE_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "displayId": 12,
            "ownerUserId": 1,
            "title": "FORM 2026 (수정본)",
            "subtitle": "변경된 전시 부제목입니다.",
            "content": "변경된 전시 소개글입니다.",
            "location": {
              "placeName": "중앙대학교 301관 갤러리 3층 전시장",
              "latitude": 37.0063,
              "longitude": 127.2267
            },
            "qnaAccount": "",
            "contract": "Email",
            "note": "물품 보관소를 운영하지 않습니다.",
            "organization": "중앙대학교",
            "department": "디자인학부 시각디자인",
            "displayType": "GRADUATION",
            "displayFields": ["DESIGN", "VIDEO"],
            "region": "SEOUL",
            "likeCount": 12,
            "isArchived": false,
            "period": {
              "startDate": "2026-05-29",
              "endDate": "2026-06-06",
              "startTime": "09:00:00",
              "endTime": "19:00:00"
            },
            "artworkContentOpen": "IMMEDIATELY",
            "exhibitionContentOpen": "ON_EXHIBITION",
            "status": "DRAFT",
            "invitationToken": null,
            "invitationDisabledAt": null,
            "images": [],
            "contentCategories": [],
            "teamMembers": [],
            "invitations": []
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-07-14T02:00:00",
          "path": "/api/v1/display"
        }
      }
      """;

  public static final String PUBLISH_REQUEST_EXAMPLE =
      """
      {
        "displayId": 12
      }
      """;

  public static final String PUBLISH_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "displayId": 12,
            "ownerUserId": 1,
            "title": "FORM 2026",
            "subtitle": "중앙대학교 디자인학부 졸업전시",
            "content": "디자인학부 학생들의...",
            "location": {
              "placeName": "중앙대학교 안성캠퍼스 301관 대전시실 2층",
              "latitude": 37.0063,
              "longitude": 127.2267
            },
            "qnaAccount": "",
            "note": "전시장 내 음료 반입 금지",
            "organization": "중앙대학교",
            "department": "디자인학부",
            "displayType": "GRADUATION",
            "displayFields": ["DESIGN", "VIDEO"],
            "region": "SEOUL",
            "likeCount": 0,
            "isArchived": false,
            "period": {
              "startDate": "2026-05-28",
              "endDate": "2026-06-05",
              "startTime": "10:00:00",
              "endTime": "18:00:00"
            },
            "artworkContentOpen": "IMMEDIATELY",
            "exhibitionContentOpen": "ON_EXHIBITION",
            "status": "PUBLISHED",
            "invitationToken": null,
            "invitationDisabledAt": null,
            "images": [],
            "contentCategories": [],
            "teamMembers": [],
            "invitations": []
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-07-14T02:00:00",
          "path": "/api/v1/display/publish"
        }
      }
      """;

  public static final String DELETE_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "displayId": 12,
            "ownerUserId": 1,
            "title": "FORM 2026",
            "subtitle": "중앙대학교 디자인학부 졸업전시",
            "content": "디자인학부 학생들의...",
            "location": {
              "placeName": "중앙대학교 안성캠퍼스 301관 대전시실 2층",
              "latitude": 37.0063,
              "longitude": 127.2267
            },
            "qnaAccount": "",
            "note": "전시장 내 음료 반입 금지",
            "organization": "중앙대학교",
            "department": "디자인학부",
            "displayType": "GRADUATION",
            "displayFields": ["DESIGN", "VIDEO"],
            "region": "SEOUL",
            "likeCount": 0,
            "isArchived": false,
            "period": {
              "startDate": "2026-05-28",
              "endDate": "2026-06-05",
              "startTime": "10:00:00",
              "endTime": "18:00:00"
            },
            "artworkContentOpen": "IMMEDIATELY",
            "exhibitionContentOpen": "ON_EXHIBITION",
            "status": "DRAFT",
            "invitationToken": null,
            "invitationDisabledAt": null,
            "images": [],
            "contentCategories": [],
            "teamMembers": [],
            "invitations": []
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-07-14T02:00:00",
          "path": "/api/v1/display/12/draft"
        }
      }
      """;

  public static final String RESERVATION_REQUEST_EXAMPLE =
      """
      {
        "artworkContentOpen": "ON_EXHIBITION",
        "exhibitionContentOpen": "IMMEDIATELY"
      }
      """;

  public static final String RESERVATION_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "displayId": 12,
            "ownerUserId": 1,
            "title": "FORM 2026",
            "subtitle": "중앙대학교 디자인학부 졸업전시",
            "content": "디자인학부 학생들의...",
            "location": {
              "placeName": "중앙대학교 안성캠퍼스 301관 대전시실 2층",
              "latitude": 37.0063,
              "longitude": 127.2267
            },
            "qnaAccount": "",
            "note": "전시장 내 음료 반입 금지",
            "organization": "중앙대학교",
            "department": "디자인학부",
            "displayType": "GRADUATION",
            "displayFields": ["DESIGN", "VIDEO"],
            "region": "SEOUL",
            "likeCount": 0,
            "isArchived": false,
            "period": {
              "startDate": "2026-05-28",
              "endDate": "2026-06-05",
              "startTime": "10:00:00",
              "endTime": "18:00:00"
            },
            "artworkContentOpen": "ON_EXHIBITION",
            "exhibitionContentOpen": "IMMEDIATELY",
            "status": "PUBLISHED",
            "invitationToken": null,
            "invitationDisabledAt": null,
            "images": [],
            "contentCategories": [],
            "teamMembers": [],
            "invitations": []
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-07-14T02:00:00",
          "path": "/api/v1/display/12/reservation"
        }
      }
      """;

  public static final String LIKE_REQUEST_EXAMPLE =
      """
      {
        "displayId": 12
      }
      """;

  public static final String LIKE_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "displayId": 12,
            "likeCount": 143
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-07-01T23:00:00",
          "path": "/api/v1/display/like"
        }
      }
      """;

  public static final String LIKE_CANCEL_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "displayId": 12,
            "likeCount": 142
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-07-01T23:00:00",
          "path": "/api/v1/display/like"
        }
      }
      """;

  public static final String LIKE_STATUS_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "isLiked": true
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-07-01T23:00:00",
          "path": "/api/v1/display/12/isliked"
        }
      }
      """;

  public static final String INVITATION_ISSUE_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "displayId": 12,
            "invitationUrl": "https://www.displayu.co.kr/display/invitation/xgpwkMqz7OJfcavJT4IswaxJv6xPYO2knbq4CrNUYW4"
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-07-17T23:30:00",
          "path": "/api/v1/display/12/invitation"
        }
      }
      """;

  public static final String INVITATION_DISABLE_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "displayId": 12,
            "invitationDisabledAt": "2026-07-17T23:30:00"
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-07-17T23:30:00",
          "path": "/api/v1/display/12/invitation/disable"
        }
      }
      """;

  public static final String MAP_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "markers": [
              {
                "displayId": 15,
                "title": "내면의 풍경",
                "startDate": "2026-05-20",
                "endDate": "2026-05-28",
                "locationName": "홍익대학교 현대미술관",
                "posterImageUrl": "https://cdn.displayu.com/posters/inner_view.png",
                "schoolDepartmentName": "중앙대학교 디자인학부",
                "latitude": 37.5513,
                "longitude": 126.9248,
                "isArchived": true
              }
            ],
            "pagination": {
              "nextCursor": 15,
              "size": 20,
              "hasNext": true
            }
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-07-12T07:00:00",
          "path": "/api/v1/display/map"
        }
      }
      """;

  public static final String CLOSING_SOON_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "exhibitions": [
              {
                "displayId": 5,
                "title": "ABOUT THE FLOW OF TIME",
                "posterImageUrl": "https://cdn.displayu.com/posters/flow_time.png",
                "schoolDepartmentName": "중앙대학교 디자인학부",
                "startedAt": "2026-06-20",
                "endedAt": "2026-07-15",
                "dayLeft": 3,
                "isArchived": true
              }
            ],
            "pagination": {
              "nextCursor": "2026-07-15:5",
              "size": 20,
              "hasNext": true
            }
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-07-12T07:00:00",
          "path": "/api/v1/display/closing-soon"
        }
      }
      """;

  public static final String SEARCH_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "exhibitions": [
              {
                "displayId": 15,
                "title": "2026 디자인 졸업전시",
                "posterImageUrl": "https://cdn.displayu.com/posters/graduation.png",
                "schoolDepartmentName": "중앙대학교 디자인학부",
                "startedAt": "2026-07-10",
                "endedAt": "2026-07-20",
                "dayLeft": 8,
                "isArchived": true
              }
            ],
            "pagination": {
              "nextCursor": 15,
              "size": 10,
              "hasNext": true
            }
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-07-12T07:00:00",
          "path": "/api/v1/display/search"
        }
      }
      """;

  public static final String GRADUATION_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "exhibitions": [
              {
                "displayId": 7,
                "title": "2026 졸업 전시",
                "posterImageUrl": "https://cdn.displayu.com/posters/graduation.png",
                "schoolDepartmentName": "중앙대학교 디자인학부",
                "startedAt": "2026-05-20",
                "endedAt": "2026-05-28",
                "dayLeft": 0,
                "isArchived": true
              }
            ]
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-07-12T07:00:00",
          "path": "/api/v1/display/graduation"
        }
      }
      """;

  public static final String DU_PICKS_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "duPicks": [
              {
                "duPickId": 1,
                "title": "THE ESSENCE IN MOTION",
                "subtitle": "색과 형태, 우리가 마주한 순간들",
                "bannerImageUrl": "https://cdn.displayu.com/home/du_pick_1.png",
                "createdAt": "2026-06-30"
              }
            ],
            "pagination": {
              "nextCursor": 1,
              "size": 20,
              "hasNext": true
            }
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-07-12T07:00:00",
          "path": "/api/v1/display/du-picks"
        }
      }
      """;

  public static final String MY_DISPLAY_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "createdDisplays": [
              {
                "displayId": 1,
                "title": "내가 만든 전시",
                "isDisplaying": true,
                "startDate": "2026-08-01",
                "endDate": "2026-08-07",
                "school": "디유대학교",
                "department": "디자인학부",
                "placeName": "디유 갤러리",
                "postImageUrl": "https://cdn.displayu.com/posters/main.png"
              }
            ],
            "participatedDisplays": [
              {
                "displayId": 2,
                "title": "내가 참여한 전시",
                "isDisplaying": false,
                "startDate": "2026-07-20",
                "endDate": "2026-07-31",
                "school": "디유대학교",
                "department": "디자인학부",
                "placeName": "디유 갤러리",
                "postImageUrl": "https://cdn.displayu.com/posters/main.png"
              }
            ]
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-08-03T18:30:00",
          "path": "/api/v1/display/me"
        }
      }
      """;

  public static final String DETAIL_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "displayId": 1,
            "ownerUserId": 10,
            "title": "2026 졸업 전시",
            "subtitle": "DisplayU Graduation Exhibition",
            "content": "전시 소개 내용입니다.",
            "location": {
              "placeName": "디스플레이유 갤러리",
              "latitude": 37.5665000,
              "longitude": 126.9780000,
              "roadAddress": "서울특별시 중구 세종대로 110"
            },
            "qnaAccount": "@displayu",
            "contract": "Instagram DM",
            "note": "입장 무료",
            "organization": "디스플레이유",
            "department": "디자인학과",
            "displayType": "GRADUATION",
            "displayFields": ["DESIGN", "VIDEO"],
            "region": "SEOUL",
            "likeCount": 143,
            "isArchived": true,
            "period": {
              "startDate": "2026-07-01",
              "endDate": "2026-07-10",
              "startTime": "10:00:00",
              "endTime": "18:00:00"
            },
            "artworkContentOpen": "IMMEDIATELY",
            "exhibitionContentOpen": "ON_EXHIBITION",
            "status": "PUBLISHED",
            "invitationToken": "token-value",
            "invitationDisabledAt": null,
            "images": [
              {
                "imageId": 1,
                "imageUrl": "https://cdn.displayu.com/display/main.jpg",
                "imageType": "MAIN",
                "sortOrder": 0
              },
              {
                "imageId": 2,
                "imageUrl": "https://cdn.displayu.com/display/detail.jpg",
                "imageType": "DETAIL",
                "sortOrder": 1
              }
            ],
            "contentCategories": [
              {
                "categoryId": 1,
                "name": "전시 소개",
                "description": "전시의 주요 소개 이미지입니다.",
                "sortOrder": 0,
                "contents": [
                  {
                    "contentId": 1,
                    "imageUrl": "https://cdn.displayu.com/display/content-1.jpg",
                    "sortOrder": 0
                  }
                ]
              }
            ],
            "teamMembers": [
              {
                "teamMemberId": 1,
                "userId": 10,
                "displayNickname": "도현",
                "role": "TEAM_LEADER",
                "accepted": true
              },
              {
                "teamMemberId": 2,
                "userId": 11,
                "displayNickname": "민지",
                "role": "TEAM_MEM",
                "accepted": false
              }
            ],
            "invitations": [
              {
                "invitationId": 1,
                "inviterUserId": 10,
                "inviteeUserId": 11,
                "createdAt": "2026-06-20T12:00:00"
              }
            ]
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-07-08T23:40:00",
          "path": "/api/v1/display/1"
        }
      }
      """;

  public static final String INVITATION_DETAIL_SUCCESS_EXAMPLE = DETAIL_SUCCESS_EXAMPLE;

  private DisplayApiDocs() {}
}

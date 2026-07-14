package com.example.demo.domain.display.presentation.docs;

public final class DisplayApiDocs {

  public static final String TAG_NAME = "Display";
  public static final String TAG_DESCRIPTION = "전시 API";

  public static final String CREATE_SUMMARY = "전시 생성";
  public static final String CREATE_DESCRIPTION =
      "전시 기본 정보와 일정/장소 정보를 생성합니다. region은 SEOUL, GYEONGGI_INCHEON, OTHERS 중 하나를 사용합니다.";
  public static final String CREATE_REQUEST_DESCRIPTION = "전시 생성 요청";
  public static final String CREATE_REQUEST_EXAMPLE_NAME = "Display create request";
  public static final String CREATE_SUCCESS_DESCRIPTION = "전시 생성 성공";
  public static final String CREATE_SUCCESS_EXAMPLE_NAME = "Display create success";

  public static final String MAP_SUMMARY = "지도 영역 전시 조회";
  public static final String MAP_DESCRIPTION = "현재 지도 화면 영역에 포함된 발행 전시 마커 목록을 조회합니다.";
  public static final String MAP_SUCCESS_DESCRIPTION = "지도 영역 전시 조회 성공";
  public static final String MAP_SUCCESS_EXAMPLE_NAME = "Display map success";

  public static final String SEARCH_SUMMARY = "전시 검색";
  public static final String SEARCH_DESCRIPTION =
      "발행된 전시를 displayId 오름차순 커서 방식으로 검색합니다. "
          + "searchWord는 title 부분 일치 필터로만 사용하고, region=ALL이면 지역 조건을 적용하지 않습니다.";
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

  public static final String DETAIL_SUMMARY = "전시 상세 조회";
  public static final String DETAIL_DESCRIPTION = "displayId에 해당하는 전시의 전체 상세 데이터를 조회합니다.";
  public static final String DETAIL_SUCCESS_DESCRIPTION = "전시 상세 조회 성공";
  public static final String DETAIL_SUCCESS_EXAMPLE_NAME = "Display detail success";
  public static final String DETAIL_DISPLAY_ID_DESCRIPTION = "전시 ID";
  public static final String DETAIL_DISPLAY_ID_EXAMPLE = "1";

  public static final String CREATE_REQUEST_EXAMPLE =
      """
      {
        "title": "FORM 2026",
        "posterImageUrl": "https://cdn...",
        "type": "GRADUATION",
        "fields": ["DESIGN", "MEDIA"],
        "region": "SEOUL",
        "schoolOrOrganization": "중앙대학교",
        "departmentOrClub": "디자인학부",
        "hostOrganizationName": null,
        "subtitle": "중앙대학교 디자인학부 졸업전시",
        "description": "디자인학부 학생들의...",
        "startDate": "2026-05-28",
        "endDate": "2026-06-05",
        "openTime": "10:00",
        "closeTime": "18:00",
        "locationName": "중앙대학교 안성캠퍼스 301관 대전시실 2층",
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
              "latitude": 0,
              "longitude": 0
            },
            "qnaAccount": "",
            "note": "전시장 내 음료 반입 금지",
            "organization": "중앙대학교",
            "department": "디자인학부",
            "displayType": "GRADUATION",
            "displayFields": ["DESIGN", "VIDEO"],
            "region": "SEOUL",
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
                "width": 1,
                "height": 1,
                "sortOrder": 0
              }
            ],
            "contentCategories": [],
            "teamMembers": [],
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
                "latitude": 37.5513,
                "longitude": 126.9248
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
                "startedAt": "2026-06-20",
                "endedAt": "2026-07-15",
                "dayLeft": 3
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
                "startedAt": "2026-07-10",
                "endedAt": "2026-07-20",
                "dayLeft": 8
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
                "startedAt": "2026-05-20",
                "endedAt": "2026-05-28",
                "dayLeft": 0
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
              "longitude": 126.9780000
            },
            "qnaAccount": "@displayu",
            "note": "입장 무료",
            "organization": "디스플레이유",
            "department": "디자인학과",
            "displayType": "GRADUATION",
            "displayFields": ["DESIGN", "VIDEO"],
            "region": "SEOUL",
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
                "width": 1200,
                "height": 800,
                "sortOrder": 0
              },
              {
                "imageId": 2,
                "imageUrl": "https://cdn.displayu.com/display/detail.jpg",
                "imageType": "DETAIL",
                "width": 1200,
                "height": 800,
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
                    "width": 1200,
                    "height": 800,
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

  private DisplayApiDocs() {}
}

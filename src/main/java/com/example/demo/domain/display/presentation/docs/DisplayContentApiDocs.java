package com.example.demo.domain.display.presentation.docs;

public final class DisplayContentApiDocs {

  public static final String TAG_NAME = "DisplayContent";
  public static final String TAG_DESCRIPTION = "전시 콘텐츠 이미지/카테고리 API";

  public static final String DISPLAY_ID_DESCRIPTION = "전시 ID";
  public static final String DISPLAY_ID_EXAMPLE = "12";
  public static final String CATEGORY_ID_DESCRIPTION = "전시 콘텐츠 카테고리 ID";
  public static final String CATEGORY_ID_EXAMPLE = "3";
  public static final String CONTENT_ID_DESCRIPTION = "전시 콘텐츠 이미지 ID";
  public static final String CONTENT_ID_EXAMPLE = "7";

  public static final String CREATE_CATEGORY_SUMMARY = "전시 콘텐츠 카테고리 생성";
  public static final String CREATE_CATEGORY_DESCRIPTION =
      "수락된 전시 팀원이 전시 콘텐츠 이미지들을 담을 카테고리를 생성합니다. sortOrder는 서버가 자동 부여합니다.";
  public static final String CREATE_CATEGORY_REQUEST_DESCRIPTION = "전시 콘텐츠 카테고리 생성 요청";
  public static final String CREATE_CATEGORY_REQUEST_EXAMPLE_NAME =
      "Display content category create request";
  public static final String CREATE_CATEGORY_SUCCESS_DESCRIPTION = "전시 콘텐츠 카테고리 생성 성공";
  public static final String CREATE_CATEGORY_SUCCESS_EXAMPLE_NAME =
      "Display content category create success";

  public static final String UPDATE_CATEGORY_SUMMARY = "전시 콘텐츠 카테고리 수정";
  public static final String UPDATE_CATEGORY_DESCRIPTION = "수락된 전시 팀원이 전시 콘텐츠 카테고리의 이름과 설명을 수정합니다.";
  public static final String UPDATE_CATEGORY_REQUEST_DESCRIPTION = "전시 콘텐츠 카테고리 수정 요청";
  public static final String UPDATE_CATEGORY_REQUEST_EXAMPLE_NAME =
      "Display content category update request";
  public static final String UPDATE_CATEGORY_SUCCESS_DESCRIPTION = "전시 콘텐츠 카테고리 수정 성공";
  public static final String UPDATE_CATEGORY_SUCCESS_EXAMPLE_NAME =
      "Display content category update success";

  public static final String DELETE_CATEGORY_SUMMARY = "전시 콘텐츠 카테고리 삭제";
  public static final String DELETE_CATEGORY_DESCRIPTION =
      "수락된 전시 팀원이 전시 콘텐츠 카테고리를 삭제합니다. 카테고리 안의 이미지도 함께 삭제됩니다.";
  public static final String DELETE_CATEGORY_SUCCESS_DESCRIPTION = "전시 콘텐츠 카테고리 삭제 성공";
  public static final String DELETE_CATEGORY_SUCCESS_EXAMPLE_NAME =
      "Display content category delete success";

  public static final String CREATE_CONTENT_SUMMARY = "전시 콘텐츠 이미지 생성";
  public static final String CREATE_CONTENT_DESCRIPTION =
      "수락된 전시 팀원이 카테고리 안에 이미지를 추가합니다. 한 카테고리에는 최대 20개까지 등록할 수 있고 sortOrder는 0부터 자동 부여됩니다. "
          + "이 sortOrder는 카테고리 내부 콘텐츠 이미지 순서이며, 전시 대표 이미지(imageType=MAIN, sortOrder=0)와는 별개입니다.";
  public static final String CREATE_CONTENT_REQUEST_DESCRIPTION = "전시 콘텐츠 이미지 생성 요청";
  public static final String CREATE_CONTENT_REQUEST_EXAMPLE_NAME = "Display content create request";
  public static final String CREATE_CONTENT_SUCCESS_DESCRIPTION = "전시 콘텐츠 이미지 생성 성공";
  public static final String CREATE_CONTENT_SUCCESS_EXAMPLE_NAME = "Display content create success";

  public static final String UPDATE_CONTENT_SUMMARY = "전시 콘텐츠 이미지 수정";
  public static final String UPDATE_CONTENT_DESCRIPTION =
      "수락된 전시 팀원이 카테고리 안의 이미지 URL과 이미지 크기를 수정합니다. 콘텐츠 이미지 수정은 전시 대표 이미지(imageType=MAIN, sortOrder=0)에 영향을 주지 않습니다.";
  public static final String UPDATE_CONTENT_REQUEST_DESCRIPTION = "전시 콘텐츠 이미지 수정 요청";
  public static final String UPDATE_CONTENT_REQUEST_EXAMPLE_NAME = "Display content update request";
  public static final String UPDATE_CONTENT_SUCCESS_DESCRIPTION = "전시 콘텐츠 이미지 수정 성공";
  public static final String UPDATE_CONTENT_SUCCESS_EXAMPLE_NAME = "Display content update success";

  public static final String DELETE_CONTENT_SUMMARY = "전시 콘텐츠 이미지 삭제";
  public static final String DELETE_CONTENT_DESCRIPTION = "수락된 전시 팀원이 카테고리 안의 이미지를 삭제합니다.";
  public static final String DELETE_CONTENT_SUCCESS_DESCRIPTION = "전시 콘텐츠 이미지 삭제 성공";
  public static final String DELETE_CONTENT_SUCCESS_EXAMPLE_NAME = "Display content delete success";

  public static final String REORDER_CONTENTS_SUMMARY = "전시 콘텐츠 이미지 순서 수정";
  public static final String REORDER_CONTENTS_DESCRIPTION =
      "수락된 전시 팀원이 카테고리 안의 이미지 순서를 수정합니다. 요청한 ID 순서대로 sortOrder가 0부터 다시 부여됩니다. "
          + "이 순서는 카테고리 콘텐츠 전용이며 전시 대표 이미지 조회 조건과는 별개입니다.";
  public static final String REORDER_CONTENTS_REQUEST_DESCRIPTION = "전시 콘텐츠 이미지 순서 수정 요청";
  public static final String REORDER_CONTENTS_REQUEST_EXAMPLE_NAME =
      "Display content reorder request";
  public static final String REORDER_CONTENTS_SUCCESS_DESCRIPTION = "전시 콘텐츠 이미지 순서 수정 성공";
  public static final String REORDER_CONTENTS_SUCCESS_EXAMPLE_NAME =
      "Display content reorder success";

  public static final String CATEGORY_REQUEST_EXAMPLE =
      """
      {
        "name": "전시장 전경",
        "description": "입구부터 메인 전시장까지의 공간 이미지입니다."
      }
      """;

  public static final String CREATE_CATEGORY_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "displayId": 12,
            "categoryId": 3,
            "name": "전시장 전경",
            "description": "입구부터 메인 전시장까지의 공간 이미지입니다.",
            "sortOrder": 0,
            "contents": []
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-07-24T12:00:00",
          "path": "/api/v1/displays/12/content-categories"
        }
      }
      """;

  public static final String UPDATE_CATEGORY_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "displayId": 12,
            "categoryId": 3,
            "name": "메인 전시장",
            "description": "메인 전시장 설치 전경입니다.",
            "sortOrder": 0,
            "contents": [
              {
                "categoryId": 3,
                "contentId": 7,
                "imageUrl": "https://cdn.displayu.com/display/content-7.jpg",
                "sortOrder": 0
              }
            ]
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-07-24T12:03:00",
          "path": "/api/v1/displays/12/content-categories/3"
        }
      }
      """;

  public static final String DELETE_CATEGORY_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "displayId": 12,
            "categoryId": 3
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-07-24T12:05:00",
          "path": "/api/v1/displays/12/content-categories/3"
        }
      }
      """;

  public static final String CONTENT_REQUEST_EXAMPLE =
      """
      {
        "imageUrl": "https://cdn.displayu.com/display/content-7.jpg"
      }
      """;

  public static final String CREATE_CONTENT_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "categoryId": 3,
            "contentId": 7,
            "imageUrl": "https://cdn.displayu.com/display/content-7.jpg",
            "sortOrder": 0
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-07-24T12:10:00",
          "path": "/api/v1/displays/12/content-categories/3/contents"
        }
      }
      """;

  public static final String UPDATE_CONTENT_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "categoryId": 3,
            "contentId": 7,
            "imageUrl": "https://cdn.displayu.com/display/content-7-updated.jpg",
            "sortOrder": 0
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-07-24T12:12:00",
          "path": "/api/v1/displays/12/content-categories/3/contents/7"
        }
      }
      """;

  public static final String DELETE_CONTENT_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "displayId": 12,
            "categoryId": 3,
            "contentId": 7
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-07-24T12:15:00",
          "path": "/api/v1/displays/12/content-categories/3/contents/7"
        }
      }
      """;

  public static final String REORDER_CONTENTS_REQUEST_EXAMPLE =
      """
      {
        "orderedContentIds": [9, 7, 8]
      }
      """;

  public static final String REORDER_CONTENTS_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "displayId": 12,
            "categoryId": 3,
            "contents": [
              {
                "categoryId": 3,
                "contentId": 9,
                "imageUrl": "https://cdn.displayu.com/display/content-9.jpg",
                "sortOrder": 0
              },
              {
                "categoryId": 3,
                "contentId": 7,
                "imageUrl": "https://cdn.displayu.com/display/content-7.jpg",
                "sortOrder": 1
              },
              {
                "categoryId": 3,
                "contentId": 8,
                "imageUrl": "https://cdn.displayu.com/display/content-8.jpg",
                "sortOrder": 2
              }
            ]
          }
        },
        "error": null,
        "meta": {
          "timestamp": "2026-07-24T12:20:00",
          "path": "/api/v1/displays/12/content-categories/3/contents/reorder"
        }
      }
      """;

  private DisplayContentApiDocs() {}
}

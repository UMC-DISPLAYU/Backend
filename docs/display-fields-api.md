# 전시 생성·수정 필드 API

관련 이슈는 [#597](https://github.com/UMC-DISPLAYU/Backend/issues/597)입니다.

## 수정 요청

`PATCH /api/v1/display`는 `displayId`와 변경할 필드만 받습니다. 아래 필드를 생략하거나 `null`로 보내면 기존 값을 유지합니다.

| 필드 | JSON 타입 | 규칙 |
| --- | --- | --- |
| `instagramUrl` | string | HTTP(S) URL, 최대 255자. 빈 문자열은 삭제 |
| `websiteUrl` | string | HTTP(S) URL, 최대 255자. 빈 문자열은 삭제 |
| `roadAddress` | string | 공백만 있는 주소는 허용하지 않음 |
| `latitude` | number | -90 이상 90 이하 |
| `longitude` | number | -180 이상 180 이하 |
| `region` | string | `SEOUL`, `GYEONGGI_INCHEON`, `OTHERS`. `ALL`은 허용하지 않음 |
| `displayImageUrl` | string[] | 대표 이미지 외 이미지 최대 4장. 전달한 배열로 전체 교체. 빈 배열은 전체 삭제 |

생성 요청의 외부 링크는 생략과 `null`을 허용하고 빈 문자열은 허용하지 않습니다. 수정 요청의 빈 문자열만 삭제 의미로 사용합니다. 추가 이미지의 각 URL은 빈 값일 수 없습니다.

```json
{
  "displayId": 123,
  "instagramUrl": "https://www.instagram.com/displayu",
  "websiteUrl": "",
  "roadAddress": "서울특별시 종로구 세종대로 175",
  "latitude": 37.5729,
  "longitude": 126.9769,
  "region": "SEOUL",
  "displayImageUrl": [
    "https://example.com/poster-detail-1.jpg",
    "https://example.com/poster-detail-2.jpg"
  ]
}
```

위 요청은 웹사이트 링크를 삭제하고 추가 이미지를 2장으로 교체합니다. 장소명과 대표 이미지 URL 등 전달하지 않은 필드는 유지합니다. 장소명은 기존 수정 필드인 `placeName`으로 변경합니다. 생성 요청의 장소명 필드는 `locationName`입니다.

수정 응답과 상세 조회 응답에서 외부 링크는 `instagramUrl`, `websiteUrl`로 반환합니다. 위치는 `location`, 지역은 `region`, 이미지는 `images`에 반환합니다. 추가 이미지의 요청 필드명은 단수형 `displayImageUrl`이며 값은 배열입니다.

## 전시 분야

생성·수정 요청의 `fields`는 다음 12개 값으로 구성합니다.

| 디자인 표시 | enum |
| --- | --- |
| 회화 | `PAINTING` |
| 조소 | `SCULPTURE` |
| 사진 | `PHOTOGRAPHY` |
| 그래픽 | `GRAPHIC` |
| 브랜딩 | `BRANDING` |
| 제품 | `PRODUCT` |
| 영상 | `VIDEO` |
| 공간 | `SPACE` |
| 건축 | `ARCHITECTURE` |
| UXUI | `UXUI` |
| 공예 | `CRAFTS` |
| 기타 | `OTHERS` |

기존 `DESIGN`, `FASHION`, `INTERDISCIPLINARY`, `ILLUSTRATION`은 저장 데이터와 조회·검색 호환을 위해 유지합니다. 생성·수정 요청의 `fields`에는 사용할 수 없습니다. 기존 값을 신규 분야로 자동 변환하지 않습니다.

기존 전시의 제목이나 주소만 수정할 때 `fields`를 생략하면 기존 분야를 보존합니다. 분야 자체를 수정할 때는 신규 12개 값 중에서 다시 선택합니다. 조회 응답에 기존 분야가 내려올 수 있으므로 FE는 기존 값의 표시도 유지해야 합니다.

## 대표 포스터 표시 범위

생성·수정 요청의 `posterCrop`은 원본 이미지에 대한 비율입니다. `x`, `y`는 왼쪽 위 시작점이고 `w`, `h`는 너비와 높이입니다.

```json
{
  "displayId": 123,
  "posterCrop": {
    "x": 0.1,
    "y": 0.2,
    "w": 0.8,
    "h": 0.6
  }
}
```

- 객체를 전달하면 네 값이 모두 필요합니다.
- 각 값은 0 이상 1 이하이며 소수점 이하 최대 10자리입니다.
- `w`, `h`는 0보다 커야 합니다.
- `x + w`, `y + h`는 1 이하여야 합니다.
- 생성 시 생략하거나 `null`이면 표시 범위를 저장하지 않습니다.
- 수정 시 생략하거나 `null`이면 기존 범위를 유지합니다.
- `posterImageUrl`을 다른 URL로 바꾸면서 범위를 보내지 않으면 기존 범위를 초기화합니다.
- 같은 대표 URL을 다시 보내면 기존 범위를 유지합니다.
- 전체 이미지를 표시하도록 되돌리려면 `{ "x": 0, "y": 0, "w": 1, "h": 1 }`을 보냅니다.

상세 응답과 포스터가 있는 카드 응답은 같은 구조의 `posterCrop`을 반환합니다. 기존 데이터와 범위가 초기화된 전시는 `null`입니다. FE는 `null`이면 기본 이미지 표시 방식을 사용합니다.

별도로 등록하는 디유픽의 `bannerImageUrl`은 대표 포스터가 아니므로 해당 배너에는 범위를 적용하지 않습니다.

표시 범위를 반환하는 GET 경로는 다음과 같습니다. 목록에서는 각 전시 항목에 `posterCrop`이 들어갑니다.

| 화면 | 경로 |
| --- | --- |
| 상세 | `/api/v1/display/{displayId}` |
| 인기 전시 | `/api/v1/display/popular` |
| 종료 임박 | `/api/v1/display/closing-soon` |
| 졸업 전시 | `/api/v1/display/graduation` |
| 탐색·검색 | `/api/v1/display/search` |
| 지도 | `/api/v1/display/map` |
| 내 전시 | `/api/v1/display/me` |
| 작가의 전시 | `/api/v1/display/artists/{userId}` |
| 아카이브 | `/api/v1/archives/exhibitions` |
| 초대받은 전시 | `/api/v1/display-invitations` |
| 내 초대 목록 | `/api/v1/display-invitations/me` |
| 관리자 심사 목록 | `/api/v1/admin/displays` |

## DB 변경

- `V20261111000600__expand_display_fields.sql`은 기존 enum을 보존하며 신규 분야 5개를 추가합니다.
- `V20261111000700__add_display_poster_crop.sql`은 nullable `DECIMAL(11,10)` 컬럼 4개와 범위 제약을 추가합니다.
- 조정 값은 네 컬럼이 모두 `null`이거나 유효한 사각형이어야 합니다.
- 새 애플리케이션 코드가 실행되기 전에 마이그레이션을 적용해야 합니다.

## 검증

기본 검증 명령은 다음과 같습니다. Gradle 테스트 작업은 `test` 프로필을 사용합니다.

```bash
./gradlew spotlessCheck clean build --no-daemon
```

- `DisplayControllerCreateTest`, `DisplayControllerUpdateTest`는 입력 검증, 저장 후 재조회, 생략·삭제·대표 이미지 교체 규칙을 확인합니다.
- `DisplayPosterCropTest`는 저장된 값을 사용해 목록 응답, 캐시 재조회, 아카이브·초대·관리자 변환과 기존 행의 `null`을 확인합니다.
- `DisplayOpenApiTest`는 생성·수정 분야 12개, 조정 값의 nullable 구조와 필수 좌표를 확인합니다.
- `DisplayCacheIntegrationTest`는 수정 커밋 후 목록 캐시 무효화를 확인합니다.
- `DisplayDetailImagesTest`는 기존 이미지 순서의 행을 재사용하고 잘못된 입력에서 기존 상태를 보존하는지 확인합니다.

이 작업에서는 MySQL 8.4 격리 DB에도 전체 SQL 마이그레이션을 적용했습니다. 기존 enum 보존, 신규 enum 저장, 기존 행의 조정 값 `null`, DB 범위 제약을 확인했습니다. 개발 환경의 JPA 이름 규칙을 적용한 별도 실행에서 이미지 교체·삭제와 조정 값 생성·수정·목록 응답 테스트 7개를 통과했습니다. 테스트 픽스처의 가상 사용자 ID 때문에 해당 실행의 외래 키 검사는 비활성화했으며, 이미지 순서 UNIQUE와 포스터 범위 CHECK 제약은 유지했습니다.

추가 이미지 교체는 기존 순서의 행을 재사용합니다. 전체 삭제 후 재삽입하면 Hibernate의 INSERT 순서 때문에 MySQL의 활성 이미지 순서 UNIQUE 제약과 충돌할 수 있습니다.

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

# 영상 업로드 API

전시 포스터와 전시 작품은 이미지 또는 영상을 저장한다. 기존 이미지 URL 요청은 계속 지원한다. 영상 길이는 프론트가 검사하고 서버는 발급 시 신고 용량과 등록 시 S3 객체의 실제 용량을 검사한다.

## 정책

| 항목 | 전시 포스터 | 작품 |
| --- | --- | --- |
| 미디어 개수 | 대표 1개와 추가 최대 4개 | 연작 전체에서 ARTWORK 최대 10개 |
| 영상 개수 | 최종 전체 목록에서 최대 1개 | ARTWORK 10개 안에서 여러 개 허용 |
| 영상 크기 | 200,000,000 bytes 이하 | 500,000,000 bytes 이하 |
| 길이 | 프론트에서 30초 이하 검사 | 프론트에서 180초 이하 검사 |
| 업로드 domain | display | artwork |

WORK_PROCESS는 이미지만 허용한다. 영상 썸네일은 같은 domain의 IMAGE로 업로드한다. 서버는 영상 원본과 썸네일의 존재, 양수 크기, 해당 용도 상한, Content-Type 메타데이터를 확인한다. 영상은 MP4, MOV, WEBM을 허용한다. 썸네일은 JPEG, PNG, WEBP, GIF를 허용한다.

Content-Type은 실제 코덱 검사 결과가 아니다. 서버는 영상 변환이나 길이 분석을 수행하지 않는다. 실제 브라우저 재생 가능 여부는 내부 코덱에 따라 다르다.

## 업로드 순서

1. 프론트에서 파일 크기와 영상 길이를 확인한다.
2. POST /api/v1/files/presigned-url로 영상과 썸네일 URL을 각각 발급받는다.
3. 발급 요청과 동일한 Content-Type으로 각 uploadUrl에 PUT한다.
4. 두 파일 업로드가 모두 성공한 뒤 아래 미디어 정보를 등록하거나 수정한다.

발급 예시:

```json
{"fileType":"VIDEO","domain":"display","fileName":"poster.mp4","contentType":"video/mp4","fileSize":1024000}
```

서버는 IMAGE 업로드에 대한 기존 정책을 유지한다. VIDEO domain은 display와 artwork만 허용한다. 파일 크기 단위는 decimal bytes이다.

## 전시 요청

POST와 PATCH /api/v1/display에 다음 필드를 추가한다. 아래 JSON은 전체 요청이 아닌 미디어 부분이다.

```json
{
  "posterMedia": {
    "imageUrl": "https://cdn.example/videos/display/2026/11/generated-poster.mp4",
    "mediaType": "VIDEO",
    "thumbnailUrl": "https://cdn.example/images/display/2026/11/generated-thumbnail.png"
  },
  "displayMedia": [
    {"imageUrl":"https://cdn.example/images/display/2026/11/generated-photo.png","mediaType":"IMAGE"}
  ]
}
```

실제로는 발급 응답의 fileUrl을 그대로 사용한다. VIDEO와 썸네일 URL은 설정된 CDN과 용도 경로에 속해야 한다. query, fragment, 인코딩된 경로와 경로 이동은 허용하지 않는다.

| 입력 | 처리 |
| --- | --- |
| posterImageUrl | 기존 이미지 대표 입력 |
| posterMedia | 이미지 또는 영상 대표 입력 |
| 두 대표 필드 동시 제공 | 400 |
| displayImageUrl | 기존 추가 이미지 URL 배열 |
| displayMedia | 추가 미디어 객체 배열 |
| 두 추가 목록 동시 제공 | 400 |
| POST에서 대표 미입력 | 400 |
| PATCH 필드 생략 또는 null | 해당 그룹 유지 |
| PATCH 추가 목록 빈 배열 | 추가 미디어 전체 제거 |
| IMAGE의 thumbnailUrl 제공 | 400 |
| VIDEO의 thumbnailUrl 누락 | 400 |

새 전시 미디어 객체의 mediaType은 필수다. 기존 URL 입력은 IMAGE로 해석한다. 이미 저장된 영상 원본 URL을 IMAGE로 되돌려 보내는 수정은 거절한다.

대표와 추가 목록의 최종 상태를 함께 검증한다. 하나의 영상이 추가 목록에서 대표로 이동하는 요청도 허용한다. 대표가 영상이면 posterCrop은 null이다. 영상에 crop을 함께 보내면 400이다. 대표를 교체하면 이전 crop을 초기화한다. 같은 이미지와 생략한 crop은 기존 범위를 유지한다.

## 작품 요청

POST /api/v1/artworks와 PATCH /api/v1/artworks/{artworkId}의 기존 images 항목에 mediaType과 thumbnailUrl을 추가한다.

```json
{
  "imageUrl": "https://cdn.example/videos/artwork/2026/11/generated-work.mp4",
  "mediaType": "VIDEO",
  "thumbnailUrl": "https://cdn.example/images/artwork/2026/11/generated-cover.png",
  "imageType": "ARTWORK",
  "isThumbnail": true,
  "sortOrder": 0,
  "width": 160,
  "height": 90
}
```

기존 요청에서 mediaType을 생략하면 IMAGE이다. imageType은 ARTWORK와 WORK_PROCESS 용도를 구분한다. isThumbnail은 대표 항목 선택이다. thumbnailUrl은 영상 미리보기 이미지다. 영상의 width와 height는 카드에 표시할 썸네일 크기다. 작품 PATCH는 기존과 같이 최종 목록 전체를 교체한다. 이미 저장된 영상 원본을 mediaType 없이 다시 보내 이미지로 바꾸는 요청은 거절한다.

### 연작 등록과 수정

작품 1의 작품명·제작 연도·재료/매체·규격은 기존 최상위 필드에 보낸다. 작품 2부터는 화면에 표시할 순서대로 `parts`에 담는다. 각 항목의 `artworkName`은 필수이고 `productionYear`, `materialMedia`, `size`는 선택이다. 작가·작품 설명·분야·작업 과정은 출품작 전체가 공유한다.

- `ARTWORK`의 `partIndex`는 0부터 시작한다. 생략하거나 0이면 작품 1, 1이면 `parts[0]`이다. `WORK_PROCESS`에는 인덱스를 보내지 않는다.
- 작품 2부터는 각 작품에 `ARTWORK` 사진(`IMAGE`)을 1장 이상 넣는다. 영상 썸네일과 작업 과정 사진은 이 장수에서 제외한다. 같은 작품에 영상 여러 개를 올릴 수 있다.
- `isThumbnail`은 작품 1의 미디어에 지정한다. `artworkCrop`과 `artworkFit`은 이 대표 이미지에 적용한다.
- PATCH는 화면에 남은 작품과 미디어를 `parts`와 `images`에 보낸다. 중간 작품을 지우거나 순서를 바꾸면 `partIndex`도 새 순서에 맞춘다. `parts`를 생략하면 기존 추가 작품을 유지하고, `[]`를 보내면 모두 제거한다.

등록·수정 응답과 상세·수정 화면 조회에서 같은 인덱스를 쓴다. 예를 들어 상세 화면에서 `partIndex: 1`인 사진을 보여 줄 때는 `parts[0].artworkName`을 표시한다. 단일 작품의 응답에는 `parts: []`가 온다.

## 조회 규칙

상세와 수정용 images[].imageUrl은 원본 URL이다. mediaType과 thumbnailUrl을 함께 반환한다. 카드의 posterImageUrl, postImageUrl, artworkImageUrl, thumbnailUrl은 항상 표시할 이미지 URL이며 영상 대표에는 썸네일을 반환한다.

홈·검색·내 전시 카드에는 posterVideoUrl을 추가한다. 전시별 작품 목록에는 artworkVideoUrl을 추가한다. 이미지 대표의 영상 URL은 null이다. 지도, 작가 프로필, 저장 목록, 초대, 작품 상세의 연결 전시 썸네일에도 같은 이미지 선택 규칙을 적용한다.

## 실패 처리

| 상황 | 처리 |
| --- | --- |
| 신고 크기 또는 실제 크기 초과 | 400 INVALID_INPUT_VALUE |
| 잘못된 미디어 조합이나 용도 경로 | 400 INVALID_INPUT_VALUE |
| 작품의 개수·작업 과정 영상·기존 영상 타입 변경 | 400 INVALID_ARTWORK_MEDIA |
| S3에서 파일 없음 확인 | 400 INVALID_INPUT_VALUE |
| 허용하지 않는 Content-Type | 415 UNSUPPORTED_MEDIA_TYPE |
| S3 권한 또는 통신 오류 | 502 EXTERNAL_API_ERROR |
| 영상 저장소 설정 누락 | 503 SERVICE_UNAVAILABLE |

영상 성공 후 썸네일이 실패하면 썸네일만 다시 업로드하고 등록한다. 발급 URL이 만료됐으면 재발급한다. 파일 업로드 후 등록이 실패하면 업로드 결과를 보관하고 등록만 재시도한다. 수정 실패 시 DB 트랜잭션이 기존 미디어를 보존한다.

S3 HEAD는 등록 시점의 크기와 메타데이터를 확인한다. 사용자별 업로드 소유권이나 이후 재업로드 불변성을 보장하지 않는다. 기존 presigned PUT URL은 만료 전 같은 키를 덮어쓸 수 있다.

## 파일 정리 정책

교체하거나 삭제한 미디어는 DB 커밋 후 정리한다. 현재 다른 데이터가 참조하는 파일은 보존하고 저장소 삭제 실패는 재시도한다. 구체적인 참조 검사 범위와 남는 동시성 제약은 구현 검증 결과에 기록한다.

등록되지 않은 업로드는 자동 삭제하지 않는다. 업로드 기록 없이 images/ 또는 videos/에 일괄 만료 정책을 적용하면 등록된 파일도 삭제할 수 있다. 자동 정리가 필요하면 업로드 기록과 첨부 상태를 별도 설계한다.

## 배포와 프론트 연동

DB 마이그레이션과 백엔드 API를 먼저 배포한다. 기존 이미지 클라이언트는 기존 요청을 유지할 수 있다. 프론트는 길이 검사, 썸네일 업로드, 영상 필드 복원, 자동 재생 실패 시 썸네일 표시를 연결한 뒤 영상 입력을 노출한다.

영상이 등록된 뒤에는 구버전 백엔드로 단순히 롤백하지 않는다. 구버전은 원본 URL을 이미지로 반환하므로 카드가 깨질 수 있다. 영상 입력을 중단하고 미디어 필드를 이해하는 호환 버전으로 복구한다. 두 미디어 열을 제거하면 영상 타입과 썸네일을 잃는다.

관리자 심사 상세는 기존 imageUrls에 표시용 이미지를 유지하고 media 배열에 imageUrl, mediaType, thumbnailUrl을 함께 반환한다. 두 목록의 항목 순서는 같다.

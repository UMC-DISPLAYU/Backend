# 프론트 요청 API 정합성

관련 이슈 [#614](https://github.com/UMC-DISPLAYU/Backend/issues/614). 기존 응답 래퍼 `success.data`를 유지합니다.

## 분야와 작품 입력

작가 활동 분야, 전시 분야, 전시 작품 분야의 신규 선택 값은 다음 12개입니다.

`PAINTING`, `SCULPTURE`, `PHOTOGRAPHY`, `GRAPHIC`, `BRANDING`, `PRODUCT`, `VIDEO`, `SPACE`, `ARCHITECTURE`, `UX_UI`, `CRAFTS`, `OTHERS`.

| 기존 입력 | 저장·응답 표준 값 |
| --- | --- |
| `UXUI` | `UX_UI` |
| `MEDIA` | `VIDEO` |
| `CRAFT` | `CRAFTS` |
| `ETC` | `OTHERS` |

이 별칭은 JSON 입력과 분야 검색에 적용합니다. DB의 과거 별칭 행은 일괄 변경하지 않으며 표준 값 필터로도 검색됩니다. `DESIGN`, `FASHION`, `ILLUSTRATION`, `COMPLEX`, `INTERDISCIPLINARY` 등 각 도메인에 존재하던 모호한 과거 값은 그대로 조회할 수 있고, 이미 선택된 값을 수정 요청에 다시 보내 유지할 수 있습니다. 신규 선택과 삭제한 과거 값의 재선택은 거절합니다.

작가 생성은 기존 `activityFields`, 수정·조회는 `fields`를 유지합니다. 작가는 최대 2개, 작품 생성·수정의 `types`는 최대 3개입니다. 기존 단일 `type` 입력도 지원합니다. 작품은 `types`가 있으면 이를 사용하고 첫 분야를 `type`으로 반환합니다. 개인 작품 API의 분야 계약은 이번 범위에 포함하지 않습니다.

전시 작품 `materialMedia`는 선택 입력입니다. 생략·null·공백은 DB NULL로 저장합니다. 작품 PATCH는 기존처럼 내용 전체를 전달하는 방식이므로 수정 요청에서 생략해도 기존 재료 값을 비웁니다.

## 전시 탐색과 지도

`GET /api/v1/display/search`의 `searchWord`는 전시명·학교명(`organization`)·세부 소속(`department`)의 부분 일치를 검색합니다.

- `sort=LATEST`가 기본이며 기존 등록 ID 내림차순을 유지합니다.
- `sort=LIKES`는 현재 활성 좋아요 수 내림차순, 같은 수에서는 ID 내림차순입니다.
- 좋아요순 다음 페이지는 응답 `nextCursor`와 `nextCursorLikeCount`를 각각 `cursor`, `cursorLikeCount`로 함께 보냅니다.
- 페이지 사이 좋아요 수가 바뀌는 경우 전체 조회 시점의 스냅샷은 보장하지 않습니다.

`GET /api/v1/display/map`의 기존 필수 좌표 범위는 유지합니다. 검색은 전시명·장소명·학교명·세부 소속에 적용하며, 한국 날짜 기준 오늘 종료하는 전시와 이후 전시만 반환합니다. 공개되지 않았거나 삭제된 전시는 제외합니다.

`all=true`이면 해당 좌표 범위의 모든 결과를 반환합니다. `size`는 무시하며 `cursor`와 함께 보내면 400입니다. `pagination`은 `nextCursor=null`, `hasNext=false`, `size=실제 반환 개수`입니다. `all` 생략 시 기존 페이지 조회를 유지합니다.

## 작품 검색과 홈 추천

`GET /api/v1/artworks/search`는 다음 파라미터를 받습니다.

| 파라미터 | 규칙 |
| --- | --- |
| `searchWord` | 작품명 또는 대표·공동 작가명의 부분 일치. 이름만 등록한 작가도 포함하고 Q&A 전용 담당자는 제외 |
| `type` | 작품이 속한 전시의 기존 `DisplayType` 값 |
| `status` | 전시 기간 기준 `UPCOMING`, `ONGOING`, `ENDED`, `CLOSING_SOON` |
| `page` | 0부터 시작, 기본 0 |
| `size` | 1~100, 기본 20 |

공개 상태이며 삭제되지 않은 전시·작품만 ID 내림차순으로 반환합니다. `%`, `_`는 검색어 문자 그대로 처리합니다. 응답은 미리보기와 같은 `artworks`, `page`, `size`, `isLast` 구조이며 작품 카드에 `artworkCrop`을 포함합니다. 작품 목록 `GET /api/v1/artworks?displayId=...`에는 기존 대표 `artistUserId`와 함께 `artistUserIds`를 추가합니다. 대표·공동 작가의 계정 ID만 중복 없이 포함하고 계정 없는 작가와 Q&A 전용 담당자는 제외합니다.

`GET /api/v1/artworks/preview?type=RECOMMEND`는 분야·학교 필터를 먼저 적용합니다. 작품이 있는 전시를 그 전시의 가장 최근 최초 공개 시각 내림차순으로 정렬합니다. 전시 안에서는 작품의 최초 공개 시각 내림차순으로 정렬한 뒤 전시마다 한 작품씩 순환합니다. 작품이 소진된 전시는 건너뛰고 최대 10개에서 멈춥니다. 시각이 같으면 전시·작품 ID 내림차순으로 결정합니다.

추천의 기본 `page=0`, 응답 `size=10`, `isLast=true`입니다. 기존 페이지 클라이언트의 중복 표시를 막기 위해 `page>0`은 빈 마지막 페이지를 반환합니다. `GRADUATION`은 기존 페이지 조회를 유지하며 다음 페이지 경계에서 작품을 건너뛰지 않도록 offset을 수정했습니다. 분야 필터는 최대 2개이며 선택한 분야를 모두 포함하는 작품을 조회합니다.

새 작품은 실제 최초 공개 순간을 UTC로 저장합니다. 전시 공개·예약 공개의 일괄 처리도 동일하며 재공개·재처리는 값을 바꾸지 않습니다. 과거 공개 작품은 정확한 공개 이력이 없어 `createdAt`을 근사값으로 이관합니다. 과거 초안은 공개되기 전까지 null입니다.

## 팀원과 섹션

`GET /api/v1/display/{displayId}/members`의 `memberAccept`, `memberPending` 각 항목에 기본 닉네임 `nickname`을 추가합니다. 전시 작가명 필드는 유지합니다. 대기 항목은 `invitationId`를 함께 반환하며 수락된 항목은 null입니다.

`DELETE /api/v1/display-invitations/{invitationId}`는 초대 관리 권한을 가진 팀 리더가 대기 초대를 취소합니다. 취소 상태는 `CANCELLED`입니다. 같은 권한으로 재취소하면 성공하며, 수락·거절된 초대는 409입니다. 취소된 초대는 수락할 수 없고 재초대할 수 있습니다. 수락·취소는 같은 초대 행을 잠가 처리합니다.

`PATCH /api/v1/display/{displayId}/content-categories/reorder`에 다음을 보냅니다.

```json
{"orderedCategoryIds": [32, 30, 31]}
```

해당 전시의 모든 섹션 ID를 중복 없이 보내야 합니다. 누락·중복·타 전시 ID는 400이며 빈 전시는 빈 배열을 허용합니다. 동시 순서 변경 충돌은 409입니다. 상세 조회도 저장된 순서로 반환합니다.

섹션 생성의 선택 필드 `imageUrls`에 사진 1~20개 URL을 보내면 섹션과 사진을 한 트랜잭션에서 저장합니다. 명시적인 빈 배열은 거절합니다. FE 전환 기간 동안 필드 생략·null은 기존 빈 섹션 생성 흐름을 유지합니다. `description`은 선택 입력으로 유지하며 마지막 사진 삭제 정책은 변경하지 않습니다.

## 대표 이미지 크롭과 HEIC

작품 생성·수정에 `artworkCrop`을 받습니다. `x`, `y`는 원본 대비 왼쪽 위 위치, `w`, `h`는 너비·높이 비율입니다. 각 값은 0~1, 소수점 이하 최대 10자리이며 너비·높이는 양수, `x+w`, `y+h`는 1 이하여야 합니다.

```json
{"artworkCrop": {"x": 0.1, "y": 0.2, "w": 0.8, "h": 0.6}}
```

이미지 대표에는 원본 이미지, 영상 대표에는 `thumbnailUrl` 기준으로 적용합니다. 수정 시 크롭을 생략·null로 보내면 실제 카드 이미지 URL이 같은 동안 유지합니다. 카드 이미지가 바뀌면 초기화하며 명시한 새 크롭이 우선합니다. 영상 파일만 바뀌고 썸네일이 같으면 유지합니다. 전체 이미지로 되돌리려면 `{ "x": 0, "y": 0, "w": 1, "h": 1 }`을 보냅니다.

생성·수정·상세·수정용 조회와 전시 작품 목록, 홈 미리보기에서 크롭을 반환합니다. 기존 작품은 null입니다.

`GET /api/v1/display/du-picks`의 칼럼 배너는 `posterCrop`을 반환합니다. 관리자 `PATCH /api/v1/admin/du-picks/{duPickId}/poster-crop`에 `{ "posterCrop": {...} }`을 보내 저장하며 null·생략은 해제입니다. 저장 후 목록 캐시를 무효화합니다.

`POST /api/v1/files/presigned-url`은 `contentType=image/heic`을 허용합니다. 서명 발급과 업로드 검증을 지원하며 이미지 변환은 수행하지 않습니다. 클라이언트별 HEIC 렌더링 지원은 별도 확인이 필요합니다.

## DB 적용

새 코드 실행 전에 `V20261115000300`부터 `V20261115000800`까지 적용합니다. 기존 enum 값 보존, 재료 NULL 허용, 초대 취소 상태, 최초 공개 시각, 작품·칼럼 크롭 컬럼을 추가합니다. 이미 배포된 마이그레이션 파일은 수정하지 않습니다.

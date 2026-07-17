# Presigned URL Upload

## 1. 목적

이미지 파일은 백엔드 서버를 거치지 않고 프론트엔드가 S3에 직접 업로드한다.
백엔드는 업로드 가능한 presigned PUT URL을 발급하고, 조회에 사용할 CloudFront URL을 함께 내려준다.

```text
Frontend -> Backend: presigned URL 요청
Backend -> Frontend: uploadUrl + fileKey + fileUrl 반환
Frontend -> S3: uploadUrl로 PUT 업로드
Frontend -> Backend: 도메인 생성/수정 API에 fileUrl 또는 fileKey 전달
Client -> CloudFront: fileUrl로 이미지 조회
```

CloudFront는 업로드 대상이 아니다. 업로드는 S3로 하고, 조회만 CloudFront URL을 사용한다.

## 2. 환경변수

서버 런타임에는 다음 값이 필요하다.

```env
AWS_REGION=ap-northeast-2
S3_BUCKET=displayu-s3-demo
CDN_BASE_URL=https://d1tdgnysscm2va.cloudfront.net
PRESIGNED_URL_EXPIRATION_SECONDS=3600
```

규칙:

- `S3_BUCKET`에는 버킷 URI가 아니라 버킷 이름만 넣는다.
- `CDN_BASE_URL`에는 `https://`를 포함하고 마지막 `/`는 붙이지 않는다.
- `PRESIGNED_URL_EXPIRATION_SECONDS`는 기본 3600초이며, 업로드 URL에만 적용된다.
- EC2 IAM Role을 사용하는 경우 서버 `.env`에는 `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`를 넣지 않는다.
- EC2 IAM Role에는 최소 `s3:PutObject`, 필요 시 `s3:GetObject` 권한을 버킷 객체 경로에 부여한다.

## 3. API Spec

### Presigned URL 발급

```text
POST /api/v1/files/presigned-url
```

Request Body:

```json
{
  "fileType": "IMAGE",
  "domain": "display",
  "fileName": "poster.png",
  "contentType": "image/png",
  "fileSize": 1048576
}
```

필드 규칙:

- `domain`: 파일을 사용하는 기능 영역. 예: `display`, `artwork`, `lounge`, `profile`
- `fileType`: 최상위 경로. `IMAGE`는 `images`, `VIDEO`는 `videos`로 저장한다.
- `fileName`: 원본 파일명. 확장자 추출과 디버깅 용도로 사용한다.
- `contentType`: S3 PUT 요청 시 사용할 MIME type. presigned URL 생성 시점과 프론트 PUT 요청에서 동일해야 한다.
- `fileSize`: 파일 크기. 서버 검증용이며, presigned PUT 자체가 업로드 크기를 강제하지는 않는다.

Response Body:

```json
{
  "resultType": "SUCCESS",
  "success": {
    "data": {
      "uploadUrl": "https://displayu-s3-demo.s3.ap-northeast-2.amazonaws.com/images/display/2026/07/uuid-poster.png?...",
      "fileKey": "images/display/2026/07/uuid-poster.png",
      "fileUrl": "https://d1tdgnysscm2va.cloudfront.net/images/display/2026/07/uuid-poster.png",
      "expiresIn": 3600
    }
  },
  "error": null,
  "meta": {
    "timestamp": "2026-07-17T12:00:00",
    "path": "/api/v1/files/presigned-url"
  }
}
```

프론트엔드는 `uploadUrl`로 파일 바이너리를 `PUT` 업로드하고, 도메인 생성/수정 API에는 `fileUrl` 또는 `fileKey`를 전달한다.
현재 프로젝트는 이미지 URL을 저장하는 구조가 많으므로 초기 구현은 `fileUrl` 저장 방식과 맞춘다.

## 4. Frontend PUT 요청

프론트엔드는 백엔드에서 받은 `uploadUrl`에 다음처럼 요청한다.

```http
PUT {uploadUrl}
Content-Type: image/png

(binary file)
```

주의사항:

- `Content-Type`은 presigned URL 요청 때 보낸 값과 같아야 한다.
- S3 CORS에 프론트엔드 Origin과 `PUT` 메서드가 허용되어 있어야 한다.
- presigned URL의 1시간 만료는 업로드 URL에만 적용된다. CloudFront 조회 URL은 별도 만료가 없다.
- 이미지 교체 시 같은 key를 재사용하면 CloudFront 캐시 때문에 이전 이미지가 보일 수 있으므로 UUID 기반 새 key를 생성한다.

## 5. S3 Key 규칙

초기 구현에서는 다음 형식을 사용한다.

```text
{images|videos}/{domain}/{yyyy}/{MM}/{uuid}-{sanitizedFileName}
```

예:

```text
images/display/2026/07/018f3a4c-poster.png
videos/artwork/2026/07/018f3a4c-making.mp4
```

규칙:

- key는 백엔드에서만 생성한다.
- 프론트엔드가 임의 key를 지정하지 않는다.
- `fileType`으로 1차 경로를 `images` 또는 `videos`로 나눈다.
- 원본 파일명은 경로 구분자와 위험 문자를 제거한 뒤 사용한다.
- 같은 파일명이라도 UUID를 붙여 충돌과 CloudFront 캐시 문제를 줄인다.

## 6. 구현 플랜

### 1단계: 의존성 추가

`build.gradle`에 AWS SDK S3 presigner 사용을 위한 의존성을 추가한다.

```text
software.amazon.awssdk:s3
```

### 2단계: 설정 추가

`application-dev.yaml` 또는 공통 설정에 환경변수 매핑을 추가한다.

```yaml
app:
  file:
    storage:
      region: ${AWS_REGION:ap-northeast-2}
      bucket: ${S3_BUCKET}
      cdn-base-url: ${CDN_BASE_URL}
      presigned-url-expiration-seconds: ${PRESIGNED_URL_EXPIRATION_SECONDS:3600}
```

구현 시 `@ConfigurationProperties`로 묶어 관리한다.

### 3단계: 패키지 생성

공통 파일 기능이므로 `global/file` 아래에 배치한다.

```text
src/main/java/com/example/demo/global/file
├── application
│   ├── CreatePresignedUrlService.java
│   ├── command
│   │   └── CreatePresignedUrlCommand.java
│   └── result
│       └── PresignedUrlResult.java
├── infrastructure
│   ├── S3PresignedUrlGenerator.java
│   └── S3FileKeyGenerator.java
└── presentation
    ├── FileController.java
    ├── mapper
    │   └── FilePresentationMapper.java
    ├── request
    │   └── CreatePresignedUrlRequest.java
    └── response
        └── PresignedUrlResponse.java
```

단순 기능이므로 별도 도메인 모델이나 Repository는 만들지 않는다.

### 4단계: Request/Response 작성

`CreatePresignedUrlRequest`는 입력과 validation만 담당한다.
Command 변환은 `FilePresentationMapper`에서 처리한다.

필수 validation:

- `domain`: blank 불가
- `fileName`: blank 불가
- `contentType`: blank 불가
- `fileSize`: 양수

초기 허용 content type:

```text
image/jpeg
image/png
image/webp
image/gif
video/mp4
video/quicktime
video/webm
```

### 5단계: Service 구현

`CreatePresignedUrlService` 책임:

- 입력 command 검증
- S3 key 생성 요청
- 1시간 만료 presigned PUT URL 생성 요청
- CloudFront `fileUrl` 조립
- result 반환

트랜잭션은 DB 작업이 없으므로 붙이지 않는다.

### 6단계: Infrastructure 구현

`S3PresignedUrlGenerator` 책임:

- `S3Presigner`로 presigned PUT URL 생성
- `bucket`, `key`, `contentType`, `expiresIn` 사용

`S3FileKeyGenerator` 책임:

- fileType/domain/date/uuid/fileName 기반 key 생성
- 파일명 sanitize

### 7단계: Controller 구현

`FileController`:

```text
POST /api/v1/files/presigned-url
```

응답은 기존 공통 응답 형식인 `ApiResponseBody.success(data, request)`를 사용한다.

### 8단계: Swagger 문서화

초기 구현은 현재 `DisplayController`처럼 Controller에 Swagger 애너테이션을 직접 작성한다.
추후 컨벤션 통일 시 docs interface 분리 여부를 결정한다.

문서화 항목:

- 요청 필드 설명
- 응답 예시
- 지원 content type
- presigned URL 사용 방법

### 9단계: 검증

최소 검증:

- `./gradlew test`
- `./gradlew spotlessJavaCheck`
- 로컬 또는 dev 환경에서 presigned URL 발급 확인
- 발급된 `uploadUrl`로 실제 `PUT` 업로드 확인
- `fileUrl` CloudFront 조회 확인

## 7. 구현 시 주의사항

- presigned URL 발급은 파일 메타데이터 저장이 아니다. DB 저장은 전시 생성/수정 등 도메인 API에서 처리한다.
- 프론트엔드가 CloudFront 도메인을 직접 알 필요는 없다. 백엔드 응답의 `fileUrl`을 사용한다.
- CloudFront 접근 권한은 서버 IAM Role에 필요하지 않다. CloudFront Origin/OAC/S3 Bucket Policy 설정으로 처리한다.
- 파일 삭제 API가 추가되기 전까지는 `s3:DeleteObject` 권한을 부여하지 않아도 된다.
- 파일 크기 제한을 강하게 보장해야 하는 경우 presigned PUT 대신 presigned POST 정책 기반 업로드를 검토한다.

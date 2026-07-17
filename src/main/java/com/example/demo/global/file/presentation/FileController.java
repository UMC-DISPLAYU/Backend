package com.example.demo.global.file.presentation;

import com.example.demo.global.file.application.CreatePresignedUrlService;
import com.example.demo.global.file.application.result.PresignedUrlResult;
import com.example.demo.global.file.presentation.mapper.FilePresentationMapper;
import com.example.demo.global.file.presentation.request.CreatePresignedUrlRequest;
import com.example.demo.global.file.presentation.response.PresignedUrlResponse;
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "File", description = "파일 업로드 API")
@RestController
public class FileController {

  private static final String CREATE_REQUEST_EXAMPLE =
      """
      {
        "fileType": "IMAGE",
        "domain": "display",
        "fileName": "poster.png",
        "contentType": "image/png",
        "fileSize": 1048576
      }
      """;

  private static final String CREATE_SUCCESS_EXAMPLE =
      """
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
      """;

  private final CreatePresignedUrlService createPresignedUrlService;
  private final FilePresentationMapper mapper;

  public FileController(
      CreatePresignedUrlService createPresignedUrlService, FilePresentationMapper mapper) {
    this.createPresignedUrlService = createPresignedUrlService;
    this.mapper = mapper;
  }

  @PostMapping("/api/v1/files/presigned-url")
  @Operation(
      summary = "Presigned URL 발급",
      description =
          "프론트엔드가 S3에 직접 파일을 업로드할 수 있는 1시간 유효 presigned PUT URL과 CloudFront 조회 URL을 발급합니다. "
              + "지원 contentType: IMAGE(image/jpeg, image/png, image/webp, image/gif), "
              + "VIDEO(video/mp4, video/quicktime, video/webm).")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "Presigned URL 발급 요청",
      required = true,
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "기본 요청", value = CREATE_REQUEST_EXAMPLE)))
  @ApiResponse(
      responseCode = "200",
      description = "Presigned URL 발급 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "성공 응답", value = CREATE_SUCCESS_EXAMPLE)))
  public ApiResponseBody<PresignedUrlResponse> createPresignedUrl(
      @Valid @RequestBody CreatePresignedUrlRequest createPresignedUrlRequest,
      HttpServletRequest request) {
    PresignedUrlResult result =
        createPresignedUrlService.createPresignedUrl(mapper.toCommand(createPresignedUrlRequest));
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }
}

package com.example.demo.global.file.infrastructure;

import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.file.application.FileType;
import com.example.demo.global.file.application.MediaUsage;
import com.example.demo.global.file.application.port.MediaStorageVerifier;
import com.example.demo.global.file.domain.MediaContent;
import com.example.demo.global.file.domain.MediaType;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Component
public class S3MediaStorageVerifier implements MediaStorageVerifier {

  private final S3Client s3Client;
  private final FileStorageProperties properties;

  public S3MediaStorageVerifier(S3Client s3Client, FileStorageProperties properties) {
    this.s3Client = s3Client;
    this.properties = properties;
  }

  @Override
  public void verifyVideo(MediaContent media, MediaUsage usage) {
    if (media.mediaType() == MediaType.IMAGE) {
      return;
    }
    if (!properties.isConfigured()) {
      throw new BusinessException(GlobalErrorCode.SERVICE_UNAVAILABLE, "파일 저장소 설정이 올바르지 않습니다.");
    }
    ManagedMediaUrls urls = new ManagedMediaUrls(properties);
    String videoKey = urls.requireKey(media.imageUrl(), FileType.VIDEO, usage);
    String thumbnailKey = urls.requireKey(media.thumbnailUrl(), FileType.IMAGE, usage);
    verifyObject(videoKey, FileType.VIDEO, usage);
    verifyObject(thumbnailKey, FileType.IMAGE, usage);
  }

  private void verifyObject(String fileKey, FileType type, MediaUsage usage) {
    HeadObjectResponse object;
    try {
      object =
          s3Client.headObject(
              HeadObjectRequest.builder().bucket(properties.bucket()).key(fileKey).build());
    } catch (S3Exception exception) {
      if (exception.statusCode() == 404) {
        throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE, "업로드한 파일을 찾을 수 없습니다.");
      }
      throw new BusinessException(GlobalErrorCode.EXTERNAL_API_ERROR, exception);
    } catch (SdkException exception) {
      throw new BusinessException(GlobalErrorCode.EXTERNAL_API_ERROR, exception);
    }
    if (object.contentLength() == null
        || object.contentLength() <= 0
        || object.contentLength() > usage.maxVideoBytes()) {
      throw new BusinessException(
          GlobalErrorCode.INVALID_INPUT_VALUE, "업로드한 파일의 실제 크기가 허용 범위를 벗어났습니다.");
    }
    if (!type.supportsContentType(object.contentType())) {
      throw new BusinessException(
          GlobalErrorCode.UNSUPPORTED_MEDIA_TYPE, "업로드한 파일의 Content-Type이 올바르지 않습니다.");
    }
  }
}

package com.example.demo.global.file.application;

import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.file.application.command.CreatePresignedUrlCommand;
import com.example.demo.global.file.application.result.PresignedUrlResult;
import com.example.demo.global.file.infrastructure.FileStorageProperties;
import com.example.demo.global.file.infrastructure.S3FileKeyGenerator;
import com.example.demo.global.file.infrastructure.S3PresignedUrlGenerator;
import java.time.Duration;
import org.springframework.stereotype.Service;

@Service
public class CreatePresignedUrlService {

  private final FileStorageProperties properties;
  private final S3FileKeyGenerator fileKeyGenerator;
  private final S3PresignedUrlGenerator presignedUrlGenerator;

  public CreatePresignedUrlService(
      FileStorageProperties properties,
      S3FileKeyGenerator fileKeyGenerator,
      S3PresignedUrlGenerator presignedUrlGenerator) {
    this.properties = properties;
    this.fileKeyGenerator = fileKeyGenerator;
    this.presignedUrlGenerator = presignedUrlGenerator;
  }

  public PresignedUrlResult createPresignedUrl(CreatePresignedUrlCommand command) {
    validateStorageConfigured();
    validateContentType(command.fileType(), command.contentType());
    if (command.fileSize() <= 0) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE, "파일 크기는 양수여야 합니다.");
    }
    String domain = command.domain();
    if (command.fileType() == FileType.VIDEO) {
      MediaUsage usage = MediaUsage.fromDomain(domain);
      usage.validateVideoSize(command.fileSize());
      domain = usage.domain();
    }

    long expiresIn = properties.effectivePresignedUrlExpirationSeconds();
    String fileKey = fileKeyGenerator.generate(command.fileType(), domain, command.fileName());
    String uploadUrl =
        presignedUrlGenerator.generatePutUrl(
            fileKey, command.contentType(), Duration.ofSeconds(expiresIn));
    String fileUrl = properties.normalizedCdnBaseUrl() + "/" + fileKey;

    return new PresignedUrlResult(uploadUrl, fileKey, fileUrl, expiresIn);
  }

  private void validateStorageConfigured() {
    if (!properties.isConfigured()) {
      throw new BusinessException(GlobalErrorCode.SERVICE_UNAVAILABLE, "파일 저장소 설정이 올바르지 않습니다.");
    }
  }

  private void validateContentType(FileType fileType, String contentType) {
    if (fileType == null || !fileType.supportsContentType(contentType)) {
      throw new BusinessException(
          GlobalErrorCode.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 파일 Content-Type입니다.");
    }
  }
}

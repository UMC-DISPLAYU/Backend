package com.example.demo.global.file.infrastructure;

import java.time.Duration;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Component
public class S3PresignedUrlGenerator {

  private final S3Presigner s3Presigner;
  private final FileStorageProperties properties;

  public S3PresignedUrlGenerator(S3Presigner s3Presigner, FileStorageProperties properties) {
    this.s3Presigner = s3Presigner;
    this.properties = properties;
  }

  public String generatePutUrl(String fileKey, String contentType, Duration expiresIn) {
    PutObjectRequest putObjectRequest =
        PutObjectRequest.builder()
            .bucket(properties.bucket())
            .key(fileKey)
            .contentType(contentType)
            .build();

    PutObjectPresignRequest presignRequest =
        PutObjectPresignRequest.builder()
            .signatureDuration(expiresIn)
            .putObjectRequest(putObjectRequest)
            .build();

    return s3Presigner.presignPutObject(presignRequest).url().toString();
  }
}

package com.example.demo.domain.displayartwork.infrastructure.file;

import com.example.demo.domain.displayartwork.application.port.ArtworkImageDeletionPort;
import com.example.demo.global.file.infrastructure.FileStorageProperties;
import java.net.URI;
import java.util.List;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@Component
public class S3ArtworkImageDeletionAdapter implements ArtworkImageDeletionPort {

  private final S3Client s3Client;
  private final FileStorageProperties properties;

  public S3ArtworkImageDeletionAdapter(S3Client s3Client, FileStorageProperties properties) {
    this.s3Client = s3Client;
    this.properties = properties;
  }

  @Override
  public void deleteAll(List<String> imageUrls) {
    if (!properties.isConfigured()) {
      return;
    }
    imageUrls.stream().map(this::toOwnedFileKey).filter(key -> key != null).forEach(this::delete);
  }

  private String toOwnedFileKey(String imageUrl) {
    if (imageUrl == null || imageUrl.isBlank()) {
      return null;
    }
    String baseUrl = properties.normalizedCdnBaseUrl();
    String prefix = baseUrl + "/";
    if (!imageUrl.startsWith(prefix)) {
      // 외부 URL은 이 서비스가 소유한 S3 객체가 아니므로 삭제하지 않는다.
      return null;
    }
    String relativeUrl = imageUrl.substring(prefix.length());
    String path = URI.create("https://placeholder/" + relativeUrl).getPath().substring(1);
    return path.isBlank() ? null : path;
  }

  private void delete(String fileKey) {
    s3Client.deleteObject(
        DeleteObjectRequest.builder().bucket(properties.bucket()).key(fileKey).build());
  }
}

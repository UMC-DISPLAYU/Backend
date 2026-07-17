package com.example.demo.global.file.infrastructure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.file.storage")
public record FileStorageProperties(
    String region, String bucket, String cdnBaseUrl, Long presignedUrlExpirationSeconds) {

  private static final String DEFAULT_REGION = "ap-northeast-2";
  private static final long DEFAULT_EXPIRATION_SECONDS = 3600L;

  public String effectiveRegion() {
    if (region == null || region.isBlank()) {
      return DEFAULT_REGION;
    }
    return region;
  }

  public long effectivePresignedUrlExpirationSeconds() {
    if (presignedUrlExpirationSeconds == null || presignedUrlExpirationSeconds <= 0) {
      return DEFAULT_EXPIRATION_SECONDS;
    }
    return presignedUrlExpirationSeconds;
  }

  public String normalizedCdnBaseUrl() {
    if (cdnBaseUrl == null || cdnBaseUrl.isBlank()) {
      return null;
    }
    String trimmed = cdnBaseUrl.trim();
    while (trimmed.endsWith("/")) {
      trimmed = trimmed.substring(0, trimmed.length() - 1);
    }
    return trimmed;
  }

  public boolean isConfigured() {
    return bucket != null
        && !bucket.isBlank()
        && normalizedCdnBaseUrl() != null
        && !normalizedCdnBaseUrl().isBlank();
  }
}

package com.example.demo.global.file.application;

import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.util.Locale;

public enum MediaUsage {
  DISPLAY("display", 200_000_000L),
  ARTWORK("artwork", 500_000_000L);

  private final String domain;
  private final long maxVideoBytes;

  MediaUsage(String domain, long maxVideoBytes) {
    this.domain = domain;
    this.maxVideoBytes = maxVideoBytes;
  }

  public String domain() {
    return domain;
  }

  public long maxVideoBytes() {
    return maxVideoBytes;
  }

  public static MediaUsage fromDomain(String domain) {
    if (domain != null) {
      String normalized = domain.trim().toLowerCase(Locale.ROOT);
      for (MediaUsage usage : values()) {
        if (usage.domain.equals(normalized)) {
          return usage;
        }
      }
    }
    throw new BusinessException(
        GlobalErrorCode.INVALID_INPUT_VALUE, "영상은 display 또는 artwork 용도로만 업로드할 수 있습니다.");
  }

  public void validateVideoSize(long fileSize) {
    if (fileSize <= 0 || fileSize > maxVideoBytes) {
      throw new BusinessException(
          GlobalErrorCode.INVALID_INPUT_VALUE,
          "영상 파일 크기는 1 byte 이상 " + maxVideoBytes + " bytes 이하여야 합니다.");
    }
  }
}

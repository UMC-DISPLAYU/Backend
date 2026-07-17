package com.example.demo.global.file.infrastructure;

import com.example.demo.global.file.application.FileType;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class S3FileKeyGenerator {

  private final Clock clock;

  public S3FileKeyGenerator(Clock clock) {
    this.clock = clock;
  }

  public String generate(FileType fileType, String domain, String fileName) {
    LocalDate today = LocalDate.now(clock);
    return "%s/%s/%d/%02d/%s-%s"
        .formatted(
            fileType.directoryName(),
            sanitizeDomain(domain),
            today.getYear(),
            today.getMonthValue(),
            UUID.randomUUID(),
            sanitizeFileName(fileName));
  }

  private String sanitizeDomain(String domain) {
    return domain.trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_-]", "-");
  }

  private String sanitizeFileName(String fileName) {
    String normalized = fileName.replace("\\", "/");
    String name = normalized.substring(normalized.lastIndexOf('/') + 1);
    String sanitized = name.replaceAll("[^A-Za-z0-9._-]", "_");
    if (sanitized.isBlank() || sanitized.replace("_", "").isBlank()) {
      return "file";
    }
    return sanitized;
  }
}

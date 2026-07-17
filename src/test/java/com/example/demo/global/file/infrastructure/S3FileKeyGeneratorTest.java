package com.example.demo.global.file.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.global.file.application.FileType;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

class S3FileKeyGeneratorTest {

  private final S3FileKeyGenerator generator =
      new S3FileKeyGenerator(
          Clock.fixed(Instant.parse("2026-07-17T00:00:00Z"), ZoneId.of("Asia/Seoul")));

  @Test
  void generateImageKeyWithFileTypeDomainDateAndUuid() {
    String fileKey = generator.generate(FileType.IMAGE, "Display", "poster image.png");

    assertThat(fileKey)
        .startsWith("images/display/2026/07/")
        .endsWith("-poster_image.png")
        .matches("images/display/2026/07/[0-9a-f-]{36}-poster_image\\.png");
  }

  @Test
  void generateVideoKeyWithFileTypeDomainDateAndUuid() {
    String fileKey = generator.generate(FileType.VIDEO, "artwork", "making.mp4");

    assertThat(fileKey)
        .startsWith("videos/artwork/2026/07/")
        .endsWith("-making.mp4")
        .matches("videos/artwork/2026/07/[0-9a-f-]{36}-making\\.mp4");
  }

  @Test
  void sanitizePathInFileName() {
    String fileKey = generator.generate(FileType.IMAGE, "display", "../poster.png");

    assertThat(fileKey).endsWith("-poster.png");
  }
}

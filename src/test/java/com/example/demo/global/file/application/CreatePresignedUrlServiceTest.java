package com.example.demo.global.file.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.file.application.command.CreatePresignedUrlCommand;
import com.example.demo.global.file.infrastructure.FileStorageProperties;
import com.example.demo.global.file.infrastructure.S3FileKeyGenerator;
import com.example.demo.global.file.infrastructure.S3PresignedUrlGenerator;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class CreatePresignedUrlServiceTest {

  private final S3FileKeyGenerator keyGenerator = mock(S3FileKeyGenerator.class);
  private final S3PresignedUrlGenerator urlGenerator = mock(S3PresignedUrlGenerator.class);
  private final CreatePresignedUrlService service =
      new CreatePresignedUrlService(
          new FileStorageProperties("ap-northeast-2", "bucket", "https://cdn.example", 3600L),
          keyGenerator,
          urlGenerator);

  @BeforeEach
  void setUp() {
    when(keyGenerator.generate(any(), anyString(), anyString())).thenReturn("generated-key");
    when(urlGenerator.generatePutUrl(anyString(), anyString(), any()))
        .thenReturn("https://upload.example");
  }

  @ParameterizedTest
  @CsvSource({"display,200000000", "artwork,500000000", "display,1"})
  void issuesVideoUrlAtAllowedSize(String domain, long size) {
    var result = service.createPresignedUrl(video(domain, size));

    assertThat(result.fileUrl()).isEqualTo("https://cdn.example/generated-key");
    assertThat(result.uploadUrl()).isEqualTo("https://upload.example");
    verify(keyGenerator).generate(FileType.VIDEO, domain, "video.mp4");
  }

  @ParameterizedTest
  @CsvSource({"display,200000001", "artwork,500000001", "display,0", "artwork,-1"})
  void rejectsInvalidSizeBeforeSigning(String domain, long size) {
    assertInvalid(() -> service.createPresignedUrl(video(domain, size)));
    verifyNoInteractions(keyGenerator, urlGenerator);
  }

  @ParameterizedTest
  @ValueSource(strings = {"profile", "../display", "display/artwork", "", "display-extra"})
  void rejectsUnknownVideoUsage(String domain) {
    assertInvalid(() -> service.createPresignedUrl(video(domain, 10)));
    verifyNoInteractions(keyGenerator, urlGenerator);
  }

  @Test
  void canonicalizesKnownVideoUsage() {
    service.createPresignedUrl(video(" Display ", 10));
    verify(keyGenerator).generate(FileType.VIDEO, "display", "video.mp4");
  }

  @Test
  void preservesImageUploadDomainsAndExistingSizePolicy() {
    service.createPresignedUrl(
        new CreatePresignedUrlCommand(
            FileType.IMAGE, "profile", "image.png", "image/png", 600_000_000L));
    verify(keyGenerator).generate(FileType.IMAGE, "profile", "image.png");
  }

  @ParameterizedTest
  @CsvSource({
    "image/jpeg,image.jpg",
    "image/png,image.png",
    "image/webp,image.webp",
    "image/gif,image.gif",
    "image/heic,image.heic"
  })
  void issuesImageUrlWithRequestedContentType(String contentType, String fileName) {
    var result =
        service.createPresignedUrl(
            new CreatePresignedUrlCommand(FileType.IMAGE, "display", fileName, contentType, 1024));

    assertThat(result.uploadUrl()).isEqualTo("https://upload.example");
    assertThat(result.fileUrl()).isEqualTo("https://cdn.example/generated-key");
    verify(keyGenerator).generate(FileType.IMAGE, "display", fileName);
    verify(urlGenerator).generatePutUrl("generated-key", contentType, Duration.ofSeconds(3600));
  }

  @ParameterizedTest
  @ValueSource(strings = {"image/heif", "application/octet-stream", "video/mp4"})
  void rejectsUnsupportedImageContentTypeBeforeSigning(String contentType) {
    assertThatThrownBy(
            () ->
                service.createPresignedUrl(
                    new CreatePresignedUrlCommand(
                        FileType.IMAGE, "display", "image.heic", contentType, 1024)))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(GlobalErrorCode.UNSUPPORTED_MEDIA_TYPE));
    verifyNoInteractions(keyGenerator, urlGenerator);
  }

  @Test
  void rejectsUnsupportedVideoContentType() {
    assertThatThrownBy(
            () ->
                service.createPresignedUrl(
                    new CreatePresignedUrlCommand(
                        FileType.VIDEO, "display", "video.mp4", "image/png", 10)))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(GlobalErrorCode.UNSUPPORTED_MEDIA_TYPE));
    verifyNoInteractions(keyGenerator, urlGenerator);
  }

  private CreatePresignedUrlCommand video(String domain, long size) {
    return new CreatePresignedUrlCommand(FileType.VIDEO, domain, "video.mp4", "video/mp4", size);
  }

  private void assertInvalid(Runnable action) {
    assertThatThrownBy(action::run)
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                assertThat(exception.errorCode()).isEqualTo(GlobalErrorCode.INVALID_INPUT_VALUE));
  }
}

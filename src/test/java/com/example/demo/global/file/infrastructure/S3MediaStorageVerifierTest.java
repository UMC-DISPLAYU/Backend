package com.example.demo.global.file.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.file.application.MediaUsage;
import com.example.demo.global.file.domain.MediaContent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

class S3MediaStorageVerifierTest {

  private static final String VIDEO = "https://cdn.example/videos/display/2026/11/video.mp4";
  private static final String THUMBNAIL = "https://cdn.example/images/display/2026/11/cover.jpg";
  private final S3Client s3 = mock(S3Client.class);
  private final S3MediaStorageVerifier verifier =
      new S3MediaStorageVerifier(
          s3, new FileStorageProperties("ap-northeast-2", "bucket", "https://cdn.example", 3600L));

  @Test
  void verifiesOriginalAndThumbnailAtByteLimit() {
    when(s3.headObject(any(HeadObjectRequest.class)))
        .thenReturn(object(200_000_000L, "video/mp4"), object(1000L, "image/jpeg"));

    verifier.verifyVideo(MediaContent.video(VIDEO, THUMBNAIL), MediaUsage.DISPLAY);

    verify(s3)
        .headObject(
            HeadObjectRequest.builder()
                .bucket("bucket")
                .key("videos/display/2026/11/video.mp4")
                .build());
    verify(s3)
        .headObject(
            HeadObjectRequest.builder()
                .bucket("bucket")
                .key("images/display/2026/11/cover.jpg")
                .build());
  }

  @Test
  void acceptsHeicThumbnailMetadata() {
    when(s3.headObject(any(HeadObjectRequest.class)))
        .thenReturn(object(10L, "video/mp4"), object(20L, "image/heic"));

    verifier.verifyVideo(
        MediaContent.video(VIDEO, "https://cdn.example/images/display/2026/11/cover.heic"),
        MediaUsage.DISPLAY);

    verify(s3)
        .headObject(
            HeadObjectRequest.builder()
                .bucket("bucket")
                .key("images/display/2026/11/cover.heic")
                .build());
  }

  @Test
  void supportsConfiguredCdnBasePath() {
    var withBasePath =
        new S3MediaStorageVerifier(
            s3, new FileStorageProperties("region", "bucket", "https://cdn.example/assets/", null));
    when(s3.headObject(any(HeadObjectRequest.class)))
        .thenReturn(object(10L, "video/webm"), object(20L, "image/png"));

    withBasePath.verifyVideo(
        MediaContent.video(
            "https://cdn.example/assets/videos/artwork/video.webm",
            "https://cdn.example/assets/images/artwork/cover.png"),
        MediaUsage.ARTWORK);

    verify(s3)
        .headObject(
            HeadObjectRequest.builder().bucket("bucket").key("videos/artwork/video.webm").build());
  }

  @Test
  void leavesLegacyExternalImagesUnchangedWithoutStorageConfiguration() {
    var unconfigured =
        new S3MediaStorageVerifier(s3, new FileStorageProperties(null, null, null, null));
    unconfigured.verifyVideo(
        MediaContent.image("https://external.example/image.png"), MediaUsage.DISPLAY);
    verifyNoInteractions(s3);
  }

  @Test
  void rejectsVideoWhenStorageIsUnconfigured() {
    var unconfigured =
        new S3MediaStorageVerifier(s3, new FileStorageProperties(null, null, null, null));
    assertError(
        () -> unconfigured.verifyVideo(MediaContent.video(VIDEO, THUMBNAIL), MediaUsage.DISPLAY),
        GlobalErrorCode.SERVICE_UNAVAILABLE);
    verifyNoInteractions(s3);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "https://cdn.example.evil/videos/display/video.mp4",
        "https://user@cdn.example/videos/display/video.mp4",
        "http://cdn.example/videos/display/video.mp4",
        "https://cdn.example:444/videos/display/video.mp4",
        "https://cdn.example/videos/artwork/video.mp4",
        "https://cdn.example/videos/display-extra/video.mp4",
        "https://cdn.example/videos/display/../video.mp4",
        "https://cdn.example/videos/display/./video.mp4",
        "https://cdn.example/videos/display/%2e%2e/video.mp4",
        "https://cdn.example/videos/display/a%2Fb.mp4",
        "https://cdn.example/videos/display/a%5Cb.mp4",
        "https://cdn.example/videos/display/a%252fb.mp4",
        "https://cdn.example/videos//display/video.mp4",
        "https://cdn.example/videos/display/",
        "https://cdn.example/videos/display/video.mp4?download=true",
        "https://cdn.example/videos/display/video.mp4#fragment",
        "not a url"
      })
  void rejectsUnownedOrAmbiguousVideoUrlBeforeS3(String url) {
    assertError(
        () -> verifier.verifyVideo(MediaContent.video(url, THUMBNAIL), MediaUsage.DISPLAY),
        GlobalErrorCode.INVALID_INPUT_VALUE);
    verifyNoInteractions(s3);
  }

  @Test
  void rejectsWrongThumbnailNamespaceBeforeS3() {
    assertError(
        () ->
            verifier.verifyVideo(
                MediaContent.video(VIDEO, "https://cdn.example/images/artwork/cover.jpg"),
                MediaUsage.DISPLAY),
        GlobalErrorCode.INVALID_INPUT_VALUE);
    verifyNoInteractions(s3);
  }

  @ParameterizedTest
  @ValueSource(longs = {0, -1, 200_000_001L})
  void rejectsActualVideoSizeOutsidePolicy(long size) {
    when(s3.headObject(any(HeadObjectRequest.class))).thenReturn(object(size, "video/mp4"));
    assertError(
        () -> verifier.verifyVideo(MediaContent.video(VIDEO, THUMBNAIL), MediaUsage.DISPLAY),
        GlobalErrorCode.INVALID_INPUT_VALUE);
  }

  @Test
  void rejectsActualThumbnailSizeOutsidePolicy() {
    when(s3.headObject(any(HeadObjectRequest.class)))
        .thenReturn(object(10L, "video/mp4"), object(200_000_001L, "image/jpeg"));
    assertError(
        () -> verifier.verifyVideo(MediaContent.video(VIDEO, THUMBNAIL), MediaUsage.DISPLAY),
        GlobalErrorCode.INVALID_INPUT_VALUE);
  }

  @Test
  void rejectsVideoWithImageMetadata() {
    when(s3.headObject(any(HeadObjectRequest.class))).thenReturn(object(10L, "image/png"));
    assertError(
        () -> verifier.verifyVideo(MediaContent.video(VIDEO, THUMBNAIL), MediaUsage.DISPLAY),
        GlobalErrorCode.UNSUPPORTED_MEDIA_TYPE);
  }

  @Test
  void rejectsThumbnailWithVideoMetadata() {
    when(s3.headObject(any(HeadObjectRequest.class)))
        .thenReturn(object(10L, "video/mp4"), object(10L, "video/mp4"));
    assertError(
        () -> verifier.verifyVideo(MediaContent.video(VIDEO, THUMBNAIL), MediaUsage.DISPLAY),
        GlobalErrorCode.UNSUPPORTED_MEDIA_TYPE);
  }

  @Test
  void mapsMissingObjectToInvalidInput() {
    when(s3.headObject(any(HeadObjectRequest.class)))
        .thenThrow(S3Exception.builder().statusCode(404).build());
    assertError(
        () -> verifier.verifyVideo(MediaContent.video(VIDEO, THUMBNAIL), MediaUsage.DISPLAY),
        GlobalErrorCode.INVALID_INPUT_VALUE);
  }

  @ParameterizedTest
  @ValueSource(ints = {403, 500, 503})
  void mapsStorageFailureToUpstreamError(int status) {
    when(s3.headObject(any(HeadObjectRequest.class)))
        .thenThrow(S3Exception.builder().statusCode(status).build());
    assertError(
        () -> verifier.verifyVideo(MediaContent.video(VIDEO, THUMBNAIL), MediaUsage.DISPLAY),
        GlobalErrorCode.EXTERNAL_API_ERROR);
  }

  @Test
  void mapsTransportFailureToUpstreamError() {
    when(s3.headObject(any(HeadObjectRequest.class)))
        .thenThrow(SdkClientException.create("network unavailable"));
    assertError(
        () -> verifier.verifyVideo(MediaContent.video(VIDEO, THUMBNAIL), MediaUsage.DISPLAY),
        GlobalErrorCode.EXTERNAL_API_ERROR);
  }

  private HeadObjectResponse object(long length, String type) {
    return HeadObjectResponse.builder().contentLength(length).contentType(type).build();
  }

  private void assertError(Runnable action, GlobalErrorCode code) {
    assertThatThrownBy(action::run)
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception -> assertThat(exception.errorCode()).isEqualTo(code));
  }
}

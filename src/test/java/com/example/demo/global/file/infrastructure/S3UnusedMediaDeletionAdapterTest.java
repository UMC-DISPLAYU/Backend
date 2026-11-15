package com.example.demo.global.file.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.demo.global.file.application.port.MediaReferenceQuery;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

class S3UnusedMediaDeletionAdapterTest {
  private static final String VIDEO = "https://cdn.example/videos/display/clip.mp4";
  private static final String THUMBNAIL = "https://cdn.example/images/display/cover.jpg";
  private final S3Client s3 = mock(S3Client.class);
  private final MediaReferenceQuery references = mock(MediaReferenceQuery.class);
  private final S3UnusedMediaDeletionAdapter adapter =
      new S3UnusedMediaDeletionAdapter(
          s3,
          new FileStorageProperties("region", "bucket", "https://cdn.example", 60L),
          references);

  @Test
  void deletesOnlyUnreferencedOriginalAndPreservesSharedThumbnail() {
    when(references.findReferencedUrls(Set.of(VIDEO, THUMBNAIL))).thenReturn(Set.of(THUMBNAIL));
    adapter.deleteAll(List.of(VIDEO, THUMBNAIL, VIDEO));
    ArgumentCaptor<DeleteObjectRequest> request =
        ArgumentCaptor.forClass(DeleteObjectRequest.class);
    verify(s3).deleteObject(request.capture());
    assertThat(request.getValue().key()).isEqualTo("videos/display/clip.mp4");
    assertThat(request.getValue().bucket()).isEqualTo("bucket");
  }

  @Test
  void referenceLookupFailurePreservesEveryFile() {
    when(references.findReferencedUrls(any()))
        .thenThrow(new IllegalStateException("Cannot resolve reference"));
    assertThatThrownBy(() -> adapter.deleteAll(List.of(VIDEO, THUMBNAIL)))
        .isInstanceOf(IllegalStateException.class);
    verifyNoInteractions(s3);
  }

  @Test
  void neverDeletesExternalAmbiguousOrUnrelatedNamespaceUrls() {
    adapter.deleteAll(
        List.of(
            "https://other.example/videos/display/clip.mp4",
            "https://cdn.example.evil/videos/display/clip.mp4",
            "https://cdn.example/images/user/profile.png",
            "https://cdn.example/videos/display/../profile.png",
            "https://cdn.example/videos/display/%63lip.mp4",
            VIDEO + "?token=secret",
            VIDEO + "#fragment",
            "https://user:secret@cdn.example/videos/display/clip.mp4",
            "not a URL"));
    verifyNoInteractions(s3, references);
  }

  @Test
  void unconfiguredStoragePreservesFiles() {
    new S3UnusedMediaDeletionAdapter(
            s3, new FileStorageProperties(null, null, null, null), references)
        .deleteAll(List.of(VIDEO));
    verifyNoInteractions(s3, references);
  }
}

package com.example.demo.global.file.infrastructure;

import com.example.demo.global.file.application.port.MediaReferenceQuery;
import com.example.demo.global.file.application.port.UnusedMediaDeletionPort;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@Component
public class S3UnusedMediaDeletionAdapter implements UnusedMediaDeletionPort {
  private final S3Client s3Client;
  private final FileStorageProperties properties;
  private final MediaReferenceQuery referenceQuery;

  public S3UnusedMediaDeletionAdapter(
      S3Client s3Client, FileStorageProperties properties, MediaReferenceQuery referenceQuery) {
    this.s3Client = s3Client;
    this.properties = properties;
    this.referenceQuery = referenceQuery;
  }

  @Override
  public void deleteAll(List<String> fileUrls) {
    if (!properties.isConfigured()) {
      return;
    }
    ManagedMediaUrls urls = new ManagedMediaUrls(properties);
    Map<String, String> candidates = new LinkedHashMap<>();
    for (String url : fileUrls) {
      String key = urls.ownedKey(url);
      if (key != null) {
        candidates.put(url, key);
      }
    }
    if (candidates.isEmpty()) {
      return;
    }
    Set<String> referenced = referenceQuery.findReferencedUrls(candidates.keySet());
    candidates.entrySet().stream()
        .filter(entry -> !referenced.contains(entry.getKey()))
        .map(Map.Entry::getValue)
        .distinct()
        .forEach(
            key ->
                s3Client.deleteObject(
                    DeleteObjectRequest.builder().bucket(properties.bucket()).key(key).build()));
  }
}

package com.example.demo.global.file.infrastructure;

import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.file.application.FileType;
import com.example.demo.global.file.application.MediaUsage;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

public final class ManagedMediaUrls {
  private final URI base;

  public ManagedMediaUrls(FileStorageProperties properties) {
    try {
      base = URI.create(properties.normalizedCdnBaseUrl());
      if (!isHttpUrl(base) || hasExtraComponents(base)) {
        throw new IllegalArgumentException();
      }
    } catch (IllegalArgumentException | NullPointerException exception) {
      throw new BusinessException(GlobalErrorCode.SERVICE_UNAVAILABLE, "파일 저장소 설정이 올바르지 않습니다.");
    }
  }

  public String requireKey(String url, FileType type, MediaUsage usage) {
    String key = ownedKey(url);
    String prefix = type.directoryName() + "/" + usage.domain() + "/";
    if (key == null || !key.startsWith(prefix)) {
      throw new BusinessException(
          GlobalErrorCode.INVALID_INPUT_VALUE, "해당 용도로 업로드한 저장소 URL을 사용해야 합니다.");
    }
    return key;
  }

  public String ownedKey(String url) {
    if (url == null || url.isBlank()) {
      return null;
    }
    URI candidate;
    try {
      candidate = URI.create(url);
    } catch (IllegalArgumentException exception) {
      return null;
    }
    if (!isHttpUrl(candidate)
        || hasExtraComponents(candidate)
        || !base.getScheme().equalsIgnoreCase(candidate.getScheme())
        || !base.getHost().equalsIgnoreCase(candidate.getHost())
        || base.getPort() != candidate.getPort()) {
      return null;
    }
    String path = candidate.getRawPath();
    if (path == null
        || path.contains("%")
        || path.contains("\\")
        || path.contains("//")
        || Arrays.stream(path.split("/"))
            .anyMatch(segment -> segment.equals(".") || segment.equals(".."))
        || path.endsWith("/")) {
      return null;
    }
    return relativeManagedKey(path);
  }

  public String referencedKey(String url) {
    URI reference;
    try {
      reference = URI.create(url);
    } catch (IllegalArgumentException exception) {
      throw new IllegalStateException("Cannot safely resolve a media reference");
    }
    if (reference.getHost() == null) {
      throw new IllegalStateException("Cannot safely resolve a media reference authority");
    }
    if (!base.getHost().equalsIgnoreCase(reference.getHost())) {
      return null;
    }
    String path = reference.getPath();
    if (path == null || path.contains("%")) {
      throw new IllegalStateException("Cannot safely resolve an encoded media reference");
    }
    try {
      path = new URI(null, null, path.replace('\\', '/'), null).normalize().getPath();
    } catch (URISyntaxException exception) {
      throw new IllegalStateException("Cannot safely normalize a managed media reference");
    }
    if (Arrays.stream(path.split("/")).anyMatch(segment -> segment.equals(".."))) {
      throw new IllegalStateException("Cannot safely resolve a media reference above the root");
    }
    return relativeManagedKey(path);
  }

  private String relativeManagedKey(String path) {
    String prefix = base.getRawPath() + "/";
    if (!path.startsWith(prefix)) {
      return null;
    }
    String key = path.substring(prefix.length());
    for (FileType type : new FileType[] {FileType.IMAGE, FileType.VIDEO}) {
      for (MediaUsage usage : MediaUsage.values()) {
        String namespace = type.directoryName() + "/" + usage.domain() + "/";
        if (key.startsWith(namespace) && key.length() > namespace.length()) {
          return key;
        }
      }
    }
    return null;
  }

  private static boolean isHttpUrl(URI uri) {
    return uri.getHost() != null
        && ("https".equalsIgnoreCase(uri.getScheme()) || "http".equalsIgnoreCase(uri.getScheme()));
  }

  private static boolean hasExtraComponents(URI uri) {
    return uri.getRawUserInfo() != null
        || uri.getRawQuery() != null
        || uri.getRawFragment() != null;
  }
}

package com.example.demo.global.file.domain;

import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.util.List;

public record MediaContent(String imageUrl, MediaType mediaType, String thumbnailUrl) {

  public MediaContent {
    if (imageUrl == null || imageUrl.isBlank() || mediaType == null) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE, "미디어 URL과 종류는 필수입니다.");
    }
    if (mediaType == MediaType.VIDEO && (thumbnailUrl == null || thumbnailUrl.isBlank())) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE, "영상 썸네일은 필수입니다.");
    }
    if (mediaType == MediaType.IMAGE && thumbnailUrl != null) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE, "이미지에는 영상 썸네일을 지정할 수 없습니다.");
    }
  }

  public static MediaContent image(String imageUrl) {
    return new MediaContent(imageUrl, MediaType.IMAGE, null);
  }

  public static MediaContent video(String imageUrl, String thumbnailUrl) {
    return new MediaContent(imageUrl, MediaType.VIDEO, thumbnailUrl);
  }

  public String cardImageUrl() {
    return mediaType == MediaType.VIDEO ? thumbnailUrl : imageUrl;
  }

  public String videoUrl() {
    return mediaType == MediaType.VIDEO ? imageUrl : null;
  }

  public List<String> fileUrls() {
    return mediaType == MediaType.VIDEO ? List.of(imageUrl, thumbnailUrl) : List.of(imageUrl);
  }
}

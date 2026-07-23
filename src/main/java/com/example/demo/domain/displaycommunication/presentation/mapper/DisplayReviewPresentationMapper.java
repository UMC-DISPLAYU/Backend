package com.example.demo.domain.displaycommunication.presentation.mapper;

import com.example.demo.domain.displaycommunication.application.command.CreateDisplayReviewCommand;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewResult;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview.ImageInfo;
import com.example.demo.domain.displaycommunication.presentation.request.CreateDisplayReviewRequest;
import com.example.demo.domain.displaycommunication.presentation.response.DisplayReviewResponse;
import com.example.demo.domain.displaycommunication.presentation.response.DisplayReviewResponse.ImageResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DisplayReviewPresentationMapper {

  public CreateDisplayReviewCommand toCommand(
      Long displayId, Long userId, CreateDisplayReviewRequest request) {
    List<ImageInfo> images =
        request.images() == null
            ? List.of()
            : request.images().stream()
                .map(image -> new ImageInfo(image.imageUrl(), image.width(), image.height()))
                .toList();
    return new CreateDisplayReviewCommand(displayId, userId, request.content(), images);
  }

  public DisplayReviewResponse toResponse(DisplayReviewResult result) {
    return new DisplayReviewResponse(
        result.displayReviewId(),
        result.content(),
        result.createdAt(),
        result.displayId(),
        result.userId(),
        result.images().stream()
            .map(
                image ->
                    new ImageResponse(
                        image.reviewImageId(),
                        image.imageUrl(),
                        image.width(),
                        image.height(),
                        image.sortOrder()))
            .toList());
  }
}

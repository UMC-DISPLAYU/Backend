package com.example.demo.domain.displaycommunication.presentation.mapper;

import com.example.demo.domain.displaycommunication.application.command.CreateDisplayReviewCommand;
import com.example.demo.domain.displaycommunication.application.command.CreateDisplayReviewReplyCommand;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewLikeResult;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewReplyResult;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewResult;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview.ImageInfo;
import com.example.demo.domain.displaycommunication.presentation.request.CreateDisplayReviewReplyRequest;
import com.example.demo.domain.displaycommunication.presentation.request.CreateDisplayReviewRequest;
import com.example.demo.domain.displaycommunication.presentation.response.DisplayReviewLikeResponse;
import com.example.demo.domain.displaycommunication.presentation.response.DisplayReviewReplyResponse;
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

  public CreateDisplayReviewReplyCommand toCommand(
      Long displayId, Long displayReviewId, Long userId, CreateDisplayReviewReplyRequest request) {
    return new CreateDisplayReviewReplyCommand(
        displayId, displayReviewId, userId, request.content());
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

  public DisplayReviewReplyResponse toResponse(DisplayReviewReplyResult result) {
    return new DisplayReviewReplyResponse(
        result.displayReviewReplyId(),
        result.createdAt(),
        result.content(),
        result.displayReviewId(),
        result.userId(),
        result.nickname(),
        result.isTeamMember());
  }

  public DisplayReviewLikeResponse toResponse(DisplayReviewLikeResult result) {
    return new DisplayReviewLikeResponse(
        result.displayReviewId(),
        result.liked(),
        result.likeCount(),
        result.createdAt(),
        result.deletedAt());
  }
}

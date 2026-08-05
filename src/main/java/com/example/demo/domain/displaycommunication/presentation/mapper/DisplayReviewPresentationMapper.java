package com.example.demo.domain.displaycommunication.presentation.mapper;

import com.example.demo.domain.displaycommunication.application.command.CreateDisplayReviewCommand;
import com.example.demo.domain.displaycommunication.application.command.CreateDisplayReviewReplyCommand;
import com.example.demo.domain.displaycommunication.application.query.GetDisplayReviewRepliesQuery;
import com.example.demo.domain.displaycommunication.application.query.GetDisplayReviewsQuery;
import com.example.demo.domain.displaycommunication.application.result.DeletedDisplayReviewReplyResult;
import com.example.demo.domain.displaycommunication.application.result.DeletedDisplayReviewResult;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewLikeResult;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewListResult;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewReplyLikeResult;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewReplyListResult;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewReplyResult;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewResult;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview.ImageInfo;
import com.example.demo.domain.displaycommunication.presentation.request.CreateDisplayReviewReplyRequest;
import com.example.demo.domain.displaycommunication.presentation.request.CreateDisplayReviewRequest;
import com.example.demo.domain.displaycommunication.presentation.response.DeletedDisplayReviewReplyResponse;
import com.example.demo.domain.displaycommunication.presentation.response.DeletedDisplayReviewResponse;
import com.example.demo.domain.displaycommunication.presentation.response.DisplayReviewLikeResponse;
import com.example.demo.domain.displaycommunication.presentation.response.DisplayReviewListResponse;
import com.example.demo.domain.displaycommunication.presentation.response.DisplayReviewListResponse.DisplayReviewItemResponse;
import com.example.demo.domain.displaycommunication.presentation.response.DisplayReviewListResponse.ImageResponse;
import com.example.demo.domain.displaycommunication.presentation.response.DisplayReviewListResponse.UserResponse;
import com.example.demo.domain.displaycommunication.presentation.response.DisplayReviewReplyLikeResponse;
import com.example.demo.domain.displaycommunication.presentation.response.DisplayReviewReplyListResponse;
import com.example.demo.domain.displaycommunication.presentation.response.DisplayReviewReplyListResponse.ReplyItemResponse;
import com.example.demo.domain.displaycommunication.presentation.response.DisplayReviewReplyResponse;
import com.example.demo.domain.displaycommunication.presentation.response.DisplayReviewResponse;
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

  public GetDisplayReviewsQuery toQuery(
      Long displayId, Long cursorId, int size, Long viewerUserId) {
    return new GetDisplayReviewsQuery(displayId, cursorId, size, viewerUserId);
  }

  public GetDisplayReviewRepliesQuery toReplyQuery(
      Long displayId, Long displayReviewId, Long cursorId, int size, Long viewerUserId) {
    return new GetDisplayReviewRepliesQuery(
        displayId, displayReviewId, cursorId, size, viewerUserId);
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
                    new DisplayReviewResponse.ImageResponse(
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

  public DeletedDisplayReviewResponse toResponse(DeletedDisplayReviewResult result) {
    return new DeletedDisplayReviewResponse(result.displayReviewId(), result.deletedAt());
  }

  public DeletedDisplayReviewReplyResponse toResponse(DeletedDisplayReviewReplyResult result) {
    return new DeletedDisplayReviewReplyResponse(result.displayReviewReplyId(), result.deletedAt());
  }

  public DisplayReviewLikeResponse toResponse(DisplayReviewLikeResult result) {
    return new DisplayReviewLikeResponse(
        result.displayReviewId(),
        result.liked(),
        result.likeCount(),
        result.createdAt(),
        result.deletedAt());
  }

  public DisplayReviewReplyLikeResponse toResponse(DisplayReviewReplyLikeResult result) {
    return new DisplayReviewReplyLikeResponse(
        result.displayReviewReplyId(),
        result.liked(),
        result.likeCount(),
        result.createdAt(),
        result.deletedAt());
  }

  public DisplayReviewListResponse toResponse(DisplayReviewListResult result) {
    return new DisplayReviewListResponse(
        result.reviews().stream()
            .map(
                review ->
                    new DisplayReviewItemResponse(
                        review.displayReviewId(),
                        review.content(),
                        review.createdAt(),
                        new UserResponse(
                            review.user().userId(),
                            review.user().nickname(),
                            review.user().profileImageUrl()),
                        review.images().stream()
                            .map(
                                image ->
                                    new ImageResponse(
                                        image.reviewImageId(),
                                        image.imageUrl(),
                                        image.width(),
                                        image.height(),
                                        image.sortOrder()))
                            .toList(),
                        review.likeCount(),
                        review.isLiked(),
                        review.replyCount()))
            .toList(),
        result.nextCursorId(),
        result.size(),
        result.hasNext());
  }

  public DisplayReviewReplyListResponse toResponse(DisplayReviewReplyListResult result) {
    return new DisplayReviewReplyListResponse(
        result.replies().stream()
            .map(
                reply ->
                    new ReplyItemResponse(
                        reply.displayReviewReplyId(),
                        reply.content(),
                        reply.createdAt(),
                        new DisplayReviewReplyListResponse.UserResponse(
                            reply.user().userId(),
                            reply.user().nickname(),
                            reply.user().profileImageUrl()),
                        reply.isTeamMember(),
                        reply.likeCount(),
                        reply.isLiked()))
            .toList(),
        result.nextCursorId(),
        result.size(),
        result.hasNext());
  }
}

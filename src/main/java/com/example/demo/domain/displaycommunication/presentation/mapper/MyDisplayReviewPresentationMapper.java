package com.example.demo.domain.displaycommunication.presentation.mapper;

import com.example.demo.domain.displaycommunication.application.result.MyDisplayReviewListResult;
import com.example.demo.domain.displaycommunication.presentation.response.MyDisplayReviewListResponse;
import com.example.demo.domain.displaycommunication.presentation.response.MyDisplayReviewListResponse.MyDisplayReviewResponse;
import org.springframework.stereotype.Component;

@Component
public class MyDisplayReviewPresentationMapper {

  public MyDisplayReviewListResponse toResponse(MyDisplayReviewListResult result) {
    return new MyDisplayReviewListResponse(
        result.reviews().stream()
            .map(
                review ->
                    new MyDisplayReviewResponse(
                        review.displayReviewId(),
                        review.displayId(),
                        review.displayName(),
                        review.content(),
                        review.createdAt()))
            .toList(),
        result.nextCursorId(),
        result.size(),
        result.hasNext());
  }
}

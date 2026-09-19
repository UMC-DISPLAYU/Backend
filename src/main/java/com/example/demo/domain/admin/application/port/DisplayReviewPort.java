package com.example.demo.domain.admin.application.port;

import com.example.demo.domain.admin.application.query.ReviewSearchQuery;
import com.example.demo.domain.admin.application.result.ReviewDetail;
import com.example.demo.domain.admin.application.result.ReviewPage;

public interface DisplayReviewPort {
  ReviewPage search(ReviewSearchQuery query);

  ReviewDetail getDetail(Long displayId);

  void approve(Long displayId, Long reviewerId);

  void reject(Long displayId, Long reviewerId, String reason);
}

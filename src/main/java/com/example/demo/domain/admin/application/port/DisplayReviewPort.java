package com.example.demo.domain.admin.application.port;

import com.example.demo.domain.admin.application.query.ReviewSearchQuery;
import com.example.demo.domain.admin.application.result.ReviewDetail;
import com.example.demo.domain.admin.application.result.ReviewPage;

/**
 * Display 연동 계약 초안. 구현체는 Display의 공개 유스케이스를 호출한다. 상태 검증, 중복·동시 처리 방지, 반려 사유·처리자·처리일 저장은 Display의
 * 트랜잭션 책임이다. 승인은 기존 콘텐츠 공개·캐시 갱신 흐름을 재사용하며 실패를 성공으로 변환하지 않는다.
 */
public interface DisplayReviewPort {
  ReviewPage search(ReviewSearchQuery query);

  ReviewDetail getDetail(Long displayId);

  void approve(Long displayId, Long reviewerId);

  void reject(Long displayId, Long reviewerId, String reason);
}

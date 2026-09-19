package com.example.demo.domain.admin.application.port;

import com.example.demo.domain.admin.application.query.ReviewSearchQuery;
import com.example.demo.domain.admin.application.result.ReviewDetail;
import com.example.demo.domain.admin.application.result.ReviewPage;

public interface DisplayReviewPort {
  /**
   * 목록 필드 계약: displayId·title·status는 필수. requesterId·requestedAt은 실제 심사 신청자와 신청 시각이며, 아직 식별할 수 없는
   * 기존 데이터는 null 허용. ownerUserId나 팀 대표자로 추정하여 채우지 않는다. school은 Display.organization(전시 등록 학교·주최명),
   * startDate·endDate는 Display.period이며 필수. leaderName은 승인되고 삭제되지 않은 TEAM_LEADER의
   * displayNickname(전시 내 대표자 표시명)이며, User.name·nickname이나 신청자 이름으로 대체하지 않는다. 대표자가 없거나 여러 명이라 단일 식별이
   * 불가능하면 null. posterImageUrl은 삭제되지 않은 MAIN 이미지의 imageUrl이며 없으면 null. 목록과 items는 null 대신 빈 목록을
   * 사용하며 기존 상태 필터·ID 커서 정렬을 유지한다.
   */
  ReviewPage search(ReviewSearchQuery query);

  ReviewDetail getDetail(Long displayId);

  void approve(Long displayId, Long reviewerId);

  void reject(Long displayId, Long reviewerId, String reason);
}

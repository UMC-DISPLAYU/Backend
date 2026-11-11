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
   * 사용하며 기존 상태 필터·ID 커서 정렬을 유지한다. q는 전시명·학교·학과 부분 검색과 숫자 ID 정확 일치 검색이다.
   */
  ReviewPage search(ReviewSearchQuery query);

  /**
   * school·leaderName·requesterId는 목록과 같은 의미와 null 정책을 적용한다. history는 저장된 모든 심사 회차를 screeningId
   * 내림차순 제공하며 없으면 []이다. 반려·취소 이력을 포함하고 최신 심사 필드와 같은 조회 결과를 사용한다. 과거 전시 본문 스냅샷은 제공하지 않는다.
   */
  ReviewDetail getDetail(Long displayId);

  void approve(Long displayId, Long reviewerId);

  void reject(Long displayId, Long reviewerId, String reason);
}

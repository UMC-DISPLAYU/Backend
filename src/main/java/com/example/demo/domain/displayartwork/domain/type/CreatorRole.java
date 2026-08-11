package com.example.demo.domain.displayartwork.domain.type;

/**
 * 작품에 연결된 Creator의 역할.
 *
 * <p>기존 {@code isLeader}는 대표 작가 여부만 표현해서, 공동 작업자와 QnA 담당 전용 대표자를 구분하지 못했다. 조회 시점에 셋을 구분해야 하는 경우에는
 * {@code isLeader} 대신 이 값을 사용한다.
 */
public enum CreatorRole {
  /** 작품의 대표 작가. 작품당 하나만 존재한다. 계정이 없는 작가를 대리 등록한 경우 userId는 null이다. */
  LEAD_ARTIST,

  /** 함께 작업한 공동 작업자. 계정 없이 이름만 입력한 경우 userId는 null이다. */
  CO_AUTHOR,

  /**
   * 작품의 작가는 아니지만 QnA 담당자로 지정된 전시 대표자. 답변 권한과 답변자 표기가 모두 Creator를 근거로 하기 때문에 Creator 행을 남기지만, 작가
   * 목록에는 노출하지 않는다.
   */
  QA_ONLY
}

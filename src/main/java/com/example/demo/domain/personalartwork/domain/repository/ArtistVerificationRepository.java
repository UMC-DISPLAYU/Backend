package com.example.demo.domain.personalartwork.domain.repository;

/**
 * 개인 작품 등록은 작가 인증을 마친 사용자만 할 수 있다.
 *
 * <p>인증 여부는 user 도메인이 관리하는 정보이므로, 이 도메인에서는 판단에 필요한 질문 하나만 포트로 선언하고 실제 조회 방법은 infrastructure에 맡긴다.
 */
public interface ArtistVerificationRepository {

  boolean isVerifiedArtist(Long userId);
}

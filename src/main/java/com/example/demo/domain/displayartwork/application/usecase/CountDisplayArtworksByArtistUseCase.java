package com.example.demo.domain.displayartwork.application.usecase;

import java.util.Map;

/** 다른 도메인이 전시 안의 작가 계정별 작품 수를 알아야 할 때 쓰는 창구. */
public interface CountDisplayArtworksByArtistUseCase {

  /**
   * @return 작가 계정 ID → 그 계정이 작가로 등록된 삭제되지 않은 작품 수. 작품이 없는 계정은 들어 있지 않다
   */
  Map<Long, Integer> countByArtist(Long displayId);
}

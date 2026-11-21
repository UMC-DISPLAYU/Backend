package com.example.demo.domain.archive.application.usecase;

import java.time.LocalDateTime;
import java.util.List;

/** 다른 도메인이 "이 전시를 저장한 사용자"를 알아야 할 때 쓰는 창구. */
public interface GetDisplaySaversUseCase {

  /** savedBefore 전에 저장해 두고 취소하지 않은 사용자 ID 목록 */
  List<Long> getSaverUserIds(Long displayId, LocalDateTime savedBefore);
}

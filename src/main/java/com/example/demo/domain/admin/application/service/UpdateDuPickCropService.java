package com.example.demo.domain.admin.application.service;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.display.application.port.DisplayListCacheEvictionPort;
import com.example.demo.domain.display.domain.repository.DuPickColumnRepository;
import com.example.demo.domain.display.domain.vo.PosterCrop;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateDuPickCropService {
  private final AdminAccessPort access;
  private final DuPickColumnRepository columns;
  private final DisplayListCacheEvictionPort cacheEviction;
  private final Clock clock;

  @Transactional
  public void update(Long operatorId, Long duPickId, PosterCrop crop) {
    if (operatorId == null) throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    if (!access.isAdmin(operatorId)) throw new BusinessException(GlobalErrorCode.FORBIDDEN);
    if (duPickId == null || duPickId <= 0) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
    if (!columns.updatePosterCrop(
        duPickId, crop, LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC))) {
      throw new BusinessException(GlobalErrorCode.NOT_FOUND);
    }
    cacheEviction.evictAfterCommit();
  }
}

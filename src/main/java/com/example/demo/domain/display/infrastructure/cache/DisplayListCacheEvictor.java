package com.example.demo.domain.display.infrastructure.cache;

import com.example.demo.domain.display.application.cache.DisplayCacheNames;
import com.example.demo.domain.display.application.port.DisplayListCacheEvictionPort;
import java.util.List;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class DisplayListCacheEvictor implements DisplayListCacheEvictionPort {

  private static final List<String> DISPLAY_LIST_CACHE_NAMES =
      List.of(
          DisplayCacheNames.GRADUATION,
          DisplayCacheNames.DU_PICKS,
          DisplayCacheNames.CLOSING_SOON_FIRST_PAGE);

  private final CacheManager cacheManager;
  private final DisplayListCacheVersion cacheVersion;

  public DisplayListCacheEvictor(CacheManager cacheManager, DisplayListCacheVersion cacheVersion) {
    this.cacheManager = cacheManager;
    this.cacheVersion = cacheVersion;
  }

  @Override
  public void evictAfterCommit() {
    if (TransactionSynchronizationManager.isSynchronizationActive()) {
      TransactionSynchronizationManager.registerSynchronization(
          new TransactionSynchronization() {
            @Override
            public void afterCommit() {
              evictAll();
            }
          });
      return;
    }

    evictAll();
  }

  private void evictAll() {
    cacheVersion.advance();
    DISPLAY_LIST_CACHE_NAMES.stream()
        .map(cacheManager::getCache)
        .forEach(
            cache -> {
              if (cache != null) {
                cache.clear();
              }
            });
  }
}

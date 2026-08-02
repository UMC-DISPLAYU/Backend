package com.example.demo.global.config;

import com.example.demo.domain.display.application.cache.DisplayCacheNames;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.List;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

  private static final long DISPLAY_CACHE_MAXIMUM_SIZE = 1_000;

  @Bean
  public CacheManager cacheManager() {
    SimpleCacheManager cacheManager = new SimpleCacheManager();
    cacheManager.setCaches(
        List.of(
            caffeineCache(DisplayCacheNames.GRADUATION, Duration.ofMinutes(5)),
            caffeineCache(DisplayCacheNames.DU_PICKS, Duration.ofMinutes(10)),
            caffeineCache(DisplayCacheNames.CLOSING_SOON_FIRST_PAGE, Duration.ofMinutes(1))));
    return cacheManager;
  }

  private Cache caffeineCache(String name, Duration ttl) {
    return new CaffeineCache(
        name,
        Caffeine.newBuilder()
            .expireAfterWrite(ttl)
            .maximumSize(DISPLAY_CACHE_MAXIMUM_SIZE)
            .recordStats()
            .build());
  }
}

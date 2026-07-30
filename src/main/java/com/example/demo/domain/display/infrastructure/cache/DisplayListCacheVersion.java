package com.example.demo.domain.display.infrastructure.cache;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

@Component("displayListCacheVersion")
public class DisplayListCacheVersion {

  private final AtomicLong version = new AtomicLong();

  public long current() {
    return version.get();
  }

  long advance() {
    return version.incrementAndGet();
  }
}

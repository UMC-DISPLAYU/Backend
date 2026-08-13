package com.example.demo.global.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ThreadPoolExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

class AsyncConfigTest {

  @Test
  void displayDeletionCleanupExecutorUsesSafeShutdownAndRejectionPolicy() {
    ThreadPoolTaskExecutor executor =
        (ThreadPoolTaskExecutor) new AsyncConfig().displayDeletionCleanupExecutor();

    try {
      assertThat(ReflectionTestUtils.getField(executor, "waitForTasksToCompleteOnShutdown"))
          .isEqualTo(true);
      assertThat(ReflectionTestUtils.getField(executor, "awaitTerminationMillis"))
          .isEqualTo(30_000L);
      assertThat(executor.getThreadPoolExecutor().getRejectedExecutionHandler())
          .isInstanceOf(ThreadPoolExecutor.CallerRunsPolicy.class);
    } finally {
      executor.shutdown();
    }
  }
}

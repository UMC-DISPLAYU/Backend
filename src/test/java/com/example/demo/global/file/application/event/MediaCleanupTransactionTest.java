package com.example.demo.global.file.application.event;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.example.demo.global.file.application.port.UnusedMediaDeletionPort;
import java.util.List;
import java.util.concurrent.Executor;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

@SpringJUnitConfig(MediaCleanupTransactionTest.Config.class)
class MediaCleanupTransactionTest {
  @Autowired private ApplicationEventPublisher publisher;
  @Autowired private UnusedMediaDeletionPort deletionPort;
  @Autowired private PlatformTransactionManager transactionManager;
  private final List<String> files =
      List.of(
          "https://cdn.example/videos/artwork/video.mp4",
          "https://cdn.example/images/artwork/cover.png");

  @BeforeEach
  void clearCalls() {
    reset(deletionPort);
  }

  @Test
  void deletesOnlyAfterCommit() {
    new TransactionTemplate(transactionManager)
        .executeWithoutResult(
            status -> {
              publisher.publishEvent(new MediaFilesRemovedEvent(files));
              verifyNoInteractions(deletionPort);
            });
    verify(deletionPort).deleteAll(files);
  }

  @Test
  void rollbackPreservesOriginalAndThumbnail() {
    new TransactionTemplate(transactionManager)
        .executeWithoutResult(
            status -> {
              publisher.publishEvent(new MediaFilesRemovedEvent(files));
              status.setRollbackOnly();
            });
    verifyNoInteractions(deletionPort);
  }

  @Test
  void eventWithoutTransactionCannotDeleteFiles() {
    publisher.publishEvent(new MediaFilesRemovedEvent(files));
    verifyNoInteractions(deletionPort);
  }

  @Configuration
  @EnableAsync
  @EnableTransactionManagement
  @Import(MediaFileCleanupEventHandler.class)
  static class Config {
    @Bean
    DataSource dataSource() {
      return new DriverManagerDataSource(
          "jdbc:h2:mem:media_cleanup_transaction;DB_CLOSE_DELAY=-1", "sa", "");
    }

    @Bean
    PlatformTransactionManager transactionManager(DataSource dataSource) {
      return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    UnusedMediaDeletionPort deletionPort() {
      return mock(UnusedMediaDeletionPort.class);
    }

    @Bean(name = "mediaFileCleanupExecutor")
    Executor mediaFileCleanupExecutor() {
      return Runnable::run;
    }
  }
}

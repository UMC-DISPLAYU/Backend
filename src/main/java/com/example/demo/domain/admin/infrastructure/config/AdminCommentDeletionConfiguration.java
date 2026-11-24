package com.example.demo.domain.admin.infrastructure.config;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.CommentDeletionPort;
import com.example.demo.domain.admin.application.port.OperationLogPort;
import com.example.demo.domain.admin.application.service.AdminCommentDeletionService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "admin.comments.enabled", havingValue = "true")
public class AdminCommentDeletionConfiguration {
  @Bean
  AdminCommentDeletionService adminCommentDeletionService(
      AdminAccessPort access, CommentDeletionPort comments, OperationLogPort logs) {
    return new AdminCommentDeletionService(access, comments, logs);
  }
}

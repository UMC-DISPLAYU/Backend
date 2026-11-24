package com.example.demo.domain.admin.infrastructure.config;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.CommentQueryPort;
import com.example.demo.domain.admin.application.port.CommentReportQueryPort;
import com.example.demo.domain.admin.application.service.AdminCommentService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "admin.comments.enabled", havingValue = "true")
public class AdminCommentConfiguration {
  @Bean
  AdminCommentService adminCommentService(
      AdminAccessPort access, CommentQueryPort comments, CommentReportQueryPort reports) {
    return new AdminCommentService(access, comments, reports);
  }
}

package com.example.demo.domain.admin.infrastructure.config;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.ContentDeletionPort;
import com.example.demo.domain.admin.application.service.AdminContentDeletionService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "admin.content-deletion.enabled", havingValue = "true")
public class AdminContentDeletionConfiguration {
  @Bean
  AdminContentDeletionService adminContentDeletionService(
      AdminAccessPort access, ContentDeletionPort contents) {
    return new AdminContentDeletionService(access, contents);
  }
}

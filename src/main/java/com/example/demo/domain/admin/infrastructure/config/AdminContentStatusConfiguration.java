package com.example.demo.domain.admin.infrastructure.config;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.ContentStatusPort;
import com.example.demo.domain.admin.application.service.AdminContentStatusService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "admin.content-status.enabled", havingValue = "true")
public class AdminContentStatusConfiguration {
  @Bean
  AdminContentStatusService adminContentStatusService(
      AdminAccessPort access, ContentStatusPort contents) {
    return new AdminContentStatusService(access, contents);
  }
}

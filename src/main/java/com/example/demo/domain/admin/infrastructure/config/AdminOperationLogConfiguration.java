package com.example.demo.domain.admin.infrastructure.config;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.OperationLogPort;
import com.example.demo.domain.admin.application.service.AdminOperationLogService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "admin.operations.enabled", havingValue = "true")
public class AdminOperationLogConfiguration {
  @Bean
  AdminOperationLogService adminOperationLogService(AdminAccessPort access, OperationLogPort logs) {
    return new AdminOperationLogService(access, logs);
  }
}

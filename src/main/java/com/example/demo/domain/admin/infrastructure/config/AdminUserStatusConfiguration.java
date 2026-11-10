package com.example.demo.domain.admin.infrastructure.config;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.UserStatusPort;
import com.example.demo.domain.admin.application.service.AdminUserStatusService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "admin.user-status.enabled", havingValue = "true")
public class AdminUserStatusConfiguration {
  @Bean
  AdminUserStatusService adminUserStatusService(AdminAccessPort access, UserStatusPort users) {
    return new AdminUserStatusService(access, users);
  }
}

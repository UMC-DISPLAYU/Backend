package com.example.demo.domain.admin.infrastructure.config;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.ReportResolutionPort;
import com.example.demo.domain.admin.application.service.AdminReportResolutionService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "admin.report-resolution.enabled", havingValue = "true")
public class AdminReportResolutionConfiguration {
  @Bean
  AdminReportResolutionService adminReportResolutionService(
      AdminAccessPort access, ReportResolutionPort reports) {
    return new AdminReportResolutionService(access, reports);
  }
}

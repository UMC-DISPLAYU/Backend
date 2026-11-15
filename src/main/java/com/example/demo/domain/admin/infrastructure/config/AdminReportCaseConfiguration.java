package com.example.demo.domain.admin.infrastructure.config;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.ReportCaseQueryPort;
import com.example.demo.domain.admin.application.service.AdminReportCaseService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "admin.report-cases.enabled", havingValue = "true")
public class AdminReportCaseConfiguration {
  @Bean
  AdminReportCaseService adminReportCaseService(
      AdminAccessPort access, ReportCaseQueryPort reports) {
    return new AdminReportCaseService(access, reports);
  }
}

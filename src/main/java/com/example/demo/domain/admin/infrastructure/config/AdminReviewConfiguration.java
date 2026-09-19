package com.example.demo.domain.admin.infrastructure.config;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.DisplayReviewPort;
import com.example.demo.domain.admin.application.service.AdminReviewService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** 연동 완료 전에는 비활성. 활성화했는데 연동 Bean이 없으면 시작에 실패한다. */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "admin.review.enabled", havingValue = "true")
public class AdminReviewConfiguration {
  @Bean
  AdminReviewService adminReviewService(AdminAccessPort access, DisplayReviewPort reviews) {
    return new AdminReviewService(access, reviews);
  }
}

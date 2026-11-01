package com.example.demo.global.file.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3PresignerConfig {

  @Bean
  S3Presigner s3Presigner(FileStorageProperties properties) {
    return S3Presigner.builder().region(Region.of(properties.effectiveRegion())).build();
  }

  @Bean
  S3Client s3Client(FileStorageProperties properties) {
    return S3Client.builder().region(Region.of(properties.effectiveRegion())).build();
  }
}

package com.example.demo.global.event;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.domain-events.publication")
public class DomainEventPublicationProperties {

  private Duration recoveryInterval = Duration.ofMinutes(1);
  private Duration minimumRetryAge = Duration.ofMinutes(1);
  private int batchSize = 100;
  private int maxInFlight = 4;
  private int maxCompletionAttempts = 10;
  private Duration completedRetention = Duration.ofDays(30);

  public Duration getRecoveryInterval() {
    return recoveryInterval;
  }

  public void setRecoveryInterval(Duration recoveryInterval) {
    this.recoveryInterval = recoveryInterval;
  }

  public Duration getMinimumRetryAge() {
    return minimumRetryAge;
  }

  public void setMinimumRetryAge(Duration minimumRetryAge) {
    this.minimumRetryAge = minimumRetryAge;
  }

  public int getBatchSize() {
    return batchSize;
  }

  public void setBatchSize(int batchSize) {
    this.batchSize = batchSize;
  }

  public int getMaxInFlight() {
    return maxInFlight;
  }

  public void setMaxInFlight(int maxInFlight) {
    this.maxInFlight = maxInFlight;
  }

  public int getMaxCompletionAttempts() {
    return maxCompletionAttempts;
  }

  public void setMaxCompletionAttempts(int maxCompletionAttempts) {
    this.maxCompletionAttempts = maxCompletionAttempts;
  }

  public Duration getCompletedRetention() {
    return completedRetention;
  }

  public void setCompletedRetention(Duration completedRetention) {
    this.completedRetention = completedRetention;
  }
}

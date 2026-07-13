package com.example.demo.domain.lounge.domain.vo;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.EqualsAndHashCode;

@Embeddable
@EqualsAndHashCode
public class UserId implements Serializable {
  private Long value;

  protected UserId() {}

  public UserId(Long value) {
    if (value == null || value <= 0) {
      throw new IllegalArgumentException("userId must be positive");
    }
    this.value = value;
  }

  public Long value() {
    return value;
  }
}

package com.example.demo.domain.display.domain.vo;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.EqualsAndHashCode;

@Embeddable
@EqualsAndHashCode
public class TeamMemberId implements Serializable {

  private Long value;

  protected TeamMemberId() {}

  public TeamMemberId(Long value) {
    if (value == null || value <= 0) {
      throw new IllegalArgumentException("teamMemberId must be positive.");
    }
    this.value = value;
  }

  public Long value() {
    return value;
  }
}

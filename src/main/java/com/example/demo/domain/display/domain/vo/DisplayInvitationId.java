package com.example.demo.domain.display.domain.vo;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.EqualsAndHashCode;

@Embeddable
@EqualsAndHashCode
public class DisplayInvitationId implements Serializable {

  private Long value;

  protected DisplayInvitationId() {}

  public DisplayInvitationId(Long value) {
    if (value == null || value <= 0) {
      throw new IllegalArgumentException("displayInvitationId must be positive.");
    }
    this.value = value;
  }

  public Long value() {
    return value;
  }
}

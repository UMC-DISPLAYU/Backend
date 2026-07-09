package com.example.demo.domain.display.domain.vo;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.EqualsAndHashCode;

@Embeddable
@EqualsAndHashCode
public class DisplayContentCategoryId implements Serializable {

  private Long value;

  protected DisplayContentCategoryId() {}

  public DisplayContentCategoryId(Long value) {
    if (value == null || value <= 0) {
      throw new IllegalArgumentException("displayContentCategoryId must be positive.");
    }
    this.value = value;
  }

  public Long value() {
    return value;
  }
}

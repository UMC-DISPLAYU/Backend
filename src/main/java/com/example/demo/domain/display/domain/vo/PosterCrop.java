package com.example.demo.domain.display.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Embeddable
@EqualsAndHashCode
public class PosterCrop {
  @Column(name = "posterCropX", precision = 11, scale = 10)
  private BigDecimal x;

  @Column(name = "posterCropY", precision = 11, scale = 10)
  private BigDecimal y;

  @Column(name = "posterCropW", precision = 11, scale = 10)
  private BigDecimal w;

  @Column(name = "posterCropH", precision = 11, scale = 10)
  private BigDecimal h;

  protected PosterCrop() {}

  public PosterCrop(BigDecimal x, BigDecimal y, BigDecimal w, BigDecimal h) {
    this.x = coordinate(x);
    this.y = coordinate(y);
    this.w = coordinate(w);
    this.h = coordinate(h);
    if (w.signum() == 0
        || h.signum() == 0
        || x.add(w).compareTo(BigDecimal.ONE) > 0
        || y.add(h).compareTo(BigDecimal.ONE) > 0) {
      throw new IllegalArgumentException(
          "posterCrop must have positive dimensions and fit inside the image.");
    }
  }

  private static BigDecimal coordinate(BigDecimal value) {
    Objects.requireNonNull(value, "posterCrop coordinates must not be null.");
    if (value.signum() < 0
        || value.compareTo(BigDecimal.ONE) > 0
        || value.stripTrailingZeros().scale() > 10) {
      throw new IllegalArgumentException(
          "posterCrop coordinates must be between 0 and 1 with at most 10 decimal places.");
    }
    return value;
  }
}

package com.example.demo.domain.displayartwork.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Embeddable
@EqualsAndHashCode
public class ArtworkCrop {
  @Column(name = "artworkCropX", precision = 11, scale = 10)
  private BigDecimal x;

  @Column(name = "artworkCropY", precision = 11, scale = 10)
  private BigDecimal y;

  @Column(name = "artworkCropW", precision = 11, scale = 10)
  private BigDecimal w;

  @Column(name = "artworkCropH", precision = 11, scale = 10)
  private BigDecimal h;

  protected ArtworkCrop() {}

  public ArtworkCrop(BigDecimal x, BigDecimal y, BigDecimal w, BigDecimal h) {
    this.x = coordinate(x);
    this.y = coordinate(y);
    this.w = coordinate(w);
    this.h = coordinate(h);
    if (w.signum() == 0
        || h.signum() == 0
        || x.add(w).compareTo(BigDecimal.ONE) > 0
        || y.add(h).compareTo(BigDecimal.ONE) > 0) {
      throw new IllegalArgumentException(
          "artworkCrop must have positive dimensions and fit inside the image.");
    }
  }

  private static BigDecimal coordinate(BigDecimal value) {
    Objects.requireNonNull(value, "artworkCrop coordinates must not be null.");
    if (value.signum() < 0
        || value.compareTo(BigDecimal.ONE) > 0
        || value.stripTrailingZeros().scale() > 10) {
      throw new IllegalArgumentException(
          "artworkCrop coordinates must be between 0 and 1 with at most 10 decimal places.");
    }
    return value;
  }
}

package com.example.demo.domain.display.domain.vo;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.EqualsAndHashCode;

@Embeddable
@EqualsAndHashCode
public class DisplayLocation {

  private static final BigDecimal MIN_LATITUDE = BigDecimal.valueOf(-90);
  private static final BigDecimal MAX_LATITUDE = BigDecimal.valueOf(90);
  private static final BigDecimal MIN_LONGITUDE = BigDecimal.valueOf(-180);
  private static final BigDecimal MAX_LONGITUDE = BigDecimal.valueOf(180);

  private String placeName;
  private BigDecimal latitude;
  private BigDecimal longitude;
  private String roadAddress;

  protected DisplayLocation() {}

  public DisplayLocation(String placeName, BigDecimal latitude, BigDecimal longitude) {
    this(placeName, latitude, longitude, null);
  }

  public DisplayLocation(
      String placeName, BigDecimal latitude, BigDecimal longitude, String roadAddress) {
    if (placeName == null || placeName.isBlank()) {
      throw new IllegalArgumentException("placeName must not be blank.");
    }
    Objects.requireNonNull(latitude, "latitude must not be null.");
    Objects.requireNonNull(longitude, "longitude must not be null.");

    if (latitude.compareTo(MIN_LATITUDE) < 0 || latitude.compareTo(MAX_LATITUDE) > 0) {
      throw new IllegalArgumentException("latitude must be between -90 and 90.");
    }
    if (longitude.compareTo(MIN_LONGITUDE) < 0 || longitude.compareTo(MAX_LONGITUDE) > 0) {
      throw new IllegalArgumentException("longitude must be between -180 and 180.");
    }

    this.placeName = placeName;
    this.latitude = latitude;
    this.longitude = longitude;
    this.roadAddress = roadAddress;
  }

  public String placeName() {
    return placeName;
  }

  public BigDecimal latitude() {
    return latitude;
  }

  public BigDecimal longitude() {
    return longitude;
  }

  public String roadAddress() {
    return roadAddress;
  }
}

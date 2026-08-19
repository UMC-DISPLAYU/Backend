package com.example.demo.domain.display.domain.type;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum DisplayField {
  PAINTING,
  DESIGN,
  PHOTOGRAPHY,
  ARCHITECTURE,
  VIDEO,
  CRAFTS,
  SCULPTURE,
  FASHION,
  @Deprecated
  INTERDISCIPLINARY,
  ILLUSTRATION,
  OTHERS;

  @JsonCreator
  public static DisplayField from(String value) {
    DisplayField field = valueOf(value);
    if (field == INTERDISCIPLINARY) {
      throw new IllegalArgumentException("INTERDISCIPLINARY is no longer supported");
    }
    return field;
  }
}

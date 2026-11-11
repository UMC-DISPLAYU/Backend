package com.example.demo.domain.display.domain.type;

public enum DisplayField {
  PAINTING,
  SCULPTURE,
  PHOTOGRAPHY,
  GRAPHIC,
  BRANDING,
  PRODUCT,
  VIDEO,
  SPACE,
  ARCHITECTURE,
  UXUI,
  CRAFTS,
  OTHERS,
  @Deprecated
  DESIGN,
  @Deprecated
  FASHION,
  @Deprecated
  INTERDISCIPLINARY,
  @Deprecated
  ILLUSTRATION;

  public boolean isSelectable() {
    return this != DESIGN && this != FASHION && this != INTERDISCIPLINARY && this != ILLUSTRATION;
  }
}

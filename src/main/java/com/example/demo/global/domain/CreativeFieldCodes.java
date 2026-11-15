package com.example.demo.global.domain;

import java.util.Set;

public final class CreativeFieldCodes {
  private static final Set<String> SELECTABLE =
      Set.of(
          "PAINTING",
          "SCULPTURE",
          "PHOTOGRAPHY",
          "GRAPHIC",
          "BRANDING",
          "PRODUCT",
          "VIDEO",
          "SPACE",
          "ARCHITECTURE",
          "UX_UI",
          "CRAFTS",
          "OTHERS");

  private CreativeFieldCodes() {}

  public static String canonical(String code) {
    return switch (code) {
      case "UXUI" -> "UX_UI";
      case "MEDIA" -> "VIDEO";
      case "CRAFT" -> "CRAFTS";
      case "ETC" -> "OTHERS";
      default -> code;
    };
  }

  public static boolean isSelectable(String code) {
    return SELECTABLE.contains(canonical(code));
  }
}

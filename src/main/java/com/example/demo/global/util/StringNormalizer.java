package com.example.demo.global.util;

public final class StringNormalizer {

  private StringNormalizer() {}

  public static String normalize(String value) {
    return value == null || value.isBlank() ? null : value.trim();
  }
}

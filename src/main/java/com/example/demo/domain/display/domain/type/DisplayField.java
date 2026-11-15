package com.example.demo.domain.display.domain.type;

import com.example.demo.global.domain.CreativeFieldCodes;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

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
  UX_UI,
  CRAFTS,
  OTHERS,
  DESIGN,
  FASHION,
  INTERDISCIPLINARY,
  ILLUSTRATION,
  UXUI,
  MEDIA,
  CRAFT,
  ETC;

  public DisplayField canonical() {
    return valueOf(CreativeFieldCodes.canonical(name()));
  }

  public boolean isSelectable() {
    return CreativeFieldCodes.isSelectable(name());
  }

  public static DisplayField fromCode(String code) {
    return valueOf(CreativeFieldCodes.canonical(code));
  }

  public Set<DisplayField> storedAliases() {
    return Arrays.stream(values())
        .filter(value -> value.canonical() == canonical())
        .collect(Collectors.toUnmodifiableSet());
  }
}

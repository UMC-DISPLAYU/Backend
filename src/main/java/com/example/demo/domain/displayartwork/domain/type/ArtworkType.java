package com.example.demo.domain.displayartwork.domain.type;

import com.example.demo.global.domain.CreativeFieldCodes;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum ArtworkType {
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
  COMPLEX,
  ILLUSTRATION,
  MEDIA,
  CRAFT,
  ETC,
  UXUI;

  public ArtworkType canonical() {
    return valueOf(CreativeFieldCodes.canonical(name()));
  }

  public boolean isSelectable() {
    return CreativeFieldCodes.isSelectable(name());
  }

  public static ArtworkType fromCode(String code) {
    return valueOf(CreativeFieldCodes.canonical(code));
  }

  public Set<ArtworkType> storedAliases() {
    return Arrays.stream(values())
        .filter(value -> value.canonical() == canonical())
        .collect(Collectors.toUnmodifiableSet());
  }
}

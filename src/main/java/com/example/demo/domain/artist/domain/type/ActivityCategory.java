package com.example.demo.domain.artist.domain.type;

import com.example.demo.global.domain.CreativeFieldCodes;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum ActivityCategory {
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
  ILLUSTRATION,
  CRAFT,
  ETC,
  MEDIA,
  UXUI;

  public ActivityCategory canonical() {
    return valueOf(CreativeFieldCodes.canonical(name()));
  }

  public boolean isSelectable() {
    return CreativeFieldCodes.isSelectable(name());
  }

  public static ActivityCategory fromCode(String code) {
    return valueOf(CreativeFieldCodes.canonical(code));
  }

  public Set<ActivityCategory> storedAliases() {
    return Arrays.stream(values())
        .filter(value -> value.canonical() == canonical())
        .collect(Collectors.toUnmodifiableSet());
  }
}

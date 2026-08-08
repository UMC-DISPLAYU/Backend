package com.example.demo.domain.lounge.domain.type;

public enum LoungePostCategory {
  DISPLAY_REVIEW,
  WORK_TIP,
  COLLABORATION,
  SPACE_RENTAL;

  public boolean requiresArtistVerification() {
    return this == WORK_TIP || this == COLLABORATION;
  }
}

package com.example.demo.domain.personalartwork.application.result;

import com.example.demo.domain.personalartwork.domain.aggregate.PersonalArtwork;

public record PersonalArtworkAccessResult(Long personalArtworkId, Long ownerUserId) {

  public static PersonalArtworkAccessResult from(PersonalArtwork personalArtwork) {
    return new PersonalArtworkAccessResult(
        personalArtwork.getId(), personalArtwork.getOwnerUserId().value());
  }

  public boolean isOwner(Long userId) {
    return ownerUserId.equals(userId);
  }
}

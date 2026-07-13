package com.example.demo.domain.personalartwork.domain.repository;

import com.example.demo.domain.personalartwork.domain.aggregate.PersonalArtwork;
import java.util.List;
import java.util.Optional;

public interface PersonalArtworkRepository {

  Optional<PersonalArtwork> findById(Long personalArtworkId);

  List<PersonalArtwork> findAllByOwnerUserIdOrderByCreatedAtAsc(Long userId);

  PersonalArtwork save(PersonalArtwork personalArtwork);
}

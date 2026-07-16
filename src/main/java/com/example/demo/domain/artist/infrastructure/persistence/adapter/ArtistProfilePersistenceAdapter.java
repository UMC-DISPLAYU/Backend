package com.example.demo.domain.artist.infrastructure.persistence.adapter;

import com.example.demo.domain.artist.domain.aggregate.ArtistProfile;
import com.example.demo.domain.artist.domain.repository.ArtistProfileRepository;
import com.example.demo.domain.artist.infrastructure.persistence.ArtistProfileJpaRepository;
import com.example.demo.domain.user.domain.aggregate.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ArtistProfilePersistenceAdapter implements ArtistProfileRepository {

    private final ArtistProfileJpaRepository artistProfileJpaRepository;

    @Override
    public ArtistProfile save(ArtistProfile artistProfile) {
        return artistProfileJpaRepository.save(artistProfile);
    }

    @Override
    public Optional<ArtistProfile> findByUser(User user) {
        return artistProfileJpaRepository.findByUser(user);
    }

    @Override
    public boolean existsByArtistName(String artistName) {
        return artistProfileJpaRepository.existsByArtistName(artistName);
    }
}

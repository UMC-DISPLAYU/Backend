package com.example.demo.domain.artist.infrastructure.persistence;

import com.example.demo.domain.artist.domain.aggregate.ArtistProfile;
import com.example.demo.domain.artist.domain.entity.AreaOfActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AreaOfActivityJpaRepository
        extends JpaRepository<AreaOfActivity, Long> {

    List<AreaOfActivity> findByArtistProfile(ArtistProfile artistProfile);
}
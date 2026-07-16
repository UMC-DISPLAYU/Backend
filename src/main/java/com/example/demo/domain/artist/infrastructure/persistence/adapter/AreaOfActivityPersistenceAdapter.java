package com.example.demo.domain.artist.infrastructure.persistence.adapter;

import com.example.demo.domain.artist.domain.aggregate.ArtistProfile;
import com.example.demo.domain.artist.domain.entity.AreaOfActivity;
import com.example.demo.domain.artist.domain.repository.AreaOfActivityRepository;
import com.example.demo.domain.artist.infrastructure.persistence.AreaOfActivityJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AreaOfActivityPersistenceAdapter implements AreaOfActivityRepository {

    private final AreaOfActivityJpaRepository areaOfActivityJpaRepository;

    @Override
    public AreaOfActivity save(AreaOfActivity areaOfActivity) {
        return areaOfActivityJpaRepository.save(areaOfActivity);
    }

    @Override
    public List<AreaOfActivity> findByArtistProfile(ArtistProfile artistProfile) {
        return areaOfActivityJpaRepository.findByArtistProfile(artistProfile);
    }
}

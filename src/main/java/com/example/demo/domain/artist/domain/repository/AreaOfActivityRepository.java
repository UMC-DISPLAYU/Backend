package com.example.demo.domain.artist.domain.repository;

import com.example.demo.domain.artist.domain.aggregate.ArtistProfile;
import com.example.demo.domain.artist.domain.entity.AreaOfActivity;

import java.util.List;

public interface AreaOfActivityRepository {

    AreaOfActivity save(AreaOfActivity areaOfActivity);

    List<AreaOfActivity> findByArtistProfile(ArtistProfile artistProfile);
}

package com.example.demo.domain.artist.infrastructure.persistence;

import com.example.demo.domain.artist.domain.aggregate.ArtistProfile;
import com.example.demo.domain.artist.domain.entity.AreaOfActivity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AreaOfActivityJpaRepository extends JpaRepository<AreaOfActivity, Long> {

  List<AreaOfActivity> findByArtistProfile(ArtistProfile artistProfile);
}

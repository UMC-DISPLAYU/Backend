package com.example.demo.domain.artist.infrastructure.persistence;

import com.example.demo.domain.artist.domain.aggregate.ArtistProfile;
import com.example.demo.domain.artist.domain.entity.AreaOfActivity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AreaOfActivityJpaRepository extends JpaRepository<AreaOfActivity, Long> {

  List<AreaOfActivity> findByArtistProfile(ArtistProfile artistProfile);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("delete from AreaOfActivity area where area.artistProfile = :artistProfile")
  void deleteAllByArtistProfile(ArtistProfile artistProfile);
}

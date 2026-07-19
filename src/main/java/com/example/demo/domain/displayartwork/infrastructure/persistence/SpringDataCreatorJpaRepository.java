package com.example.demo.domain.displayartwork.infrastructure.persistence;

import com.example.demo.domain.displayartwork.domain.entity.Creator;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCreatorJpaRepository extends JpaRepository<Creator, Long> {

  List<Creator> findByDisplayArtworkId(Long displayArtworkId);

  Optional<Creator> findFirstByDisplayArtworkIdAndIsLeaderTrue(Long displayArtworkId);

  void deleteAllByDisplayArtworkId(Long displayArtworkId);
}

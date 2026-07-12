package com.example.demo.domain.archive.infrastructure.persistence;

import com.example.demo.domain.archive.domain.aggregate.ArchiveWork;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataArchiveWorkJpaRepository extends JpaRepository<ArchiveWork, Long> {

  Optional<ArchiveWork> findByUserIdAndDisplayArtworkId(Long userId, Long displayArtworkId);

  List<ArchiveWork> findAllByUserIdOrderBySavedAtDescIdDesc(Long userId);
}

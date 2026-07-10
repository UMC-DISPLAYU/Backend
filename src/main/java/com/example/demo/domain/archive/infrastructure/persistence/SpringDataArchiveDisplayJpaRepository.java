package com.example.demo.domain.archive.infrastructure.persistence;

import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataArchiveDisplayJpaRepository extends JpaRepository<ArchiveDisplay, Long> {

  Optional<ArchiveDisplay> findByUserIdAndDisplayId(Long userId, Long displayId);

  List<ArchiveDisplay> findAllByUserIdOrderByIdDesc(Long userId);
}

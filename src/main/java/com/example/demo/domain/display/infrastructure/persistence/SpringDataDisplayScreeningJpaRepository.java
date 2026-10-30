package com.example.demo.domain.display.infrastructure.persistence;

import com.example.demo.domain.display.domain.entity.DisplayScreening;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataDisplayScreeningJpaRepository
    extends JpaRepository<DisplayScreening, Long> {

  Optional<DisplayScreening> findFirstByDisplayIdOrderByIdDesc(Long displayId);

  List<DisplayScreening> findAllByDisplayIdOrderByIdDesc(Long displayId);
}

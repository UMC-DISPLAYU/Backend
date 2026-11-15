package com.example.demo.domain.display.domain.repository;

import com.example.demo.domain.display.domain.entity.DisplayScreening;
import java.util.List;
import java.util.Optional;

public interface DisplayScreeningRepository {

  Optional<DisplayScreening> findLatestByDisplayId(Long displayId);

  Optional<DisplayScreening> findByIdAndDisplayId(Long screeningId, Long displayId);

  List<DisplayScreening> findAllByDisplayIdOrderByIdDesc(Long displayId);

  DisplayScreening save(DisplayScreening screening);
}

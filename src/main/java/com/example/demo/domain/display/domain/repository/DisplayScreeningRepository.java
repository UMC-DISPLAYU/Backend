package com.example.demo.domain.display.domain.repository;

import com.example.demo.domain.display.domain.entity.DisplayScreening;
import java.util.Optional;

public interface DisplayScreeningRepository {

  Optional<DisplayScreening> findLatestByDisplayId(Long displayId);

  DisplayScreening save(DisplayScreening screening);
}

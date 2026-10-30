package com.example.demo.domain.display.domain.repository;

import com.example.demo.domain.display.domain.entity.DisplayScreening;
import java.util.List;
import java.util.Optional;

public interface DisplayScreeningRepository {

  Optional<DisplayScreening> findLatestByDisplayId(Long displayId);

  /** 최신 요청이 먼저 오도록 정렬한다. */
  List<DisplayScreening> findAllByDisplayId(Long displayId);

  DisplayScreening save(DisplayScreening screening);
}

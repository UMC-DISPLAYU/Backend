package com.example.demo.domain.display.infrastructure.persistence.adapter;

import com.example.demo.domain.display.domain.repository.DuPickColumnRepository;
import com.example.demo.domain.display.domain.vo.PosterCrop;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDuPickQueryJpaRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaDuPickColumnRepositoryAdapter implements DuPickColumnRepository {
  private final SpringDataDuPickQueryJpaRepository repository;

  @Override
  public boolean updatePosterCrop(Long duPickId, PosterCrop crop, LocalDateTime changedAt) {
    return repository
        .findById(duPickId)
        .map(
            column -> {
              column.changePosterCrop(crop, changedAt);
              return true;
            })
        .orElse(false);
  }
}

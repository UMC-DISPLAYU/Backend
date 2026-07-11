package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtworkFeelingJpaRepository extends JpaRepository<ArtworkFeeling, Long> {
}

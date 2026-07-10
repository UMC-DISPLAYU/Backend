package com.example.demo.domain.display.domain.repository;

import com.example.demo.domain.display.domain.aggregate.Display;
import java.util.List;
import java.util.Optional;

public interface DisplayRepository {

  Optional<Display> findById(Long displayId);

  List<Display> findAll();

  Display save(Display display);

  void delete(Display display);
}

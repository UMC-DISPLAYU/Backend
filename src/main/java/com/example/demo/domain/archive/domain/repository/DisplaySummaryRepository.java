package com.example.demo.domain.archive.domain.repository;

import java.util.List;

public interface DisplaySummaryRepository {

  List<DisplaySummary> findByDisplayIdIn(List<Long> displayIds);
}

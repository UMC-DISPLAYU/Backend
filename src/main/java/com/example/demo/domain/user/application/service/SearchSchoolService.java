package com.example.demo.domain.user.application.service;

import com.example.demo.domain.user.application.result.SchoolSearchResult;
import com.example.demo.domain.user.domain.repository.UniversityRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchSchoolService {

  private final UniversityRepository universityRepository;

  @Transactional(readOnly = true)
  public List<SchoolSearchResult> execute(String keyword) {
    if (keyword == null || keyword.isBlank()) {
      return List.of();
    }

    return universityRepository.searchByName(keyword.trim()).stream()
        .map(university -> new SchoolSearchResult(university.getName()))
        .toList();
  }
}

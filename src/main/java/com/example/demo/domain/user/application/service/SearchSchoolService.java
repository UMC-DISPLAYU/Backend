package com.example.demo.domain.user.application.service;

import com.example.demo.domain.user.application.result.SchoolSearchResult;
import com.example.demo.domain.user.domain.enums.University;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SearchSchoolService {

  public List<SchoolSearchResult> execute(String keyword) {
    return University.searchBySchoolName(keyword).stream()
        .map(university -> new SchoolSearchResult(university.getSchoolName()))
        .distinct()
        .toList();
  }
}

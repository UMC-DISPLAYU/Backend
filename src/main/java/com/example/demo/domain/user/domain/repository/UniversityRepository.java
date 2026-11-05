package com.example.demo.domain.user.domain.repository;

import com.example.demo.domain.user.domain.aggregate.University;
import java.util.List;
import java.util.Optional;

public interface UniversityRepository {

  List<University> searchByName(String keyword);

  Optional<University> findByName(String name);

  boolean existsEmailDomain(Long universityId, String domain);
}

package com.example.demo.domain.user.infrastructure.persistence;

import com.example.demo.domain.user.domain.aggregate.University;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UniversityJpaRepository extends JpaRepository<University, Long> {

  @Query(
      """
      SELECT DISTINCT university
      FROM University university
      LEFT JOIN UniversityAlias alias ON alias.university = university
      WHERE university.active = true
        AND (LOWER(university.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(alias.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
      ORDER BY university.name
      """)
  List<University> searchByName(@Param("keyword") String keyword);

  @Query(
      """
      SELECT DISTINCT university
      FROM University university
      LEFT JOIN UniversityAlias alias ON alias.university = university
      WHERE university.active = true
        AND (university.name = :name OR alias.name = :name)
      """)
  Optional<University> findByNameOrAlias(@Param("name") String name);
}

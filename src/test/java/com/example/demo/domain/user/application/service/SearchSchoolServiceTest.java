package com.example.demo.domain.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.domain.user.domain.aggregate.University;
import com.example.demo.domain.user.domain.repository.UniversityRepository;
import java.util.List;
import org.junit.jupiter.api.Test;

class SearchSchoolServiceTest {

  private final UniversityRepository universityRepository = mock(UniversityRepository.class);
  private final SearchSchoolService service = new SearchSchoolService(universityRepository);

  @Test
  void searchesExactSchoolName() {
    University university = university("서울대학교");
    when(universityRepository.searchByName("서울대학교")).thenReturn(List.of(university));

    assertThat(service.execute("서울대학교")).extracting(result -> result.name()).contains("서울대학교");
  }

  @Test
  void searchesPartialSchoolNameInSortedOrder() {
    University dongSeoul = university("동서울대학교");
    University seoul = university("서울대학교");
    University seoulCity = university("서울시립대학교");
    when(universityRepository.searchByName("서울")).thenReturn(List.of(dongSeoul, seoul, seoulCity));

    assertThat(service.execute("서울"))
        .extracting(result -> result.name())
        .contains("서울대학교", "서울시립대학교", "동서울대학교")
        .containsExactly("동서울대학교", "서울대학교", "서울시립대학교");
  }

  @Test
  void trimsKeywordAndIgnoresEnglishCase() {
    University university = university("GIST");
    when(universityRepository.searchByName("gist")).thenReturn(List.of(university));

    assertThat(service.execute("  gist  ")).extracting(result -> result.name()).contains("GIST");
  }

  @Test
  void returnsEmptyListForBlankOrUnknownKeyword() {
    assertThat(service.execute("   ")).isEmpty();
    assertThat(service.execute("존재하지않는대학교")).isEmpty();
  }

  private University university(String name) {
    University university = mock(University.class);
    when(university.getName()).thenReturn(name);
    return university;
  }
}

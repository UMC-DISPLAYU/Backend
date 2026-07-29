package com.example.demo.domain.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SearchSchoolServiceTest {

  private final SearchSchoolService service = new SearchSchoolService();

  @Test
  void searchesExactSchoolName() {
    assertThat(service.execute("서울대학교")).extracting(result -> result.name()).contains("서울대학교");
  }

  @Test
  void searchesPartialSchoolNameInSortedOrder() {
    assertThat(service.execute("서울"))
        .extracting(result -> result.name())
        .contains("서울대학교", "서울시립대학교", "동서울대학교")
        .isSorted();
  }

  @Test
  void trimsKeywordAndIgnoresEnglishCase() {
    assertThat(service.execute("  gist  ")).extracting(result -> result.name()).contains("GIST");
  }

  @Test
  void returnsEmptyListForBlankOrUnknownKeyword() {
    assertThat(service.execute("   ")).isEmpty();
    assertThat(service.execute("존재하지않는대학교")).isEmpty();
  }
}

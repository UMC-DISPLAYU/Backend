package com.example.demo.domain.user.presentation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.user.application.result.SchoolSearchResult;
import com.example.demo.domain.user.application.service.SearchSchoolService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class SchoolControllerTest {

  private final SearchSchoolService searchSchoolService = mock(SearchSchoolService.class);
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(new SchoolController(searchSchoolService)).build();
  }

  @Test
  void searchesSchoolsWithoutAuthentication() throws Exception {
    when(searchSchoolService.execute("서울"))
        .thenReturn(List.of(new SchoolSearchResult("서울대학교"), new SchoolSearchResult("서울시립대학교")));

    mockMvc
        .perform(get("/api/v1/schools").param("keyword", "서울"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data[0].name").value("서울대학교"))
        .andExpect(jsonPath("$.success.data[1].name").value("서울시립대학교"));
  }

  @Test
  void returnsEmptyArrayWhenNoSchoolMatches() throws Exception {
    when(searchSchoolService.execute("없음")).thenReturn(List.of());

    mockMvc
        .perform(get("/api/v1/schools").param("keyword", "없음"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data").isArray())
        .andExpect(jsonPath("$.success.data").isEmpty());
  }
}

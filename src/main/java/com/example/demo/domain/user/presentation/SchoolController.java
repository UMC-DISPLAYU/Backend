package com.example.demo.domain.user.presentation;

import com.example.demo.domain.user.application.service.SearchSchoolService;
import com.example.demo.domain.user.presentation.docs.SchoolControllerDocs;
import com.example.demo.domain.user.presentation.response.SchoolSearchResponse;
import com.example.demo.global.response.ApiResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schools")
public class SchoolController implements SchoolControllerDocs {

  private final SearchSchoolService searchSchoolService;

  @Override
  @GetMapping
  public ApiResponseBody<List<SchoolSearchResponse>> search(
      @RequestParam(value = "keyword", required = false) String keyword,
      HttpServletRequest httpRequest) {
    List<SchoolSearchResponse> response =
        searchSchoolService.execute(keyword).stream()
            .map(result -> new SchoolSearchResponse(result.name()))
            .toList();
    return ApiResponseBody.success(response, httpRequest);
  }
}

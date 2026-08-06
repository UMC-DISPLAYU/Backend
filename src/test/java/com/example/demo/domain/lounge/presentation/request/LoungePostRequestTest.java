package com.example.demo.domain.lounge.presentation.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.lounge.domain.type.LoungePostCategory;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import org.junit.jupiter.api.Test;

class LoungePostRequestTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void validatesTitleLength() {
    assertThat(validator.validate(request("가".repeat(100), "내용"))).isEmpty();
    assertThat(validator.validate(request("가".repeat(101), "내용")))
        .extracting(violation -> violation.getPropertyPath().toString())
        .containsExactly("title");
  }

  @Test
  void validatesContentLength() {
    assertThat(validator.validate(request("제목", "가".repeat(1500)))).isEmpty();
    assertThat(validator.validate(request("제목", "가".repeat(1501))))
        .extracting(violation -> violation.getPropertyPath().toString())
        .containsExactly("content");
  }

  private LoungePostRequest request(String title, String content) {
    return new LoungePostRequest(title, List.of(), content, LoungePostCategory.DISPLAY_REVIEW);
  }
}

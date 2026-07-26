package com.example.demo.domain.user.presentation;

import com.example.demo.domain.user.application.query.AgreementQueryService;
import com.example.demo.domain.user.presentation.docs.AgreementControllerDocs;
import com.example.demo.domain.user.presentation.mapper.AgreementPresentationMapper;
import com.example.demo.domain.user.presentation.response.AgreementResponse;
import com.example.demo.global.response.ApiResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/agreements")
public class AgreementController implements AgreementControllerDocs {

  private final AgreementQueryService agreementQueryService;
  private final AgreementPresentationMapper agreementPresentationMapper;

  @Override
  @GetMapping
  public ApiResponseBody<List<AgreementResponse>> getAgreements(HttpServletRequest request) {
    List<AgreementResponse> response =
        agreementQueryService.getAgreements().stream()
            .map(agreementPresentationMapper::toResponse)
            .toList();

    return ApiResponseBody.success(response, request);
  }
}

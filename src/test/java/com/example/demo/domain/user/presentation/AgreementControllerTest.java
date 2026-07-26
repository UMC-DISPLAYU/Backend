package com.example.demo.domain.user.presentation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.user.application.query.AgreementQueryService;
import com.example.demo.domain.user.application.result.AgreementResult;
import com.example.demo.domain.user.presentation.mapper.AgreementPresentationMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AgreementControllerTest {

  private final AgreementQueryService agreementQueryService = mock(AgreementQueryService.class);
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    AgreementController controller =
        new AgreementController(agreementQueryService, new AgreementPresentationMapper());
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  void returnsDatabaseAgreementIdsInAscendingOrderWithoutAuthentication() throws Exception {
    when(agreementQueryService.getAgreements())
        .thenReturn(
            List.of(
                new AgreementResult(70L, "위치 기반 서비스 약관", true),
                new AgreementResult(110L, "서비스 이용약관", true),
                new AgreementResult(120L, "개인정보 처리방침", true),
                new AgreementResult(130L, "마케팅 정보 수신 동의", false)));

    mockMvc
        .perform(get("/api/v1/agreements"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.length()").value(4))
        .andExpect(jsonPath("$.success.data[0].agreementId").value(70))
        .andExpect(jsonPath("$.success.data[0].title").value("위치 기반 서비스 약관"))
        .andExpect(jsonPath("$.success.data[0].required").value(true))
        .andExpect(jsonPath("$.success.data[1].agreementId").value(110))
        .andExpect(jsonPath("$.success.data[2].agreementId").value(120))
        .andExpect(jsonPath("$.success.data[3].agreementId").value(130))
        .andExpect(jsonPath("$.success.data[3].required").value(false))
        .andExpect(jsonPath("$.meta.path").value("/api/v1/agreements"));
  }
}

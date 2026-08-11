package com.example.demo.domain.user.presentation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.user.application.query.AgreementQueryService;
import com.example.demo.domain.user.application.result.AgreementResult;
import com.example.demo.domain.user.domain.type.Type;
import com.example.demo.domain.user.presentation.mapper.AgreementPresentationMapper;
import java.time.LocalDate;
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
  void returnsActiveSignupAgreementsWithLifecycleFields() throws Exception {
    when(agreementQueryService.getAgreements())
        .thenReturn(
            List.of(
                result(11L, "TERMS_OF_SERVICE", "서비스 이용약관", Type.SERVICE, true, 1),
                result(12L, "PRIVACY_COLLECTION_USE", "개인정보 수집·이용 동의", Type.PRIVACY, true, 2),
                result(7L, "LOCATION_BASED_SERVICE", "위치기반서비스 이용약관", Type.SERVICE, false, 3)));

    mockMvc
        .perform(get("/api/v1/agreements"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.length()").value(3))
        .andExpect(jsonPath("$.success.data[0].agreementId").value(11))
        .andExpect(jsonPath("$.success.data[0].code").value("TERMS_OF_SERVICE"))
        .andExpect(jsonPath("$.success.data[0].version").value("1.0"))
        .andExpect(jsonPath("$.success.data[0].effectiveDate").value("2026-08-01"))
        .andExpect(jsonPath("$.success.data[0].displayOrder").value(1))
        .andExpect(jsonPath("$.success.data[1].code").value("PRIVACY_COLLECTION_USE"))
        .andExpect(jsonPath("$.success.data[2].code").value("LOCATION_BASED_SERVICE"))
        .andExpect(jsonPath("$.success.data[2].required").value(false))
        .andExpect(jsonPath("$.meta.path").value("/api/v1/agreements"));
  }

  private static AgreementResult result(
      Long id, String code, String title, Type type, boolean required, int displayOrder) {
    return new AgreementResult(
        id, code, title, type, "content", required, "1.0", LocalDate.of(2026, 8, 1), displayOrder);
  }
}

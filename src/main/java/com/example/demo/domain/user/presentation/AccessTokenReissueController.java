package com.example.demo.domain.user.presentation;

import com.example.demo.domain.user.application.service.AuthService;
import com.example.demo.domain.user.presentation.docs.AccessTokenReissueControllerDocs;
import com.example.demo.domain.user.presentation.response.RefreshResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AccessTokenReissueController implements AccessTokenReissueControllerDocs {

  private final AuthService authService;

  @Override
  @GetMapping("/reissue")
  public RefreshResponse reissue(
      @CookieValue(name = "refreshToken", required = false) String refreshToken) {
    return new RefreshResponse(authService.refresh(refreshToken));
  }
}

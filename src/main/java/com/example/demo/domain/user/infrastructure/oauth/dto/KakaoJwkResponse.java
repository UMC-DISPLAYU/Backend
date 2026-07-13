package com.example.demo.domain.user.infrastructure.oauth.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoJwkResponse {

  private List<KakaoJwkKey> keys;
}

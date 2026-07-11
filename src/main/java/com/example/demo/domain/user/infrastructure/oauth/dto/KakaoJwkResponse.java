package com.example.demo.domain.user.infrastructure.oauth.dto;



import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class KakaoJwkResponse {

    private List<KakaoJwkKey> keys;
}

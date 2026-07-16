package com.example.demo.domain.user.infrastructure.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleJwkKey {

  /** Key Type RSA */
  private String kty;

  /** Key ID */
  private String kid;

  /** RSA Algorithm */
  private String alg;

  /** Public Key Use */
  private String use;

  /** Modulus */
  private String n;

  /** Exponent */
  private String e;

  /** X.509 Certificate Chain */
  @JsonProperty("x5c")
  private String[] x5c;
}

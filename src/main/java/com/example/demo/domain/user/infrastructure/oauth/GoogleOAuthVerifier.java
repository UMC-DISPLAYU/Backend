package com.example.demo.domain.user.infrastructure.oauth;

import com.example.demo.domain.user.application.auth.SocialUserInfo;
import com.example.demo.domain.user.application.port.GoogleOAuthVerifierPort;
import com.example.demo.domain.user.domain.error.AuthErrorCode;
import com.example.demo.domain.user.domain.type.Provider;
import com.example.demo.domain.user.infrastructure.oauth.dto.GoogleJwkKey;
import com.example.demo.global.error.BusinessException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleOAuthVerifier implements GoogleOAuthVerifierPort {

  @Value("${app.google.client.id}")
  private String googleClientId;

  private final GoogleJwkClient googleJwkClient;

  @Override
  public SocialUserInfo verify(String idToken) {

    try {

      SignedJWT signedJWT = SignedJWT.parse(idToken);
      log.debug(
          "Google ID token parsed. keyIdPresent={}",
          signedJWT.getHeader() != null && signedJWT.getHeader().getKeyID() != null);

      validateSignature(signedJWT);

      JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

      validateIssuer(claims);

      validateAudience(claims);

      validateExpiration(claims);

      log.info(
          "Google ID token verification completed. subjectPresent={}, emailPresent={}",
          claims.getSubject() != null,
          claims.getStringClaim("email") != null);
      return new SocialUserInfo(
          Provider.Google,
          claims.getSubject(),
          claims.getStringClaim("name"),
          claims.getStringClaim("email"));

    } catch (Exception e) {
      log.warn(
          "Google ID token verification failed. reason={}, exception={}",
          e.getMessage(),
          e.getClass().getSimpleName());
      throw new BusinessException(AuthErrorCode.INVALID_SOCIAL_TOKEN);
    }
  }

  private void validateSignature(SignedJWT signedJWT) throws Exception {

    GoogleJwkKey jwk = googleJwkClient.getKey(signedJWT.getHeader().getKeyID());

    RSAPublicKey publicKey = generatePublicKey(jwk);

    JWSVerifier verifier = new RSASSAVerifier(publicKey);

    if (!signedJWT.verify(verifier)) {

      throw new IllegalArgumentException("Invalid signature");
    }
  }

  private RSAPublicKey generatePublicKey(GoogleJwkKey jwk) throws Exception {

    byte[] modulusBytes = Base64.getUrlDecoder().decode(jwk.getN());

    byte[] exponentBytes = Base64.getUrlDecoder().decode(jwk.getE());

    BigInteger modulus = new BigInteger(1, modulusBytes);

    BigInteger exponent = new BigInteger(1, exponentBytes);

    RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, exponent);

    KeyFactory keyFactory = KeyFactory.getInstance("RSA");

    return (RSAPublicKey) keyFactory.generatePublic(keySpec);
  }

  private void validateIssuer(JWTClaimsSet claims) {

    String issuer = claims.getIssuer();

    if (!"https://accounts.google.com".equals(issuer) && !"accounts.google.com".equals(issuer)) {

      throw new IllegalArgumentException("Invalid issuer");
    }
  }

  private void validateAudience(JWTClaimsSet claims) {

    if (claims.getAudience() == null || !claims.getAudience().contains(googleClientId)) {

      throw new IllegalArgumentException("Invalid audience");
    }
  }

  private void validateExpiration(JWTClaimsSet claims) {

    Date expiration = claims.getExpirationTime();

    if (expiration == null || expiration.before(new Date())) {

      throw new IllegalArgumentException("Expired token");
    }
  }
}

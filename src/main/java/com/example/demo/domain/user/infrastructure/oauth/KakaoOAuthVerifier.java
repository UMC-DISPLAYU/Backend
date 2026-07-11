package com.example.demo.domain.user.infrastructure.oauth;

import com.example.demo.domain.user.application.auth.SocialUserInfo;
import com.example.demo.domain.user.domain.enums.Provider;
import com.example.demo.domain.user.exception.AuthErrorCode;
import com.example.demo.domain.user.infrastructure.oauth.dto.KakaoJwkKey;
import com.example.demo.global.error.BusinessException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class KakaoOAuthVerifier {

    // 현재 카카오만 구현
    // 추후 Google 추가 시 Provider별 Verifier 분리 예정
    @Value("${app.kakao.client.id}")
    private String kakaoClientId;


    private final KakaoJwkClient kakaoJwkClient;


    public SocialUserInfo verify(String idToken) {

        try {

            SignedJWT signedJWT =
                    SignedJWT.parse(idToken);


            validateSignature(signedJWT);


            JWTClaimsSet claims =
                    signedJWT.getJWTClaimsSet();


            validateIssuer(claims);
            validateAudience(claims);
            validateExpiration(claims);


            return new SocialUserInfo(
                    Provider.Kakao,
                    claims.getSubject(),
                    claims.getStringClaim("nickname"),
                    claims.getStringClaim("email")
            );


        }catch (Exception e) {

            throw new BusinessException(
                    AuthErrorCode.INVALID_SOCIAL_TOKEN
            );
        }
    }


    private void validateSignature(
            SignedJWT signedJWT
    ) throws Exception {


        JWSHeader header =
                signedJWT.getHeader();


        KakaoJwkKey jwk =
                kakaoJwkClient.getKey(
                        header.getKeyID()
                );


        RSAPublicKey publicKey =
                generatePublicKey(jwk);


        JWSVerifier verifier =
                new RSASSAVerifier(publicKey);


        if (!signedJWT.verify(verifier)) {

            throw new IllegalArgumentException(
                    "Invalid signature"
            );
        }
    }


    private RSAPublicKey generatePublicKey(
            KakaoJwkKey jwk
    ) throws Exception {


        byte[] modulusBytes =
                Base64.getUrlDecoder()
                        .decode(jwk.getN());


        byte[] exponentBytes =
                Base64.getUrlDecoder()
                        .decode(jwk.getE());


        BigInteger modulus =
                new BigInteger(
                        1,
                        modulusBytes
                );


        BigInteger exponent =
                new BigInteger(
                        1,
                        exponentBytes
                );


        RSAPublicKeySpec keySpec =
                new RSAPublicKeySpec(
                        modulus,
                        exponent
                );


        KeyFactory keyFactory =
                KeyFactory.getInstance("RSA");


        return (RSAPublicKey)
                keyFactory.generatePublic(keySpec);
    }


    private void validateIssuer(
            JWTClaimsSet claims
    ) {


        if (!"https://kauth.kakao.com"
                .equals(claims.getIssuer())) {


            throw new IllegalArgumentException(
                    "Invalid issuer"
            );
        }
    }


    private void validateAudience(
            JWTClaimsSet claims
    ) {


        if (claims.getAudience() == null
                || !claims.getAudience()
                .contains(kakaoClientId)) {


            throw new IllegalArgumentException(
                    "Invalid audience"
            );
        }
    }


    private void validateExpiration(
            JWTClaimsSet claims
    ) {


        Date expiration =
                claims.getExpirationTime();


        if (expiration == null
                || expiration.before(new Date())) {


            throw new IllegalArgumentException(
                    "Expired token"
            );
        }
    }
}
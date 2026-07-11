package com.example.demo.global.security;

import com.example.demo.domain.user.application.auth.SocialUserInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtFactory {


    private final JwtProperties jwtProperties;


    public String create(
            String subject,
            long expiration,
            String tokenType
    ) {

        Date now = new Date();


        return Jwts.builder()
                .setSubject(subject)
                .claim(
                        "type",
                        tokenType
                )
                .setIssuedAt(now)
                .setExpiration(
                        new Date(
                                now.getTime()
                                        + expiration
                        )
                )
                .signWith(
                        getSecretKey(),
                        SignatureAlgorithm.HS256
                )
                .compact();
    }



    public String createSignupToken(
            SocialUserInfo socialUserInfo
    ) {

        Date now = new Date();


        return Jwts.builder()
                .claim(
                        "type",
                        "SIGNUP"
                )
                .claim(
                        "provider",
                        socialUserInfo.provider().name()
                )
                .claim(
                        "providerId",
                        socialUserInfo.providerId()
                )
                .claim(
                        "name",
                        socialUserInfo.name()
                )
                .claim(
                        "socialEmail",
                        socialUserInfo.socialEmail()
                )
                .setIssuedAt(now)
                .setExpiration(
                        new Date(
                                now.getTime()
                                        + jwtProperties.getSignupExpiration()
                        )
                )
                .signWith(
                        getSecretKey(),
                        SignatureAlgorithm.HS256
                )
                .compact();
    }



    public Claims parse(
            String token
    ) {

        return Jwts.parserBuilder()
                .setSigningKey(
                        getSecretKey()
                )
                .build()
                .parseClaimsJws(token)
                .getBody();
    }



    private SecretKeySpec getSecretKey() {

        return new SecretKeySpec(
                jwtProperties.getSecret()
                        .getBytes(
                                StandardCharsets.UTF_8
                        ),
                SignatureAlgorithm.HS256
                        .getJcaName()
        );
    }
}
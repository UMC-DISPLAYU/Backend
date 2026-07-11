package com.example.demo.global.security;

import com.example.demo.domain.user.application.auth.SocialUserInfo;
import com.example.demo.domain.user.domain.entity.User;
import com.example.demo.domain.user.domain.enums.Provider;
import com.example.demo.domain.user.exception.AuthErrorCode;
import com.example.demo.global.error.BusinessException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class TokenProvider {


    private final JwtFactory jwtFactory;
    private final JwtProperties jwtProperties;



    public String createAccessToken(
            User user
    ) {

        return jwtFactory.create(
                user.getId().toString(),
                jwtProperties.getAccessExpiration(),
                "ACCESS"
        );
    }



    public String createRefreshToken(
            User user
    ) {

        return jwtFactory.create(
                user.getId().toString(),
                jwtProperties.getRefreshExpiration(),
                "REFRESH"
        );
    }



    public String createSignupToken(
            SocialUserInfo socialUserInfo
    ) {

        return jwtFactory.createSignupToken(
                socialUserInfo
        );
    }



    public SocialUserInfo parseSignupToken(
            String signupToken
    ) {

        try {

            Claims claims =
                    jwtFactory.parse(
                            signupToken
                    );


            validateTokenType(
                    claims,
                    "SIGNUP"
            );


            return new SocialUserInfo(
                    Provider.valueOf(
                            claims.get(
                                    "provider",
                                    String.class
                            )
                    ),
                    claims.get(
                            "providerId",
                            String.class
                    ),
                    claims.get(
                            "name",
                            String.class
                    ),
                    claims.get(
                            "socialEmail",
                            String.class
                    )
            );


        } catch (Exception e) {

            throw new BusinessException(
                    AuthErrorCode.INVALID_SIGNUP_TOKEN
            );
        }
    }



    public boolean validateToken(
            String token
    ) {

        try {

            jwtFactory.parse(
                    token
            );

            return true;


        } catch (Exception e) {

            return false;
        }
    }



    public boolean validateRefreshToken(
            String token
    ) {

        try {

            Claims claims =
                    jwtFactory.parse(
                            token
                    );


            return "REFRESH"
                    .equals(
                            claims.get(
                                    "type",
                                    String.class
                            )
                    );


        } catch (Exception e) {

            return false;
        }
    }



    public Long getUserId(
            String token
    ) {

        Claims claims =
                jwtFactory.parse(
                        token
                );


        return Long.parseLong(
                claims.getSubject()
        );
    }



    private void validateTokenType(
            Claims claims,
            String type
    ) {

        if (!type.equals(
                claims.get(
                        "type",
                        String.class
                )
        )) {

            throw new IllegalArgumentException(
                    "Invalid token type"
            );
        }
    }
}
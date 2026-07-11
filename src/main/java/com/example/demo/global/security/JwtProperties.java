package com.example.demo.global.security;

import lombok.Getter;
import lombok.Setter;
import com.example.demo.global.security.JwtProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret;

    private long accessExpiration;

    private long refreshExpiration;

    private long signupExpiration;
}
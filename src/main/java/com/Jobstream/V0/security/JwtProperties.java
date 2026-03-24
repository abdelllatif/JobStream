package com.Jobstream.V0.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "application.jwt")
@Getter
@Setter
public class JwtProperties {

    private String secret;
    private long expirationMs;
    private long refreshExpirationMs;
}

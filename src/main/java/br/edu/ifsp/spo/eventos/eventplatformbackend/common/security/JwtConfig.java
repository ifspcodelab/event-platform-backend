package br.edu.ifsp.spo.eventos.eventplatformbackend.common.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("jwt")
@Getter
@Setter
public class JwtConfig {
    private String issuer;
    private String secret;
    private Integer accessTokenExpiresIn;
    private Integer refreshTokenExpiresIn;
}

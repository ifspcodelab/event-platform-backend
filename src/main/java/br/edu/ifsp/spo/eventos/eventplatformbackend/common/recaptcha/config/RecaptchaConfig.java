package br.edu.ifsp.spo.eventos.eventplatformbackend.common.recaptcha.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("recaptcha")
@Getter
@Setter
public class RecaptchaConfig {
    private String secret;
    private String site;
}

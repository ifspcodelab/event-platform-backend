package br.edu.ifsp.spo.eventos.eventplatformbackend.common.recaptcha;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.recaptcha.config.RecaptchaConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
@Slf4j
public class RecaptchaService {
    private RestTemplate restTemplate;
    private RecaptchaConfig config;

    public boolean isValid(String recaptcha){
        return restTemplate.getForObject(buildRequestURL(recaptcha), RecaptchaResponseDto.class).isSuccess();
    }

    public String buildRequestURL(String recaptcha){
        return config.getSite() + "?secret=" + config.getSecret() + "&response=" + recaptcha;
    }
}

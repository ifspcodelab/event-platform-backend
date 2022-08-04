package br.edu.ifsp.spo.eventos.eventplatformbackend.common.recaptcha;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.recaptcha.config.RecaptchaConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
@Slf4j
public class RecaptchaService {
    private RestTemplate restTemplate;
    private RecaptchaConfig config;

    public boolean isValid(String recaptcha){
        try{
            return restTemplate.getForObject(buildRequestURL(recaptcha), RecaptchaResponseDto.class).isSuccess();
        } catch (RestClientException ex) {
            log.warn(ex.getMessage(), ex);
            return true;
        }
    }

    public String buildRequestURL(String recaptcha){
        return config.getSite() + "?secret=" + config.getSecret() + "&response=" + recaptcha;
    }
}

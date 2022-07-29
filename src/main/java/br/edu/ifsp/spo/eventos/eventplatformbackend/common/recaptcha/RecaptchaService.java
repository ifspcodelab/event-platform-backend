package br.edu.ifsp.spo.eventos.eventplatformbackend.common.recaptcha;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.recaptcha.config.RecaptchaConfig;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@AllArgsConstructor
public class RecaptchaService {

    private RestTemplate restTemplate;
    private RecaptchaConfig config;

    public boolean verifyRecaptcha(String recaptcha){
            String url = buildRequestURL(recaptcha);
            RecaptchaResponseDto response = restTemplate.getForObject(url, RecaptchaResponseDto.class);
        return response.isSuccess();
    }

    public String buildRequestURL(String recaptcha){
        String url = (
                config.getSite() +
                "?secret=" +
                config.getSecret() +
                "&response=" +
                recaptcha);
        return url;
    }
}

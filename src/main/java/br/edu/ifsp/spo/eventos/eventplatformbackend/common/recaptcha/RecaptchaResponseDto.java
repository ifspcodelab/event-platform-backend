package br.edu.ifsp.spo.eventos.eventplatformbackend.common.recaptcha;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RecaptchaResponseDto {

    private boolean success;
    private String hostname;

}

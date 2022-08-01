package br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions;

import lombok.Getter;

@Getter
public class RecaptchaException extends RuntimeException{

    private RecaptchaExceptionType recaptchaExceptionType;
    private String email;

    public RecaptchaException(RecaptchaExceptionType recaptchaExceptionType, String email){
        this.recaptchaExceptionType = recaptchaExceptionType;
        this.email = email;
    }
}

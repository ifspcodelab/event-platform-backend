package br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RecaptchaExceptionType {

    INVALID_RECAPTCHA("Recaptcha exception: recaptcha was not validated by the Google API for the email=%s");

    String message;
}

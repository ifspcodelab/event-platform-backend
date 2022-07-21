package br.edu.ifsp.spo.eventos.eventplatformbackend.account.authentication;

import lombok.Getter;

@Getter
public class LoginException extends RuntimeException {
    private LoginExceptionType loginExceptionType;
    private String email;

    public LoginException(LoginExceptionType loginExceptionType, String email) {
        this.loginExceptionType = loginExceptionType;
        this.email = email;
    }
}

package br.edu.ifsp.spo.eventos.eventplatformbackend.account.authentication;

import lombok.Getter;

@Getter
public class AuthenticationException extends RuntimeException {
    private AuthenticationExceptionType authenticationExceptionType;
    private String email;

    public AuthenticationException(AuthenticationExceptionType authenticationExceptionType, String email) {
        this.authenticationExceptionType = authenticationExceptionType;
        this.email = email;
    }
}

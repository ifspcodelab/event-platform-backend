package br.edu.ifsp.spo.eventos.eventplatformbackend.account.authentication;

import lombok.Getter;

@Getter
public class LogoutException extends RuntimeException {
    private String reason;

    public LogoutException(String reason) {
        this.reason = reason;
    }
}

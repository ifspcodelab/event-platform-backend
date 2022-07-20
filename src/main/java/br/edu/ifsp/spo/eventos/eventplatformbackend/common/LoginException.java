package br.edu.ifsp.spo.eventos.eventplatformbackend.common;

import lombok.Getter;

@Getter
public class LoginException extends RuntimeException {
    private String reason;

    public LoginException(String reason){
        super();
        this.reason = reason;
    }
}

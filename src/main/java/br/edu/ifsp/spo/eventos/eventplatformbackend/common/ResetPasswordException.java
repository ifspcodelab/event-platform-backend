package br.edu.ifsp.spo.eventos.eventplatformbackend.common;

import lombok.Getter;

@Getter
public class ResetPasswordException extends RuntimeException{

    private String reason;

    public ResetPasswordException(String reason){
        super();
        this.reason = reason;
    }

}

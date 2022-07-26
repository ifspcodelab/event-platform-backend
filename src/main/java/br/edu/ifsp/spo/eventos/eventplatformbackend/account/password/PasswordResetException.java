package br.edu.ifsp.spo.eventos.eventplatformbackend.account.password;

import lombok.Getter;

@Getter
public class PasswordResetException extends RuntimeException{

    private PasswordResetExceptionType passwordResetExceptionType;
    private String email;

    public PasswordResetException(PasswordResetExceptionType passwordResetExceptionType, String email) {
        this.passwordResetExceptionType = passwordResetExceptionType;
        this.email = email;
    }

    public PasswordResetException(PasswordResetExceptionType passwordResetExceptionType){
        this.passwordResetExceptionType = passwordResetExceptionType;
        this.email = "";
    }
}

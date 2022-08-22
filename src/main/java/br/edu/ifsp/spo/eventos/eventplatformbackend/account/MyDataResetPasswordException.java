package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import lombok.Getter;

@Getter
public class MyDataResetPasswordException extends RuntimeException {
    private MyDataResetPasswordExceptionType myDataResetPasswordExceptionType;
    private String email;

    public MyDataResetPasswordException(MyDataResetPasswordExceptionType myDataResetPasswordExceptionType, String email) {
        this.myDataResetPasswordExceptionType = myDataResetPasswordExceptionType;
        this.email = email;
    }
}

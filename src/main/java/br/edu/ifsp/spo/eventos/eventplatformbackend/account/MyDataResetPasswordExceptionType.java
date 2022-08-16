package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MyDataResetPasswordExceptionType {
    PASSWORD_CONFIRMATION_DOESNT_MATCH("My Data Reset Password Exception: at account with email=%s password confirmation does not match new password"),
    INCORRECT_PASSWORD("My Data Reset Password Exception: at account with email=%s given password does not match current password");

    String message;
}

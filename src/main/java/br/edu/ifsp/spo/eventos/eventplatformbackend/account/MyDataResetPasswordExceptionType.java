package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MyDataResetPasswordExceptionType {
    INCORRECT_PASSWORD("My Data Reset Password Exception: at account with email=%s given password does not match current password"),
    SAME_PASSWORD("My Data Reset Password Exception: at account with email=%s new password is the same as current password");

    String message;
}

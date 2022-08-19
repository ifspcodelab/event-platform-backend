package br.edu.ifsp.spo.eventos.eventplatformbackend.account.password;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PasswordResetExceptionType {
    NONEXISTENT_ACCOUNT("Password Reset Exception: the account for the email=%s not found"),
    UNVERIFIED_ACCOUNT("Password Reset Exception: the account for the email=%s is not yet verified"),
    OPEN_REQUEST("Password Reset Exception: account already have a open request for the email=%s"),
    RESET_TOKEN_NOT_FOUND("Password Reset Exception: reset token not found"),
    RESET_TOKEN_EXPIRED("Password Reset Exception: reset token expired for email=%s");


    String message;

}

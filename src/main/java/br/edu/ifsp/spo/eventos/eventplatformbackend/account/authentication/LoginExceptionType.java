package br.edu.ifsp.spo.eventos.eventplatformbackend.account.authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LoginExceptionType {
    UNVERIFIED_ACCOUNT("Login Exception: the account for the email=%s is not yet verified"),
    INCORRECT_PASSWORD("Login Exception: the entered password is incorrect for the email=%s"),
    NONEXISTENT_ACCOUNT("Login Exception: the account for the email=%s not found");
    String message;
}

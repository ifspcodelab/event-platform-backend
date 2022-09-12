package br.edu.ifsp.spo.eventos.eventplatformbackend.account.deletion;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AccountDeletionExceptionType {
    INCORRECT_PASSWORD("Account Deletion Exception: the password is invalid for email=%s");

    String message;
}

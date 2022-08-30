package br.edu.ifsp.spo.eventos.eventplatformbackend.account.deletion;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AccountDeletionExceptionType {
    INCORRECT_PASSWORD("Account Deletion Exception: the password is invalid for email=%s"),
    ACCOUNT_DELETION_TOKEN_EXPIRED("Account Deletion Exception: account deletion token expired for email=%s");

    String message;
}

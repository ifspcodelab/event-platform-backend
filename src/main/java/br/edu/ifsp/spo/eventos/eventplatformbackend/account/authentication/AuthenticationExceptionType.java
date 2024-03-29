package br.edu.ifsp.spo.eventos.eventplatformbackend.account.authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AuthenticationExceptionType {
    UNVERIFIED_ACCOUNT("Login Exception: the account for the email=%s is not yet verified"),
    BLOCKED_BY_ADMIN_ACCOUNT("Login Exception: the account for the email=%s is blocked by admin"),
    WAITING_FOR_EXCLUSION_ACCOUNT("Login Exception: the account for the email=%s is waiting for exclusion"),
    INCORRECT_PASSWORD("Login Exception: the entered password is incorrect for the email=%s"),
    NONEXISTENT_ACCOUNT_BY_EMAIL("Login Exception: the account for the email=%s not found"),
    NONEXISTENT_ACCOUNT_BY_ID("Login Exception: the account for the id=%s not found"),
    NONEXISTENT_TOKEN("Refresh Token Rotation Exception: the specified refresh token for the email=%s does not exist");
    String message;
}

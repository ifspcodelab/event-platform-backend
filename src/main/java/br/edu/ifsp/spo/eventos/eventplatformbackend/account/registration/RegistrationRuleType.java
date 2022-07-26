package br.edu.ifsp.spo.eventos.eventplatformbackend.account.registration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RegistrationRuleType {
    NONEXISTENT_TOKEN("Registration Exception: verification token not found"),
    VERIFICATION_TOKEN_EXPIRED("Registration Exception: verification token with email=%s is expired");

    String message;
}

package br.edu.ifsp.spo.eventos.eventplatformbackend.account.signup;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SignupRuleType {
    NONEXISTENT_TOKEN("Signup Exception: verification token not found"),
    VERIFICATION_TOKEN_EXPIRED("Signup Exception: verification token with email=%s is expired");

    String message;
}

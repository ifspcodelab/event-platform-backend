package br.edu.ifsp.spo.eventos.eventplatformbackend.account.signup;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SignupRuleType {
    NONEXISTENT_TOKEN("Signup Exception: verification token not found"),
    VERIFICATION_TOKEN_EXPIRED("Signup Exception: verification token with email=%s is expired"),
    SIGNUP_ACCOUNT_WITH_EXISTENT_EMAIL_NOT_VERIFIED("Signup Exception: account with email=%s already registered and cpf=%s remade sign up without been verified"),
    SIGNUP_ACCOUNT_WITH_EXISTENT_CPF_NOT_VERIFIED("Signup Exception: account with cpf=%s already registered and email=%s remade sign up without been verified");

    String message;
}

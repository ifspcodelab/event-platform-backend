package br.edu.ifsp.spo.eventos.eventplatformbackend.account.signup;

import lombok.Getter;

@Getter
public class SignupException extends RuntimeException{
    private final SignupRuleType signupRuleType;
    private String email;
    private String cpf;

    public SignupException(SignupRuleType signupRuleType, String email, String cpf) {
        super();
        this.signupRuleType = signupRuleType;
        this.email = email;
        this.cpf = cpf;

    }
    public SignupException(SignupRuleType signupRuleType, String email) {
        super();
        this.signupRuleType = signupRuleType;
        this.email = email;
    }

    public SignupException(SignupRuleType signupRuleType) {
        super();
        this.signupRuleType = signupRuleType;
    }
}

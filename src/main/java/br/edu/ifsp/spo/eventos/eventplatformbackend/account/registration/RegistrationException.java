package br.edu.ifsp.spo.eventos.eventplatformbackend.account.registration;

import lombok.Getter;

@Getter
public class RegistrationException extends RuntimeException{
    private RegistrationRuleType registrationRuleType;
    private String email;

    public RegistrationException(RegistrationRuleType registrationRuleType, String email) {
        super();
        this.registrationRuleType = registrationRuleType;
        this.email = email;
    }

    public RegistrationException(RegistrationRuleType registrationRuleType) {
        super();
        this.registrationRuleType = registrationRuleType;
    }
}

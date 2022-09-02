package br.edu.ifsp.spo.eventos.eventplatformbackend.session;


import lombok.Getter;

@Getter
public class SessionRuleException extends RuntimeException {
    private SessionRuleType ruleType;

    public SessionRuleException(SessionRuleType ruleType) {
        super();
        this.ruleType = ruleType;
    }
}
package br.edu.ifsp.spo.eventos.eventplatformbackend.common;

import lombok.Getter;

@Getter
public class BusinessRuleException extends RuntimeException {
    private BusinessRuleType businessRuleType;
    private String message;

    public BusinessRuleException(BusinessRuleType businessRuleType, String message) {
        super();
        this.businessRuleType = businessRuleType;
        this.message = message;
    }
}

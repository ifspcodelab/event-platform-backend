package br.edu.ifsp.spo.eventos.eventplatformbackend.common;

import lombok.Getter;

@Getter
public class BusinessRuleException extends RuntimeException {
    private BusinessRuleType businessRuleType;

    public BusinessRuleException(BusinessRuleType businessRuleType) {
        super();
        this.businessRuleType = businessRuleType;
    }
}

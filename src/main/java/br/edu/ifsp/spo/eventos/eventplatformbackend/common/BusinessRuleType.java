package br.edu.ifsp.spo.eventos.eventplatformbackend.common;

import lombok.Getter;

@Getter
public enum BusinessRuleType {
    SUBEVENT_BEFORE_EVENT("Subvent start date is before the start date of the event"),
    SUBEVENT_AFTER_EVENT("Subvent end date is after the end date of the event"),
    SUBEVENT_IS_NOT_ASSOCIATED_EVENT("Subvent is not associated to event");

    private String message;

    BusinessRuleType(String message) {
        this.message = message;
    }
}

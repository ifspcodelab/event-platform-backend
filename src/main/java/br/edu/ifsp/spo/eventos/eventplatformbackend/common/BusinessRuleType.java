package br.edu.ifsp.spo.eventos.eventplatformbackend.common;

import lombok.Getter;

@Getter
public enum BusinessRuleType {
    EVENT_REGISTRATION_END_AFTER_EVENT_EXECUTION_END("Event registration end date is after execution end date of the event"),
    EVENT_REGISTRATION_START_AFTER_EVENT_EXECUTION_START("Event registration start date is after execution start date of the event"),
    SUBEVENT_BEFORE_EVENT(""),
    SUBEVENT_AFTER_EVENT("");

    String message;

    BusinessRuleType(String message) {
        this.message = message;
    }
}

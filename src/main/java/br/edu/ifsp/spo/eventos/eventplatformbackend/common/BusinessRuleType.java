package br.edu.ifsp.spo.eventos.eventplatformbackend.common;

import lombok.Getter;

@Getter
public enum BusinessRuleType {
    EVENT_REGISTRATION_END_AFTER_EVENT_EXECUTION_END("Event registration end date is after execution end date of the event"),
    EVENT_REGISTRATION_START_AFTER_EVENT_EXECUTION_START("Event registration start date is after execution start date of the event"),
    EVENT_DELETE_WITH_STATUS_CANCELED("It is not possible to delete an event with canceled status"),
    EVENT_DELETE_IN_PERIOD_REGISTRATION_START("It is not possible to delete an event during the registration period"),
    EVENT_DELETE_WITH_SUBEVENTS("It is not possible to delete an event with associated sub-events"),
    SUBEVENT_BEFORE_EVENT(""),
    SUBEVENT_AFTER_EVENT("");

    String message;

    BusinessRuleType(String message) {
        this.message = message;
    }
}

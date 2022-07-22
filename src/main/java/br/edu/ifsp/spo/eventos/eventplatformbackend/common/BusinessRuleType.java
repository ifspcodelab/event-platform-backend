package br.edu.ifsp.spo.eventos.eventplatformbackend.common;

import lombok.Getter;

@Getter
public enum BusinessRuleType {
    EVENT_REGISTRATION_PERIOD_BEFORE_TODAY("Event registration period is before today"),
    EVENT_EXECUTION_PERIOD_BEFORE_TODAY("Event execution period is before today"),
    EVENT_REGISTRATION_END_AFTER_EVENT_EXECUTION_END("Event registration end date is after execution end date of the event"),
    EVENT_REGISTRATION_START_AFTER_EVENT_EXECUTION_START("Event registration start date is after execution start date of the event"),
    EVENT_DELETE_WITH_CANCELED_STATUS("It is not possible to delete an event with canceled status"),
    EVENT_DELETE_WITH_PUBLISHED_STATUS_IN_REGISTRATION_PERIOD("It is not possible to delete an event with published status during the registration period"),
    EVENT_DELETE_WITH_PUBLISHED_STATUS_AFTER_EXECUTION_PERIOD("It is not possible to delete an event with published status after execution period"),
    EVENT_DELETE_WITH_SUBEVENTS("It is not possible to delete an event with associated subevents"),
    EVENT_UPDATE_WITH_CANCELED_STATUS("It is not possible to update an event with canceled status"),
    EVENT_UPDATE_WITH_PUBLISHED_STATUS_AFTER_EXECUTION_PERIOD("It is not possible to update an event with published status after execution period");

    String message;

    BusinessRuleType(String message) {
        this.message = message;
    }
}

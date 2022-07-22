package br.edu.ifsp.spo.eventos.eventplatformbackend.common;

import lombok.Getter;

@Getter
public enum BusinessRuleType {
    SUBEVENT_EXECUTION_PERIOD_BEFORE_TODAY("Subevent execution period is before today"),
    SUBEVENT_BEFORE_EVENT("Subvent start date is before the start date of the event"),
    SUBEVENT_AFTER_EVENT("Subvent end date is after the end date of the event"),
    SUBEVENT_IS_NOT_ASSOCIATED_EVENT("Subvent is not associated to event"),
    SUBEVENT_WITH_PUBLISHED_STATUS_DELETE_IN_REGISTRATION_PERIOD("It is not possible to delete an subevent with published status during the registration period"),
    SUBEVENT_DELETE_WITH_STATUS_CANCELED("It is not possible to delete an Subevent with canceled status"),
    SUBEVENT_DELETE_WITH_PUBLISHED_STATUS_AFTER_EXECUTION_PERIOD("It is not possible to delete an subevent with published status after execution period"),
    SUBEVENT_UPDATE_WITH_CANCELED_STATUS("It is not possible to update an subevent with canceled status"),
    SUBEVENT_UPDATE_WITH_PUBLISHED_STATUS_AFTER_EXECUTION_PERIOD("It is not possible to update an subevent with published status after execution period");

    private String message;

    BusinessRuleType(String message) {
        this.message = message;
    }
}

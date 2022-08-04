package br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions;

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
    EVENT_UPDATE_WITH_PUBLISHED_STATUS_AFTER_EXECUTION_PERIOD("It is not possible to update an event with published status after execution period"),
    EVENT_CANCEL_WITH_DRAFT_STATUS("It is not possible to cancel an event with draft status"),
    EVENT_CANCEL_WITH_CANCELED_STATUS("It is not possible to cancel an event with canceled status"),
    EVENT_CANCEL_WITH_PUBLISHED_STATUS_AND_REGISTRATION_PERIOD_NOT_START("It is not possible to cancel an event with published status and the registration period not start"),
    EVENT_CANCEL_WITH_PUBLISHED_STATUS_AND_EXECUTION_PERIOD_END("It is not possible to cancel an event with published status and the execution period end"),
    EVENT_PUBLISH_WITH_DRAFT_STATUS_AND_REGISTRATION_PERIOD_START("It is not possible to publish an event with draft status and the registration period start"),
    EVENT_PUBLISH_WITH_PUBLISHED_STATUS("It is not possible to publish an event with published status"),
    EVENT_PUBLISH_WITH_CANCELED_STATUS("It is not possible to publish an event with canceled status"),
    EVENT_UNPUBLISH_WITH_DRAFT_STATUS("It is not possible to unpublish an event with draft status"),
    EVENT_UNPUBLISH_WITH_CANCELED_STATUS("It is not possible to unpublish an event with canceled status"),
    EVENT_UNPUBLISH_WITH_PUBLISHED_STATUS_AND_REGISTRATION_PERIOD_START("It is not possible to unpublish an event with published status and the registration period start"),
    SUBEVENT_EXECUTION_PERIOD_BEFORE_TODAY("Subevent execution period is before today"),
    SUBEVENT_BEFORE_EVENT("Subvent start date is before the start date of the event"),
    SUBEVENT_AFTER_EVENT("Subvent end date is after the end date of the event"),
    SUBEVENT_IS_NOT_ASSOCIATED_EVENT("Subvent is not associated to event"),
    SUBEVENT_WITH_PUBLISHED_STATUS_DELETE_IN_REGISTRATION_PERIOD("It is not possible to delete an subevent with published status during the registration period"),
    SUBEVENT_DELETE_WITH_STATUS_CANCELED("It is not possible to delete an Subevent with canceled status"),
    SUBEVENT_DELETE_WITH_PUBLISHED_STATUS_AFTER_EXECUTION_PERIOD("It is not possible to delete an subevent with published status after execution period"),
    SUBEVENT_UPDATE_WITH_CANCELED_STATUS("It is not possible to update an subevent with canceled status"),
    SUBEVENT_UPDATE_WITH_PUBLISHED_STATUS_AFTER_EXECUTION_PERIOD("It is not possible to update an subevent with published status after execution period"),
    SUBEVENT_CANCEL_WITH_DRAFT_STATUS("It is not possible to cancel an subevent with draft status"),
    SUBEVENT_CANCEL_WITH_CANCELED_STATUS("It is not possible to cancel an subevent with canceled status"),
    SUBEVENT_CANCEL_WITH_PUBLISHED_STATUS_AND_REGISTRATION_PERIOD_NOT_START("It is not possible to cancel an subevent with published status and the registration period not start"),
    SUBEVENT_CANCEL_WITH_PUBLISHED_STATUS_AND_EXECUTION_PERIOD_END("It is not possible to cancel an subevent with published status and the execution period end"),
    SUBEVENT_PUBLISH_WITH_PUBLISHED_STATUS("It is not possible to publish an subevent with published status"),
    SUBEVENT_PUBLISH_WITH_CANCELED_STATUS("It is not possible to publish an subevent with canceled status"),
    SUBEVENT_PUBLISH_WITH_DRAFT_STATUS_AND_REGISTRATION_PERIOD_START("It is not possible to publish an subevent with draft status and the registration period start"),
    SUBEVENT_UNPUBLISH_WITH_DRAFT_STATUS("It is not possible to unpublish an subevent with draft status"),
    SUBEVENT_UNPUBLISH_WITH_CANCELED_STATUS("It is not possible to unpublish an subevent with canceled status"),
    SUBEVENT_UNPUBLISH_WITH_PUBLISHED_STATUS_AND_REGISTRATION_PERIOD_START("It is not possible to unpublish an subevent with published status and the registration period start"),
    ACTIVITY_CREATE_WITH_EVENT_CANCELED_STATUS("It is not possible to create an activity with a canceled event"),
    ACTIVITY_CREATE_WITH_SUBEVENT_CANCELED_STATUS("It is not possible to create an activity with a canceled subevent"),
    ACTIVITY_UPDATE_WITH_EVENT_CANCELED_STATUS("It is not possible to update an activity with a canceled event"),
    ACTIVITY_UPDATE_WITH_SUBEVENT_CANCELED_STATUS("It is not possible to update an activity with a canceled subevent"),
    ACTIVITY_PUBLISH_WITH_EVENT_CANCELED_STATUS("It is not possible to publish an activity with a canceled event"),
    ACTIVITY_UNPUBLISH_WITH_EVENT_CANCELED_STATUS("It is not possible to unpublish an activity with a canceled event"),
    ACTIVITY_CANCEL_WITH_DRAFT_STATUS("It is not possible to cancel an activity with draft status"),
    ACTIVITY_CANCEL_WITH_CANCEL_STATUS("It is not possible to cancel an activity with cancel status"),
    ACTIVITY_IS_NOT_ASSOCIATED_EVENT("Activity is not associated to event"),
    ACTIVITY_IS_NOT_ASSOCIATED_SUBEVENT("Activity is not associated to subevent"),
    ACTIVITY_PUBLISH_WITH_PUBLISHED_STATUS("It is not possible to publish an activity with published status"),
    ACTIVITY_PUBLISH_WITH_CANCELED_STATUS("It is not possible to publish an activity with canceled status"),
    ACTIVITY_UPDATE_WITH_CANCELED_STATUS("It is not possible to update an activity with canceled status"),
    ACTIVITY_UNPUBLISH_WITH_CANCELED_STATUS("It is not possible to unpublish an activity with canceled status"),
    ACTIVITY_UNPUBLISH_WITH_DRAFT_STATUS("It is not possible to unpublish an activity with draft status"),
    ACTIVITY_WITH_PUBLISHED_STATUS_DELETE_IN_REGISTRATION_PERIOD("It is not possible to delete an activity with published status during the event registration period"),
    ACTIVITY_DELETE_WITH_PUBLISHED_STATUS_AFTER_EXECUTION_PERIOD("It is not possible to delete an activity with published status after event execution period"),
    ACTIVITY_DELETE_WITH_STATUS_CANCELED("It is not possible to delete an activity with canceled status");
    String message;

    BusinessRuleType(String message) {
        this.message = message;
    }
}

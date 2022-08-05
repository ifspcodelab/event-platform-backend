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
    ACTIVITY_IS_NOT_ASSOCIATED_EVENT("Atividade não está associada ao evento"),
    ACTIVITY_IS_NOT_ASSOCIATED_SUBEVENT("Atividade não está associada ao subevento"),
    ACTIVITY_CREATE_WITH_EVENT_CANCELED_STATUS("Não é possível criar uma atividade com um evento cancelado"),
    ACTIVITY_CREATE_WITH_EVENT_REGISTRATION_PERIOD_BEFORE_TODAY("Não é possível criar uma atividade em que o período de inscrição do evento é antes de hoje"),
    ACTIVITY_CREATE_WITH_SUBEVENT_CANCELED_STATUS("It is not possible to create an activity with a canceled subevent"),
    ACTIVITY_UPDATE_WITH_EVENT_CANCELED_STATUS("Não é possível criar uma atividade com um subevento cancelado"),
    ACTIVITY_UPDATE_WITH_SUBEVENT_CANCELED_STATUS("Não é possível atualizar uma atividade com um subevento cancelado"),
    ACTIVITY_UPDATE_WITH_CANCELED_STATUS("Não é possível atualizar uma atividade com status cancelado"),
    ACTIVITY_UPDATE_WITH_EVENT_REGISTRATION_PERIOD_BEFORE_TODAY("Não é possível atualizar uma atividade em que o período de inscrição do evento é antes de hoje"),
    ACTIVITY_DELETE_WITH_PUBLISHED_STATUS_AFTER_EXECUTION_PERIOD("Não é possível excluir uma atividade com status publicado após o período de execução do evento"),
    ACTIVITY_DELETE_WITH_PUBLISHED_STATUS_AFTER_EXECUTION_PERIOD__SUBEVENT("Não é possível excluir uma atividade com status publicado após o período de execução do evento"),
    ACTIVITY_DELETE_WITH_STATUS_CANCELED("Não é possível excluir uma atividade com status cancelado"),
    ACTIVITY_DELETE_WITH_EVENT_CANCELED_STATUS("Não é possível excluir uma atividade com um evento cancelado"),
    ACTIVITY_DELETE_WITH_SUBEVENT_CANCELED_STATUS("Não é possível excluir uma atividade com um subevento cancelado"),
    ACTIVITY_DELETE_WITH_PUBLISHED_STATUS_AFTER_REGISTRATION_PERIOD_START("Não é possível deletar uma atividade publicada em que o período de inscrições já tenha iniciado"),
    ACTIVITY_CANCEL_WITH_DRAFT_STATUS("Não é possível cancelar uma atividade com status de rascunho"),
    ACTIVITY_CANCEL_WITH_CANCELED_STATUS("Não é possível cancelar uma atividade com status cancelado"),
    ACTIVITY_CANCEL_WITH_EVENT_CANCELED_STATUS("Não é possível cancelar uma atividade com um evento cancelado"),
    ACTIVITY_CANCEL_WITH_EVENT_REGISTRATION_PERIOD_BEFORE_TODAY("Não é possível cancele uma atividade em que o período de inscrição do evento é antes de hoje"),
    ACTIVITY_CANCEL_WITH_EVENT_DRAFT_STATUS("Não é possível cancelar uma atividade com um evento em rascunho"),
    ACTIVITY_CANCEL_WITH_SUBEVENT_CANCELED_STATUS("Não é possível cancelar uma atividade com um subevento cancelado"),
    ACTIVITY_CANCEL_WITH_SUBEVENT_DRAFT_STATUS("Não é possível cancelar uma atividade com um subevento em rascunho"),
    ACTIVITY_PUBLISH_WITH_EVENT_CANCELED_STATUS("Não é possível publicar uma atividade com um evento cancelado"),
    ACTIVITY_PUBLISH_WITH_EVENT_REGISTRATION_PERIOD_BEFORE_TODAY("Não é possível publicar uma atividade em que o período de inscrição do evento é antes de hoje"),
    ACTIVITY_PUBLISH_WITH_EVENT_DRAFT_STATUS("Não é possível publicar uma atividade com um evento em rascunho"),
    ACTIVITY_PUBLISH_WITH_SUBEVENT_CANCELED_STATUS("Não é possível publicar uma atividade com um subevento cancelado"),
    ACTIVITY_PUBLISH_WITH_PUBLISHED_STATUS("Não é possível publicar uma atividade com status publicado"),
    ACTIVITY_PUBLISH_WITH_CANCELED_STATUS("Não é possível publicar uma atividade com status cancelado"),
    ACTIVITY_UNPUBLISH_WITH_PUBLISHED_STATUS_AND_REGISTRATION_PERIOD_START("Não é possível despublicar uma atividade publicada que o seu periodo de inscrições já tenha iniciado"),
    ACTIVITY_UNPUBLISH_WITH_EVENT_CANCELED_STATUS("Não é possível despublicar uma atividade com um evento cancelado"),
    ACTIVITY_UNPUBLISH_WITH_EVENT_REGISTRATION_PERIOD_BEFORE_TODAY("Não é possível despublicar uma atividade em que o período de inscrição do evento é antes de hoje"),
    ACTIVITY_UNPUBLISH_WITH_SUBEVENT_CANCELED_STATUS("Não é possível despublicar uma atividade com um subevento cancelado"),
    ACTIVITY_UNPUBLISH_WITH_CANCELED_STATUS("Não é possível despublicar uma atividade status com status cancelado"),
    ACTIVITY_UNPUBLISH_WITH_DRAFT_STATUS("Não é possível despublicar uma atividade com status de rascunho");
    String message;

    BusinessRuleType(String message) {
        this.message = message;
    }
}

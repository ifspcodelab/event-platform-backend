package br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions;

import lombok.Getter;

@Getter
public enum BusinessRuleType {
    EVENT_REGISTRATION_PERIOD_BEFORE_TODAY("O período de incrições está no passado"),
    EVENT_EXECUTION_PERIOD_BEFORE_TODAY("O período de execução está no passado"),
    EVENT_REGISTRATION_END_AFTER_EVENT_EXECUTION_END("A data de fim das incrições é posterior à data de fim de execução"),
    EVENT_REGISTRATION_START_AFTER_EVENT_EXECUTION_START("A data de início das incrições está posterior à data de início de execução"),
    EVENT_DELETE_WITH_CANCELED_STATUS("Não é possível excluir um evento cancelado"),
    EVENT_DELETE_WITH_PUBLISHED_STATUS_IN_REGISTRATION_PERIOD("Não é possível excluir um evento publicado durante o seu período de inscrições"),
    EVENT_DELETE_WITH_PUBLISHED_STATUS_AFTER_EXECUTION_PERIOD("Não é possível excluir um evento publicado que já tenha sido finalizado"),
    EVENT_DELETE_WITH_SUBEVENTS("Não é possível excluir um evento com subeventos associados"),
    EVENT_UPDATE_WITH_CANCELED_STATUS("Não é possivel editar um evento cancelado"),
    EVENT_UPDATE_WITH_PUBLISHED_STATUS_AFTER_EXECUTION_PERIOD("Não é possível editar um evento publicado que já tenha sido finalizado"),
    EVENT_CANCEL_WITH_DRAFT_STATUS("Não é possível cancelar um evento em rascunho"),
    EVENT_CANCEL_WITH_CANCELED_STATUS("Não é possível cancelar um evento que já está cancelado"),
    EVENT_CANCEL_WITH_PUBLISHED_STATUS_AND_REGISTRATION_PERIOD_NOT_START("Não é possível cancelar um evento publicado que o seu período de inscrições não tenha iniciado"),
    EVENT_CANCEL_WITH_PUBLISHED_STATUS_AND_EXECUTION_PERIOD_END("Não é possível cancelar um evento publicado que já tenha sido finalizado"),
    EVENT_PUBLISH_WITH_DRAFT_STATUS_AND_REGISTRATION_PERIOD_START("Não é possível publicar um evento em rascunho que o seu período de inscrições já tenha iniciado"),
    EVENT_PUBLISH_WITH_PUBLISHED_STATUS("Não é possível publicar um evento que já está publicado"),
    EVENT_PUBLISH_WITH_CANCELED_STATUS("Não é possível publicar um evento cancelado"),
    EVENT_UNPUBLISH_WITH_DRAFT_STATUS("Não é possível despublicar de um evento em rascunho"),
    EVENT_UNPUBLISH_WITH_CANCELED_STATUS("Não é possível despublicar de um evento cancelado"),
    EVENT_UNPUBLISH_WITH_PUBLISHED_STATUS_AND_REGISTRATION_PERIOD_START("Não é possível despublicar um evento publicado que o seu período de inscrições ja tenha iniciado"),
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
    SUBEVENT_UNPUBLISH_WITH_PUBLISHED_STATUS_AND_REGISTRATION_PERIOD_START("It is not possible to unpublish an subevent with published status and the registration period start");

    String message;

    BusinessRuleType(String message) {
        this.message = message;
    }
}

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
    EVENT_DELETE_WITH_PUBLISHED_STATUS_AFTER_EXECUTION_PERIOD("Não é possível excluir um evento publicado que tenha sido finalizado"),
    EVENT_DELETE_WITH_SUBEVENTS("Não é possível excluir um evento com subeventos associados"),
    EVENT_UPDATE_WITH_CANCELED_STATUS("Não é possivel editar um evento cancelado"),
    EVENT_UPDATE_WITH_PUBLISHED_STATUS_AFTER_EXECUTION_PERIOD("Não é possível editar um evento publicado que tenha sido finalizado"),
    EVENT_CANCEL_WITH_DRAFT_STATUS("Não é possível cancelar um evento em rascunho"),
    EVENT_CANCEL_WITH_CANCELED_STATUS("Não é possível cancelar um evento que já está cancelado"),
    EVENT_CANCEL_WITH_PUBLISHED_STATUS_AND_REGISTRATION_PERIOD_NOT_START("Não é possível cancelar um evento publicado que o seu período de inscrições não tenha iniciado"),
    EVENT_CANCEL_WITH_PUBLISHED_STATUS_AND_EXECUTION_PERIOD_END("Não é possível cancelar um evento publicado que tenha sido finalizado"),
    EVENT_PUBLISH_WITH_DRAFT_STATUS_AND_REGISTRATION_PERIOD_START("Não é possível publicar um evento em rascunho que o seu período de inscrições já tenha iniciado"),
    EVENT_PUBLISH_WITH_PUBLISHED_STATUS("Não é possível publicar um evento que já está publicado"),
    EVENT_PUBLISH_WITH_CANCELED_STATUS("Não é possível publicar um evento cancelado"),
    EVENT_UNPUBLISH_WITH_DRAFT_STATUS("Não é possível despublicar de um evento em rascunho"),
    EVENT_UNPUBLISH_WITH_CANCELED_STATUS("Não é possível despublicar de um evento cancelado"),
    EVENT_UNPUBLISH_WITH_PUBLISHED_STATUS_AND_REGISTRATION_PERIOD_START("Não é possível despublicar um evento publicado que o seu período de inscrições ja tenha iniciado"),
    SUBEVENT_EXECUTION_PERIOD_BEFORE_TODAY("O período de execução está no passado"),
    SUBEVENT_BEFORE_EVENT("Data de início do subevento é anterior à data de início do evento"),
    SUBEVENT_AFTER_EVENT("A data de fim do subevento é posterior à data de fim do evento"),
    SUBEVENT_IS_NOT_ASSOCIATED_EVENT("Subevento não está associado ao evento"),
    SUBEVENT_WITH_PUBLISHED_STATUS_DELETE_IN_REGISTRATION_PERIOD("Não é possível deletar um subevento publicado durante o período de inscrições"),
    SUBEVENT_DELETE_WITH_STATUS_CANCELED("Não é possível deletar um subevento cancelado"),
    SUBEVENT_DELETE_WITH_PUBLISHED_STATUS_AFTER_EXECUTION_PERIOD("Não é possível deletar um subevento publicado que tenha sido finalizado"),
    SUBEVENT_UPDATE_WITH_CANCELED_STATUS("Não é possível editar um subevento cancelado"),
    SUBEVENT_UPDATE_WITH_PUBLISHED_STATUS_AFTER_EXECUTION_PERIOD("Não é possível editar um subevento publicado que tenha sido finalizado"),
    SUBEVENT_CANCEL_WITH_DRAFT_STATUS("Não é possível cancelar um subevento em rascunho"),
    SUBEVENT_CANCEL_WITH_CANCELED_STATUS("Não é possível cancelar um subevento que já está cancelado"),
    SUBEVENT_CANCEL_WITH_PUBLISHED_STATUS_AND_REGISTRATION_PERIOD_NOT_START("Não é possível cancelar um subevento publicado que o seu período de inscrições não tenha iniciado"),
    SUBEVENT_CANCEL_WITH_PUBLISHED_STATUS_AND_EXECUTION_PERIOD_END("Não é possível cancelar um subevento publicado que tenha sido finalizado"),
    SUBEVENT_PUBLISH_WITH_PUBLISHED_STATUS("Não é possível publicar um subevento que já está publicado"),
    SUBEVENT_PUBLISH_WITH_CANCELED_STATUS("Não é possível publicar um subevento cancelado"),
    SUBEVENT_PUBLISH_WITH_DRAFT_STATUS_AND_REGISTRATION_PERIOD_START("Não é possível publicar um subevento em rascunho que o seu período de inscrições já tenha iniciado"),
    SUBEVENT_UNPUBLISH_WITH_DRAFT_STATUS("Não é possível despublicar um subevento em rascunho"),
    SUBEVENT_UNPUBLISH_WITH_CANCELED_STATUS("Não é possível despublicar um subevento cancelado "),
    SUBEVENT_UNPUBLISH_WITH_PUBLISHED_STATUS_AND_REGISTRATION_PERIOD_START("Não é possível despublicar um subevento publicado que o seu período de inscrições ja tenha iniciado"),
    ORGANIZER_CREATE_WITH_CANCELED_STATUS("It is not possible to add an organizer with canceled event status"),
    ORGANIZER_SUBEVENT_CREATE_WITH_CANCELED_STATUS("It is not possible to add an organizer with canceled subevent status");

    String message;

    BusinessRuleType(String message) {
        this.message = message;
    }
}

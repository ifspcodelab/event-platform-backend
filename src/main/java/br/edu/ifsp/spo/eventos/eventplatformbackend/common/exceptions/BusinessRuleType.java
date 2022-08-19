package br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions;

import lombok.Getter;

@Getter
public enum BusinessRuleType {
    EVENT_REGISTRATION_PERIOD_BEFORE_TODAY("O período de incrições está no passado"),
    EVENT_EXECUTION_PERIOD_BEFORE_TODAY("O período de execução está no passado"),
    EVENT_REGISTRATION_END_AFTER_EVENT_EXECUTION_END("A data de fim das incrições é posterior à data de fim de execução"),
    EVENT_REGISTRATION_START_AFTER_EVENT_EXECUTION_START("A data de início das incrições está posterior à data de início de execução"),
    EVENT_DELETE_WITH_CANCELED_STATUS("Não é possível excluir um evento cancelado"),
    EVENT_DELETE_WITH_PUBLISHED_STATUS_AFTER_REGISTRATION_PERIOD_START("Não é possível excluir um evento publicado que o seu período de inscrições já tenha iniciado"),
    EVENT_DELETE_WITH_SUBEVENTS("Não é possível excluir um evento com subeventos associados"),
    EVENT_UPDATE_WITH_CANCELED_STATUS("Não é possivel editar um evento cancelado"),
    EVENT_UPDATE_WITH_PUBLISHED_STATUS_AFTER_EXECUTION_PERIOD("Não é possível editar um evento publicado que tenha sido finalizado"),
    EVENT_UPDATE_WITH_PUBLISHED_STATUS_AND_MODIFIED_SLUG_AFTER_RERISTRATION_PERIOD_START("Não é possível editar O slug de um evento publicado que o seu período de inscrições já tenha iniciado"),
    EVENT_UPDATE_WITH_PUBLISHED_STATUS_AND_RERISTRATION_PERIOD_START_MODIFIED_AFTER_RERISTRATION_PERIOD_START("Não é possível editar o início do período de inscrições de um evento publicado que o seu período de inscrições já tenha iniciado"),
    EVENT_UPDATE_WITH_PUBLISHED_STATUS_AND_EXECUTION_PERIOD_START_MODIFIED_AFTER_RERISTRATION_PERIOD_START("Não é possível editar o início do período de execução de um evento publicado que o seu período de inscrições já tenha iniciado"),
    EVENT_CANCEL_WITH_DRAFT_STATUS("Não é possível cancelar um evento em rascunho"),
    EVENT_CANCEL_WITH_CANCELED_STATUS("Não é possível cancelar um evento que já está cancelado"),
    EVENT_CANCEL_WITH_PUBLISHED_STATUS_AND_REGISTRATION_PERIOD_NOT_START("Não é possível cancelar um evento publicado que o seu período de inscrições não tenha iniciado"),
    EVENT_CANCEL_WITH_PUBLISHED_STATUS_AND_EXECUTION_PERIOD_END("Não é possível cancelar um evento publicado que tenha sido finalizado"),
    EVENT_PUBLISH_WITH_DRAFT_STATUS_AND_REGISTRATION_PERIOD_END("Não é possível publicar um evento em rascunho que o seu período de inscrições já tenha finalizado"),
    EVENT_PUBLISH_WITH_PUBLISHED_STATUS("Não é possível publicar um evento que já está publicado"),
    EVENT_PUBLISH_WITH_CANCELED_STATUS("Não é possível publicar um evento cancelado"),
    EVENT_UNPUBLISH_WITH_DRAFT_STATUS("Não é possível despublicar de um evento em rascunho"),
    EVENT_UNPUBLISH_WITH_CANCELED_STATUS("Não é possível despublicar de um evento cancelado"),
    EVENT_UNPUBLISH_WITH_PUBLISHED_STATUS_AND_REGISTRATION_PERIOD_START("Não é possível despublicar um evento publicado que o seu período de inscrições ja tenha iniciado"),
    SUBEVENT_CREATE_WITH_EVENT_WITH_CANCELED_STATUS("Não é possível criar um subevento com seu evento cancelado"),
    SUBEVENT_EXECUTION_PERIOD_BEFORE_TODAY("O período de execução está no passado"),
    SUBEVENT_BEFORE_EVENT("Data de início do subevento é anterior à data de início do evento"),
    SUBEVENT_AFTER_EVENT("A data de fim do subevento é posterior à data de fim do evento"),
    SUBEVENT_IS_NOT_ASSOCIATED_EVENT("Subevento não está associado ao evento"),
    SUBEVENT_DELETE_WITH_PUBLISHED_STATUS_AFTER_REGISTRATION_PERIOD_START("Não é possível deletar um subevento publicado que o seu período de inscrições já tenha iniciado"),
    SUBEVENT_DELETE_WITH_STATUS_CANCELED("Não é possível deletar um subevento cancelado"),
    SUBEVENT_UPDATE_WITH_CANCELED_STATUS("Não é possível editar um subevento cancelado"),
    SUBEVENT_UPDATE_WITH_PUBLISHED_STATUS_AND_MODIFIED_SLUG_AFTER_RERISTRATION_PERIOD_START("Não é possível editar O slug de um subevento publicado que o seu período de inscrições já tenha iniciado"),
    SUBEVENT_UPDATE_WITH_PUBLISHED_STATUS_AND_EXECUTION_PERIOD_START_MODIFIED_AFTER_RERISTRATION_PERIOD_START("Não é possível editar o início do período de execução de um subevento publicado que o seu período de inscrições já tenha iniciado"),
    SUBEVENT_UPDATE_WITH_PUBLISHED_STATUS_AFTER_EXECUTION_PERIOD("Não é possível editar um subevento publicado que tenha sido finalizado"),
    SUBEVENT_CANCEL_WITH_DRAFT_STATUS("Não é possível cancelar um subevento em rascunho"),
    SUBEVENT_CANCEL_WITH_CANCELED_STATUS("Não é possível cancelar um subevento que já está cancelado"),
    SUBEVENT_CANCEL_WITH_PUBLISHED_STATUS_AND_REGISTRATION_PERIOD_NOT_START("Não é possível cancelar um subevento publicado em que o seu período de inscrições não tenha iniciado"),
    SUBEVENT_CANCEL_WITH_PUBLISHED_STATUS_AND_EXECUTION_PERIOD_END("Não é possível cancelar um subevento publicado que tenha sido finalizado"),
    SUBEVENT_PUBLISH_WITH_EVENT_WITH_DRAFT_STATUS("Não é possível publicar um subevento com seu evento em rascunho"),
    SUBEVENT_PUBLISH_WITH_EVENT_WITH_CANCELED_STATUS("Não é possível publicar um subevento com seu evento cancelado"),
    SUBEVENT_PUBLISH_WITH_PUBLISHED_STATUS("Não é possível publicar um subevento que já está publicado"),
    SUBEVENT_PUBLISH_WITH_CANCELED_STATUS("Não é possível publicar um subevento cancelado"),
    SUBEVENT_PUBLISH_WITH_DRAFT_STATUS_AND_REGISTRATION_PERIOD_END("Não é possível publicar um subevento em rascunho que o seu período de inscrições já tenha finalizado"),
    SUBEVENT_UNPUBLISH_WITH_DRAFT_STATUS("Não é possível despublicar um subevento em rascunho"),
    SUBEVENT_UNPUBLISH_WITH_CANCELED_STATUS("Não é possível despublicar um subevento cancelado "),
    SUBEVENT_UNPUBLISH_WITH_PUBLISHED_STATUS_AND_REGISTRATION_PERIOD_START("Não é possível despublicar um subevento publicado que o seu período de inscrições ja tenha iniciado"),
    ORGANIZER_CREATE_WITH_CANCELED_STATUS("Não é possível adicionar um organizador com o evento cancelado"),
    ORGANIZER_CREATE_WITH_ACCOUNT_UNVERIFIED("Não é possível adicionar um organizador associado a uma conta não verificada"),
    ORGANIZER_DELETE_WITH_CANCELED_STATUS("Não é possível deletar um organizador com o evento cancelado"),
    ORGANIZER_SUBEVENT_CREATE_WITH_CANCELED_STATUS("Não é possível adicionar um organizador com o subevento cancelado"),
    ORGANIZER_SUBEVENT_CREATE_WITH_ACCOUNT_UNVERIFIED("Não é possível associar um organizador com uma conta não verificada"),
    ORGANIZER_SUBEVENT_DELETE_WITH_CANCELED_STATUS("Não é possível deletar um organizador com o subevento cancelado"),
    ACTIVITY_IS_NOT_ASSOCIATED_TO_EVENT("Atividade não está associada ao evento"),
    ACTIVITY_IS_NOT_ASSOCIATED_TO_SUBEVENT("Atividade não está associada ao subevento"),
    ACTIVITY_CREATE_WITH_EVENT_CANCELED_STATUS("Não é possível criar uma atividade com um evento cancelado"),
    ACTIVITY_CREATE_WITH_EVENT_REGISTRATION_PERIOD_BEFORE_TODAY("Não é possível criar uma atividade em que o período de inscrição do evento é antes de hoje"),
    ACTIVITY_CREATE_WITH_SUBEVENT_CANCELED_STATUS("It is not possible to create an activity with a canceled subevent"),
    ACTIVITY_UPDATE_WITH_EVENT_CANCELED_STATUS("Não é possível criar uma atividade com um subevento cancelado"),
    ACTIVITY_UPDATE_WITH_SUBEVENT_CANCELED_STATUS("Não é possível atualizar uma atividade com um subevento cancelado"),
    ACTIVITY_UPDATE_WITH_CANCELED_STATUS("Não é possível atualizar uma atividade com status cancelado"),
    ACTIVITY_UPDATE_WITH_EVENT_REGISTRATION_PERIOD_BEFORE_TODAY("Não é possível atualizar uma atividade em que o período de inscrição do evento é antes de hoje"),
    ACTIVITY_UPDATE_WITH_SUBEVENT_EXECUTION_PERIOD_BEFORE_TODAY("Não é possível atualizar uma atividade em que o período de execução do subevento é antes de hoje"),
    ACTIVITY_DELETE_WITH_PUBLISHED_STATUS_AFTER_SUBEVENT_EXECUTION_PERIOD("Não é possível excluir uma atividade com status publicado após o período de execução do subevento"),
    ACTIVITY_DELETE_WITH_STATUS_CANCELED("Não é possível excluir uma atividade com status cancelado"),
    ACTIVITY_DELETE_WITH_EVENT_CANCELED_STATUS("Não é possível excluir uma atividade com um evento cancelado"),
    ACTIVITY_DELETE_WITH_SUBEVENT_CANCELED_STATUS("Não é possível excluir uma atividade com um subevento cancelado"),
    ACTIVITY_DELETE_WITH_PUBLISHED_STATUS_AFTER_REGISTRATION_PERIOD_START("Não é possível deletar uma atividade publicada em que o período de inscrições já tenha iniciado"),
    ACTIVITY_CANCEL_WITH_DRAFT_STATUS("Não é possível cancelar uma atividade com status de rascunho"),
    ACTIVITY_CANCEL_WITH_CANCELED_STATUS("Não é possível cancelar uma atividade com status cancelado"),
    ACTIVITY_CANCEL_AFTER_EVENT_EXECUTION_PERIOD("Não é possível cancelar uma atividade depois que o período de execução do evento foi finalizado"),
    ACTIVITY_CANCEL_WITH_SUBEVENT_AFTER_EXECUTION_PERIOD("Não é possível cancelar uma atividade depois que o período de execução do evento foi finalizado"),
    ACTIVITY_CANCEL_WITH_AN_EVENT_WITH_DRAFT_STATUS("Não é possível cancelar uma atividade com um evento em rascunho"),
    ACTIVITY_CANCEL_WITH_AN_EVENT_WITH_CANCELLED_STATUS("Não é possível cancelar uma atividade com um evento cancelado"),
    ACTIVITY_CANCEL_WITH_A_SUBEVENT_WITH_CANCELED_STATUS("Não é possível cancelar uma atividade com um subevento cancelado"),
    ACTIVITY_CANCEL_WITH_A_SUBEVENT_WITH_DRAFT_STATUS("Não é possível cancelar uma atividade com um subevento em rascunho"),
    ACTIVITY_PUBLISH_WITH_EVENT_CANCELED_STATUS("Não é possível publicar uma atividade com um evento cancelado"),
    ACTIVITY_PUBLISH_WITH_EVENT_REGISTRATION_PERIOD_BEFORE_TODAY("Não é possível publicar uma atividade em que o período de inscrição do evento é antes de hoje"),
    ACTIVITY_PUBLISH_WITH_SUBEVENT_CANCELED_STATUS("Não é possível publicar uma atividade com um subevento cancelado"),
    ACTIVITY_PUBLISH_WITH_SUBEVENT_EXECUTION_PERIOD_BEFORE_TODAY("Não é possível publicar uma atividade em que o período de execução é antes de hoje"),
    ACTIVITY_PUBLISH_WITH_PUBLISHED_STATUS("Não é possível publicar uma atividade com status publicado"),
    ACTIVITY_PUBLISH_WITH_CANCELED_STATUS("Não é possível publicar uma atividade com status cancelado"),
    ACTIVITY_UNPUBLISH_WITH_PUBLISHED_STATUS_AND_REGISTRATION_PERIOD_START("Não é possível despublicar uma atividade publicada que o seu periodo de inscrições já tenha iniciado"),
    ACTIVITY_UNPUBLISH_WITH_EVENT_CANCELED_STATUS("Não é possível despublicar uma atividade com um evento cancelado"),
    ACTIVITY_UNPUBLISH_WITH_CANCELED_STATUS("Não é possível despublicar uma atividade status com status cancelado"),
    ACTIVITY_UNPUBLISH_WITH_SUBEVENT_EXECUTION_PERIOD_BEFORE_TODAY("Não é possível despublicar uma atividade em que o período de execução do subevento já tenha iniciado"),
    ACTIVITY_UNPUBLISH_WITH_DRAFT_STATUS("Não é possível despublicar uma atividade com status de rascunho"),
    SESSION_DELETE_WITH_STATUS_CANCELED("Não é possível excluir uma sessão com status cancelado"),
    SESSION_DELETE_WITH_ACTIVITY_STATUS_CANCELED("Não é possível excluir uma sessão com uma atividade cancelada"),
    SESSION_DELETE_WITH_ACTIVITY_PUBLISHED_STATUS_AND_AFTER_EVENT_REGISTRATION_PERIOD_START("Não é possível deletar uma sessão em uma atividade publicada e que o período de inscrições já tenha iniciado"),
    SESSION_DELETE_WITH_ACTIVITY_PUBLISHED_STATUS_AFTER_SUBEVENT_EXECUTION_PERIOD("Não é possível excluir uma sessão em uma atividade com status publicado e após o período de execução do subevento"),
    SESSION_CANCEL_WITH_CANCELED_STATUS("Não é possível cancelar uma sessão com status cancelado"),
    SESSION_CANCEL_WITH_AN_ACTIVITY_WITH_DRAFT_STATUS("Não é possível cancelar uma sessão em uma atividade com status de rascunho"),
    SESSION_CANCEL_WITH_AN_ACTIVITY_WITH_CANCELED_STATUS("Não é possível cancelar uma sesão em uma atividade com status cancelado"),
    SESSION_CANCEL_AFTER_EVENT_EXECUTION_PERIOD("Não é possível cancelar uma sessão depois que o período de execução do evento foi finalizado"),
    SESSION_CANCEL_WITH_AN_EVENT_WITH_DRAFT_STATUS("Não é possível cancelar uma sessão com um evento em rascunho"),
    SESSION_CANCEL_WITH_A_SUBEVENT_WITH_DRAFT_STATUS("Não é possível cancelar uma sessão com um subevento em rascunho"),
    SESSION_CANCEL_WITH_A_SUBEVENT_WITH_CANCELED_STATUS("Não é possível cancelar uma sessão com um subevento cancelado"),
    SESSION_CANCEL_WITH_ACTIVITY_PUBLISHED_STATUS_AFTER_SUBEVENT_EXECUTION_PERIOD("Não é possível cancelar uma sessão em uma atividade com status publicado e após o período de execução do subevento"),
    SESSION_CREATE_WITH_ACTIVITY_CANCELED_STATUS("Não é possível criar uma sessão com uma atividade cancelada"),
    SESSION_UPDATE_WITH_ACTIVITY_CANCELED_STATUS("Não é possível editar uma sessão com uma atividade cancelada"),
    SESSION_CREATE_WITH_EVENT_REGISTRATION_PERIOD_BEFORE_TODAY("Não é possível criar uma sessão em que o período de inscrição do evento é antes de hoje"),
    SESSION_SCHEDULES_EXECUTION_PERIOD_BEFORE_TODAY("O período de execução de um ou mais horários está no passado"),
    SESSION_SCHEDULE_EXECUTION_BEFORE_EVENT("Data de início do horário de uma ou mais sessão é anterior à data de início do evento"),
    SESSION_SCHEDULE_EXECUTION_AFTER_EVENT("A data de fim da sessão é posterior à data de fim do evento"),
    SESSION_SCHEDULE_EXECUTION_BEFORE_SUBEVENT_EXECUTATION("Data de início do horário da sessão é anterior à data de início do subevento"),
    SESSION_SCHEDULE_EXECUTION_AFTER_SUBEVENT_EXECUTATION("A data de fim da sessão é posterior à data de fim do subevento"),
    SESSION_CREATE_WITH_SUBEVENT_EXECUTION_PERIOD_BEFORE_TODAY("Não é possível criar uma sessão em que o período de execução do subevento é antes de hoje"),
    SESSION_SCHEDULE_EXECUTION_START_IS_AFTER_EXECUTION_END("A data de início de execução de um ou mais horários de sessão é depois da data de finalização"),
    SESSION_UPDATE_WITH_CANCELED_STATUS("Não é possível editar uma sessão cancelada"),
    SESSION_UPDATE_WITH_ACTIVITY_PUBLISHED_STATUS_AFTER_SUBEVENT_EXECUTION_PERIOD("Não é possível atualizar uma sessão em uma atividade com status publicado e após o período de execução do subevento"),
    SESSION_UPDATE_WITH_ACTIVITY_PUBLISHED_STATUS_AFTER_EVENT_EXECUTION_PERIOD("Não é possível atualizar uma sessão em uma atividade com status publicado e após o período de execução do evento"),
    SESSION_CREATE_WITH_AN_ACTIVITY_WITH_CANCELED_STATUS("Não é possível criar uma sessão em uma atividade cancelada"),
    SESSION_SCHEDULE_EXECUTION_START_IS_EQUALS_TO__EXECUTION_END("Não é possível criar horário(s) de sessão em que a data de início de execução é igual a data de finalização"),
    ADD_AREA_OR_SPACE_IN_A_NULL_LOCATION("Não é possível colocar uma área ou um espaço em uma localização vazia"),
    ADD_SPACE_IN_A_NULL_AREA("Não é possível colocar um espaço em uma área vazia");

    //TODO Melhorar as mensagens

    String message;

    BusinessRuleType(String message) {
        this.message = message;
    }
}

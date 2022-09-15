package br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions;

import lombok.Getter;

@Getter
public enum BusinessRuleType {
    MAIL_SERVER_PROBLEM("Problema com o envio do email, tente novamente mais tarde"),
    RESEND_EMAIL_DELAY("Não passou um minuto do envio do email"),
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
    ORGANIZER_CREATE_ALREADY_ASSOCIATED("Não é possível adicionar um organizador já associado ao evento"),
    ORGANIZER_CREATE_WITH_ACCOUNT_UNVERIFIED("Não é possível adicionar um organizador associado a uma conta não verificada"),
    ORGANIZER_DELETE_WITH_CANCELED_STATUS("Não é possível deletar um organizador com o evento cancelado"),
    ORGANIZER_SUBEVENT_CREATE_WITH_CANCELED_STATUS("Não é possível adicionar um organizador com o subevento cancelado"),
    ORGANIZER_SUBEVENT_CREATE_ALREADY_ASSOCIATED("Não é possível adicionar um organizador já associado ao subevento"),
    ORGANIZER_SUBEVENT_CREATE_WITH_ACCOUNT_UNVERIFIED("Não é possível associar um organizador com uma conta não verificada"),
    ORGANIZER_SUBEVENT_DELETE_WITH_CANCELED_STATUS("Não é possível deletar um organizador com o subevento cancelado"),
    ACTIVITY_IS_NOT_ASSOCIATED_TO_EVENT("Atividade não está associada ao evento"),
    ACTIVITY_IS_NOT_ASSOCIATED_TO_SUBEVENT("Atividade não está associada ao subevento"),
    ACTIVITY_CREATE_WITH_EVENT_CANCELED_STATUS("Não é possível criar uma atividade com um evento cancelado"),
    ACTIVITY_CREATE_WITH_EVENT_REGISTRATION_PERIOD_BEFORE_TODAY("Não é possível criar uma atividade em que o período de inscrição do evento é antes de hoje"),
    ACTIVITY_CREATE_WITH_EVENT_EXECUTION_PERIOD_BEFORE_TODAY("Não é possível criar uma atividade em um evento finalizado"),
    ACTIVITY_CREATE_WITH_SUBEVENT_CANCELED_STATUS("It is not possible to create an activity with a canceled subevent"),
    ACTIVITY_UPDATE_WITH_EVENT_CANCELED_STATUS("Não é possível criar uma atividade com um subevento cancelado"),
    ACTIVITY_UPDATE_WITH_SUBEVENT_CANCELED_STATUS("Não é possível atualizar uma atividade com um subevento cancelado"),
    ACTIVITY_UPDATE_WITH_CANCELED_STATUS("Não é possível atualizar uma atividade com status cancelado"),
    ACTIVITY_UPDATE_WITH_EVENT_REGISTRATION_PERIOD_BEFORE_TODAY("Não é possível atualizar uma atividade em que o período de inscrição do evento é antes de hoje"),
    ACTIVITY_UPDATE_WITH_SUBEVENT_EXECUTION_PERIOD_BEFORE_TODAY("Não é possível atualizar uma atividade em que o período de execução do subevento é antes de hoje"),
    ACTIVITY_UPDATE_WITH_EVENT_PUBLISHED_STATUS_AND_MODIFIED_SLUG_AFTER_RERISTRATION_PERIOD_START("Não é possível editar o slug de uma atividade em um evento publicado em que o seu período de inscrições já tenha iniciado"),
    ACTIVITY_DELETE_WITH_PUBLISHED_STATUS_AFTER_SUBEVENT_EXECUTION_PERIOD("Não é possível excluir uma atividade com status publicado após o período de execução do subevento"),
    ACTIVITY_DELETE_WITH_STATUS_CANCELED("Não é possível excluir uma atividade com status cancelado"),
    ACTIVITY_DELETE_WITH_EVENT_CANCELED_STATUS("Não é possível excluir uma atividade com um evento cancelado"),
    ACTIVITY_DELETE_WITH_SUBEVENT_CANCELED_STATUS("Não é possível excluir uma atividade com um subevento cancelado"),
    ACTIVITY_DELETE_WITH_PUBLISHED_STATUS_AFTER_REGISTRATION_PERIOD_START("Não é possível deletar uma atividade publicada em que o período de inscrições já tenha iniciado"),
    ACTIVITY_CANCEL_WITH_DRAFT_STATUS("Não é possível cancelar uma atividade com status de rascunho"),
    ACTIVITY_CANCEL_WITH_PUBLISHED_STATUS_AND_REGISTRATION_PERIOD_DOESNT_START("Não é possível cancelar uma atividade publicada antes do inicio do período de inscrições"),
    ACTIVITY_CANCEL_WITH_CANCELED_STATUS("Não é possível cancelar uma atividade com status cancelado"),
    ACTIVITY_CANCEL_AFTER_EVENT_EXECUTION_PERIOD("Não é possível cancelar uma atividade depois que o período de execução do evento foi finalizado"),
    ACTIVITY_CANCEL_WITH_SUBEVENT_AFTER_EXECUTION_PERIOD("Não é possível cancelar uma atividade depois que o período de execução do evento foi finalizado"),
    ACTIVITY_CANCEL_WITH_AN_EVENT_WITH_DRAFT_STATUS("Não é possível cancelar uma atividade com um evento em rascunho"),
    ACTIVITY_CANCEL_WITH_AN_EVENT_WITH_CANCELLED_STATUS("Não é possível cancelar uma atividade com um evento cancelado"),
    ACTIVITY_CANCEL_WITH_A_SUBEVENT_WITH_CANCELED_STATUS("Não é possível cancelar uma atividade com um subevento cancelado"),
    ACTIVITY_CANCEL_WITH_A_SUBEVENT_WITH_DRAFT_STATUS("Não é possível cancelar uma atividade com um subevento em rascunho"),
    ACTIVITY_PUBLISH_WITH_EVENT_CANCELED_STATUS("Não é possível publicar uma atividade com um evento cancelado"),
    ACTIVITY_PUBLISH_WITH_EVENT_REGISTRATION_PERIOD_BEFORE_TODAY("Não é possível publicar uma atividade em que o período de inscrição do evento é antes de hoje"),
    ACTIVITY_PUBLISH_WITH_EVENT_DRAFT_STATUS("Não é possível publicar uma atividade com um evento em rascunho"),
    ACTIVITY_PUBLISH_WITH_SUBEVENT_CANCELED_STATUS("Não é possível publicar uma atividade com um subevento cancelado"),
    ACTIVITY_PUBLISH_WITH_SUBEVENT_EXECUTION_PERIOD_BEFORE_TODAY("Não é possível publicar uma atividade em que o período de execução é antes de hoje"),
    ACTIVITY_PUBLISH_WITH_PUBLISHED_STATUS("Não é possível publicar uma atividade com status publicado"),
    ACTIVITY_PUBLISH_WITH_CANCELED_STATUS("Não é possível publicar uma atividade com status cancelado"),
    ACTIVITY_UNPUBLISH_WITH_PUBLISHED_STATUS_AND_REGISTRATION_PERIOD_START("Não é possível despublicar uma atividade publicada quando o período de inscrições já tenha iniciado"),
    ACTIVITY_UNPUBLISH_WITH_EVENT_CANCELED_STATUS("Não é possível despublicar uma atividade com um evento cancelado"),
    ACTIVITY_UNPUBLISH_WITH_CANCELED_STATUS("Não é possível despublicar uma atividade com status cancelado"),
    ACTIVITY_UNPUBLISH_WITH_SUBEVENT_EXECUTION_PERIOD_BEFORE_TODAY("Não é possível despublicar uma atividade em que o período de execução do subevento já tenha iniciado"),
    ACTIVITY_UNPUBLISH_WITH_DRAFT_STATUS("Não é possível despublicar uma atividade com status de rascunho"),
    SPEAKER_ADD_WITH_EVENT_CANCELED_STATUS("Não é possível adicionar um ministrante em um evento cancelado"),
    SPEAKER_ADD_WITH_SUBEVENT_CANCELED_STATUS("Não é possível adicionar um ministrante em um subevento cancelado"),
    SPEAKER_ADD_WITH_ACTIVITY_CANCELED_STATUS("Não é possível adicionar um ministrante em uma atividade cancelada"),
    SPEAKER_ADD_ALREADY_EXISTS("O ministrante já está associado a atividade"),
    SESSION_IS_NOT_ASSOCIATED_TO_ACTIVITY("Sessão não está associada a atividade"),
    SESSION_IS_NOT_ASSOCIATED_TO_SESSION_SCHEDULE("Horário de sessão não está associada a sessão"),
    SESSION_DELETE_WITH_STATUS_CANCELED("Não é possível excluir uma sessão com status cancelado"),
    SESSION_DELETE_WITH_ACTIVITY_STATUS_CANCELED("Não é possível excluir uma sessão com uma atividade cancelada"),
    SESSION_DELETE_WITH_ACTIVITY_PUBLISHED_STATUS_AND_AFTER_REGISTRATION_PERIOD_START("Não é possível deletar uma sessão em uma atividade publicada e que o período de inscrições já tenha iniciado"),
    SESSION_DELETE_WITH_ACTIVITY_PUBLISHED_STATUS_AFTER_SUBEVENT_EXECUTION_PERIOD("Não é possível excluir uma sessão em uma atividade com status publicado e após o período de execução do subevento"),
    SESSION_CANCEL_WITH_CANCELED_STATUS("Não é possível cancelar uma sessão com status cancelado"),
    SESSION_CANCEL_WITH_AN_ACTIVITY_WITH_DRAFT_STATUS("Não é possível cancelar uma sessão em uma atividade com status de rascunho"),
    SESSION_CANCEL_WITH_AN_ACTIVITY_WITH_CANCELED_STATUS("Não é possível cancelar uma sesão em uma atividade com status cancelado"),
    SESSION_CANCEL_AFTER_EVENT_EXECUTION_PERIOD("Não é possível cancelar uma sessão depois que o período de execução do evento foi finalizado"),
    SESSION_CANCEL_WITH_AN_EVENT_WITH_DRAFT_STATUS("Não é possível cancelar uma sessão com um evento em rascunho"),
    SESSION_CANCEL_WITH_A_SUBEVENT_WITH_DRAFT_STATUS("Não é possível cancelar uma sessão com um subevento em rascunho"),
    SESSION_CANCEL_WITH_A_SUBEVENT_WITH_CANCELED_STATUS("Não é possível cancelar uma sessão com um subevento cancelado"),
    SESSION_CANCEL_WITH_ACTIVITY_PUBLISHED_STATUS_AFTER_SUBEVENT_EXECUTION_PERIOD("Não é possível cancelar uma sessão em uma atividade com status publicado e após o período de execução do subevento"),
    REGISTRATION_IS_NOT_ASSOCIATED_TO_SESSION("Inscrição não está associada à sessão"),
    REGISTRATION_IS_NOT_ASSOCIATED_TO_ACCOUNT("Inscrição não está associada à conta"),
    REGISTRATION_IS_NOT_ASSOCIATED_TO_EVENT("Inscrição não está associada ao evento"),
    REGISTRATION_CREATE_ALREADY_EXISTS("Não é possível se inscrever em uma mesma sessão"),
    REGISTRATION_CREATE_WITH_ACTIVITY_DOES_NOT_NEED_REGISTRATION("Não é possível se inscrever em uma sessão em uma que não precisa de inscrições"),
    REGISTRATION_CREATE_WITH_CANCELED_SESSION("Não é possível se inscrever em uma sessão cancelada"),
    REGISTRATION_CREATE_WITH_ACTIVITY_CANCELED("Não é se possível se inscrever em uma sessão com atividade cancelada"),
    REGISTRATION_CREATE_WITH_EVENT_OUT_OF_REGISTRATION_PERIOD("Não é possível se inscrever fora do período de inscrições do evento"),
    REGISTRATION_CREATE_HAS_SCHEDULE_CONFLICT("Não é possível se inscrever, pois já existe uma inscrição confirmada ou esperando confirmação para outra sessão neste mesmo horário"),
    REGISTRATION_CREATE_WITH_NO_SEATS_AVAILABLE("Não é possível se inscrever em uma sessão sem vagas abertas"),
    REGISTRATION_CREATE_IN_WAIT_LIST_WITH_SEATS_VAILABLE("Não é possível criar uma inscrição na lista de espera em uma sessão que ainda possui vagas abertas"),
    REGISTRATION_CREATE_WITH_EXISTING_WAIT_LIST("Não é possível criar uma inscrição confirmada, pois há pessoas na lista de espera"),
    REGISTRATION_CREATE_WITH_SESSION_STARTED("Não é possível criar uma inscrição em uma sessão que já tenha iniciado"),
    REGISTRATION_CREATE_ACCOUNT_ALREADY_HAS_REGISTRATION_IN_ACTIVITY("Não é possível se inscrever em mais de uma sessão da mesma atividade"),
    REGISTRATION_ACCEPT_WITH_EXPIRED_HOURS("Não é mais possível aceitar a vaga, pois já passou do prazo especificado no e-mail"),
    REGISTRATION_DENY_WITH_EXPIRED_HOURS("Não é mais possível recusar a vaga, pois já passou do prazo especificado no e-mail"),
    REGISTRATION_ALREADY_WAS_ANSWERED("Não é possível aceitar ou negar mais de uma vez uma vaga liberada"),
    ATTENDANCE_CREATE_WITH_REGISTRATION_STATUS_NOT_CONFIRMED("Não é possível adicionar presença em uma inscrição não confirmada"),
    ATTENDANCE_CREATE_WITH_SESSION_SCHEDULE_NOT_STARTED("Não é possível adicionar presença duas horas ou mais antes do período de início da sessão"),
    ATTENDANCE_CREATE_AFTER_EVENT_EXECUTION_PERIOD("Não é possível adicionar presença depois de dois dias ou mais que o evento tenha finalizado"),
    ATTENDANCE_CREATE_AFTER_SUBEVENT_EXECUTION_PERIOD("Não é possível adicionar presença depois de dois dias ou mais que o subevento tenha finalizado"),
    ATTENDANCE_CREATE_WITH_CANCELED_SESSION("Não é possível adicionar presença em uma sessão cancelada"),
    ATTENDANCE_CREATE_WITH_ACTIVITY_CANCELED("Não é possível adicionar presença em uma sessão com atividade cancelada"),
    ATTENDANCE_ALREADY_EXISTS("Já foi adicionado presença para essa inscrição"),
    ATTENDANCE_DELETE_AFTER_EVENT_EXECUTION_END("Não é possível retirar uma presença após dois dias ou mais do período de execução do evento"),
    ATTENDANCE_DELETE_AFTER_SUBEVENT_EXECUTION_END("Não é possível retirar uma presença após dois dias ou mais do período de execução do subevento");

    String message;

    BusinessRuleType(String message) {
        this.message = message;
    }
}

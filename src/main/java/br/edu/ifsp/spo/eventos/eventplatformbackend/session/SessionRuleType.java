package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import lombok.Getter;

@Getter
public enum SessionRuleType {
    CANCELED_EVENT("Não é possível criar, excluir ou cancelar uma sessão em um evento cancelado"),
    CANCELED_SUBEVENT("Não é possível criar, excluir ou cancelar uma sessão em um subevento cancelado"),
    CANCELED_ACTIVITY("Não é possível criar, excluir ou cancelar uma sessão em uma atividade cancelada"),
    CANCELED_SESSION("Não é possível cancelar ou excluir uma sessão que já está cancelada"),
    SEATS_NOT_DEFINED("É necessário definir o número de vagas pois a atividade requer inscrição"),
    REGISTRATION_PERIOD_STARTED("Não é possível deletar uma sessão em um evento com o período de inscrição iniciado"),
    REGISTRATION_PERIOD_ENDED("Não é possível criar uma sessão em um evento com o período de inscrição finalizado"),
    EXECUTION_PERIOD_ENDED("Não é possível criar uma sessão em um evento com o período de execução finalizado"),
    SESSION_DURATION("A duração dos horários não corresponde a soma da duração e do tempo de credenciamento da atividade"),
    SCHEDULE_INVALID_PERIOD("A data e hora de início de um horário deve ser maior que a data e hora de fim"),
    SCHEDULE_IN_PAST("O período de execução de um ou mais horários da sessão está no passado"),
    SCHEDULE_HAS_INTERSECTIONS("Um ou mais horários possuem possuem intersecções de horários"),
    OUTSIDE_EXECUTION_PERIOD("Os horários devem estar dentro do período de execução do evento ou subevento"),
    URL_OR_LOCATION_NOT_DEFINED("É necessário definir pelo menos uma url e um local pois a atividade é hibrida"),
    URL_NOT_DEFINED("É necessário definir uma url em todos os horários pois a atividade é online"),
    AREA_OR_SPACE_NULL_LOCATION("Não é possível criar um ou mais horário da sessão adicionando uma área ou um espaço em um local vazio"),
    SPACE_NULL_AREA("Não é possível criar um ou mais horário da sessão adicionando um espaço em uma área vazia"),
    LOCATION_NOT_DEFINED("É necessário definir pelo menos o local nos horários pois a atividade é presencial");

    String message;

    SessionRuleType(String message) {
        this.message = message;
    }
}

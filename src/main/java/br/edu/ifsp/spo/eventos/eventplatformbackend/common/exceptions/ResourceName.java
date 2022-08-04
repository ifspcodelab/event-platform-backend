package br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResourceName {
    LOCATION("Location"),
    AREA("Area"),
    SPACE("Space"),
    EVENT("Event"),
    SUBEVENT("Subevent"),
    ACCOUNT("Account"),
    EMAIL("E-mail"),
    CPF("Cpf"),
    SPEAKER("Speaker");

    private String name;
}

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
    SPEAKER("Speaker"),
    ORGANIZER("Organizer"),
    ORGANIZERSUBEVENT("Organizer Subevent"),
    EMAIL("E-mail"),
    CPF("CPF"),
    ACTIVITY("Activity");

    private String name;
}

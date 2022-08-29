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
    ACTIVITY("Activity"),
    SESSION("Session"),
    PASSWORD_RESET_TOKEN("Password reset token"),
    REFRESH_TOKEN("Refresh token");

    private String name;
}

package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer_authorization;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OrganizerAuthorizationExceptionType {
    UNAUTHORIZED_EVENT("Organizer Authorization Exception: the account of email=%s does not have access to the event of id=%s");
    String message;
}

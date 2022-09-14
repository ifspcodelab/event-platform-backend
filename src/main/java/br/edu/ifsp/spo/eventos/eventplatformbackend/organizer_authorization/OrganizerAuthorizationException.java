package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer_authorization;

import lombok.Getter;

import java.util.UUID;

@Getter
public class OrganizerAuthorizationException extends RuntimeException {
    private final String username;
    private final OrganizerAuthorizationExceptionType organizerAuthorizationExceptionType;
    private final UUID resourceId;

    public OrganizerAuthorizationException(
            OrganizerAuthorizationExceptionType organizerAuthorizationExceptionType,
            String username,
            UUID resourceId) {
        this.organizerAuthorizationExceptionType = organizerAuthorizationExceptionType;
        this.username = username;
        this.resourceId = resourceId;
    }
}

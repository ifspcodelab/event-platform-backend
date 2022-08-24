package br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final ResourceName resourceName;
    private final String query;

    public UserNotFoundException(ResourceName resourceName, String query) {
        super();
        this.resourceName = resourceName;
        this.query = query;
    }
}

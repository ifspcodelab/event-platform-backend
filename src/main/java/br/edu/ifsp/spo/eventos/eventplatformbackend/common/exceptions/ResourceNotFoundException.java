package br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final ResourceName resourceName;
    private final UUID resourceId;

    public ResourceNotFoundException(ResourceName resourceName, UUID resourceId) {
        super();
        this.resourceName = resourceName;
        this.resourceId = resourceId;
    }
}

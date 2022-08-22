package br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final ResourceName resourceName;
    private final String resourceId;

    public ResourceNotFoundException(ResourceName resourceName, UUID resourceId) {
        super();
        this.resourceName = resourceName;
        this.resourceId = resourceId.toString();
    }

    public ResourceNotFoundException(ResourceName resourceName, String resourceValue) {
        super();
        this.resourceName = resourceName;
        this.resourceId = resourceValue;
    }
}

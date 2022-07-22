package br.edu.ifsp.spo.eventos.eventplatformbackend.common;

import lombok.Getter;
import java.util.UUID;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private String resourceName;
    private String message;

    public ResourceNotFoundException(String resourceName, UUID resourceId) {
        super();
        this.resourceName = resourceName;
        this.message = "Resource not found with id "+ resourceId;
    }
}

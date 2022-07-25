package br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ResourceAlreadyExistsException extends RuntimeException {
    private ResourceName resourceName;
    private String resourceAttribute;
    private String resourceAttributeValue;

    public ResourceAlreadyExistsException(ResourceName resourceName, String resourceAttribute, String resourceAttributeValue) {
        super();
        this.resourceName = resourceName;
        this.resourceAttribute = resourceAttribute;
        this.resourceAttributeValue = resourceAttributeValue;
    }
}

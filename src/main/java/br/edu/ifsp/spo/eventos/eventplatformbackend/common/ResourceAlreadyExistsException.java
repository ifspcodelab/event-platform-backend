package br.edu.ifsp.spo.eventos.eventplatformbackend.common;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ResourceAlreadyExistsException extends RuntimeException {
    private String resourceName;
    private String resourceAttribute;
    private String resourceAttributeValue;
    private String message;

    public ResourceAlreadyExistsException(String resourceName, String resourceAttribute, String resourceAttributeValue) {
        super();
        this.resourceName = resourceName;
        this.resourceAttribute = resourceAttribute;
        this.resourceAttributeValue = resourceAttributeValue;
        this.message = String.format(
                "Resource %s already exists with %s %s",
                resourceName,
                resourceAttribute,
                resourceAttributeValue
        );
    }
}

package br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions;

import lombok.Getter;

@Getter
public class ResourceNotExistsAssociationException extends RuntimeException {
    private final ResourceName primary;
    private final ResourceName related;

    public ResourceNotExistsAssociationException(ResourceName primary, ResourceName related) {
        super();
        this.primary = primary;
        this.related = related;
    }
}

package br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions;

import lombok.Getter;

@Getter
public class ResourceReferentialIntegrityException extends RuntimeException {
    private final ResourceName primary;
    private final ResourceName related;

    public ResourceReferentialIntegrityException(ResourceName primary, ResourceName related) {
        super();
        this.primary = primary;
        this.related = related;
    }
}

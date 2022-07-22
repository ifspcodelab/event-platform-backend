package br.edu.ifsp.spo.eventos.eventplatformbackend.common;

import lombok.Getter;

@Getter
public class ResourceReferentialIntegrityException extends RuntimeException {
    private final ResourceName location;
    private final ResourceName area;

    public ResourceReferentialIntegrityException(ResourceName location, ResourceName area) {
        super();
        this.location = location;
        this.area = area;
    }
}

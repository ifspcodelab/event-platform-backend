package br.edu.ifsp.spo.eventos.eventplatformbackend.common;

import lombok.Getter;

@Getter
public class ResourceReferentialIntegrityException extends RuntimeException {
    private final ResourceName area;
    private final ResourceName space;

    public ResourceReferentialIntegrityException(ResourceName area, ResourceName space) {
        super();
        this.area = area;
        this.space = space;
    }
}

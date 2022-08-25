package br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ResourceAlreadyReservedInTheSpaceException extends RuntimeException {
    private final LocalDateTime startSchedule;
    private final LocalDateTime endSchedule;
    private final String space;

    public ResourceAlreadyReservedInTheSpaceException(LocalDateTime startSchedule, LocalDateTime endSchedule, String space) {
        super();
        this.startSchedule = startSchedule;
        this.endSchedule = endSchedule;
        this.space = space;
    }
}
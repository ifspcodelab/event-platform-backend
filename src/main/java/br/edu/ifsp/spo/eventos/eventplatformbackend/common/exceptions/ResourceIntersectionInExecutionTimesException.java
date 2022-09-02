package br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ResourceIntersectionInExecutionTimesException extends RuntimeException {
    private final LocalDateTime startScheduleOuter;
    private final LocalDateTime endScheduleOuter;
    private final LocalDateTime startScheduleInner;
    private final LocalDateTime endScheduleInner;
    private final String space;

    public ResourceIntersectionInExecutionTimesException(LocalDateTime startScheduleOuter, LocalDateTime endScheduleOuter, LocalDateTime startScheduleInner, LocalDateTime endScheduleInner, String space) {
        super();
        this.startScheduleOuter = startScheduleOuter;
        this.endScheduleOuter = endScheduleOuter;
        this.startScheduleInner = startScheduleInner;
        this.endScheduleInner = endScheduleInner;
        this.space = space;
    }
}
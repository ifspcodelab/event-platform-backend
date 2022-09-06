package br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions;

import br.edu.ifsp.spo.eventos.eventplatformbackend.session.SessionSchedule;
import lombok.Getter;

@Getter
public class ResourceAlreadyReservedInTheSpaceException extends RuntimeException {
    private final SessionSchedule sessionSchedule;

    public ResourceAlreadyReservedInTheSpaceException(SessionSchedule sessionSchedule) {
        super();
        this.sessionSchedule = sessionSchedule;
    }
}
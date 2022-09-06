package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import lombok.Value;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class SessionScheduleCreateDto {
    @NotNull
    LocalDateTime executionStart;
    @NotNull
    LocalDateTime executionEnd;
    @URL
    String url;
    UUID locationId;
    UUID areaId;
    UUID spaceId;

    public boolean hasIntersection(SessionScheduleCreateDto sessionSchedule) {
        LocalDateTime start1 = this.executionStart;
        LocalDateTime end1 = this.executionEnd;
        LocalDateTime start2 = sessionSchedule.getExecutionStart();
        LocalDateTime end2 = sessionSchedule.getExecutionEnd();

        var condition1 = start1.isBefore(start2) || start1.isEqual(start2);
        var condition2 = end1.isAfter(start2);
        var condition3 = start2.isBefore(start1) || start2.isEqual(start1);
        var condition4 = end2.isAfter(start1);

        return (condition1 && condition2) || (condition3 && condition4);
    }
}

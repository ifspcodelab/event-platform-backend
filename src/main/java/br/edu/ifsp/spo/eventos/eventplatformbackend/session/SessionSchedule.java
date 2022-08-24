package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import br.edu.ifsp.spo.eventos.eventplatformbackend.area.Area;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.Period;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.BusinessRuleException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.BusinessRuleType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.Location;
import br.edu.ifsp.spo.eventos.eventplatformbackend.space.Space;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sessions_schedules")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SessionSchedule {
    @Id
    private UUID id;
    private LocalDateTime executionStart;
    private LocalDateTime executionEnd;
    private String url;
    @ManyToOne
    private Location location;
    @ManyToOne
    private Area area;
    @ManyToOne
    private Space space;

    public SessionSchedule(LocalDateTime executionStart, LocalDateTime executionEnd, String url, Location location, Area area, Space space) {
        this.id = UUID.randomUUID();
        this.executionStart = executionStart;
        this.executionEnd = executionEnd;
        this.url = url;
        this.location = location;
        this.area = area;
        this.space = space;
    }

    //      s------e
    //             s--------e


    private boolean datesOverlaps(LocalDateTime dateFrom1, LocalDateTime dateTo1, LocalDateTime dateFrom2, LocalDateTime dateTo2) {
        return (dateFrom2.isAfter(dateFrom1) || dateFrom2.equals(dateFrom1)) &&
                (dateFrom2.isBefore(dateTo1) || dateFrom2.equals(dateTo1)) ||
                (dateTo2.isAfter(dateFrom1) || dateTo2.equals(dateFrom1)) &&
                        (dateTo2.isBefore(dateTo1) || dateTo2.equals(dateTo1));
    }

    public boolean hasIntersection(SessionSchedule sessionSchedule) {
        return datesOverlaps(this.executionStart, this.executionEnd, sessionSchedule.getExecutionStart(), sessionSchedule.getExecutionEnd());
    }

    public boolean isInsidePeriod(Period period) {
        return  executionStart.toLocalDate().isEqual(period.getStartDate()) || executionStart.toLocalDate().isAfter(period.getStartDate())
                && executionEnd.toLocalDate().isEqual(period.getEndDate()) || executionEnd.toLocalDate().isBefore(period.getEndDate());
    }
}

package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import br.edu.ifsp.spo.eventos.eventplatformbackend.area.Area;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.Period;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.Location;
import br.edu.ifsp.spo.eventos.eventplatformbackend.space.Space;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
    @ManyToOne(optional = false)
    private Session session;

    public SessionSchedule(LocalDateTime executionStart, LocalDateTime executionEnd, String url, Location location, Area area, Space space) {
        this.id = UUID.randomUUID();
        this.executionStart = executionStart;
        this.executionEnd = executionEnd;
        this.url = url;
        this.location = location;
        this.area = area;
        this.space = space;
    }

    public boolean hasIntersection(SessionSchedule sessionSchedule) {
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

    public boolean isInsidePeriod(Period period) {
        return  executionStart.toLocalDate().isEqual(period.getStartDate()) || executionStart.toLocalDate().isAfter(period.getStartDate())
                && executionEnd.toLocalDate().isEqual(period.getEndDate()) || executionEnd.toLocalDate().isBefore(period.getEndDate());
    }

    public String toLog() {
        return "SessionSchedule{" +
            "id=" + id +
            ", executionStart=" + executionStart +
            '}';
    }
}

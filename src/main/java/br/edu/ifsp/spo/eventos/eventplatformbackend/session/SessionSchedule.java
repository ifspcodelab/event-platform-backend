package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import br.edu.ifsp.spo.eventos.eventplatformbackend.area.Area;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.Location;
import br.edu.ifsp.spo.eventos.eventplatformbackend.space.Space;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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
    private LocalDateTime execution_start;
    private LocalDateTime execution_end;
    private String url;
    @ManyToOne
    private Location location;
    @ManyToOne
    private Area area;
    @ManyToOne
    private Space space;

    public SessionSchedule(LocalDateTime execution_start, LocalDateTime execution_end, String url, Location location, Area area, Space space) {
        this.id = UUID.randomUUID();
        this.execution_start = execution_start;
        this.execution_end = execution_end;
        this.url = url;
        this.location = location;
        this.area = area;
        this.space = space;
    }

    public boolean hasConflict(SessionSchedule sessionSchedule) {
        if(this.execution_start.isEqual(sessionSchedule.execution_start) ||
            this.execution_end.isEqual(sessionSchedule.execution_end)
        ) {
            return true;
        }

        if(this.execution_start.isBefore(sessionSchedule.execution_start) &&
            this.execution_end.isAfter(sessionSchedule.execution_start)
        ) {
            return true;
        }

        if(this.execution_start.isBefore(sessionSchedule.execution_end) &&
            this.execution_end.isAfter(sessionSchedule.execution_end)
        ) {
            return true;
        }

        if(this.execution_start.isBefore(sessionSchedule.execution_start) &&
            this.execution_end.isAfter(sessionSchedule.execution_end)
        ) {
            return true;
        }

        if(this.execution_start.isAfter(sessionSchedule.execution_start) &&
            this.execution_end.isBefore(sessionSchedule.execution_end)
        ) {
            return true;
        }



        return false;
    }
}

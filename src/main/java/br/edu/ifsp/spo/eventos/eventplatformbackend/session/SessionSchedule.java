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
}

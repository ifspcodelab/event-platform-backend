package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.Activity;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.Area;
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
    private LocalDateTime start;
    private LocalDateTime end;
    private String url;
    @ManyToOne
    private Location locationId;
    @ManyToOne
    private Area areaId;
    @ManyToOne
    private Space spaceId;
    @ManyToOne
    private Activity activity;


    public SessionSchedule(LocalDateTime start, LocalDateTime end, String url, Location locationId, Area areaId, Space spaceId) {
        this.id = UUID.randomUUID();
        this.start = start;
        this.end = end;
        this.url = url;
        this.locationId = locationId;
        this.areaId = areaId;
        this.spaceId = spaceId;
    }
}

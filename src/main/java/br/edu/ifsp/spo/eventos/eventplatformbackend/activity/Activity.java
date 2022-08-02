package br.edu.ifsp.spo.eventos.eventplatformbackend.activity;

import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.Subevent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "activities")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Activity {
    @Id
    private UUID id;
    private String title;
    private String slug;
    private String description;
    @Enumerated(EnumType.STRING)
    private ActivityType type;
    @Enumerated(EnumType.STRING)
    private EventStatus status;
    private boolean isOnline;
    private boolean needRegistration;
    @ManyToOne
    private Event event;
    @ManyToOne
    private Subevent subevent;

    public Activity(
            String title,
            String slug,
            String description,
            ActivityType type,
            boolean isOnline,
            boolean needRegistration,
            Event event,
            Subevent subevent
    ) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.type = type;
        this.status = EventStatus.DRAFT;
        this.isOnline = isOnline;
        this.needRegistration = needRegistration;
        this.event = event;
        this.subevent = subevent;
    }
}

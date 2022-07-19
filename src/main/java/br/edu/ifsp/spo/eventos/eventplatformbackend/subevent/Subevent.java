package br.edu.ifsp.spo.eventos.eventplatformbackend.subevent;

import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "subevents")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Subevent {
    @Id
    private UUID id;
    private String title;
    private String slug;
    private String summary;
    private String presentation;
    private LocalDate startDate;
    private LocalDate endDate;
    private String smallerImage;
    private String biggerImage;
    @Enumerated(EnumType.STRING)
    private EventStatus status;
    @ManyToOne
    private Event event;

    public Subevent(
        String title,
        String slug,
        String summary,
        String presentation,
        LocalDate startDate,
        LocalDate endDate,
        String smallerImage,
        String biggerImage,
        Event event
    ) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.slug = slug;
        this.summary = summary;
        this.presentation = presentation;
        this.startDate = startDate;
        this.endDate = endDate;
        this.smallerImage = smallerImage;
        this.biggerImage = biggerImage;
        this.status = EventStatus.DRAFT;
        this.event = event;
    }
}

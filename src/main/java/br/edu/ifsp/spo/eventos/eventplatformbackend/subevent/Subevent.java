package br.edu.ifsp.spo.eventos.eventplatformbackend.subevent;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.Period;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus;
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
    private String contact;
    private String cancellationMessage;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "startDate", column = @Column(name = "start_date")),
            @AttributeOverride(name = "endDate", column = @Column(name = "end_date"))
    })
    private Period executionPeriod;
    private String smallerImage;
    private String biggerImage;
    @ManyToOne
    private Event event;
    @Enumerated(EnumType.STRING)
    private EventStatus status;

    public Subevent(
        String title,
        String slug,
        String summary,
        String presentation,
        String contact,
        Period executionPeriod,
        String smallerImage,
        String biggerImage,
        Event event
    ) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.slug = slug;
        this.summary = summary;
        this.presentation = presentation;
        this.contact = contact;
        this.executionPeriod = executionPeriod;
        this.smallerImage = smallerImage;
        this.biggerImage = biggerImage;
        this.status = EventStatus.DRAFT;
        this.event = event;
        this.cancellationMessage = null;
    }

    public boolean isExecutionPeriodEnded() {
        return this.getExecutionPeriod().getEndDate().isBefore(LocalDate.now());
    }

    public boolean isCanceled() {
        return this.getStatus().equals(EventStatus.CANCELED);
    }
}

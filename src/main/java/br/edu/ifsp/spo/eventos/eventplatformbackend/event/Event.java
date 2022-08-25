package br.edu.ifsp.spo.eventos.eventplatformbackend.event;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.Period;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "events")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Event {
    @Id
    private UUID id;
    private String title;
    private String slug;
    private String summary;
    private String presentation;
    private String contact;
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "startDate", column = @Column(name = "registration_start_date")),
        @AttributeOverride(name = "endDate", column = @Column(name = "registration_end_date"))
    })
    private Period registrationPeriod;
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "startDate", column = @Column(name = "start_date")),
        @AttributeOverride(name = "endDate", column = @Column(name = "end_date"))
    })
    private Period executionPeriod;
    private String smallerImage;
    private String biggerImage;
    @Enumerated(EnumType.STRING)
    private EventStatus status;
    private String cancellationMessage;

    public Event(
        String title,
        String slug,
        String summary,
        String presentation,
        String contact,
        Period registrationPeriod,
        Period executionPeriod,
        String smallerImage,
        String biggerImage
    ) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.slug = slug;
        this.summary = summary;
        this.presentation = presentation;
        this.contact = contact;
        this.registrationPeriod = registrationPeriod;
        this.executionPeriod = executionPeriod;
        this.smallerImage = smallerImage;
        this.biggerImage = biggerImage;
        this.status = EventStatus.DRAFT;
        this.cancellationMessage = null;
    }

    public boolean isRegistrationPeriodEnded() {
        return this.registrationPeriod.ended();
    }

    public boolean isRegistrationPeriodStarted() {
        return this.registrationPeriod.started();
    }

    public boolean isRegistrationPeriodNotStart(){
        return !this.registrationPeriod.started();
    }

    public boolean isExecutionPeriodEnded() {
        return this.executionPeriod.ended();
    }
}

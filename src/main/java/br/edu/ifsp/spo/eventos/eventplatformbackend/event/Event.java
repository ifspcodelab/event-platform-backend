package br.edu.ifsp.spo.eventos.eventplatformbackend.event;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.Period;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.Diffable;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "events")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Event implements Diffable<Event> {
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

    @Override
    public DiffResult<Event> diff(Event updatedEvent) {
        return new DiffBuilder<>(this, updatedEvent, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("Título", this.title, updatedEvent.title)
                .append("Slug", this.slug, updatedEvent.slug)
                .append("Resumo", this.summary, updatedEvent.summary)
                .append("Apresentação", this.presentation, updatedEvent.presentation)
                .append("Contato", this.contact, updatedEvent.contact)
                .append("Período de inscrições", this.registrationPeriod, updatedEvent.registrationPeriod)
                .append("Período de execução", this.executionPeriod, updatedEvent.executionPeriod)
                .append("Capa menor", this.smallerImage, updatedEvent.smallerImage)
                .append("Capa maior", this.biggerImage, updatedEvent.biggerImage)
                .append("Status", this.status, updatedEvent.status)
                .append("Motivo do cancelamento", this.cancellationMessage, updatedEvent.cancellationMessage)
                .build();
    }
}

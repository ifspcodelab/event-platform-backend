package br.edu.ifsp.spo.eventos.eventplatformbackend.activity;

import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.Subevent;
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
@Table(name = "activities")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Activity implements Diffable<Activity> {
    @Id
    private UUID id;
    private String title;
    private String slug;
    private String description;
    @Enumerated(EnumType.STRING)
    private ActivityType type;
    @Enumerated(EnumType.STRING)
    private EventStatus status;
    @Enumerated(EnumType.STRING)
    private ActivityModality modality;
    private boolean needRegistration;
    private Integer duration;
    private Integer setupTime;
    private String cancellationMessage;
    @ManyToOne
    private Event event;
    @ManyToOne
    private Subevent subevent;

    public Activity(
        String title,
        String slug,
        String description,
        ActivityType type,
        ActivityModality modality,
        boolean needRegistration,
        Integer duration,
        Integer setupTime,
        Event event
    ) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.type = type;
        this.status = EventStatus.DRAFT;
        this.modality = modality;
        this.needRegistration = needRegistration;
        this.duration = duration;
        this.setupTime = setupTime;
        this.cancellationMessage = null;
        this.event = event;
    }

    public Activity(
        String title,
        String slug,
        String description,
        ActivityType type,
        ActivityModality modality,
        boolean needRegistration,
        Integer duration,
        Integer setupTime,
        Event event,
        Subevent subevent
    ) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.type = type;
        this.status = EventStatus.DRAFT;
        this.modality = modality;
        this.needRegistration = needRegistration;
        this.duration = duration;
        this.setupTime = setupTime;
        this.cancellationMessage = null;
        this.event = event;
        this.subevent = subevent;
    }

    public boolean isEventCanceled() {
        return this.getEvent().getStatus().equals(EventStatus.CANCELED);
    }

    public boolean isEventDraft() {
        return this.getEvent().getStatus().equals(EventStatus.DRAFT);
    }

    public boolean isCanceled() {
        return this.getStatus().equals(EventStatus.CANCELED);
    }

    public boolean isDraft() {
        return this.getStatus().equals(EventStatus.DRAFT);
    }

    public boolean isPublished() {
        return this.getStatus().equals(EventStatus.PUBLISHED);
    }

    @Override
    public DiffResult<Activity> diff(Activity updatedActivity) {
        return new DiffBuilder<>(this, updatedActivity, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("Título", this.title, updatedActivity.title)
                .append("Slug", this.slug, updatedActivity.slug)
                .append("Descrição", this.description, updatedActivity.description)
                .append("Tipo", this.type, updatedActivity.type)
                .append("Modalidade", this.modality, updatedActivity.modality)
                .append("Requer inscrição", this.needRegistration, updatedActivity.needRegistration)
                .append("Duração", this.duration, updatedActivity.duration)
                .append("Tempo de credenciamento", this.setupTime, updatedActivity.setupTime)
                .build();
    }
}

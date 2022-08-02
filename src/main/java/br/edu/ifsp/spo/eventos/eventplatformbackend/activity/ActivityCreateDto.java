package br.edu.ifsp.spo.eventos.eventplatformbackend.activity;

import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.Subevent;
import lombok.Value;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Value
public class ActivityCreateDto {
    @NotNull
    @NotBlank
    @Size(min = 5, max = 100)
    String title;
    @NotNull
    @NotBlank
    String slug;
    @NotNull
    @NotBlank
    @Size(min = 100, max = 500)
    String description;
    @Valid
    @NotNull
    ActivityType activityType;
    @Valid
    @NotNull
    EventStatus eventStatus;
    @Valid
    @NotNull
    Event event;
    @Valid
    Subevent subevent;
    @NotNull
    @NotBlank
    boolean isOnline;
    @NotNull
    @NotBlank
    boolean needRegistration;

}

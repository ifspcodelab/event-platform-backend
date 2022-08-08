package br.edu.ifsp.spo.eventos.eventplatformbackend.activity;

import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.Subevent;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ActivityDto {
    UUID id;
    String title;
    String slug;
    String description;
    ActivityType type;
    EventStatus status;
    boolean online;
    boolean needRegistration;
    String cancellationMessage;
    Event event;
    Subevent subevent;
}

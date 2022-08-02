package br.edu.ifsp.spo.eventos.eventplatformbackend.activity;

import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.Subevent;

import java.util.UUID;

public class ActivityDto {
    UUID id;
    String title;
    String slug;
    String description;
    ActivityType type;
    EventStatus eventStatus;
    Event event;
    Subevent subevent;
    boolean isOnline;
    boolean needRegistration;
}

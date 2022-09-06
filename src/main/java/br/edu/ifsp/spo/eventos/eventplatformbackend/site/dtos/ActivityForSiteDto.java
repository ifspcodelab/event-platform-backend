package br.edu.ifsp.spo.eventos.eventplatformbackend.site.dtos;

import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.ActivityModality;
import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.ActivityType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus;

import java.util.Set;
import java.util.UUID;

public record ActivityForSiteDto(
    UUID id,
    String title,
    String slug,
    String description,
    ActivityType type,
    EventStatus status,
    ActivityModality modality,
    boolean needRegistration,
    Integer setupTime,
    Integer duration,
    Set<String> speakers,
    Set<SessionForSiteDto> sessions
) {}
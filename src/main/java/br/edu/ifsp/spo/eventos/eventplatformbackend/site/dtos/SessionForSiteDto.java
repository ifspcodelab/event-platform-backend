package br.edu.ifsp.spo.eventos.eventplatformbackend.site.dtos;

import java.util.Set;
import java.util.UUID;

public record SessionForSiteDto(
    UUID id,
    String title,
    Integer seats,
    Integer confirmedSeats,
    Set<SessionScheduleForSiteDto> sessionSchedules
) {}
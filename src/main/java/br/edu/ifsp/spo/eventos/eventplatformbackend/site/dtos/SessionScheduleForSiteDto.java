package br.edu.ifsp.spo.eventos.eventplatformbackend.site.dtos;

import br.edu.ifsp.spo.eventos.eventplatformbackend.space.SpaceType;

import java.time.LocalDateTime;
import java.util.UUID;

public record SessionScheduleForSiteDto(
    UUID id,
    LocalDateTime executionStart,
    LocalDateTime executionEnd,
    String url,
    String locationName,
    String locationAddress,
    String areaName,
    String spaceName,
    SpaceType spaceType
) {}

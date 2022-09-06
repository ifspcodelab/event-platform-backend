package br.edu.ifsp.spo.eventos.eventplatformbackend.site.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record SessionScheduleForSiteDto(
    UUID id,
    LocalDateTime executionStart,
    LocalDateTime executionEnd,
    String url,
    String location,
    String area,
    String space
) {}

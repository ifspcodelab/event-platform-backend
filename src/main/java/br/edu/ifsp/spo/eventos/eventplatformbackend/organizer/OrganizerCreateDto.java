package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer;

import lombok.Value;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Value
public class OrganizerCreateDto {
    @NotNull
    OrganizerType type;
    @NotNull
    UUID accountId;
}

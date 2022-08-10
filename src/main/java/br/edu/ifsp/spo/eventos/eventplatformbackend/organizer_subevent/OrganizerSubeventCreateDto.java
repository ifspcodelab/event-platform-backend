package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer_subevent;

import lombok.Value;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Value
public class OrganizerSubeventCreateDto {
    @NotNull
    OrganizerSubeventType organizerSubeventType;
    @NotNull
    UUID accountId;
}

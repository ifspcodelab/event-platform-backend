package br.edu.ifsp.spo.eventos.eventplatformbackend.site;

import br.edu.ifsp.spo.eventos.eventplatformbackend.organizer_subevent.OrganizerSubeventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizerSubEventSiteDto {
    UUID organizerId;
    String organizerName;
    OrganizerSubeventType organizerType;
}
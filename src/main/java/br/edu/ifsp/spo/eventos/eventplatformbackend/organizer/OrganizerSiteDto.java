package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizerSiteDto {
    UUID organizerId;
    String organizerName;
    OrganizerType organizerType;
}

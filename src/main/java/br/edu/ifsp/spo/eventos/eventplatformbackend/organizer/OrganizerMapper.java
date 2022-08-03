package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrganizerMapper {
    OrganizerDto to(Organizer organizer);
}

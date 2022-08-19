package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrganizerMapper {
    OrganizerDto to(Organizer organizer);
    List<OrganizerDto> to(List<Organizer> organizers);
}

package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer_subevent;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrganizerSubeventMapper {
    OrganizerSubeventDto to(OrganizerSubevent organizerSubevent);
    List<OrganizerSubeventDto> to(List<OrganizerSubevent> organizerSubevents);
}

package br.edu.ifsp.spo.eventos.eventplatformbackend.event;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {
    EventDto to(Event event);
}

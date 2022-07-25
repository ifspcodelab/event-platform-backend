package br.edu.ifsp.spo.eventos.eventplatformbackend.event;

import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {
    EventDto to(Event event);
    List<EventDto> to(List<Event> events);
}

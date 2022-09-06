package br.edu.ifsp.spo.eventos.eventplatformbackend.site.mappers;

import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.site.dtos.EventSiteDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventSiteMapper {
    EventSiteDto to(Event event);
    List<EventSiteDto> to(List<Event> events);
}
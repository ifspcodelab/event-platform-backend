package br.edu.ifsp.spo.eventos.eventplatformbackend.site.mappers;

import br.edu.ifsp.spo.eventos.eventplatformbackend.site.dtos.SubeventSiteDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.Subevent;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubeventSiteMapper {
    SubeventSiteDto to(Subevent event);
    List<SubeventSiteDto> to(List<Subevent> events);
}
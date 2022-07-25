package br.edu.ifsp.spo.eventos.eventplatformbackend.subevent;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubeventMapper {
    SubeventDto to(Subevent subevent);
    List<SubeventDto> to(List<Subevent> subevents);
}

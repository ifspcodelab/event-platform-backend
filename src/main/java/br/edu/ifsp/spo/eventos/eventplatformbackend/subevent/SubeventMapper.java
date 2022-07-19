package br.edu.ifsp.spo.eventos.eventplatformbackend.subevent;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubeventMapper {
    SubeventDto to(Subevent subevent);
}

package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SessionMapper {
    SessionDto to(Session session);
}

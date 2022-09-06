package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SessionMapper {
    SessionDto to(Session session);
    List<SessionDto> to(List<Session> sessions);
}

package br.edu.ifsp.spo.eventos.eventplatformbackend.space;

import org.mapstruct.Mapper;

@Mapper (componentModel = "spring")
public interface SpaceMapper {
    SpaceDto to(Space space);
}

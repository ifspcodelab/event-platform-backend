package br.edu.ifsp.spo.eventos.eventplatformbackend.space;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper (componentModel = "spring")
public interface SpaceMapper {
    SpaceDto to(Space space);
    List<SpaceDto> to(List<Space> spaces);
}

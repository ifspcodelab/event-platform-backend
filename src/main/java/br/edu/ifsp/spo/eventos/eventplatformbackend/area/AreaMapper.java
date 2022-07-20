package br.edu.ifsp.spo.eventos.eventplatformbackend.area;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AreaMapper {
    AreaDto to(Area area);
}

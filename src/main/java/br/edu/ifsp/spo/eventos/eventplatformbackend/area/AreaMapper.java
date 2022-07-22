package br.edu.ifsp.spo.eventos.eventplatformbackend.area;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AreaMapper {
    AreaDto to(Area area);
    List<AreaDto> to(List<Area> areas);
}

package br.edu.ifsp.spo.eventos.eventplatformbackend.location;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    LocationDto to(Location location);
}

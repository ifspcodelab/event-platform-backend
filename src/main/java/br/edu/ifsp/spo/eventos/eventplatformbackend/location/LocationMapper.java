package br.edu.ifsp.spo.eventos.eventplatformbackend.location;

import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    LocationDto to(Location location);
    List<LocationDto> to(List<Location> locations);
}

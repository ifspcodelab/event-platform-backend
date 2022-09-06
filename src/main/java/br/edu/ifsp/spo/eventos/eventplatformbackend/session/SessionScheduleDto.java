package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import br.edu.ifsp.spo.eventos.eventplatformbackend.area.AreaDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.LocationDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.space.SpaceDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class SessionScheduleDto {
    UUID id;
    LocalDateTime executionStart;
    LocalDateTime executionEnd;
    String url;
    LocationDto location;
    AreaDto area;
    SpaceDto space;
}

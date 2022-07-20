package br.edu.ifsp.spo.eventos.eventplatformbackend.area;

import br.edu.ifsp.spo.eventos.eventplatformbackend.location.LocationDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AreaDto {
    UUID id;
    String name;
    String reference;
    LocationDto location;
}

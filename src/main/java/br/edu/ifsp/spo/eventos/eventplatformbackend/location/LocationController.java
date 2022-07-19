package br.edu.ifsp.spo.eventos.eventplatformbackend.location;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping(value = "api/v1/locations")
public class LocationController {

    @PostMapping
    public LocationDto create(@RequestBody @Valid LocationCreateDto locationCreateDto) {
        LocationDto locationDto = new LocationDto(UUID.randomUUID(), locationCreateDto.getName(), locationCreateDto.getAddress());
        return locationDto;
    }
}

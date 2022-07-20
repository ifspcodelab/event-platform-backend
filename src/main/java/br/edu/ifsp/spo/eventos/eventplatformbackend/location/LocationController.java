package br.edu.ifsp.spo.eventos.eventplatformbackend.location;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
public class LocationController {

    @RequestMapping(value = "api/v1/locations")
    @PostMapping
    public ResponseEntity<LocationDto> create(@RequestBody @Valid LocationCreateDto locationCreateDto) {
        LocationDto locationDto = new LocationDto(UUID.randomUUID(), locationCreateDto.getName(), locationCreateDto.getAddress());
        return new ResponseEntity<>(locationDto, HttpStatus.CREATED);
    }

}

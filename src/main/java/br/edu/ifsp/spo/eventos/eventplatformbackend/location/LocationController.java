package br.edu.ifsp.spo.eventos.eventplatformbackend.location;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "api/v1/locations")
@AllArgsConstructor
public class LocationController {
    private LocationService locationService;

    @PostMapping
    public ResponseEntity<LocationDto> create(@RequestBody @Valid LocationCreateDto locationCreateDto) {
        Location location = locationService.create(locationCreateDto);
        LocationDto locationDto = new LocationDto(location.getId(), location.getName(), location.getAddress());
        return new ResponseEntity<>(locationDto, HttpStatus.CREATED);
    }
}

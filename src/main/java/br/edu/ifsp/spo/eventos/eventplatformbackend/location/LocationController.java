package br.edu.ifsp.spo.eventos.eventplatformbackend.location;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "api/v1/locations")
@AllArgsConstructor
public class LocationController {
    private final LocationService locationService;
    private final LocationMapper locationMapper;

    @PostMapping
    public ResponseEntity<LocationDto> create(@RequestBody @Valid LocationCreateDto locationCreateDto) {
        Location location = locationService.create(locationCreateDto);
        LocationDto locationDto = locationMapper.to(location);
        return new ResponseEntity<>(locationDto, HttpStatus.CREATED);
    }

    @PutMapping("{locationId}")
    public ResponseEntity<LocationDto> update(@PathVariable UUID locationId, @RequestBody @Valid LocationCreateDto locationCreateDto) {
        Location location = locationService.update(locationId, locationCreateDto);
        LocationDto locationDto = locationMapper.to(location);
        return ResponseEntity.ok(locationDto);
    }

    @GetMapping()
    public ResponseEntity<List<LocationDto>> index() {
        List<Location> locations = locationService.findAll();
        return ResponseEntity.ok(locationMapper.to(locations));
    }

    @GetMapping("{locationId}")
    public ResponseEntity<LocationDto> show(@PathVariable UUID locationId) {
        Location location = locationService.findById(locationId);
        LocationDto locationDto = locationMapper.to(location);
        return ResponseEntity.ok(locationDto);
    }

    @DeleteMapping("{locationId}")
    public ResponseEntity<Void> delete(@PathVariable UUID locationId) {
        locationService.delete(locationId);
        return ResponseEntity.noContent().build();
    }
}

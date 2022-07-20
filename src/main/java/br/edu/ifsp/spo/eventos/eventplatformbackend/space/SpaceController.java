package br.edu.ifsp.spo.eventos.eventplatformbackend.space;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/v1/locations/{locationId}/areas/{areaId}/spaces")
public class SpaceController {
    private SpaceService spaceService;
    private final SpaceMapper spaceMapper;

    @PostMapping
    public ResponseEntity<SpaceDto> create(@PathVariable UUID locationId, @PathVariable UUID areaId, @RequestBody @Valid SpaceCreateDto spaceCreateDto) {
        Space space = spaceService.create(spaceCreateDto, areaId, locationId);
        SpaceDto spaceDto = spaceMapper.to(space);
        return new ResponseEntity<>(spaceDto, HttpStatus.CREATED);
    }

    @GetMapping("{spaceId}")
    public ResponseEntity<SpaceDto> show(@PathVariable UUID locationId, @PathVariable UUID areaId, @PathVariable UUID spaceId) {
        Space space = spaceService.findById(locationId, areaId, spaceId);
        SpaceDto spaceDto = spaceMapper.to(space);
        return new ResponseEntity<>(spaceDto, HttpStatus.OK);
    }
}

package br.edu.ifsp.spo.eventos.eventplatformbackend.space;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/v1/locations/{locationId}/areas/{areaId}/spaces")
public class SpaceController {
    private final SpaceService spaceService;
    private final SpaceMapper spaceMapper;

    @PostMapping
    public ResponseEntity<SpaceDto> create(@PathVariable UUID locationId, @PathVariable UUID areaId, @RequestBody @Valid SpaceCreateDto spaceCreateDto) {
        Space space = spaceService.create(locationId, areaId, spaceCreateDto);
        SpaceDto spaceDto = spaceMapper.to(space);
        return new ResponseEntity<>(spaceDto, HttpStatus.CREATED);
    }

    @PutMapping("{spaceId}")
    public ResponseEntity<SpaceDto> update(@PathVariable UUID locationId, @PathVariable UUID areaId, @PathVariable UUID spaceId, @RequestBody @Valid SpaceCreateDto spaceCreateDto) {
        Space space = spaceService.update(locationId, areaId, spaceId, spaceCreateDto);
        SpaceDto spaceDto = spaceMapper.to(space);
        return ResponseEntity.ok(spaceDto);
    }

    @GetMapping()
    public ResponseEntity<List<SpaceDto>> index(@PathVariable UUID locationId, @PathVariable UUID areaId) {
        List<Space> spaces = spaceService.findAll(locationId, areaId);
        List<SpaceDto> spacesDto = spaces.stream()
                .map(spaceMapper::to)
                .collect(Collectors.toList());
        return ResponseEntity.ok(spacesDto);
    }

    @GetMapping("{spaceId}")
    public ResponseEntity<SpaceDto> show(@PathVariable UUID locationId, @PathVariable UUID areaId, @PathVariable UUID spaceId) {
        Space space = spaceService.findById(locationId, areaId, spaceId);
        SpaceDto spaceDto = spaceMapper.to(space);
        return ResponseEntity.ok(spaceDto);
    }

    @DeleteMapping("{spaceId}")
    public ResponseEntity<Void> delete(@PathVariable UUID locationId, @PathVariable UUID areaId, @PathVariable UUID spaceId) {
        spaceService.delete(locationId, areaId, spaceId);
        return ResponseEntity.noContent().build();
    }
}

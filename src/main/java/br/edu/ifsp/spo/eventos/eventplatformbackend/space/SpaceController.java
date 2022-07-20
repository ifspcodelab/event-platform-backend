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
    private SpaceService spaceService;
    private final SpaceMapper spaceMapper;

    @PostMapping
    public ResponseEntity<SpaceDto> create(@PathVariable UUID locationId, @PathVariable UUID areaId, @RequestBody @Valid SpaceCreateDto spaceCreateDto) {
        Space space = spaceService.create(spaceCreateDto, areaId, locationId);
        SpaceDto spaceDto = spaceMapper.to(space);
        return new ResponseEntity<>(spaceDto, HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<List<SpaceDto>> index(@PathVariable UUID locationId, @PathVariable UUID areaId) {
        List<Space> spaces = spaceService.findAll(locationId, areaId);
        List<SpaceDto> spacesDto = spaces.stream()
                .map(space -> spaceMapper.to(space))
                .collect(Collectors.toList());
        return new ResponseEntity<>(spacesDto, HttpStatus.OK);
    }

    @GetMapping("{spaceId}")
    public ResponseEntity<SpaceDto> show(@PathVariable UUID locationId, @PathVariable UUID areaId, @PathVariable UUID spaceId) {
        Space space = spaceService.findById(locationId, areaId, spaceId);
        SpaceDto spaceDto = spaceMapper.to(space);
        return new ResponseEntity<>(spaceDto, HttpStatus.OK);
    }

    @DeleteMapping("{spaceId}")
    public ResponseEntity<Void> delete(@PathVariable UUID locationId, @PathVariable UUID areaId, @PathVariable UUID spaceId) {
        spaceService.delete(locationId, areaId, spaceId);
        return ResponseEntity.noContent().build();
    }
}

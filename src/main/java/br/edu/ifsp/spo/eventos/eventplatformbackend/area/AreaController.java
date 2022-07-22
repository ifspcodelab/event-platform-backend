package br.edu.ifsp.spo.eventos.eventplatformbackend.area;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/locations/{locationId}/areas")
@AllArgsConstructor
public class AreaController {
    private final AreaService areaService;
    private final AreaMapper areaMapper;

    @PostMapping
    public ResponseEntity<AreaDto> create(@PathVariable UUID locationId, @Valid @RequestBody AreaCreateDto areaCreateDto) {
        Area area = areaService.create(locationId, areaCreateDto);
        AreaDto areaDto = areaMapper.to(area);
        return new ResponseEntity<>(areaDto, HttpStatus.CREATED);
    }

    @PutMapping("{areaId}")
    public ResponseEntity<AreaDto> update(@PathVariable UUID locationId, @PathVariable UUID areaId, @Valid @RequestBody AreaCreateDto areaCreateDto) {
        Area area = areaService.update(locationId, areaId, areaCreateDto);
        AreaDto areaDto = areaMapper.to(area);
        return ResponseEntity.ok(areaDto);
    }

    @GetMapping
    public ResponseEntity<List<AreaDto>> index(@PathVariable UUID locationId) {
        List<Area> areas = areaService.findAll(locationId);
        return ResponseEntity.ok(areaMapper.to(areas));
    }

    @GetMapping("{areaId}")
    public ResponseEntity<AreaDto> show(@PathVariable UUID locationId, @PathVariable UUID areaId) {
        Area area = areaService.findById(locationId, areaId);
        AreaDto areaDto = areaMapper.to(area);
        return ResponseEntity.ok(areaDto);
    }

    @DeleteMapping("{areaId}")
    public ResponseEntity<Void> delete(@PathVariable UUID locationId, @PathVariable UUID areaId) {
        areaService.delete(locationId, areaId);
        return ResponseEntity.noContent().build();
    }
}

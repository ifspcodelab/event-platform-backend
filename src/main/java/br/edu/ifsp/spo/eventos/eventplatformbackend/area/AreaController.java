package br.edu.ifsp.spo.eventos.eventplatformbackend.area;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/locations/{locationId}/areas")
@AllArgsConstructor
public class AreaController {
    private AreaService areaService;
    private AreaMapper areaMapper;

    @PostMapping
    public ResponseEntity<AreaDto> create(@PathVariable UUID locationId, @Valid @RequestBody AreaCreateDto areaCreateDto) {
        System.out.println(locationId);
        Area area = areaService.create(areaCreateDto, locationId);
        AreaDto areaDto = areaMapper.to(area);
        return new ResponseEntity<>(areaDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AreaDto>> index(@PathVariable UUID locationId) {
        List<Area> areas = areaService.findAll(locationId);
        List<AreaDto> areasDto = new ArrayList<>();
        for (Area area : areas) {
            areasDto.add(areaMapper.to(area));
        }
        return new ResponseEntity<>(areasDto, HttpStatus.OK);
    }

    @GetMapping("{areaId}")
    public ResponseEntity<AreaDto> show(@PathVariable UUID locationId, @PathVariable UUID areaId) {
        Area area = areaService.findById(locationId, areaId);
        AreaDto areaDto = areaMapper.to(area);
        return new ResponseEntity<>(areaDto, HttpStatus.OK);
    }
}

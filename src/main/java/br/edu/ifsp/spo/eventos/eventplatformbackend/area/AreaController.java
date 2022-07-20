package br.edu.ifsp.spo.eventos.eventplatformbackend.area;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/locations/{locationId}/areas")
@AllArgsConstructor
public class AreaController {
    private AreaService areaService;
    private AreaMapper areaMapper;

    @PostMapping
    public AreaDto create(@PathVariable UUID locationId, @Valid @RequestBody AreaCreateDto areaCreateDto) {
        System.out.println(locationId);
        Area area = areaService.create(areaCreateDto, locationId);
        AreaDto areaDto = areaMapper.to(area);
        return areaDto;
    }
}
package br.edu.ifsp.spo.eventos.eventplatformbackend.area;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/locations/{locationId}/areas")
public class AreaController {

    @PostMapping
    public AreaDto create(@PathVariable UUID locationId, @Valid @RequestBody AreaCreateDto areaCreateDto) {
        System.out.println(locationId);
        AreaDto areaDto = new AreaDto(UUID.randomUUID(), areaCreateDto.getName(), areaCreateDto.getReference());
        return areaDto;
    }

}

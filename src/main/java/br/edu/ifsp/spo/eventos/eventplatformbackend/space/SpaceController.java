package br.edu.ifsp.spo.eventos.eventplatformbackend.space;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping(value = "api/v1/locations/{locationId}/areas/{areaId}/spaces")
public class SpaceController {

    @PostMapping
    public SpaceDto create (@PathVariable UUID locationId, @PathVariable UUID areaId, @RequestBody @Valid SpaceCreateDto spaceCreateDto){
        System.out.println(locationId);
        System.out.println(areaId);
        SpaceDto spaceDto = new SpaceDto(UUID.randomUUID(), spaceCreateDto.getName(), spaceCreateDto.getCapacity(), spaceCreateDto.getType());
        return spaceDto;
    }

}

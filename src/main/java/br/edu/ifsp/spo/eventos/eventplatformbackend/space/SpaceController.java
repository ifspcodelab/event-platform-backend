package br.edu.ifsp.spo.eventos.eventplatformbackend.space;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/v1/locations/{locationId}/areas/{areaId}/spaces")
public class SpaceController {
    private SpaceService spaceService;

    @PostMapping
    public SpaceDto create (@PathVariable UUID locationId, @PathVariable UUID areaId, @RequestBody @Valid SpaceCreateDto spaceCreateDto){
        System.out.println(locationId);
        System.out.println(areaId);
        Space space = spaceService.create(spaceCreateDto, areaId, locationId);
        SpaceDto spaceDto = new SpaceDto(space.getId(), space.getName(), space.getCapacity(), space.getType());
        return spaceDto;
    }
}

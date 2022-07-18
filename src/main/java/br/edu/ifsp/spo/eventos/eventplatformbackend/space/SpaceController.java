package br.edu.ifsp.spo.eventos.eventplatformbackend.space;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
public class SpaceController {

    @RequestMapping(value = "api/v1/locations/{idLocation}/areas/{idArea}/spaces")
    public SpaceDto create (@RequestBody @Valid SpaceCreateDto spaceCreateDto){
        SpaceDto spaceDto = new SpaceDto(UUID.randomUUID(), spaceCreateDto.getName(), spaceCreateDto.getCapacity(), spaceCreateDto.getType());
        return spaceDto;
    }

}

package br.edu.ifsp.spo.eventos.eventplatformbackend.space;

import br.edu.ifsp.spo.eventos.eventplatformbackend.area.Area;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.AreaRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.ResourceAlreadyExistsException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.ResourceNotFoundException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.LocationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class SpaceService {
    private SpaceRepository spaceRepository;
    private AreaRepository areaRepository;
    private LocationRepository locationRepository;

    public Space create(SpaceCreateDto dto, UUID areaId, UUID locationId) {
        if(!locationRepository.existsById(locationId)) {
            throw new ResourceNotFoundException("location", locationId);
        }

        Area area = getArea(areaId);

        if(spaceRepository.existsByNameAndArea(dto.getName(), area)) {
           throw new ResourceAlreadyExistsException("space", "name", dto.getName());
        }

        Space space = new Space(dto.getName(), dto.getCapacity(), dto.getType(), area);
        return spaceRepository.save(space);
    }

    public Space findById(UUID locationId, UUID areaId, UUID spaceId) {
        if(!locationRepository.existsById(locationId)) {
            throw new ResourceNotFoundException("location", locationId);
        }

        if(!areaRepository.existsById(areaId)) {
            throw new ResourceNotFoundException("area", areaId);
        }

        return spaceRepository.findById(spaceId).get();
    }

    private Area getArea(UUID areaId) {
        return areaRepository.findById(areaId).orElseThrow(() -> new ResourceNotFoundException("area", areaId));
    }


}


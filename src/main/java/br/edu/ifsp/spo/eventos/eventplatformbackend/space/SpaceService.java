package br.edu.ifsp.spo.eventos.eventplatformbackend.space;

import br.edu.ifsp.spo.eventos.eventplatformbackend.area.Area;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.AreaRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.ResourceNotFoundException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.LocationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
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

        Space space = new Space(dto.getName(), dto.getCapacity(), dto.getType(), area);
        return spaceRepository.save(space);
    }

    private Area getArea(UUID areaId) {
        Optional<Area> optionalArea = areaRepository.findById(areaId);
        if(optionalArea.isEmpty()) {
            throw new ResourceNotFoundException("area", areaId);
        }
        return optionalArea.get();
    }
}


package br.edu.ifsp.spo.eventos.eventplatformbackend.area;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.ResourceAlreadyExistsException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.ResourceNotFoundException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.Location;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.LocationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AreaService {
    private AreaRepository areaRepository;
    private LocationRepository locationRepository;

    public Area create(AreaCreateDto dto, UUID locationId) {
        if(!locationRepository.existsById(locationId)) {
            throw new ResourceNotFoundException("location", locationId);
        }

        Location location = getLocation(locationId);

        if(areaRepository.existsByNameAndLocation(dto.getName(), location)) {
            throw new ResourceAlreadyExistsException("area", "name", dto.getName());
        }

        Area area = new Area(dto.getName(), dto.getReference(), location);
        return areaRepository.save(area);
    }

    private Location getLocation(UUID locationId) {
        return locationRepository.findById(locationId).orElseThrow(() -> new ResourceNotFoundException("location", locationId));
    }
}

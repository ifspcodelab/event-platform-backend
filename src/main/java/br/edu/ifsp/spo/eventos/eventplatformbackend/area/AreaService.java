package br.edu.ifsp.spo.eventos.eventplatformbackend.area;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.ResourceAlreadyExistsException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.ResourceNotFoundException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.Location;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.LocationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AreaService {

    private AreaRepository areaRepository;
    private LocationRepository locationRepository;

    public Area create(@RequestBody @Valid AreaCreateDto dto, @PathVariable UUID locationId) {
        Location location = getLocation(locationId);

        if(areaRepository.existsByNameAndLocation(dto.getName(), location)) {
            throw new ResourceAlreadyExistsException("area", "name", dto.getName());
        }

        Area area = new Area(dto.getName(), dto.getReference(), location);
        return areaRepository.save(area);
    }

    public List<Area> findAll(UUID locationId) {
        checkLocationExists(locationId);
        return areaRepository.findAllByLocationId(locationId);
    }

    public Area findById(UUID locationId, UUID areaId) {
        checkLocationExists(locationId);
        return getArea(areaId);
    }

    private Location getLocation(UUID locationId) {
        return locationRepository.findById(locationId).orElseThrow(() -> new ResourceNotFoundException("location", locationId));
    }

    private Area getArea(UUID areaId) {
        return areaRepository.findById(areaId).orElseThrow(() -> new ResourceNotFoundException("area", areaId));
    }

    private void checkLocationExists(UUID locationId) {
        if(!locationRepository.existsById(locationId)) {
            throw new ResourceNotFoundException("location", locationId);
        }
    }
}

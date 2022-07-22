package br.edu.ifsp.spo.eventos.eventplatformbackend.location;

import br.edu.ifsp.spo.eventos.eventplatformbackend.area.AreaRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.ResourceAlreadyExistsException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.ResourceName;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.ResourceNotFoundException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.ResourceReferentialIntegrityException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class LocationService {
    private final LocationRepository locationRepository;
    private final AreaRepository areaRepository;

    public Location create(LocationCreateDto dto) {
        if(locationRepository.existsByName(dto.getName())) {
            throw new ResourceAlreadyExistsException("location", "name", dto.getName());
        }

        Location location = new Location(dto.getName(), dto.getAddress());
        return locationRepository.save(location);
    }

    public Location update(UUID locationId, LocationCreateDto dto) {
        Location location = getLocation(locationId);

        if(locationRepository.existsByNameAndIdNot(dto.getName(), locationId)) {
            throw new ResourceAlreadyExistsException("location", "name", dto.getName());
        }

        location.setName(dto.getName());
        location.setAddress(dto.getAddress());
        return locationRepository.save(location);
    }

    public List<Location> findAll() {
        return locationRepository.findAll();
    }

    public Location findById(UUID locationId) {
        return getLocation(locationId);
    }

    public void delete(UUID locationId) {
        Location location = getLocation(locationId);
        checkAreaExistsByLocationId(locationId);
        locationRepository.deleteById(locationId);
        log.info("Delete location id={}, name={}", locationId, location.getName());
    }

    private Location getLocation(UUID locationId) {
        return locationRepository.findById(locationId).orElseThrow(() -> new ResourceNotFoundException("location", locationId));
    }

    private void checkAreaExistsByLocationId(UUID locationId) {
        if(areaRepository.existsByLocationId(locationId)) {
            throw new ResourceReferentialIntegrityException(ResourceName.LOCATION, ResourceName.AREA);
        }
    }
}

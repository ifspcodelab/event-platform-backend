package br.edu.ifsp.spo.eventos.eventplatformbackend.location;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.ResourceAlreadyExistsException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;

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
        getLocation(locationId);
        //TODO: verificar se existe áreas associadas

        locationRepository.deleteById(locationId);
        //TODO: criar um log de informação que foi deletado
    }

    private Location getLocation(UUID locationId) {
        return locationRepository.findById(locationId).orElseThrow(() -> new ResourceNotFoundException("location", locationId));
    }
}

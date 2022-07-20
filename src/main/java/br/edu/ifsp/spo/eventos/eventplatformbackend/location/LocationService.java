package br.edu.ifsp.spo.eventos.eventplatformbackend.location;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.ResourceAlreadyExistsException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class LocationService {
    private LocationRepository locationRepository;

    public Location create(LocationCreateDto dto) {
        if(locationRepository.existsByName(dto.getName())) {
            throw new ResourceAlreadyExistsException("location", "name", dto.getName());
        }

        Location location = new Location(dto.getName(), dto.getAddress());
        return locationRepository.save(location);
    }

    public List<Location> findAll() {
        return locationRepository.findAll();
    }

}

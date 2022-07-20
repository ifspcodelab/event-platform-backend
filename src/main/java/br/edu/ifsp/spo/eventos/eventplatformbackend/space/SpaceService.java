package br.edu.ifsp.spo.eventos.eventplatformbackend.space;

import br.edu.ifsp.spo.eventos.eventplatformbackend.area.Area;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.AreaDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.AreaMapper;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.AreaRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.ResourceAlreadyExistsException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.ResourceNotFoundException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.Location;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.LocationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SpaceService {
    private SpaceRepository spaceRepository;
    private AreaRepository areaRepository;
    private LocationRepository locationRepository;
    private AreaMapper areaMapper;
    private SpaceMapper spaceMapper;

    public Space create(SpaceCreateDto dto, UUID areaId, UUID locationId) {
        Location location = getLocation(locationId);
        Area area = getArea(areaId);
        AreaDto areaDto = areaMapper.to(area);

        if(!areaRepository.existsByNameAndLocation(areaDto.getName(), location)) {
            throw new ResourceNotFoundException("area", areaId);
        }

        if(spaceRepository.existsByNameAndArea(dto.getName(), area)) {
            throw new ResourceAlreadyExistsException("space", "name", dto.getName());
        }

        Space space = new Space(dto.getName(), dto.getCapacity(), dto.getType(), area);
        return spaceRepository.save(space);
    }

    public List<Space> findAll(UUID locationId, UUID areaId) {
        Location location = getLocation(locationId);
        Area area = getArea(areaId);
        AreaDto areaDto = areaMapper.to(area);

        if(!areaRepository.existsByNameAndLocation(areaDto.getName(), location)) {
            throw new ResourceNotFoundException("area", areaId);
        }

        return spaceRepository.findAllByAreaId(areaId);
    }

    public Space findById(UUID locationId, UUID areaId, UUID spaceId) {
        Location location = getLocation(locationId);
        Area area = getArea(areaId);
        AreaDto areaDto = areaMapper.to(area);
        Space space = getSpace(spaceId);
        SpaceDto spaceDto = spaceMapper.to(space);

        if(!areaRepository.existsByNameAndLocation(areaDto.getName(), location)) {
            throw new ResourceNotFoundException("area", areaId);
        }

        if(!spaceRepository.existsByNameAndArea(spaceDto.getName(), area)) {
            throw new ResourceNotFoundException("space", spaceId);
        }

        return getSpace(spaceId);
    }

    public void delete(UUID locationId, UUID areaId, UUID spaceId) {
        Location location = getLocation(locationId);
        Area area = getArea(areaId);
        AreaDto areaDto = areaMapper.to(area);
        Space space = getSpace(spaceId);
        SpaceDto spaceDto = spaceMapper.to(space);

        if(!areaRepository.existsByNameAndLocation(areaDto.getName(), location)) {
            throw new ResourceNotFoundException("area", areaId);
        }

        if(!spaceRepository.existsByNameAndArea(spaceDto.getName(), area)) {
            throw new ResourceNotFoundException("space", spaceId);
        }

        spaceRepository.deleteById(spaceId);
    }

    private Location getLocation(UUID locationId) {
        return locationRepository.findById(locationId).orElseThrow(() -> new ResourceNotFoundException("location", locationId));
    }

    private Area getArea(UUID areaId) {
        return areaRepository.findById(areaId).orElseThrow(() -> new ResourceNotFoundException("area", areaId));
    }

    private Space getSpace(UUID spaceId) {
        return spaceRepository.findById(spaceId).orElseThrow(() -> new ResourceNotFoundException("space", spaceId));
    }
}


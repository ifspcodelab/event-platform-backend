package br.edu.ifsp.spo.eventos.eventplatformbackend.space;

import br.edu.ifsp.spo.eventos.eventplatformbackend.area.Area;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.AreaRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.ResourceAlreadyExistsException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.ResourceNotFoundException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class SpaceService {
    private final SpaceRepository spaceRepository;
    private final AreaRepository areaRepository;

    public Space create(UUID locationId, UUID areaId, SpaceCreateDto dto) {
        Area area = getArea(areaId);
        checkIfAreaIsAssociateToLocation(area, locationId);

        if(spaceRepository.existsByNameAndAreaId(dto.getName(), areaId)) {
            throw new ResourceAlreadyExistsException("space", "name", dto.getName());
        }

        Space space = new Space(dto.getName(), dto.getCapacity(), dto.getType(), area);
        return spaceRepository.save(space);
    }

    public Space update(UUID locationId, UUID areaId, UUID spaceId, SpaceCreateDto dto) {
        Area area = getArea(areaId);
        checkIfAreaIsAssociateToLocation(area, locationId);

        Space space = getSpace(spaceId);
        checkIfSpaceIsAssociateToArea(space, areaId);

        if (spaceRepository.existsByNameAndAreaIdAndIdNot(dto.getName(), areaId, spaceId)) {
            throw new ResourceAlreadyExistsException("space", "name", dto.getName());
        }

        space.setName(dto.getName());
        space.setCapacity(dto.getCapacity());
        space.setType(dto.getType());
        return spaceRepository.save(space);
    }

    public List<Space> findAll(UUID locationId, UUID areaId) {
        Area area = getArea(areaId);
        checkIfAreaIsAssociateToLocation(area, locationId);
        return spaceRepository.findAllByAreaId(areaId);
    }

    public Space findById(UUID locationId, UUID areaId, UUID spaceId) {
        Space space = getSpace(spaceId);
        checkIfSpaceIsAssociateToArea(space, areaId);
        checkIfAreaIsAssociateToLocation(space.getArea(), locationId);
        return space;
    }

    public void delete(UUID locationId, UUID areaId, UUID spaceId) {
        Space space = getSpace(spaceId);
        checkIfSpaceIsAssociateToArea(space, areaId);
        checkIfAreaIsAssociateToLocation(space.getArea(), locationId);
        spaceRepository.deleteById(spaceId);
        log.info("Delete space id={}, name={}", spaceId, space.getName());
    }

    private Area getArea(UUID areaId) {
        return areaRepository.findById(areaId)
                .orElseThrow(() -> new ResourceNotFoundException("area", areaId));
    }

    private Space getSpace(UUID spaceId) {
        return spaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("space", spaceId));
    }

    private void checkIfSpaceIsAssociateToArea(Space space, UUID areaId) {
        if (!space.getArea().getId().equals(areaId)) {
            throw new ResourceNotFoundException("area", areaId);
        }
    }

    private void checkIfAreaIsAssociateToLocation(Area area, UUID locationId) {
        if (!area.getLocation().getId().equals(locationId)) {
            throw new ResourceNotFoundException("location", locationId);
        }
    }
}

package br.edu.ifsp.spo.eventos.eventplatformbackend.space;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.AuditService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.Area;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.AreaRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.*;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.SessionScheduleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.DiffResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class SpaceService {
    private final SpaceRepository spaceRepository;
    private final AreaRepository areaRepository;
    private final SessionScheduleRepository sessionScheduleRepository;
    private final AuditService auditService;

    public Space create(UUID locationId, UUID areaId, SpaceCreateDto dto) {
        Area area = getArea(areaId);
        checkIfAreaIsAssociateToLocation(area, locationId);

        if(spaceRepository.existsByNameAndAreaId(dto.getName(), areaId)) {
            throw new ResourceAlreadyExistsException(ResourceName.SPACE, "name", dto.getName());
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
            throw new ResourceAlreadyExistsException(ResourceName.SPACE, "name", dto.getName());
        }

        Space currentSpace = new Space();
        currentSpace.setName(space.getName());
        currentSpace.setCapacity(space.getCapacity());
        currentSpace.setType(space.getType());

        space.setName(dto.getName());
        space.setCapacity(dto.getCapacity());
        space.setType(dto.getType());

        spaceRepository.save(space);

        DiffResult<?> diffResult = currentSpace.diff(space);
        auditService.logAdminUpdate(ResourceName.SPACE, diffResult.getDiffs().toString(), spaceId);

        return space;
    }

    public List<Space> findAll(UUID locationId, UUID areaId) {
        Area area = getArea(areaId);
        checkIfAreaIsAssociateToLocation(area, locationId);
        return spaceRepository.findAllByAreaId(areaId);
    }

    public Space findById(UUID locationId, UUID areaId, UUID spaceId) {
        Space space = getSpace(spaceId);
        checkIfAreaIsAssociateToLocation(getArea(areaId), locationId);
        checkIfSpaceIsAssociateToArea(space, areaId);
        return space;
    }

    public void delete(UUID locationId, UUID areaId, UUID spaceId) {
        Space space = getSpace(spaceId);
        checkIfAreaIsAssociateToLocation(getArea(areaId), locationId);
        checkIfSpaceIsAssociateToArea(space, areaId);

        if(sessionScheduleRepository.existsBySpaceId(spaceId)) {
            throw new BusinessRuleException(BusinessRuleType.SPACE_DELETE_WITH_SESSIONS_SCHEDULE);
        }

        spaceRepository.deleteById(spaceId);
        log.info("Delete space id={}, name={}", spaceId, space.getName());
        auditService.logAdminDelete(ResourceName.SPACE, spaceId);
    }

    private Area getArea(UUID areaId) {
        return areaRepository.findById(areaId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.AREA, areaId));
    }

    private Space getSpace(UUID spaceId) {
        return spaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.SPACE, spaceId));
    }

    private void checkIfSpaceIsAssociateToArea(Space space, UUID areaId) {
        if (!space.getArea().getId().equals(areaId)) {
            throw new ResourceReferentialIntegrityException(ResourceName.SPACE, ResourceName.AREA);
        }
    }

    private void checkIfAreaIsAssociateToLocation(Area area, UUID locationId) {
        if (!area.getLocation().getId().equals(locationId)) {
            throw new ResourceNotExistsAssociationException(ResourceName.AREA, ResourceName.LOCATION);
        }
    }
}

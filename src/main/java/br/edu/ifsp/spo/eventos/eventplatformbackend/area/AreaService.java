package br.edu.ifsp.spo.eventos.eventplatformbackend.area;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.AuditService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.*;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtUserDetails;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.Location;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.LocationRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.space.SpaceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.DiffResult;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class AreaService {
    private final AreaRepository areaRepository;
    private final LocationRepository locationRepository;
    private final SpaceRepository spaceRepository;
    private final AuditService auditService;

    public Area create(UUID locationId, AreaCreateDto dto) {
        Location location = getLocation(locationId);

        if(areaRepository.existsByNameAndLocationId(dto.getName(), locationId)) {
            throw new ResourceAlreadyExistsException(ResourceName.AREA, "name", dto.getName());
        }

        Area area = new Area(dto.getName(), dto.getReference(), location);
        return areaRepository.save(area);
    }

    public Area update(UUID locationId, UUID areaId, AreaCreateDto dto) {
        Area area = getArea(areaId);
        checkAreaExistsByLocationId(area, locationId);

        if(areaRepository.existsByNameAndLocationIdAndIdNot(dto.getName(), locationId, areaId)) {
            throw new ResourceAlreadyExistsException(ResourceName.AREA, "name", dto.getName());
        }

        Area currentArea = new Area();
        currentArea.setName(area.getName());
        currentArea.setReference(area.getReference());

        area.setName(dto.getName());
        area.setReference(dto.getReference());

        areaRepository.save(area);

        DiffResult<?> diffResult = currentArea.diff(area);
        JwtUserDetails jwtUserDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        auditService.logAdminUpdate(jwtUserDetails.getId(), ResourceName.AREA, diffResult.getDiffs().toString(), areaId);

        return area;
    }

    public List<Area> findAll(UUID locationId) {
        checkLocationExists(locationId);
        return areaRepository.findAllByLocationId(locationId);
    }

    public Area findById(UUID locationId, UUID areaId) {
        Area area = getArea(areaId);
        checkAreaExistsByLocationId(area, locationId);
        return area;
    }

    public void delete(UUID locationId, UUID areaId) {
        Area area = getArea(areaId);
        checkAreaExistsByLocationId(area, locationId);
        checkSpaceExistsByAreaId(areaId);
        areaRepository.deleteById(areaId);
        log.info("Delete area id={}, name={}", areaId, area.getName());
        JwtUserDetails jwtUserDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        auditService.logAdminDelete(jwtUserDetails.getId(), ResourceName.AREA, areaId);
    }

    private Location getLocation(UUID locationId) {
        return locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.LOCATION, locationId));
    }

    private Area getArea(UUID areaId) {
        return areaRepository.findById(areaId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.AREA, areaId));
    }

    private void checkLocationExists(UUID locationId) {
        if(!locationRepository.existsById(locationId)) {
            throw new ResourceNotFoundException(ResourceName.LOCATION, locationId);
        }
    }

    private void checkSpaceExistsByAreaId(UUID areaId) {
        if(spaceRepository.existsByAreaId(areaId)) {
            throw new ResourceReferentialIntegrityException(ResourceName.AREA, ResourceName.SPACE);
        }
    }

    private void checkAreaExistsByLocationId(Area area, UUID locationId) {
        if (!area.getLocation().getId().equals(locationId)) {
            throw new ResourceNotExistsAssociationException(ResourceName.AREA, ResourceName.LOCATION);
        }
    }
}

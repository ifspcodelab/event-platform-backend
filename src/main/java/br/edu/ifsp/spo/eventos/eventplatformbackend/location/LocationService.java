package br.edu.ifsp.spo.eventos.eventplatformbackend.location;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.AuditService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.AreaRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceAlreadyExistsException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceName;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceNotFoundException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceReferentialIntegrityException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtUserDetails;
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
public class LocationService {
    private final LocationRepository locationRepository;
    private final AreaRepository areaRepository;
    private final AuditService auditService;

    public Location create(LocationCreateDto dto) {
        if(locationRepository.existsByName(dto.getName())) {
            throw new ResourceAlreadyExistsException(ResourceName.LOCATION, "name", dto.getName());
        }

        Location location = new Location(dto.getName(), dto.getAddress());
        return locationRepository.save(location);
    }

    public Location update(UUID locationId, LocationCreateDto dto) {
        Location location = getLocation(locationId);

        if(locationRepository.existsByNameAndIdNot(dto.getName(), locationId)) {
            throw new ResourceAlreadyExistsException(ResourceName.LOCATION, "name", dto.getName());
        }

        Location currentLocation = new Location();
        currentLocation.setName(location.getName());
        currentLocation.setAddress(location.getAddress());

        location.setName(dto.getName());
        location.setAddress(dto.getAddress());

        locationRepository.save(location);

        DiffResult<?> diffResult = currentLocation.diff(location);
        JwtUserDetails jwtUserDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        auditService.logAdminUpdate(jwtUserDetails.getId(), ResourceName.LOCATION, diffResult.getDiffs().toString(), locationId);

        return location;
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
        JwtUserDetails jwtUserDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        auditService.logAdminDelete(jwtUserDetails.getId(), ResourceName.LOCATION, locationId);
    }

    private Location getLocation(UUID locationId) {
        return locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.LOCATION, locationId));
    }

    private void checkAreaExistsByLocationId(UUID locationId) {
        if(areaRepository.existsByLocationId(locationId)) {
            throw new ResourceReferentialIntegrityException(ResourceName.LOCATION, ResourceName.AREA);
        }
    }
}

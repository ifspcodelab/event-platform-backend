package br.edu.ifsp.spo.eventos.eventplatformbackend.location;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.AuditService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.AreaRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceAlreadyExistsException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceName;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceNotFoundException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceReferentialIntegrityException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTest {
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private AreaRepository areaRepository;
    @Mock
    private AuditService auditService;
    @InjectMocks
    private LocationService locationService;

    @Test
    public void create_ThrowsException_WhenLocationNameAlreadyExists() {
        LocationCreateDto locationCreateDto = new LocationCreateDto(
                "Shopping D",
                "Av. Cruzeiro do Sul"
        );
        when(locationRepository.existsByName(any(String.class))).thenReturn(true);

        assertThatThrownBy(() -> locationService.create(locationCreateDto))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    public void create_ReturnsLocation_WhenSuccessful() {
        LocationCreateDto locationCreateDto = new LocationCreateDto(
                "Shopping D",
                "Av. Cruzeiro do Sul"
        );
        Location location = new Location(
                locationCreateDto.getName(),
                locationCreateDto.getAddress()
        );
        when(locationRepository.existsByName(any(String.class))).thenReturn(false);
        when(locationRepository.save(any(Location.class))).thenReturn(location);

        Location locationCreated = locationService.create(locationCreateDto);

        verify(locationRepository, times(1)).save(any(Location.class));
        assertThat(locationCreated).isNotNull();
        assertThat(locationCreated.getId()).isEqualTo(location.getId());
        assertThat(locationCreated.getName()).isEqualTo(location.getName());
        assertThat(locationCreated.getAddress()).isEqualTo(location.getAddress());
    }

    @Test
    public void update_ThrowsException_WhenLocationDoesNotExists() {
        LocationCreateDto locationCreateDto = new LocationCreateDto(
                "Shopping D",
                "Av. Cruzeiro do Sul"
        );
        UUID locationId = UUID.randomUUID();
        when(locationRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> locationService.update(locationId, locationCreateDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }
    @Test
    public void update_ThrowsException_WhenLocationNameAlreadyExistsExcludingTheProvided() {
        LocationCreateDto locationCreateDto = new LocationCreateDto(
                "Shopping D",
                "Av. Cruzeiro do Sul"
        );
        Location location = new Location(
                locationCreateDto.getName(),
                locationCreateDto.getAddress()
        );
        UUID locationId = UUID.randomUUID();
        when(locationRepository.findById(any(UUID.class))).thenReturn(Optional.of(location));
        when(locationRepository.existsByNameAndIdNot(any(String.class),any(UUID.class))).thenReturn(true);

        assertThatThrownBy(() -> locationService.update(locationId, locationCreateDto))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    public void update_ReturnsLocation_WhenSuccessful() {
        LocationCreateDto locationCreateDto = new LocationCreateDto(
                "Shopping D",
                "Av. Cruzeiro do Sul"
        );
        Location location = new Location(
                locationCreateDto.getName(),
                locationCreateDto.getAddress()
        );
        UUID locationId = UUID.randomUUID();
        when(locationRepository.findById(any(UUID.class))).thenReturn(Optional.of(location));
        when(locationRepository.existsByNameAndIdNot(any(String.class),any(UUID.class))).thenReturn(false);

        Location locationUpdated = locationService.update(locationId, locationCreateDto);

        verify(locationRepository, times(1)).save(any(Location.class));
        verify(auditService, times(1)).logAdminUpdate(
                any(ResourceName.class),
                any(String.class),
                any(UUID.class)
        );
        assertThat(locationUpdated).isNotNull();
        assertThat(locationUpdated.getId()).isEqualTo(location.getId());
        assertThat(locationUpdated.getName()).isEqualTo(location.getName());
        assertThat(locationUpdated.getAddress()).isEqualTo(location.getAddress());
    }

    @Test
    public void delete_ThrowsException_WhenLocationDoesNotExists() {
        UUID locationId = UUID.randomUUID();
        when(locationRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> locationService.delete(locationId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void delete_ThrowsException_WhenLocationHasArea() {
        Location location = new Location(
                "Shopping D",
                "Av. Cruzeiro do Sul"
        );
        UUID locationId = UUID.randomUUID();
        when(locationRepository.findById(any(UUID.class))).thenReturn(Optional.of(location));
        when(areaRepository.existsByLocationId(any(UUID.class))).thenReturn(true);

        assertThatThrownBy(() -> locationService.delete(locationId))
                .isInstanceOf(ResourceReferentialIntegrityException.class);
    }

    @Test
    public void delete_DeletesLocation_WhenSuccessful() {
        Location location = new Location(
                "Shopping D",
                "Av. Cruzeiro do Sul"
        );
        UUID locationId = UUID.randomUUID();
        when(locationRepository.findById(any(UUID.class))).thenReturn(Optional.of(location));
        when(areaRepository.existsByLocationId(any(UUID.class))).thenReturn(false);

        locationService.delete(locationId);

        verify(locationRepository, times(1)).deleteById(any(UUID.class));
        verify(auditService, times(1)).logAdminDelete(
                any(ResourceName.class),
                any(UUID.class)
        );
    }
}

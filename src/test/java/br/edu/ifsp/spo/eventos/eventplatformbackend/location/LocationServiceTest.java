package br.edu.ifsp.spo.eventos.eventplatformbackend.location;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.AuditService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.AreaRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceAlreadyExistsException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceName;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceNotFoundException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceReferentialIntegrityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
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
    private Location location;

    @BeforeEach
    public void setUp() {
        location = LocationFactory.sampleLocation();
    }

    @Test
    public void create_ThrowsException_WhenLocationNameAlreadyExists() {
        LocationCreateDto locationCreateDto = sampleLocationCreateDto(location);
        when(locationRepository.existsByName(any(String.class))).thenReturn(true);

        ResourceAlreadyExistsException exception = (ResourceAlreadyExistsException)
                catchThrowable(() -> locationService.create(locationCreateDto));

        assertThat(exception).isInstanceOf(ResourceAlreadyExistsException.class);
        assertThat(exception.getResourceName()).isEqualTo(ResourceName.LOCATION);
        assertThat(exception.getResourceAttribute()).isEqualTo("name");
        assertThat(exception.getResourceAttributeValue()).isEqualTo(locationCreateDto.getName());
    }

    @Test
    public void create_ReturnsLocation_WhenSuccessful() {
        LocationCreateDto locationCreateDto = sampleLocationCreateDto(location);
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
        LocationCreateDto locationCreateDto = sampleLocationCreateDto(location);
        UUID locationId = location.getId();
        when(locationRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        ResourceNotFoundException exception = (ResourceNotFoundException)
                catchThrowable(() -> locationService.update(locationId, locationCreateDto));

        assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
        assertThat(exception.getResourceName()).isEqualTo(ResourceName.LOCATION);
        assertThat(exception.getResourceId()).isEqualTo(locationId.toString());
    }

    @Test
    public void update_ThrowsException_WhenLocationNameAlreadyExistsExcludingTheProvided() {
        LocationCreateDto locationCreateDto = sampleLocationCreateDto(location);
        UUID locationId = location.getId();
        when(locationRepository.findById(any(UUID.class))).thenReturn(Optional.of(location));
        when(locationRepository.existsByNameAndIdNot(any(String.class),any(UUID.class))).thenReturn(true);

        ResourceAlreadyExistsException exception = (ResourceAlreadyExistsException)
                catchThrowable(() -> locationService.update(locationId, locationCreateDto));

        assertThat(exception).isInstanceOf(ResourceAlreadyExistsException.class);
        assertThat(exception.getResourceName()).isEqualTo(ResourceName.LOCATION);
        assertThat(exception.getResourceAttribute()).isEqualTo("name");
        assertThat(exception.getResourceAttributeValue()).isEqualTo(locationCreateDto.getName());
    }

    @Test
    public void update_ReturnsLocation_WhenSuccessful() {
        LocationCreateDto locationCreateDto = sampleLocationCreateDto(location);
        UUID locationId = location.getId();
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
        UUID locationId = location.getId();
        when(locationRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        ResourceNotFoundException exception = (ResourceNotFoundException)
                catchThrowable(() -> locationService.delete(locationId));

        assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
        assertThat(exception.getResourceName()).isEqualTo(ResourceName.LOCATION);
        assertThat(exception.getResourceId()).isEqualTo(locationId.toString());
    }

    @Test
    public void delete_ThrowsException_WhenLocationHasArea() {
        UUID locationId = location.getId();
        when(locationRepository.findById(any(UUID.class))).thenReturn(Optional.of(location));
        when(areaRepository.existsByLocationId(any(UUID.class))).thenReturn(true);

        ResourceReferentialIntegrityException exception = (ResourceReferentialIntegrityException)
                catchThrowable(() -> locationService.delete(locationId));

        assertThat(exception).isInstanceOf(ResourceReferentialIntegrityException.class);
        assertThat(exception.getPrimary()).isEqualTo(ResourceName.LOCATION);
        assertThat(exception.getRelated()).isEqualTo(ResourceName.AREA);
    }

    @Test
    public void delete_DeletesLocation_WhenSuccessful() {
        UUID locationId = location.getId();
        when(locationRepository.findById(any(UUID.class))).thenReturn(Optional.of(location));
        when(areaRepository.existsByLocationId(any(UUID.class))).thenReturn(false);

        locationService.delete(locationId);

        verify(locationRepository, times(1)).deleteById(any(UUID.class));
        verify(auditService, times(1)).logAdminDelete(
                any(ResourceName.class),
                any(UUID.class)
        );
    }

    @Test
    public void findAll_ReturnsEmptyList_WhenLocationsDoNotExist()
    {
        when(locationRepository.findAll()).thenReturn(Collections.emptyList());

        List<Location> locations = locationService.findAll();

        assertThat(locations).isEmpty();
    }

    @Test
    public void findAll_ReturnsLocationList_WhenSuccessful()
    {
        when(locationRepository.findAll()).thenReturn(List.of(location));

        List<Location> locations = locationService.findAll();

        assertThat(locations).hasSize(1);
    }

    @Test
    public void findById_ThrowsException_WhenLocationDoesNotExist()
    {
        UUID locationId = location.getId();
        when(locationRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        ResourceNotFoundException exception = (ResourceNotFoundException)
                catchThrowable(() -> locationService.findById(locationId));

        assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
        assertThat(exception.getResourceName()).isEqualTo(ResourceName.LOCATION);
        assertThat(exception.getResourceId()).isEqualTo(locationId.toString());
    }

    @Test
    public void findById_ReturnsLocation_WhenSuccessful()
    {
        UUID locationId = location.getId();
        when(locationRepository.findById(any(UUID.class))).thenReturn(Optional.of(location));

        Location locationFound = locationService.findById(locationId);

        assertThat(locationFound).isNotNull();
        assertThat(locationFound.getId()).isEqualTo(locationId);
    }

    private LocationCreateDto sampleLocationCreateDto(Location location) {
        return new LocationCreateDto(
            location.getName(),
            location.getAddress()
        );
    }
}

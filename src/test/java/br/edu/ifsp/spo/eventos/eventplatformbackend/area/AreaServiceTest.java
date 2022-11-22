package br.edu.ifsp.spo.eventos.eventplatformbackend.area;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.AuditService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.*;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.Location;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.LocationRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.space.SpaceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AreaServiceTest {
    @Mock
    private AreaRepository areaRepository;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private SpaceRepository spaceRepository;
    @Mock
    private AuditService auditService;
    @InjectMocks
    private AreaService areaService;

    @Test
    public void areaServiceShouldNotBeNull() {
        assertThat(areaService).isNotNull();
    }

    @Test
    public void create_ThrowsException_WhenLocationDoesNotExist() {
        AreaCreateDto areaCreateDto = new AreaCreateDto(
                "Bloco A",
                "Piso Superior"
        );
        UUID locationId = UUID.randomUUID();

        when(locationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> areaService.create(locationId, areaCreateDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void create_ThrowsException_WhenAreaAlreadyExists() {
        AreaCreateDto areaCreateDto = new AreaCreateDto(
                "Bloco A",
                "Piso Superior"
        );
        Location location = new Location(
                UUID.randomUUID(),
                "IFSP Campus São Paulo",
                "R. Pedro Vicente, 625 - Canindé, São Paulo - SP, 01109-010"
        );

        when(locationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(location));
        when(areaRepository.existsByNameAndLocationId(
                areaCreateDto.getName(),
                location.getId())
        ).thenReturn(true);

        assertThatThrownBy(() -> areaService.create(location.getId(), areaCreateDto))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    public void create_ReturnsArea_WhenSuccessful() {
        AreaCreateDto areaCreateDto = new AreaCreateDto(
                "Bloco A",
                "Piso Superior"
        );
        Location location = new Location(
                UUID.randomUUID(),
                "IFSP Campus São Paulo",
                "R. Pedro Vicente, 625 - Canindé, São Paulo - SP, 01109-010"
        );
        Area area = new Area(
                areaCreateDto.getName(),
                areaCreateDto.getReference(),
                location
        );
        when(locationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(location));
        when(areaRepository.existsByNameAndLocationId(
                areaCreateDto.getName(),
                location.getId())
        ).thenReturn(false);
        when(areaRepository.save(any(Area.class)))
                .thenReturn(area);

        Area createdArea = areaService.create(location.getId(), areaCreateDto);

        verify(areaRepository, times(1)).save(any(Area.class));
        assertThat(createdArea).isNotNull();
        assertThat(createdArea.getId()).isEqualTo(area.getId());
        assertThat(createdArea.getName()).isEqualTo(area.getName());
        assertThat(createdArea.getReference()).isEqualTo(area.getReference());
        assertThat(createdArea.getLocation()).isEqualTo(area.getLocation());
    }

    @Test
    public void update_ThrowsException_WhenAreaDoesNotExist() {
        AreaCreateDto areaCreateDto = new AreaCreateDto(
                "Bloco A",
                "Piso Superior"
        );
        UUID locationId = UUID.randomUUID();
        UUID areaId = UUID.randomUUID();
        when(areaRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> areaService.update(locationId, areaId, areaCreateDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void update_ThrowsException_WhenLocationDoesNotExistInGivenArea() {
        AreaCreateDto areaCreateDto = new AreaCreateDto(
                "Bloco C",
                "Térreo"
        );
        Location location = new Location(
                UUID.randomUUID(),
                "IFSP Campus São Paulo",
                "R. Pedro Vicente, 625 - Canindé, São Paulo - SP, 01109-010"
        );
        Area area = new Area(
                "Bloco A",
                "Piso Superior",
                location
        );
        UUID locationId = UUID.randomUUID();
        UUID areaId = area.getId();

        when(areaRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(area));

        assertThatThrownBy(() -> areaService.update(locationId, areaId, areaCreateDto))
                .isInstanceOf(ResourceNotExistsAssociationException.class);
    }

    @Test
    public void update_ThrowsException_WhenLocationAndGivenAreaAlreadyExist() {
        AreaCreateDto areaCreateDto = new AreaCreateDto(
                "Bloco C",
                "Térreo"
        );
        Location location = new Location(
                UUID.randomUUID(),
                "IFSP Campus São Paulo",
                "R. Pedro Vicente, 625 - Canindé, São Paulo - SP, 01109-010"
        );
        Area area = new Area(
                "Bloco A",
                "Piso Superior",
                location
        );
        UUID locationId = location.getId();
        UUID areaId = area.getId();

        when(areaRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(area));
        when(areaRepository.existsByNameAndLocationIdAndIdNot(anyString(), any(UUID.class), any(UUID.class)))
                .thenReturn(Boolean.TRUE);

        assertThatThrownBy(() -> areaService.update(locationId, areaId, areaCreateDto))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    public void update_ReturnsUpdatedArea_WhenSuccessful() {
        AreaCreateDto areaCreateDto = new AreaCreateDto(
                "Bloco C",
                "Térreo"
        );
        Location location = new Location(
                UUID.randomUUID(),
                "IFSP Campus São Paulo",
                "R. Pedro Vicente, 625 - Canindé, São Paulo - SP, 01109-010"
        );
        Area area = new Area(
                "Bloco A",
                "Piso Superior",
                location
        );
        UUID locationId = location.getId();
        UUID areaId = area.getId();

        when(areaRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(area));
        when(areaRepository.existsByNameAndLocationIdAndIdNot(anyString(), any(UUID.class), any(UUID.class)))
                .thenReturn(Boolean.FALSE);
        when(areaRepository.save(any(Area.class)))
                .thenReturn(any(Area.class));

        Area updatedArea = areaService.update(locationId, areaId, areaCreateDto);

        verify(areaRepository, times(1)).save(any(Area.class));
        verify(auditService, times(1)).logAdminUpdate(
                any(ResourceName.class),
                anyString(),
                any(UUID.class)
        );
        assertThat(updatedArea).isNotNull();
        assertThat(updatedArea.getName()).isEqualTo(areaCreateDto.getName());
        assertThat(updatedArea.getReference()).isEqualTo(areaCreateDto.getReference());
        assertThat(updatedArea.getLocation()).isEqualTo(location);
    }

    @Test
    public void delete_ThrowsException_WhenAreaDoesNotExist() {
        UUID locationId = UUID.randomUUID();
        UUID areaId = UUID.randomUUID();

        when(areaRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> areaService.delete(locationId, areaId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void delete_ThrowsException_WhenLocationDoesNotExistInGivenArea() {
        Location location = new Location(
                UUID.randomUUID(),
                "IFSP Campus São Paulo",
                "R. Pedro Vicente, 625 - Canindé, São Paulo - SP, 01109-010"
        );
        Area area = new Area(
                "Bloco A",
                "Piso Superior",
                location
        );
        UUID locationId = UUID.randomUUID();
        UUID areaId = area.getId();

        when(areaRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(area));

        assertThatThrownBy(() -> areaService.delete(locationId, areaId))
                .isInstanceOf(ResourceNotExistsAssociationException.class);
    }

    @Test
    public void delete_ThrowsException_WhenSpaceExistsInGivenArea() {
        Location location = new Location(
                UUID.randomUUID(),
                "IFSP Campus São Paulo",
                "R. Pedro Vicente, 625 - Canindé, São Paulo - SP, 01109-010"
        );
        Area area = new Area(
                "Bloco A",
                "Piso Superior",
                location
        );
        UUID locationId = location.getId();
        UUID areaId = area.getId();

        when(areaRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(area));
        when(spaceRepository.existsByAreaId(any(UUID.class)))
                .thenReturn(Boolean.TRUE);

        assertThatThrownBy(() -> areaService.delete(locationId, areaId))
                .isInstanceOf(ResourceReferentialIntegrityException.class);
    }

    @Test
    public void delete_LogsDeletedArea_WhenSuccessful() {
        Location location = new Location(
                UUID.randomUUID(),
                "IFSP Campus São Paulo",
                "R. Pedro Vicente, 625 - Canindé, São Paulo - SP, 01109-010"
        );
        Area area = new Area(
                "Bloco A",
                "Piso Superior",
                location
        );
        UUID locationId = location.getId();
        UUID areaId = area.getId();

        when(areaRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(area));
        when(spaceRepository.existsByAreaId(any(UUID.class)))
                .thenReturn(Boolean.FALSE);

        areaService.delete(locationId, areaId);

        verify(areaRepository, times(1)).deleteById(any(UUID.class));
        verify(auditService, times(1)).logAdminDelete(
                any(ResourceName.class),
                any(UUID.class)
        );
    }

    @Test
    public void findById_ThrowsException_WhenAreaDoesNotExist() {
        UUID locationId = UUID.randomUUID();
        UUID areaId = UUID.randomUUID();

        when(areaRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> areaService.findById(locationId, areaId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}

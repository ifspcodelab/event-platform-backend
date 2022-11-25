package br.edu.ifsp.spo.eventos.eventplatformbackend.area;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.AuditService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.*;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.Location;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.LocationFactory;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.LocationRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.space.SpaceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
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
        AreaCreateDto areaCreateDto = getSampleAreaCreateDtoA();
        UUID locationId = UUID.randomUUID();

        when(locationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        //exception assertion without using catch throwable
//        assertThatThrownBy(() -> areaService.create(locationId, areaCreateDto))
//                .isInstanceOf(ResourceNotFoundException.class);

        ResourceNotFoundException exception = (ResourceNotFoundException) catchThrowable(() -> areaService.create(locationId, areaCreateDto));
        assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
        assertThat(exception.getResourceId()).isEqualTo(locationId.toString());
        assertThat(exception.getResourceName()).isEqualTo(ResourceName.LOCATION);
    }

    @Test
    public void create_ThrowsException_WhenAreaAlreadyExists() {
        AreaCreateDto areaCreateDto = getSampleAreaCreateDtoA();
        Location location = LocationFactory.sampleLocation();
        UUID locationId = location.getId();

        when(locationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(location));
        when(areaRepository.existsByNameAndLocationId(
                areaCreateDto.getName(),
                location.getId())
        ).thenReturn(true);

//        assertThatThrownBy(() -> areaService.create(locationId, areaCreateDto))
//                .isInstanceOf(ResourceAlreadyExistsException.class);
        ResourceAlreadyExistsException exception = (ResourceAlreadyExistsException) catchThrowable(() -> areaService.create(locationId, areaCreateDto));

        assertThat(exception).isInstanceOf(ResourceAlreadyExistsException.class);
        assertThat(exception.getResourceAttributeValue()).isEqualTo(areaCreateDto.getName());
        assertThat(exception.getResourceName()).isEqualTo(ResourceName.AREA);
    }

    @Test
    public void create_ReturnsArea_WhenSuccessful() {
        AreaCreateDto areaCreateDto = getSampleAreaCreateDtoA();
        Location location = LocationFactory.sampleLocation();
        UUID locationId = location.getId();
        Area area = AreaFactory.sampleArea();

        when(locationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(location));
        when(areaRepository.existsByNameAndLocationId(
                areaCreateDto.getName(),
                location.getId())
        ).thenReturn(false);
        when(areaRepository.save(any(Area.class)))
                .thenReturn(area);

        areaService.create(locationId, areaCreateDto);

        verify(areaRepository, times(1)).save(any(Area.class));
    }

    @Test
    public void update_ThrowsException_WhenAreaDoesNotExist() {
        AreaCreateDto areaCreateDto = getSampleAreaCreateDtoB();
        UUID locationId = UUID.randomUUID();
        UUID areaId = UUID.randomUUID();
        when(areaRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> areaService.update(locationId, areaId, areaCreateDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void update_ThrowsException_WhenLocationDoesNotExistInGivenArea() {
        AreaCreateDto areaCreateDto = getSampleAreaCreateDtoB();
        Area area = AreaFactory.sampleArea();
        UUID locationId = UUID.randomUUID();
        UUID areaId = area.getId();

        when(areaRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(area));

        assertThatThrownBy(() -> areaService.update(locationId, areaId, areaCreateDto))
                .isInstanceOf(ResourceNotExistsAssociationException.class);
    }

    @Test
    public void update_ThrowsException_WhenLocationAndGivenAreaAlreadyExist() {
        AreaCreateDto areaCreateDto = getSampleAreaCreateDtoB();

        Area area = AreaFactory.sampleArea();
        UUID locationId = area.getLocation().getId();
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
        AreaCreateDto areaCreateDto = getSampleAreaCreateDtoB();
        Area area = AreaFactory.sampleArea();
        UUID locationId = area.getLocation().getId();
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
        assertThat(updatedArea.getLocation()).isEqualTo(area.getLocation());
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
        Area area = AreaFactory.sampleArea();
        UUID locationId = UUID.randomUUID();
        UUID areaId = area.getId();

        when(areaRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(area));

        assertThatThrownBy(() -> areaService.delete(locationId, areaId))
                .isInstanceOf(ResourceNotExistsAssociationException.class);
    }

    @Test
    public void delete_ThrowsException_WhenSpaceExistsInGivenArea() {
        Area area = AreaFactory.sampleArea();
        UUID locationId = area.getLocation().getId();
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
        Area area = AreaFactory.sampleArea();
        UUID locationId = area.getLocation().getId();
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

    @Test
    public void findById_ThrowsException_WhenLocationDoesNotExistInGivenArea() {
        Area area = AreaFactory.sampleArea();
        UUID locationId = UUID.randomUUID();
        UUID areaId = area.getId();

        when(areaRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(area));

        assertThatThrownBy(() -> areaService.findById(locationId, areaId))
                .isInstanceOf(ResourceNotExistsAssociationException.class);
    }

    @Test
    public void findById_ReturnsArea_WhenSuccessful() {
        Area area = AreaFactory.sampleArea();
        UUID locationId = area.getLocation().getId();
        UUID areaId = area.getId();

        when(areaRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(area));

        Area foundArea = areaService.findById(locationId, areaId);

        assertThat(foundArea).isNotNull();
        assertThat(foundArea.getId()).isEqualTo(areaId);
        assertThat(foundArea.getLocation().getId()).isEqualTo(locationId);
    }

    @Test
    public void findAll_ThrowsException_WhenLocationDoesNotExist() {
        UUID locationId = UUID.randomUUID();

        when(locationRepository.existsById(any(UUID.class)))
                .thenReturn(Boolean.FALSE);

        assertThatThrownBy(() -> areaService.findAll(locationId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void findAll_ReturnsEmptyList_WhenAreasDoNotExistInGivenLocation() {
        UUID locationId = UUID.randomUUID();

        when(locationRepository.existsById(any(UUID.class)))
                .thenReturn(Boolean.TRUE);
        when(areaRepository.findAllByLocationId(any(UUID.class)))
                .thenReturn(List.of());

        List<Area> foundArea = areaService.findAll(locationId);

        assertThat(foundArea).hasSize(0);
    }

    @Test
    public void findAll_ReturnsAreaList_WhenSuccessful() {
        Location location = LocationFactory.sampleLocation();
        Area area = AreaFactory.sampleArea();
        UUID locationId = location.getId();

        when(locationRepository.existsById(any(UUID.class)))
                .thenReturn(Boolean.TRUE);
        when(areaRepository.findAllByLocationId(any(UUID.class)))
                .thenReturn(List.of(area));

        List<Area> foundArea = areaService.findAll(locationId);

        assertThat(foundArea).hasSize(1);
    }

    private AreaCreateDto getSampleAreaCreateDtoA() {
        return new AreaCreateDto(
                "Bloco A",
                "Piso Superior"
        );
    }

    private AreaCreateDto getSampleAreaCreateDtoB() {
        return new AreaCreateDto(
                "Bloco C",
                "Térreo"
        );
    }
}

package br.edu.ifsp.spo.eventos.eventplatformbackend.space;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.AuditService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.Area;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.AreaRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceAlreadyExistsException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceNotExistsAssociationException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceNotFoundException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.Location;
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
class SpaceServiceTest {
    @Mock
    private SpaceRepository spaceRepository;
    @Mock
    private AreaRepository areaRepository;
    @Mock
    private AuditService auditService;
    @InjectMocks
    private SpaceService spaceService;

    @Test
    public void spaceServiceShouldNotBeNull() {
        assertThat(spaceService).isNotNull();
    }

    @Test
    public void create_ResourceNotFoundExceptionException_WhenThereIsNoAreaPersisted() {
        SpaceCreateDto dto = new SpaceCreateDto("nome", 123, SpaceType.AUDITORIUM);

        UUID randomLocationUuid = UUID.randomUUID();
        UUID randomAreaUuid = UUID.randomUUID();

        assertThatThrownBy(() -> spaceService.create(randomLocationUuid, randomAreaUuid , dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void create_ThrowsException_WhenLocationDoesNotExistInGivenArea() {
        Location location = new Location(
                "nome",
                "endereco"
        );

        SpaceCreateDto dto = new SpaceCreateDto(
                "nome",
                123,
                SpaceType.AUDITORIUM
        );

        Area area = new Area(
                "nome",
                "referencia",
                location
        );

        UUID locationId = UUID.randomUUID();
        UUID areaId = area.getId();

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(area));

        assertThatThrownBy(() -> spaceService.create(locationId, areaId, dto))
                .isInstanceOf(ResourceNotExistsAssociationException.class);
    }

    @Test
    public void create_ThrowException_WhenThereIsAlreadyASpaceWithNameAndAreaId() {
        Location location = new Location(
                "nome",
                "endereco"
        );

        SpaceCreateDto dto = new SpaceCreateDto(
                "nome",
                123,
                SpaceType.AUDITORIUM
        );

        Area area = new Area(
                "nome",
                "referencia",
                location
        );

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(area));

        UUID locationId = location.getId();
        UUID areaLocationId = area.getLocation().getId();
        UUID areaId = area.getId();

        assertThat(locationId.equals(areaLocationId)).isTrue();

        when(spaceRepository.existsByNameAndAreaId(dto.getName(), areaId)).thenReturn(Boolean.TRUE);

        assertThatThrownBy(() -> spaceService.create(locationId, areaId, dto))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    public void create_ReturnsArea_WhenSuccessful() {
        Location location = new Location(
                "nome",
                "endereco"
        );

        SpaceCreateDto dto = new SpaceCreateDto(
                "nome",
                123,
                SpaceType.AUDITORIUM
        );

        Area area = new Area(
                "nome",
                "referencia",
                location
        );

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(area));

        UUID locationId = location.getId();
        UUID areaLocationId = area.getLocation().getId();
        UUID areaId = area.getId();

        assertThat(locationId.equals(areaLocationId)).isTrue();

        when(spaceRepository.existsByNameAndAreaId(dto.getName(), areaId)).thenReturn(Boolean.FALSE);

        String name = dto.getName();
        Integer capacity = dto.getCapacity();
        SpaceType type = dto.getType();

        Space space = new Space(name, capacity, type, area);

        when(spaceRepository.save(any(Space.class))).thenReturn(space);

        Space createdSpace = spaceService.create(locationId, areaId, dto);

        verify(spaceRepository, times(1)).save(any(Space.class));
        assertThat(createdSpace).isNotNull();
        assertThat(createdSpace.getId()).isEqualTo(space.getId());
        assertThat(createdSpace.getName()).isEqualTo(space.getName());
        assertThat(createdSpace.getArea()).isEqualTo(space.getArea());
        assertThat(createdSpace.getCapacity()).isEqualTo(space.getCapacity());
        assertThat(createdSpace.getType()).isEqualTo(space.getType());
    }

    @Test
    public void update_ThrowsException_WhenThereIsNoAreaPersisted() {
        SpaceCreateDto dto = new SpaceCreateDto(
                "nome",
                123,
                SpaceType.AUDITORIUM
        );

        Location location = new Location(
                "nome",
                "endereco"
        );

        Area area = new Area(
                "nome",
                "referencia",
                location
        );

        String name = dto.getName();
        Integer capacity = dto.getCapacity();
        SpaceType type = dto.getType();

        Space space = new Space(name, capacity, type, area);

        UUID locationId = location.getId();
        UUID randomAreaId = UUID.randomUUID();
        UUID spaceId = space.getId();

        assertThatThrownBy(() -> spaceService.update(locationId, randomAreaId , spaceId, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
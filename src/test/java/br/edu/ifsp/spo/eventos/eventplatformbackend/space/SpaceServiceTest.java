package br.edu.ifsp.spo.eventos.eventplatformbackend.space;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.AuditService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.Area;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.AreaFactory;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.AreaRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.*;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.Location;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.LocationFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
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
    public void create_ThrowsException_WhenThereIsNoAreaPersisted() {
        SpaceCreateDto spaceCreateDto = getSampleSpaceCreateDto();

        UUID randomLocationId = UUID.randomUUID();
        UUID randomAreaId = UUID.randomUUID();

        when(areaRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

//        Testing using assertThatThrownBy
//        assertThatThrownBy(() -> spaceService.create(randomLocationId, randomAreaId , spaceCreateDto))
//                .isInstanceOf(ResourceNotFoundException.class);

//        Testing using catchThrowable
        ResourceNotFoundException exception = (ResourceNotFoundException) catchThrowable(() -> spaceService.create(randomLocationId, randomAreaId, spaceCreateDto));
        assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
        assertThat(exception.getResourceId()).isEqualTo(randomAreaId.toString());
        assertThat(exception.getResourceName()).isEqualTo(ResourceName.AREA);
    }

    @Test
    public void create_ThrowsException_WhenLocationDoesNotExistInGivenArea() {
        Area area = AreaFactory.sampleArea();

        SpaceCreateDto spaceCreateDto = getSampleSpaceCreateDto();

        UUID randomLocationId = UUID.randomUUID();
        UUID areaId = area.getId();

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(area));

//        Testing using assertThatThrownBy
//        assertThatThrownBy(() -> spaceService.create(randomLocationId, areaId, spaceCreateDto))
//                .isInstanceOf(ResourceNotExistsAssociationException.class);


//        Testing using catchThrowable
        ResourceNotExistsAssociationException exception = (ResourceNotExistsAssociationException) catchThrowable(
                () -> spaceService.create(randomLocationId, areaId, spaceCreateDto)
        );
        assertThat(exception).isInstanceOf(ResourceNotExistsAssociationException.class);
        assertThat(exception.getPrimary()).isEqualTo(ResourceName.AREA);
        assertThat(exception.getRelated()).isEqualTo(ResourceName.LOCATION);
    }

    @Test
    public void create_ThrowException_WhenThereIsAlreadyASpaceWithNameAndAreaId() {
        Location location = LocationFactory.sampleLocationWithHardcodedUuid();

        Area area = AreaFactory.sampleAreaWithHardcodedLocationUuid();

        SpaceCreateDto spaceCreateDto = getSampleSpaceCreateDto();

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(area));

        UUID locationId = location.getId();
        UUID areaLocationId = area.getLocation().getId();
        UUID areaId = area.getId();

        assertThat(locationId.equals(areaLocationId)).isTrue();

        when(spaceRepository.existsByNameAndAreaId(spaceCreateDto.getName(), areaId)).thenReturn(Boolean.TRUE);

//        Testing using assertThatThrownBy
//        assertThatThrownBy(() -> spaceService.create(locationId, areaId, spaceCreateDto))
//                .isInstanceOf(ResourceAlreadyExistsException.class);

//        Testing using catchThrowable
        ResourceAlreadyExistsException exception = (ResourceAlreadyExistsException) catchException(
                () -> spaceService.create(locationId, areaId, spaceCreateDto)
        );
        assertThat(exception).isInstanceOf(ResourceAlreadyExistsException.class);
        assertThat(exception.getResourceAttribute()).isEqualTo("name");
        assertThat(exception.getResourceName()).isEqualTo(ResourceName.SPACE);
        assertThat(exception.getResourceAttributeValue()).isEqualTo("IVO");
    }

    @Test
    public void create_ReturnsArea_WhenSuccessful() {
        Location location = LocationFactory.sampleLocationWithHardcodedUuid();

        Area area = AreaFactory.sampleAreaWithHardcodedLocationUuid();

        SpaceCreateDto spaceCreateDto = getSampleSpaceCreateDto();

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(area));

        UUID locationId = location.getId();
        UUID areaLocationId = area.getLocation().getId();
        UUID areaId = area.getId();

        assertThat(locationId.equals(areaLocationId)).isTrue();

        when(spaceRepository.existsByNameAndAreaId(spaceCreateDto.getName(), areaId)).thenReturn(Boolean.FALSE);

        String name = spaceCreateDto.getName();
        Integer capacity = spaceCreateDto.getCapacity();
        SpaceType type = spaceCreateDto.getType();

        Space space = new Space(name, capacity, type, area);

        when(spaceRepository.save(any(Space.class))).thenReturn(space);

        Space createdSpace = spaceService.create(locationId, areaId, spaceCreateDto);

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
        Location location = LocationFactory.sampleLocationWithHardcodedUuid();

        Area area = AreaFactory.sampleAreaWithHardcodedLocationUuid();

        Space space = SpaceFactory.sampleSpace();

        SpaceCreateDto spaceCreateDto = getSampleSpaceCreateDto();

        when(areaRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        UUID locationId = location.getId();
        UUID randomAreaId = UUID.randomUUID();
        UUID spaceId = space.getId();

//        Testing using assertThatThrownBy
//        assertThatThrownBy(() -> spaceService.update(locationId, randomAreaId , spaceId, spaceCreateDto))
//                .isInstanceOf(ResourceNotFoundException.class);

//        Testing using catchThrowable
        ResourceNotFoundException exception = (ResourceNotFoundException) catchThrowable(
                () -> spaceService.update(locationId, randomAreaId , spaceId, spaceCreateDto)
        );
        assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
        assertThat(exception.getResourceId()).isEqualTo(randomAreaId.toString());
        assertThat(exception.getResourceName()).isEqualTo(ResourceName.AREA);
    }

    @Test
    public void update_ThrowsException_WhenLocationDoesNotExistInGivenArea() {
        Location location = LocationFactory.sampleLocationWithHardcodedUuid();

        Area area = AreaFactory.sampleAreaWithHardcodedLocationUuid();

        Space space = SpaceFactory.sampleSpace();

        SpaceCreateDto spaceCreateDto = getSampleSpaceCreateDto();

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(area));

        UUID randomLocationId = UUID.randomUUID();
        UUID areaId = area.getId();
        UUID spaceId = space.getId();

//        Testing using assertThatThrownBy
//        assertThatThrownBy(() -> spaceService.update(locationId, areaId, spaceId, spaceCreateDto))
//                .isInstanceOf(ResourceNotExistsAssociationException.class);

//        Testing using catchThrowable
        ResourceNotExistsAssociationException exception = (ResourceNotExistsAssociationException) catchThrowable(
                () -> spaceService.create(randomLocationId, areaId, spaceCreateDto)
        );
        assertThat(exception).isInstanceOf(ResourceNotExistsAssociationException.class);
        assertThat(exception.getPrimary()).isEqualTo(ResourceName.AREA);
        assertThat(exception.getRelated()).isEqualTo(ResourceName.LOCATION);
    }

    @Test
    public void update_ThrowsException_WhenThereIsNoSpacePersisted() {
        Location location = LocationFactory.sampleLocationWithHardcodedUuid();

        Area area = AreaFactory.sampleAreaWithHardcodedLocationUuid();

        Space space = SpaceFactory.sampleSpace();

        SpaceCreateDto spaceCreateDto = getSampleSpaceCreateDto();

        UUID locationId = location.getId();
        UUID areaLocationId = area.getLocation().getId();
        UUID areaId = area.getId();
        UUID randomSpaceId = UUID.randomUUID();

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(area));

        assertThat(locationId.equals(areaLocationId)).isTrue();

//        Testing using assertThatThrownBy
//        assertThatThrownBy(() -> spaceService.update(locationId, areaId, randomSpaceId, spaceCreateDto))
//                .isInstanceOf(ResourceNotFoundException.class);

//        Testing using catchThrowable
        ResourceNotFoundException exception = (ResourceNotFoundException) catchThrowable(
                () -> spaceService.update(locationId, areaId , randomSpaceId, spaceCreateDto)
        );
        assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
        assertThat(exception.getResourceId()).isEqualTo(randomSpaceId.toString());
        assertThat(exception.getResourceName()).isEqualTo(ResourceName.SPACE);
    }

    @Test
    public void update_ThrowsException_WhenAreaDoesNotExistInGivenSpace() {
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

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(area));

        when(spaceRepository.findById(any(UUID.class))).thenReturn(Optional.of(space));

        assertThatThrownBy(() -> spaceService.update(locationId, randomAreaId, spaceId, dto))
                .isInstanceOf(ResourceReferentialIntegrityException.class);
    }

    @Test
    public void update_ThrowsException_WhenThereIsAlreadyASpaceWithNameAreaIdAndNotSpaceId() {
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
        UUID areaId = area.getId();
        UUID spaceId = space.getId();

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(area));

        when(spaceRepository.findById(any(UUID.class))).thenReturn(Optional.of(space));

        UUID spaceAreaId = space.getArea().getId();

        assertThat(areaId.equals(spaceAreaId)).isTrue();

        when(spaceRepository.existsByNameAndAreaIdAndIdNot(any(String.class), any(UUID.class), any(UUID.class))).thenReturn(Boolean.TRUE);

        assertThatThrownBy(() -> spaceService.update(locationId, areaId, spaceId, dto))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    public void update_ReturnsSpace_WhenSuccessful() {
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

        Space space = new Space(name, capacity, type, area); //Old space instantiated

        UUID locationId = location.getId();
        UUID areaId = area.getId();
        UUID spaceId = space.getId();

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(area));

        when(spaceRepository.findById(any(UUID.class))).thenReturn(Optional.of(space));

        UUID spaceAreaId = space.getArea().getId();

        assertThat(areaId.equals(spaceAreaId)).isTrue();

        when(spaceRepository.existsByNameAndAreaIdAndIdNot(any(String.class), any(UUID.class), any(UUID.class))).thenReturn(Boolean.FALSE);

        SpaceCreateDto updateDto = new SpaceCreateDto(
                "novo nome",
                456,
                SpaceType.CLASSROOM
        );

        space.setName(updateDto.getName());
        space.setCapacity(updateDto.getCapacity());
        space.setType(updateDto.getType());

        Space updatedSpace = spaceService.update(locationId, areaId, spaceId, updateDto);

        verify(spaceRepository, times(1)).save(any(Space.class));
        assertThat(updatedSpace).isNotNull();
        assertThat(updatedSpace.getName().equals(space.getName())).isTrue();
        assertThat(updatedSpace.getCapacity().equals(space.getCapacity())).isTrue();
        assertThat(updatedSpace.getType().equals(space.getType())).isTrue();
        assertThat(updatedSpace.getArea().equals(space.getArea())).isTrue();

        verify(auditService, times(1)).logAdminUpdate(any(ResourceName.class), any(String.class), any(UUID.class));
    }

    @Test
    public void delete_ThrowsException_WhenThereIsNoSpacePersisted() {
        Location location = new Location(
                "nome",
                "endereco"
        );

        Area area = new Area(
                "nome",
                "referencia",
                location
        );

        UUID locationId = location.getId();
        UUID areaId = area.getId();
        UUID randomSpaceId = UUID.randomUUID();

        assertThatThrownBy(() -> spaceService.delete(locationId, areaId, randomSpaceId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void delete_ThrowsException_WhenThereIsNoAreaPersisted() {
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

        String spaceName = dto.getName();
        Integer spaceCapacity = dto.getCapacity();
        SpaceType spaceType = dto.getType();

        Space space = new Space(spaceName, spaceCapacity, spaceType, area);

        UUID locationId = location.getId();
        UUID randomAreaId = UUID.randomUUID();
        UUID spaceId = space.getId();

        when(spaceRepository.findById(any(UUID.class))).thenReturn(Optional.of(space));

        assertThatThrownBy(() -> spaceService.delete(locationId, randomAreaId, spaceId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void delete_ThrowsException_WhenLocationDoesNotExistInGivenArea() {
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

        String spaceName = dto.getName();
        Integer spaceCapacity = dto.getCapacity();
        SpaceType spaceType = dto.getType();

        Space space = new Space(spaceName, spaceCapacity, spaceType, area);

        UUID randomLocationId = UUID.randomUUID();
        UUID areaId = area.getId();
        UUID spaceId = space.getId();

        when(spaceRepository.findById(any(UUID.class))).thenReturn(Optional.of(space));

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(area));

        assertThatThrownBy(() -> spaceService.delete(randomLocationId, areaId, spaceId))
                .isInstanceOf(ResourceNotExistsAssociationException.class);
    }

    @Test
    public void delete_ThrowsException_WhenAreaDoesNotExistInGivenSpace() {
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

        String spaceName = dto.getName();
        Integer spaceCapacity = dto.getCapacity();
        SpaceType spaceType = dto.getType();

        Space space = new Space(spaceName, spaceCapacity, spaceType, area);

        UUID locationId = location.getId();
        UUID randomAreaId = UUID.randomUUID();
        UUID spaceId = space.getId();

        when(spaceRepository.findById(any(UUID.class))).thenReturn(Optional.of(space));

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(area));

        assertThat(location.getId().equals(area.getLocation().getId())).isTrue();

        assertThatThrownBy(() -> spaceService.delete(locationId, randomAreaId, spaceId))
                .isInstanceOf(ResourceReferentialIntegrityException.class);
    }

    @Test
    public void delete_ReturnVoid_WhenSpaceIsDeleted() {
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

        String spaceName = dto.getName();
        Integer spaceCapacity = dto.getCapacity();
        SpaceType spaceType = dto.getType();

        Space space = new Space(spaceName, spaceCapacity, spaceType, area);

        UUID locationId = location.getId();
        UUID areaId = area.getId();
        UUID spaceId = space.getId();

        when(spaceRepository.findById(any(UUID.class))).thenReturn(Optional.of(space));

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(area));

        assertThat(location.getId().equals(area.getLocation().getId())).isTrue();

        assertThat(area.getId().equals(space.getArea().getId())).isTrue();

        spaceService.delete(locationId, areaId, spaceId);

        verify(spaceRepository, times(1)).deleteById(any(UUID.class));

        verify(auditService, times(1)).logAdminDelete(any(ResourceName.class), any(UUID.class));
    }

    private SpaceCreateDto getSampleSpaceCreateDto() {
        return new SpaceCreateDto(
                "IVO",
                100,
                SpaceType.AUDITORIUM
        );
    }

    private SpaceCreateDto getSampleSpaceCreateDtoUpdate() {
        return new SpaceCreateDto(
                "SP1",
                100,
                SpaceType.AUDITORIUM
        );
    }



}
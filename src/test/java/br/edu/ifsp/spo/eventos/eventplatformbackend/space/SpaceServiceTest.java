package br.edu.ifsp.spo.eventos.eventplatformbackend.space;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.AuditService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.Area;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.AreaFactory;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.AreaRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.*;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.Location;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.LocationFactory;
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
class SpaceServiceTest {
    @Mock
    private SpaceRepository spaceRepository;
    @Mock
    private AreaRepository areaRepository;
    @Mock
    private AuditService auditService;
    @InjectMocks
    private SpaceService spaceService;

    private Location location;
    private Location locationWithHardCodedUuid;
    private Area area;
    private Area areaWithHardCodedUuid;
    private Space space;
    private Space spaceWithHardCodedUuid;
    private SpaceCreateDto spaceCreateDto;

    @BeforeEach
    public void setUp() {
        location = LocationFactory.sampleLocation();
        locationWithHardCodedUuid = LocationFactory.sampleLocationWithHardcodedUuid();
        area = AreaFactory.sampleArea();
        areaWithHardCodedUuid = AreaFactory.sampleAreaWithHardcodedLocationUuid();
        space = SpaceFactory.sampleSpace();
        spaceWithHardCodedUuid = SpaceFactory.sampleSpaceWithHardcodedUuid();
        spaceCreateDto = getSampleSpaceCreateDto();
    }

    @Test
    public void spaceServiceShouldNotBeNull() {
        assertThat(spaceService).isNotNull();
    }

    @Test
    public void create_ThrowsException_WhenThereIsNoAreaPersisted() {
        UUID locationId = location.getId();
        UUID areaId = area.getId();

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

//        Testing using assertThatThrownBy
//        assertThatThrownBy(() -> spaceService.create(locationId, areaId , spaceCreateDto))
//                .isInstanceOf(ResourceNotFoundException.class);

//        Testing using catchThrowable
        ResourceNotFoundException exception = (ResourceNotFoundException) catchThrowable(
                () -> spaceService.create(locationId, areaId, spaceCreateDto)
        );
        assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
        assertThat(exception.getResourceId()).isEqualTo(areaId.toString());
        assertThat(exception.getResourceName()).isEqualTo(ResourceName.AREA);
    }

    @Test
    public void create_ThrowsException_WhenLocationDoesNotExistInGivenArea() {
        UUID locationId = location.getId();
        UUID areaId = area.getId();
        UUID areaLocationId = area.getLocation().getId();

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(area));

        assertThat(locationId.equals(areaLocationId)).isFalse();

//        Testing using assertThatThrownBy
//        assertThatThrownBy(() -> spaceService.create(locationId, areaId, spaceCreateDto))
//                .isInstanceOf(ResourceNotExistsAssociationException.class);


//        Testing using catchThrowable
        ResourceNotExistsAssociationException exception = (ResourceNotExistsAssociationException) catchThrowable(
                () -> spaceService.create(locationId, areaId, spaceCreateDto)
        );
        assertThat(exception).isInstanceOf(ResourceNotExistsAssociationException.class);
        assertThat(exception.getPrimary()).isEqualTo(ResourceName.AREA);
        assertThat(exception.getRelated()).isEqualTo(ResourceName.LOCATION);
    }

    @Test
    public void create_ThrowException_WhenThereIsAlreadyASpaceWithNameAndAreaId() {
        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(areaWithHardCodedUuid));

        UUID locationId = locationWithHardCodedUuid.getId();
        UUID areaLocationId = areaWithHardCodedUuid.getLocation().getId();
        UUID areaId = areaWithHardCodedUuid.getId();

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
        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(areaWithHardCodedUuid));

        UUID locationId = locationWithHardCodedUuid.getId();
        UUID areaLocationId = areaWithHardCodedUuid.getLocation().getId();
        UUID areaId = areaWithHardCodedUuid.getId();

        assertThat(locationId.equals(areaLocationId)).isTrue();

        when(spaceRepository.existsByNameAndAreaId(spaceCreateDto.getName(), areaId)).thenReturn(Boolean.FALSE);

        Space space = SpaceFactory.sampleSpace();

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
        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        UUID locationId = location.getId();
        UUID areaId = area.getId();
        UUID spaceId = space.getId();

//        Testing using assertThatThrownBy
//        assertThatThrownBy(() -> spaceService.update(locationId, areaId , spaceId, spaceCreateDto))
//                .isInstanceOf(ResourceNotFoundException.class);

//        Testing using catchThrowable
        ResourceNotFoundException exception = (ResourceNotFoundException) catchThrowable(
                () -> spaceService.update(locationId, areaId , spaceId, spaceCreateDto)
        );
        assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
        assertThat(exception.getResourceId()).isEqualTo(areaId.toString());
        assertThat(exception.getResourceName()).isEqualTo(ResourceName.AREA);
    }

    @Test
    public void update_ThrowsException_WhenLocationDoesNotExistInGivenArea() {
        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(area));

        UUID locationId = location.getId();
        UUID areaLocationId = area.getLocation().getId();
        UUID areaId = area.getId();
        UUID spaceId = space.getId();

        assertThat(locationId.equals(areaLocationId)).isFalse();

//        Testing using assertThatThrownBy
//        assertThatThrownBy(() -> spaceService.update(locationId, areaId, spaceId, spaceCreateDto))
//                .isInstanceOf(ResourceNotExistsAssociationException.class);

//        Testing using catchThrowable
        ResourceNotExistsAssociationException exception = (ResourceNotExistsAssociationException) catchThrowable(
                () -> spaceService.update(locationId, areaId, spaceId, spaceCreateDto)
        );
        assertThat(exception).isInstanceOf(ResourceNotExistsAssociationException.class);
        assertThat(exception.getPrimary()).isEqualTo(ResourceName.AREA);
        assertThat(exception.getRelated()).isEqualTo(ResourceName.LOCATION);
    }

    @Test
    public void update_ThrowsException_WhenThereIsNoSpacePersisted() {
        UUID locationId = locationWithHardCodedUuid.getId();
        UUID areaLocationId = areaWithHardCodedUuid.getLocation().getId();
        UUID areaId = areaWithHardCodedUuid.getId();
        UUID spaceId = space.getId();

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(areaWithHardCodedUuid));

        assertThat(locationId.equals(areaLocationId)).isTrue();

        when(spaceRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

//        Testing using assertThatThrownBy
//        assertThatThrownBy(() -> spaceService.update(locationId, areaId, spaceId, spaceCreateDto))
//                .isInstanceOf(ResourceNotFoundException.class);

//        Testing using catchThrowable
        ResourceNotFoundException exception = (ResourceNotFoundException) catchThrowable(
                () -> spaceService.update(locationId, areaId , spaceId, spaceCreateDto)
        );
        assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
        assertThat(exception.getResourceId()).isEqualTo(spaceId.toString());
        assertThat(exception.getResourceName()).isEqualTo(ResourceName.SPACE);
    }

    @Test
    public void update_ThrowsException_WhenAreaDoesNotExistInGivenSpace() {
        UUID locationId = locationWithHardCodedUuid.getId();
        UUID areaLocationId = areaWithHardCodedUuid.getLocation().getId();
        UUID areaId = areaWithHardCodedUuid.getId();
        UUID spaceAreaId = space.getArea().getId();
        UUID spaceId = space.getId();

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(areaWithHardCodedUuid));

        assertThat(locationId.equals(areaLocationId)).isTrue();

        when(spaceRepository.findById(any(UUID.class))).thenReturn(Optional.of(space));

        assertThat(areaId.equals(spaceAreaId)).isFalse();

//        Testing using assertThatThrownBy
//        assertThatThrownBy(() -> spaceService.update(locationId, areaId, spaceId, spaceCreateDto))
//                .isInstanceOf(ResourceReferentialIntegrityException.class);

//        Testing using catchThrowable
        ResourceReferentialIntegrityException exception = (ResourceReferentialIntegrityException) catchThrowable(
                () -> spaceService.update(locationId, areaId, spaceId, spaceCreateDto)
        );
        assertThat(exception).isInstanceOf(ResourceReferentialIntegrityException.class);
        assertThat(exception.getPrimary()).isEqualTo(ResourceName.SPACE);
        assertThat(exception.getRelated()).isEqualTo(ResourceName.AREA);
    }

    @Test
    public void update_ThrowsException_WhenThereIsAlreadyASpaceWithNameAreaIdAndNotSpaceId() {
        UUID locationId = locationWithHardCodedUuid.getId();
        UUID areaLocationId = areaWithHardCodedUuid.getLocation().getId();
        UUID areaId = areaWithHardCodedUuid.getId();
        UUID spaceAreaId = spaceWithHardCodedUuid.getArea().getId();
        UUID spaceId = spaceWithHardCodedUuid.getId();

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(areaWithHardCodedUuid));

        assertThat(locationId.equals(areaLocationId)).isTrue();

        when(spaceRepository.findById(any(UUID.class))).thenReturn(Optional.of(spaceWithHardCodedUuid));

        assertThat(areaId.equals(spaceAreaId)).isTrue();

        when(spaceRepository.existsByNameAndAreaIdAndIdNot(any(String.class), any(UUID.class), any(UUID.class))).thenReturn(Boolean.TRUE);

//        Testing using assertThatThrownBy
//        assertThatThrownBy(() -> spaceService.update(locationId, areaId, spaceId, spaceCreateDto))
//                .isInstanceOf(ResourceAlreadyExistsException.class);

//        Testing using catchThrowable
        ResourceAlreadyExistsException exception = (ResourceAlreadyExistsException) catchException(
                () -> spaceService.update(locationId, areaId, spaceId, spaceCreateDto)
        );
        assertThat(exception).isInstanceOf(ResourceAlreadyExistsException.class);
        assertThat(exception.getResourceAttribute()).isEqualTo("name");
        assertThat(exception.getResourceName()).isEqualTo(ResourceName.SPACE);
        assertThat(exception.getResourceAttributeValue()).isEqualTo("IVO");
    }

    @Test
    public void update_ReturnsSpace_WhenSuccessful() {
        UUID locationId = locationWithHardCodedUuid.getId();
        UUID areaLocationId = areaWithHardCodedUuid.getLocation().getId();
        UUID areaId = areaWithHardCodedUuid.getId();
        UUID spaceAreaId = spaceWithHardCodedUuid.getArea().getId();
        UUID spaceId = spaceWithHardCodedUuid.getId();

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(areaWithHardCodedUuid));

        assertThat(locationId.equals(areaLocationId)).isTrue();

        when(spaceRepository.findById(any(UUID.class))).thenReturn(Optional.of(spaceWithHardCodedUuid));

        assertThat(areaId.equals(spaceAreaId)).isTrue();

        when(spaceRepository.existsByNameAndAreaIdAndIdNot(any(String.class), any(UUID.class), any(UUID.class))).thenReturn(Boolean.FALSE);

        SpaceCreateDto updateDto = getSampleSpaceCreateDtoUpdate();

        spaceWithHardCodedUuid.setName(updateDto.getName());
        spaceWithHardCodedUuid.setCapacity(updateDto.getCapacity());
        spaceWithHardCodedUuid.setType(updateDto.getType());

        Space updatedSpace = spaceService.update(locationId, areaId, spaceId, updateDto);

        verify(spaceRepository, times(1)).save(any(Space.class));
        assertThat(updatedSpace).isNotNull();
        assertThat(updatedSpace.getName().equals(spaceWithHardCodedUuid.getName())).isTrue();
        assertThat(updatedSpace.getCapacity().equals(spaceWithHardCodedUuid.getCapacity())).isTrue();
        assertThat(updatedSpace.getType().equals(spaceWithHardCodedUuid.getType())).isTrue();
        assertThat(updatedSpace.getArea().equals(spaceWithHardCodedUuid.getArea())).isTrue();

        verify(auditService, times(1)).logAdminUpdate(any(ResourceName.class), any(String.class), any(UUID.class));
    }

    @Test
    public void delete_ThrowsException_WhenThereIsNoSpacePersisted() {
        UUID locationId = location.getId();
        UUID areaId = area.getId();
        UUID spaceId = space.getId();

        when(spaceRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

//        Testing using assertThatThrownBy
//        assertThatThrownBy(() -> spaceService.delete(locationId, areaId, spaceId))
//                .isInstanceOf(ResourceNotFoundException.class);

//        Testing using catchThrowable
        ResourceNotFoundException exception = (ResourceNotFoundException) catchThrowable(
                () -> spaceService.delete(locationId, areaId, spaceId)
        );
        assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
        assertThat(exception.getResourceId()).isEqualTo(spaceId.toString());
        assertThat(exception.getResourceName()).isEqualTo(ResourceName.SPACE);
    }

    @Test
    public void delete_ThrowsException_WhenThereIsNoAreaPersisted() {
        UUID locationId = location.getId();
        UUID areaId = area.getId();
        UUID spaceId = space.getId();

        when(spaceRepository.findById(any(UUID.class))).thenReturn(Optional.of(space));

//        Testing using assertThatThrownBy
//        assertThatThrownBy(() -> spaceService.delete(locationId, areaId, spaceId))
//                .isInstanceOf(ResourceNotFoundException.class);

//        Testing using catchThrowable
        ResourceNotFoundException exception = (ResourceNotFoundException) catchThrowable(
                () -> spaceService.delete(locationId, areaId, spaceId)
        );
        assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
        assertThat(exception.getResourceId()).isEqualTo(areaId.toString());
        assertThat(exception.getResourceName()).isEqualTo(ResourceName.AREA);
    }

    @Test
    public void delete_ThrowsException_WhenLocationDoesNotExistInGivenArea() {
        UUID locationId = location.getId();
        UUID areaLocationId = area.getLocation().getId();
        UUID areaId = area.getId();
        UUID spaceId = space.getId();

        when(spaceRepository.findById(any(UUID.class))).thenReturn(Optional.of(space));

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(area));

        assertThat(locationId.equals(areaLocationId)).isFalse();

//        Testing using assertThatThrownBy
//        assertThatThrownBy(() -> spaceService.delete(locationId, areaId, spaceId))
//                .isInstanceOf(ResourceNotExistsAssociationException.class);

//        Testing using catchThrowable
        ResourceNotExistsAssociationException exception = (ResourceNotExistsAssociationException) catchThrowable(
                () -> spaceService.delete(locationId, areaId, spaceId)
        );
        assertThat(exception).isInstanceOf(ResourceNotExistsAssociationException.class);
        assertThat(exception.getPrimary()).isEqualTo(ResourceName.AREA);
        assertThat(exception.getRelated()).isEqualTo(ResourceName.LOCATION);
    }

    @Test
    public void delete_ThrowsException_WhenAreaDoesNotExistInGivenSpace() {
        UUID locationId = locationWithHardCodedUuid.getId();
        UUID areaLocationId = areaWithHardCodedUuid.getLocation().getId();
        UUID areaId = areaWithHardCodedUuid.getId();
        UUID spaceAreaId = space.getArea().getId();
        UUID spaceId = space.getId();

        when(spaceRepository.findById(any(UUID.class))).thenReturn(Optional.of(space));

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(areaWithHardCodedUuid));

        assertThat(locationId.equals(areaLocationId)).isTrue();

        assertThat(areaId.equals(spaceAreaId)).isFalse();

//        Testing using assertThatThrownBy
//        assertThatThrownBy(() -> spaceService.delete(locationId, areaId, spaceId))
//                .isInstanceOf(ResourceReferentialIntegrityException.class);

//        Testing using catchThrowable
        ResourceReferentialIntegrityException exception = (ResourceReferentialIntegrityException) catchThrowable(
                () -> spaceService.delete(locationId, areaId, spaceId)
        );
        assertThat(exception).isInstanceOf(ResourceReferentialIntegrityException.class);
        assertThat(exception.getPrimary()).isEqualTo(ResourceName.SPACE);
        assertThat(exception.getRelated()).isEqualTo(ResourceName.AREA);
    }

    @Test
    public void delete_ReturnVoid_WhenSuccessful() {
        UUID locationId = locationWithHardCodedUuid.getId();
        UUID areaLocationId = areaWithHardCodedUuid.getLocation().getId();
        UUID areaId = areaWithHardCodedUuid.getId();
        UUID spaceAreaId = spaceWithHardCodedUuid.getArea().getId();
        UUID spaceId = spaceWithHardCodedUuid.getId();

        when(spaceRepository.findById(any(UUID.class))).thenReturn(Optional.of(spaceWithHardCodedUuid));

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(areaWithHardCodedUuid));

        assertThat(locationId.equals(areaLocationId)).isTrue();

        assertThat(areaId.equals(spaceAreaId)).isTrue();

        spaceService.delete(locationId, areaId, spaceId);

        verify(spaceRepository, times(1)).deleteById(any(UUID.class));

        verify(auditService, times(1)).logAdminDelete(any(ResourceName.class), any(UUID.class));
    }

    @Test
    public void findAll_ThrowsException_WhenThereIsNoAreaPersisted() {
        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        UUID locationId = location.getId();
        UUID areaId = area.getId();

//        Testing using assertThatThrownBy
//        assertThatThrownBy(() -> spaceService.findAll(locationId, areaId))
//                .isInstanceOf(ResourceNotFoundException.class);

//        Testing using catchThrowable
        ResourceNotFoundException exception = (ResourceNotFoundException) catchThrowable(
                () -> spaceService.findAll(locationId, areaId)
        );
        assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
        assertThat(exception.getResourceId()).isEqualTo(areaId.toString());
        assertThat(exception.getResourceName()).isEqualTo(ResourceName.AREA);
    }

    @Test
    public void findAll_ThrowsException_WhenLocationDoesNotExistInGivenArea() {
        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(area));

        UUID locationId = location.getId();
        UUID areaLocationId = area.getLocation().getId();
        UUID areaId = area.getId();

        assertThat(locationId.equals(areaLocationId)).isFalse();

//        Testing using assertThatThrownBy
//        assertThatThrownBy(() -> spaceService.findAll(locationId, areaId))
//                .isInstanceOf(ResourceNotExistsAssociationException.class);

//        Testing using catchThrowable
        ResourceNotExistsAssociationException exception = (ResourceNotExistsAssociationException) catchThrowable(
                () -> spaceService.findAll(locationId, areaId));
        assertThat(exception).isInstanceOf(ResourceNotExistsAssociationException.class);
        assertThat(exception.getPrimary()).isEqualTo(ResourceName.AREA);
        assertThat(exception.getRelated()).isEqualTo(ResourceName.LOCATION);
    }

    @Test
    public void findAll_ReturnListOfSpaces_WhenSuccessful() {
        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(areaWithHardCodedUuid));

        UUID locationId = locationWithHardCodedUuid.getId();
        UUID areaLocationId = areaWithHardCodedUuid.getLocation().getId();
        UUID areaId = areaWithHardCodedUuid.getId();

        assertThat(locationId.equals(areaLocationId)).isTrue();

        Space space = SpaceFactory.sampleSpace();

        when(spaceRepository.findAllByAreaId(any(UUID.class))).thenReturn(List.of(space));

        List<Space> spaces = spaceService.findAll(locationId, areaId);

        assertThat(spaces.size()).isEqualTo(1);
    }

    @Test
    public void findAll_ReturnEmptyList_WhenSuccessful() {
        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(areaWithHardCodedUuid));

        UUID locationId = locationWithHardCodedUuid.getId();
        UUID areaLocationId = areaWithHardCodedUuid.getLocation().getId();
        UUID areaId = areaWithHardCodedUuid.getId();

        assertThat(locationId.equals(areaLocationId)).isTrue();

        when(spaceRepository.findAllByAreaId(any(UUID.class))).thenReturn(Collections.emptyList());

        List<Space> spaces = spaceService.findAll(locationId, areaId);

        assertThat(spaces.size()).isEqualTo(0);
    }

    @Test
    public void findById_ThrowsException_WhenThereIsNoSpacePersisted() {
        when(spaceRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        UUID locationId = location.getId();
        UUID areaId = area.getId();
        UUID spaceId = space.getId();

//        Testing using assertThatThrownBy
//        assertThatThrownBy(() -> spaceService.findById(locationId, areaId, spaceId))
//                .isInstanceOf(ResourceNotFoundException.class);

//        Testing using catchThrowable
        ResourceNotFoundException exception = (ResourceNotFoundException) catchThrowable(
                () -> spaceService.findById(locationId, areaId, spaceId)
        );
        assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
        assertThat(exception.getResourceId()).isEqualTo(spaceId.toString());
        assertThat(exception.getResourceName()).isEqualTo(ResourceName.SPACE);
    }

    @Test
    public void findById_ThrowsException_WhenThereIsNoAreaPersisted() {
        when(spaceRepository.findById(any(UUID.class))).thenReturn(Optional.of(space));

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        UUID locationId = location.getId();
        UUID areaId = area.getId();
        UUID spaceId = space.getId();

//        Testing using assertThatThrownBy
//        assertThatThrownBy(() -> spaceService.findById(locationId, areaId, spaceId))
//                .isInstanceOf(ResourceNotFoundException.class);

//        Testing using catchThrowable
        ResourceNotFoundException exception = (ResourceNotFoundException) catchThrowable(
                () -> spaceService.findById(locationId, areaId, spaceId)
        );
        assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
        assertThat(exception.getResourceId()).isEqualTo(areaId.toString());
        assertThat(exception.getResourceName()).isEqualTo(ResourceName.AREA);
    }

    @Test
    public void findById_ThrowsException_WhenLocationDoesNotExistInGivenArea() {
        when(spaceRepository.findById(any(UUID.class))).thenReturn(Optional.of(space));

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(area));

        UUID locationId = location.getId();
        UUID areaLocationId = area.getLocation().getId();
        UUID areaId = area.getId();
        UUID spaceId = space.getId();

        assertThat(locationId.equals(areaLocationId)).isFalse();

//        Testing using assertThatThrownBy
//        assertThatThrownBy(() -> spaceService.findById(locationId, areaId, spaceId))
//                .isInstanceOf(ResourceNotExistsAssociationException.class);

//        Testing using catchThrowable
        ResourceNotExistsAssociationException exception = (ResourceNotExistsAssociationException) catchThrowable(
                () -> spaceService.findById(locationId, areaId, spaceId)
        );
        assertThat(exception).isInstanceOf(ResourceNotExistsAssociationException.class);
        assertThat(exception.getPrimary()).isEqualTo(ResourceName.AREA);
        assertThat(exception.getRelated()).isEqualTo(ResourceName.LOCATION);
    }

    @Test
    public void findById_ThrowsException_WhenAreaDoesNotExistInGivenSpace() {
        when(spaceRepository.findById(any(UUID.class))).thenReturn(Optional.of(space));

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(areaWithHardCodedUuid));

        UUID locationId = locationWithHardCodedUuid.getId();
        UUID areaLocationId = areaWithHardCodedUuid.getLocation().getId();
        UUID areaId = areaWithHardCodedUuid.getId();
        UUID spaceAreaId = space.getArea().getId();
        UUID spaceId = space.getId();

        assertThat(locationId.equals(areaLocationId)).isTrue();

        assertThat(areaId.equals(spaceAreaId)).isFalse();

//        Testing using assertThatThrownBy
//        assertThatThrownBy(() -> spaceService.findById(locationId, areaId, spaceId))
//                .isInstanceOf(ResourceReferentialIntegrityException.class);

//        Testing using catchThrowable
        ResourceReferentialIntegrityException exception = (ResourceReferentialIntegrityException) catchThrowable(
                () -> spaceService.findById(locationId, areaId, spaceId)
        );
        assertThat(exception).isInstanceOf(ResourceReferentialIntegrityException.class);
        assertThat(exception.getPrimary()).isEqualTo(ResourceName.SPACE);
        assertThat(exception.getRelated()).isEqualTo(ResourceName.AREA);
    }

    @Test
    public void findById_ReturnsSpace_WhenSuccessful() {
        when(spaceRepository.findById(any(UUID.class))).thenReturn(Optional.of(spaceWithHardCodedUuid));

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(areaWithHardCodedUuid));

        UUID locationId = locationWithHardCodedUuid.getId();
        UUID areaLocationId = areaWithHardCodedUuid.getLocation().getId();
        UUID areaId = areaWithHardCodedUuid.getId();
        UUID spaceAreaId = spaceWithHardCodedUuid.getArea().getId();
        UUID spaceId = spaceWithHardCodedUuid.getId();

        assertThat(locationId.equals(areaLocationId)).isTrue();

        assertThat(areaId.equals(spaceAreaId)).isTrue();

        Space spaceFound = spaceService.findById(locationId, areaId, spaceId);

        assertThat(spaceFound).isNotNull();
        assertThat(spaceFound.getId().equals(spaceId)).isTrue();
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
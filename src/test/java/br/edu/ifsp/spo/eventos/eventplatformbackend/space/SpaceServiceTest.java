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

    @Test
    public void spaceServiceShouldNotBeNull() {
        assertThat(spaceService).isNotNull();
    }

    @Test
    public void create_ThrowsException_WhenThereIsNoAreaPersisted() {
        Location location = LocationFactory.sampleLocation();

        Area area = AreaFactory.sampleArea();

        SpaceCreateDto spaceCreateDto = getSampleSpaceCreateDto();

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
        Location location = LocationFactory.sampleLocation();

        Area area = AreaFactory.sampleArea();

        SpaceCreateDto spaceCreateDto = getSampleSpaceCreateDto();

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
        Location location = LocationFactory.sampleLocation();

        Area area = AreaFactory.sampleArea();

        Space space = SpaceFactory.sampleSpace();

        SpaceCreateDto spaceCreateDto = getSampleSpaceCreateDto();

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
        Location location = LocationFactory.sampleLocation();

        Area area = AreaFactory.sampleArea();

        Space space = SpaceFactory.sampleSpace();

        SpaceCreateDto spaceCreateDto = getSampleSpaceCreateDto();

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
        Location location = LocationFactory.sampleLocationWithHardcodedUuid();

        Area area = AreaFactory.sampleAreaWithHardcodedLocationUuid();

        Space space = SpaceFactory.sampleSpace();

        SpaceCreateDto spaceCreateDto = getSampleSpaceCreateDto();

        UUID locationId = location.getId();
        UUID areaLocationId = area.getLocation().getId();
        UUID areaId = area.getId();
        UUID spaceId = space.getId();

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(area));

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
        Location location = LocationFactory.sampleLocationWithHardcodedUuid();

        Area area = AreaFactory.sampleAreaWithHardcodedLocationUuid();

        Space space = SpaceFactory.sampleSpace();

        SpaceCreateDto spaceCreateDto = getSampleSpaceCreateDto();

        UUID locationId = location.getId();
        UUID areaLocationId = area.getLocation().getId();
        UUID areaId = area.getId();
        UUID spaceAreaId = space.getArea().getId();
        UUID spaceId = space.getId();

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(area));

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
        Location location = LocationFactory.sampleLocationWithHardcodedUuid();

        Area area = AreaFactory.sampleAreaWithHardcodedLocationUuid();

        Space space = SpaceFactory.sampleSpaceWithHardcodedUuid();

        SpaceCreateDto spaceCreateDto = getSampleSpaceCreateDto();

        UUID locationId = location.getId();
        UUID areaLocationId = area.getLocation().getId();
        UUID areaId = area.getId();
        UUID spaceAreaId = space.getArea().getId();
        UUID spaceId = space.getId();

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(area));

        assertThat(locationId.equals(areaLocationId)).isTrue();

        when(spaceRepository.findById(any(UUID.class))).thenReturn(Optional.of(space));

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
        Location location = LocationFactory.sampleLocationWithHardcodedUuid();

        Area area = AreaFactory.sampleAreaWithHardcodedLocationUuid();

        Space space = SpaceFactory.sampleSpaceWithHardcodedUuid();

        UUID locationId = location.getId();
        UUID areaLocationId = area.getLocation().getId();
        UUID areaId = area.getId();
        UUID spaceAreaId = space.getArea().getId();
        UUID spaceId = space.getId();

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(area));

        assertThat(locationId.equals(areaLocationId)).isTrue();

        when(spaceRepository.findById(any(UUID.class))).thenReturn(Optional.of(space));

        assertThat(areaId.equals(spaceAreaId)).isTrue();

        when(spaceRepository.existsByNameAndAreaIdAndIdNot(any(String.class), any(UUID.class), any(UUID.class))).thenReturn(Boolean.FALSE);

        SpaceCreateDto updateDto = getSampleSpaceCreateDtoUpdate();

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
        Location location = LocationFactory.sampleLocation();

        Area area = AreaFactory.sampleArea();

        Space space = SpaceFactory.sampleSpace();

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
        Location location = LocationFactory.sampleLocation();

        Area area = AreaFactory.sampleArea();

        Space space = SpaceFactory.sampleSpace();

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
        Location location = LocationFactory.sampleLocation();

        Area area = AreaFactory.sampleArea();

        Space space = SpaceFactory.sampleSpace();

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
        Location location = LocationFactory.sampleLocationWithHardcodedUuid();

        Area area = AreaFactory.sampleAreaWithHardcodedLocationUuid();

        Space space = SpaceFactory.sampleSpace();

        UUID locationId = location.getId();
        UUID areaLocationId = area.getLocation().getId();
        UUID areaId = area.getId();
        UUID spaceAreaId = space.getArea().getId();
        UUID spaceId = space.getId();

        when(spaceRepository.findById(any(UUID.class))).thenReturn(Optional.of(space));

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(area));

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
        Location location = LocationFactory.sampleLocationWithHardcodedUuid();

        Area area = AreaFactory.sampleAreaWithHardcodedLocationUuid();

        Space space = SpaceFactory.sampleSpaceWithHardcodedUuid();

        UUID locationId = location.getId();
        UUID areaLocationId = area.getLocation().getId();
        UUID areaId = area.getId();
        UUID spaceAreaId = space.getArea().getId();
        UUID spaceId = space.getId();

        when(spaceRepository.findById(any(UUID.class))).thenReturn(Optional.of(space));

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(area));

        assertThat(locationId.equals(areaLocationId)).isTrue();

        assertThat(areaId.equals(spaceAreaId)).isTrue();

        spaceService.delete(locationId, areaId, spaceId);

        verify(spaceRepository, times(1)).deleteById(any(UUID.class));

        verify(auditService, times(1)).logAdminDelete(any(ResourceName.class), any(UUID.class));
    }

    @Test
    public void findAll_ThrowsException_WhenThereIsNoAreaPersisted() {
        Location location = LocationFactory.sampleLocation();

        Area area = AreaFactory.sampleArea();

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
        Location location = LocationFactory.sampleLocation();

        Area area = AreaFactory.sampleArea();

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
        Location location = LocationFactory.sampleLocationWithHardcodedUuid();

        Area area = AreaFactory.sampleAreaWithHardcodedLocationUuid();

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(area));

        UUID locationId = location.getId();
        UUID areaLocationId = area.getLocation().getId();
        UUID areaId = area.getId();

        assertThat(locationId.equals(areaLocationId)).isTrue();

        Space space = SpaceFactory.sampleSpace();

        when(spaceRepository.findAllByAreaId(any(UUID.class))).thenReturn(List.of(space));

        List<Space> spaces = spaceService.findAll(locationId, areaId);

        assertThat(spaces.size()).isEqualTo(1);
    }

    @Test
    public void findAll_ReturnEmptyList_WhenSuccessful() {
        Location location = LocationFactory.sampleLocationWithHardcodedUuid();

        Area area = AreaFactory.sampleAreaWithHardcodedLocationUuid();

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(area));

        UUID locationId = location.getId();
        UUID areaLocationId = area.getLocation().getId();
        UUID areaId = area.getId();

        assertThat(locationId.equals(areaLocationId)).isTrue();

        when(spaceRepository.findAllByAreaId(any(UUID.class))).thenReturn(Collections.emptyList());

        List<Space> spaces = spaceService.findAll(locationId, areaId);

        assertThat(spaces.size()).isEqualTo(0);
    }

    @Test
    public void findById_ThrowsException_WhenThereIsNoSpacePersisted() {
        Location location = LocationFactory.sampleLocation();

        Area area = AreaFactory.sampleArea();

        Space space = SpaceFactory.sampleSpace();

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
        Location location = LocationFactory.sampleLocation();

        Area area = AreaFactory.sampleArea();

        Space space = SpaceFactory.sampleSpace();

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
        Location location = LocationFactory.sampleLocation();

        Area area = AreaFactory.sampleArea();

        Space space = SpaceFactory.sampleSpace();

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
        Location location = LocationFactory.sampleLocationWithHardcodedUuid();

        Area area = AreaFactory.sampleAreaWithHardcodedLocationUuid();

        Space space = SpaceFactory.sampleSpace();

        when(spaceRepository.findById(any(UUID.class))).thenReturn(Optional.of(space));

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(area));

        UUID locationId = location.getId();
        UUID areaLocationId = area.getLocation().getId();
        UUID areaId = area.getId();
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
        Location location = LocationFactory.sampleLocationWithHardcodedUuid();

        Area area = AreaFactory.sampleAreaWithHardcodedLocationUuid();

        Space space = SpaceFactory.sampleSpaceWithHardcodedUuid();

        when(spaceRepository.findById(any(UUID.class))).thenReturn(Optional.of(space));

        when(areaRepository.findById(any(UUID.class))).thenReturn(Optional.of(area));

        UUID locationId = location.getId();
        UUID areaLocationId = area.getLocation().getId();
        UUID areaId = area.getId();
        UUID spaceAreaId = space.getArea().getId();
        UUID spaceId = space.getId();

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
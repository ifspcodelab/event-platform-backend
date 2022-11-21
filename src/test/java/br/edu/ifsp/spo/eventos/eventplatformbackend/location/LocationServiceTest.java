package br.edu.ifsp.spo.eventos.eventplatformbackend.location;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.AuditService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.AreaRepository;
//import org.junit.jupiter.api.BeforeEach;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceAlreadyExistsException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceName;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
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
    private AuditService AuditService;
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
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .extracting("resourceName", InstanceOfAssertFactories.type(ResourceName.class))
                .isEqualTo(ResourceName.LOCATION);
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
}

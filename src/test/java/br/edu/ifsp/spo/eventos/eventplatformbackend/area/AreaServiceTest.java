package br.edu.ifsp.spo.eventos.eventplatformbackend.area;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.AuditService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceAlreadyExistsException;
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
import static org.mockito.Mockito.when;

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
}

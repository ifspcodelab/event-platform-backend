package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.AuditService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.Activity;
import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.ActivityFactory;
import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.ActivityRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.AreaRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.BusinessRuleException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.BusinessRuleType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceName;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceNotFoundException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtUserDetails;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventFactory;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.LocationRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.registration.RegistrationRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.space.SpaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(SpringRunner.class)
@WithMockUser
class SessionServiceTest {
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private SessionScheduleRepository sessionScheduleRepository;
    @Mock
    private ActivityRepository activityRepository;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private AreaRepository areaRepository;
    @Mock
    private SpaceRepository spaceRepository;
    @Mock
    private AuditService auditService;
    @Mock
    private RegistrationRepository registrationRepository;
    @InjectMocks
    private SessionService sessionService;

    private Event event;
    private Activity activity;
    private Activity activityRandomId;
    private SessionCreateDto sessionCreateDto;
    private SecurityContext securityContext;
    private Authentication authentication;
    private JwtUserDetails jwtUserDetailsNonAdmin;
    private JwtUserDetails jwtUserDetailsAdmin;

    @BeforeEach
    public void setUp() {
        // TODO: 06/12/2022 Setup clock service with fixed time when date and time are necessary and use it in test methods accordingly
        event = EventFactory.sampleEvent();
        activity = ActivityFactory.sampleActivity();
        activityRandomId = ActivityFactory.sampleActivityRandomId();
        sessionCreateDto = getSampleSessionCreateDto();
        authentication = mock(Authentication.class);
        securityContext = mock(SecurityContext.class);
        jwtUserDetailsNonAdmin = JwtUserDetailsFactory.sampleJwtUserDetailsNonAdmin();
        jwtUserDetailsAdmin = JwtUserDetailsFactory.sampleJwtUserDetailsAdmin();
    }

    @Test
    public void sessionServiceShouldNotBeNull() { assertThat(sessionService).isNotNull(); }

    @Test
    public void create_ThrowsException_WhenUserIsNotAuthorized() {
        UUID eventId = event.getId();
        UUID activityId = activity.getId();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(jwtUserDetailsNonAdmin);

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> sessionService.create(eventId, activityId, sessionCreateDto)
        );
        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType()).isEqualTo(BusinessRuleType.UNAUTHORIZED_ACTION);
    }

    @Test
    public void create_ThrowsException_WhenActivityIsNotPersisted() {
        UUID eventId = event.getId();
        UUID activityId = activity.getId();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(jwtUserDetailsAdmin);

        when(activityRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        ResourceNotFoundException exception = (ResourceNotFoundException) catchThrowable(
                () -> sessionService.create(eventId, activityId, sessionCreateDto)
        );
        assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
        assertThat(exception.getResourceId()).isEqualTo(activityId.toString());
        assertThat(exception.getResourceName()).isEqualTo(ResourceName.ACTIVITY);
    }

    private SessionCreateDto getSampleSessionCreateDto() {
        return new SessionCreateDto(
                "Sessão 1",
                20,
                List.of(
                        new SessionScheduleCreateDto(
                                LocalDateTime.of(2023, 1, 9, 10, 0, 0),
                                LocalDateTime.of(2023, 1, 9, 11, 45, 0),
                                "",
                                UUID.fromString("6af7fd0b-84c7-440a-9159-7a1fb26bbb47"),
                                UUID.fromString("d2bf49f1-4ef5-4cf4-90e5-c72a0ea58cef"),
                                UUID.fromString("8215f714-1bd5-4a17-bbef-6aa9396775a8")
                        )
                )
        );
    }
}
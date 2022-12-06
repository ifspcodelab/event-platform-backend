package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.AuditService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.Activity;
import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.ActivityFactory;
import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.ActivityRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.AreaRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.Period;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.*;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtUserDetails;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventFactory;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus;
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

import java.time.Duration;
import java.time.LocalDate;
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

    @Test
    public void create_ThrowsException_WhenActivityDoesNotExistInGivenEvent() {
        UUID eventId = event.getId();
        UUID activityEventId = activityRandomId.getEvent().getId();
        UUID activityId = activityRandomId.getId();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(jwtUserDetailsAdmin);

        when(activityRepository.findById(any(UUID.class))).thenReturn(Optional.of(activityRandomId));

        assertThat(eventId.toString()).isNotEqualTo(activityEventId.toString());

        ResourceReferentialIntegrityException exception = (ResourceReferentialIntegrityException) catchThrowable(
                () -> sessionService.create(eventId, activityId, sessionCreateDto)
        );
        assertThat(exception).isInstanceOf(ResourceReferentialIntegrityException.class);
        assertThat(exception.getPrimary()).isEqualTo(ResourceName.ACTIVITY);
        assertThat(exception.getRelated()).isEqualTo(ResourceName.EVENT);
    }

    @Test
    public void create_ThrowsException_WhenSessionTitleAlreadyExists() {
        UUID eventId = event.getId();
        UUID activityEventId = activity.getEvent().getId();
        UUID activityId = activity.getId();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(jwtUserDetailsAdmin);

        when(activityRepository.findById(any(UUID.class))).thenReturn(Optional.of(activity));

        assertThat(eventId.toString()).isEqualTo(activityEventId.toString());

        var title = sessionCreateDto.getTitle();
        when(sessionRepository.existsByTitleIgnoreCaseAndActivityId(title, activityId)).thenReturn(Boolean.TRUE);

        ResourceAlreadyExistsException exception = (ResourceAlreadyExistsException) catchThrowable(
                () -> sessionService.create(eventId, activityId, sessionCreateDto)
        );
        assertThat(exception).isInstanceOf(ResourceAlreadyExistsException.class);
        assertThat(exception.getResourceName()).isEqualTo(ResourceName.SESSION);
        assertThat(exception.getResourceAttributeValue()).isEqualTo(sessionCreateDto.getTitle());
    }

    @Test
    public void create_ThrowsException_WhenActivitysEventIsCanceled() {
        UUID eventId = event.getId();
        UUID activityEventId = activity.getEvent().getId();
        UUID activityId = activity.getId();
        activity.getEvent().setStatus(EventStatus.CANCELED);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(jwtUserDetailsAdmin);

        when(activityRepository.findById(any(UUID.class))).thenReturn(Optional.of(activity));

        assertThat(eventId.toString()).isEqualTo(activityEventId.toString());

        var title = sessionCreateDto.getTitle();
        when(sessionRepository.existsByTitleIgnoreCaseAndActivityId(title, activityId)).thenReturn(Boolean.FALSE);

        SessionRuleException exception = (SessionRuleException) catchThrowable(
                () -> sessionService.create(eventId, activityId, sessionCreateDto)
        );
        assertThat(exception).isInstanceOf(SessionRuleException.class);
        assertThat(exception.getRuleType()).isEqualTo(SessionRuleType.CANCELED_EVENT);
    }

    @Test
    public void create_ThrowsException_WhenActivitysSubeventIsCanceled() {
        UUID eventId = event.getId();
        UUID activityEventId = activity.getEvent().getId();
        UUID activityId = activity.getId();
        activity.getSubevent().setStatus(EventStatus.CANCELED);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(jwtUserDetailsAdmin);

        when(activityRepository.findById(any(UUID.class))).thenReturn(Optional.of(activity));

        assertThat(eventId.toString()).isEqualTo(activityEventId.toString());

        var title = sessionCreateDto.getTitle();
        when(sessionRepository.existsByTitleIgnoreCaseAndActivityId(title, activityId)).thenReturn(Boolean.FALSE);

        SessionRuleException exception = (SessionRuleException) catchThrowable(
                () -> sessionService.create(eventId, activityId, sessionCreateDto)
        );
        assertThat(exception).isInstanceOf(SessionRuleException.class);
        assertThat(exception.getRuleType()).isEqualTo(SessionRuleType.CANCELED_SUBEVENT);
    }

    @Test
    public void create_ThrowsException_WhenActivityIsCanceled() {
        UUID eventId = event.getId();
        UUID activityEventId = activity.getEvent().getId();
        UUID activityId = activity.getId();
        activity.setStatus(EventStatus.CANCELED);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(jwtUserDetailsAdmin);

        when(activityRepository.findById(any(UUID.class))).thenReturn(Optional.of(activity));

        assertThat(eventId.toString()).isEqualTo(activityEventId.toString());

        var title = sessionCreateDto.getTitle();
        when(sessionRepository.existsByTitleIgnoreCaseAndActivityId(title, activityId)).thenReturn(Boolean.FALSE);

        SessionRuleException exception = (SessionRuleException) catchThrowable(
                () -> sessionService.create(eventId, activityId, sessionCreateDto)
        );
        assertThat(exception).isInstanceOf(SessionRuleException.class);
        assertThat(exception.getRuleType()).isEqualTo(SessionRuleType.CANCELED_ACTIVITY);
    }

    @Test
    public void create_ThrowsException_WhenSessionSchedulesPeriodStartIsAfterEnd() {
        UUID eventId = event.getId();
        UUID activityEventId = activity.getEvent().getId();
        UUID activityId = activity.getId();
        SessionCreateDto sessionCreateDtoWithInvalidPeriod = getSampleSessionCreateDtoWithInvalidPeriodStartIsAfterEnd();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(jwtUserDetailsAdmin);

        when(activityRepository.findById(any(UUID.class))).thenReturn(Optional.of(activity));

        assertThat(eventId.toString()).isEqualTo(activityEventId.toString());

        var title = sessionCreateDtoWithInvalidPeriod.getTitle();
        when(sessionRepository.existsByTitleIgnoreCaseAndActivityId(title, activityId)).thenReturn(Boolean.FALSE);

        var start = sessionCreateDtoWithInvalidPeriod.getSessionSchedules().get(0).getExecutionStart();
        var end = sessionCreateDtoWithInvalidPeriod.getSessionSchedules().get(0).getExecutionEnd();

        assertThat(start).isAfter(end);

        SessionRuleException exception = (SessionRuleException) catchThrowable(
                () -> sessionService.create(eventId, activityId, sessionCreateDtoWithInvalidPeriod)
        );
        assertThat(exception).isInstanceOf(SessionRuleException.class);
        assertThat(exception.getRuleType()).isEqualTo(SessionRuleType.SCHEDULE_INVALID_PERIOD);
    }

    @Test
    public void create_ThrowsException_WhenSessionSchedulesPeriodStartIsEqualToEnd() {
        UUID eventId = event.getId();
        UUID activityEventId = activity.getEvent().getId();
        UUID activityId = activity.getId();
        SessionCreateDto sessionCreateDtoWithInvalidPeriod = getSampleSessionCreateDtoWithInvalidPeriodStartIsEqualToEnd();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(jwtUserDetailsAdmin);

        when(activityRepository.findById(any(UUID.class))).thenReturn(Optional.of(activity));

        assertThat(eventId.toString()).isEqualTo(activityEventId.toString());

        var title = sessionCreateDtoWithInvalidPeriod.getTitle();
        when(sessionRepository.existsByTitleIgnoreCaseAndActivityId(title, activityId)).thenReturn(Boolean.FALSE);

        var start = sessionCreateDtoWithInvalidPeriod.getSessionSchedules().get(0).getExecutionStart();
        var end = sessionCreateDtoWithInvalidPeriod.getSessionSchedules().get(0).getExecutionEnd();

        assertThat(start).isEqualTo(end);

        SessionRuleException exception = (SessionRuleException) catchThrowable(
                () -> sessionService.create(eventId, activityId, sessionCreateDtoWithInvalidPeriod)
        );
        assertThat(exception).isInstanceOf(SessionRuleException.class);
        assertThat(exception.getRuleType()).isEqualTo(SessionRuleType.SCHEDULE_INVALID_PERIOD);
    }

    @Test
    public void create_ThrowsException_WhenSessionScheduleIsInThePast() {
        UUID eventId = event.getId();
        UUID activityEventId = activity.getEvent().getId();
        UUID activityId = activity.getId();
        SessionCreateDto sessionCreateDtoWithPeriodInThePast = getSampleSessionCreateDtoWithInvalidPeriodInThePast();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(jwtUserDetailsAdmin);

        when(activityRepository.findById(any(UUID.class))).thenReturn(Optional.of(activity));

        assertThat(eventId.toString()).isEqualTo(activityEventId.toString());

        var title = sessionCreateDtoWithPeriodInThePast.getTitle();
        when(sessionRepository.existsByTitleIgnoreCaseAndActivityId(title, activityId)).thenReturn(Boolean.FALSE);

        var start = sessionCreateDtoWithPeriodInThePast.getSessionSchedules().get(0).getExecutionStart();
        var end = sessionCreateDtoWithPeriodInThePast.getSessionSchedules().get(0).getExecutionEnd();

        assertThat(start).isBefore(LocalDateTime.now());
        assertThat(end).isBefore(LocalDateTime.now());

        SessionRuleException exception = (SessionRuleException) catchThrowable(
                () -> sessionService.create(eventId, activityId, sessionCreateDtoWithPeriodInThePast)
        );
        assertThat(exception).isInstanceOf(SessionRuleException.class);
        assertThat(exception.getRuleType()).isEqualTo(SessionRuleType.SCHEDULE_IN_PAST);
    }

    @Test
    public void create_ThrowsException_WhenSessionScheduleHasIntersections() {
        UUID eventId = event.getId();
        UUID activityEventId = activity.getEvent().getId();
        UUID activityId = activity.getId();
        SessionCreateDto sessionCreateDtoWithInterception = getSampleSessionCreateDtoWithScheduleInterception();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(jwtUserDetailsAdmin);

        when(activityRepository.findById(any(UUID.class))).thenReturn(Optional.of(activity));

        assertThat(eventId.toString()).isEqualTo(activityEventId.toString());

        var title = sessionCreateDtoWithInterception.getTitle();
        when(sessionRepository.existsByTitleIgnoreCaseAndActivityId(title, activityId)).thenReturn(Boolean.FALSE);

        var startSession1 = sessionCreateDtoWithInterception.getSessionSchedules().get(0).getExecutionStart();
        var endSession1 = sessionCreateDtoWithInterception.getSessionSchedules().get(0).getExecutionEnd();

        var startSession2 = sessionCreateDtoWithInterception.getSessionSchedules().get(1).getExecutionStart();
        var endSession2 = sessionCreateDtoWithInterception.getSessionSchedules().get(1).getExecutionEnd();

        var condition1 = startSession1.isBefore(startSession2) || startSession1.isEqual(startSession2);
        var condition2 = endSession1.isAfter(startSession2);
        var condition3 = startSession2.isBefore(startSession1) || startSession2.isEqual(startSession1);
        var condition4 = endSession2.isAfter(startSession1);
        var condition5 = condition1 && condition2;
        var condition6 = condition3 && condition4;
        var condition7 = condition5 || condition6;

        assertThat(condition7).isTrue();

        SessionRuleException exception = (SessionRuleException) catchThrowable(
                () -> sessionService.create(eventId, activityId, sessionCreateDtoWithInterception)
        );
        assertThat(exception).isInstanceOf(SessionRuleException.class);
        assertThat(exception.getRuleType()).isEqualTo(SessionRuleType.SCHEDULE_HAS_INTERSECTIONS);
    }

    @Test
    public void create_ThrowsException_WhenSessionSchedulePeriodIsOutsideSubeventPeriod() {
        UUID eventId = event.getId();
        UUID activityEventId = activity.getEvent().getId();
        UUID activityId = activity.getId();
        SessionCreateDto sessionCreateDtoWithOutsideExecutionPeriod = getSampleSessionCreateDtoWithSessionScheduleOutsideEventOrSubeventPeriod();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(jwtUserDetailsAdmin);

        when(activityRepository.findById(any(UUID.class))).thenReturn(Optional.of(activity));

        assertThat(eventId.toString()).isEqualTo(activityEventId.toString());

        var title = sessionCreateDtoWithOutsideExecutionPeriod.getTitle();
        when(sessionRepository.existsByTitleIgnoreCaseAndActivityId(title, activityId)).thenReturn(Boolean.FALSE);

        var sessionStart = sessionCreateDtoWithOutsideExecutionPeriod.getSessionSchedules().get(0).getExecutionStart();
        var sessionEnd = sessionCreateDtoWithOutsideExecutionPeriod.getSessionSchedules().get(0).getExecutionStart();
        var subeventStart = activity.getSubevent().getExecutionPeriod().getStartDate().atStartOfDay();
        var subeventEnd = activity.getSubevent().getExecutionPeriod().getEndDate().plusDays(1).atStartOfDay();

        var condition1 = sessionStart.isBefore(subeventStart);
        var condition2 = sessionEnd.isAfter(subeventEnd);
        var condition3 = condition1 || condition2;

        assertThat(condition3).isTrue();

        SessionRuleException exception = (SessionRuleException) catchThrowable(
                () -> sessionService.create(eventId, activityId, sessionCreateDtoWithOutsideExecutionPeriod)
        );
        assertThat(exception).isInstanceOf(SessionRuleException.class);
        assertThat(exception.getRuleType()).isEqualTo(SessionRuleType.OUTSIDE_EXECUTION_PERIOD);
    }

    @Test
    public void create_ThrowsException_WhenSessionSchedulePeriodIsOutsideEventPeriod() {
        UUID eventId = event.getId();
        UUID activityEventId = activity.getEvent().getId();
        UUID activityId = activity.getId();
        activity.setSubevent(null);
        SessionCreateDto sessionCreateDtoWithOutsideExecutionPeriod = getSampleSessionCreateDtoWithSessionScheduleOutsideEventOrSubeventPeriod();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(jwtUserDetailsAdmin);

        when(activityRepository.findById(any(UUID.class))).thenReturn(Optional.of(activity));

        assertThat(eventId.toString()).isEqualTo(activityEventId.toString());

        var title = sessionCreateDtoWithOutsideExecutionPeriod.getTitle();
        when(sessionRepository.existsByTitleIgnoreCaseAndActivityId(title, activityId)).thenReturn(Boolean.FALSE);

        var sessionStart = sessionCreateDtoWithOutsideExecutionPeriod.getSessionSchedules().get(0).getExecutionStart();
        var sessionEnd = sessionCreateDtoWithOutsideExecutionPeriod.getSessionSchedules().get(0).getExecutionStart();
        var eventStart = activity.getEvent().getExecutionPeriod().getStartDate().atStartOfDay();
        var eventEnd = activity.getEvent().getExecutionPeriod().getEndDate().plusDays(1).atStartOfDay();

        var condition1 = sessionStart.isBefore(eventStart);
        var condition2 = sessionEnd.isAfter(eventEnd);
        var condition3 = condition1 || condition2;

        assertThat(condition3).isTrue();

        SessionRuleException exception = (SessionRuleException) catchThrowable(
                () -> sessionService.create(eventId, activityId, sessionCreateDtoWithOutsideExecutionPeriod)
        );
        assertThat(exception).isInstanceOf(SessionRuleException.class);
        assertThat(exception.getRuleType()).isEqualTo(SessionRuleType.OUTSIDE_EXECUTION_PERIOD);
    }

    @Test
    public void create_ThrowsException_WhenDurationOfAllSessionsIsNotEqualToActivityDuration() {
        UUID eventId = event.getId();
        UUID activityEventId = activity.getEvent().getId();
        UUID activityId = activity.getId();

        SessionCreateDto sessionCreateDtoWithDifferentDuration = getSampleSessionCreateDtoWithDifferentDuration();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(jwtUserDetailsAdmin);

        when(activityRepository.findById(any(UUID.class))).thenReturn(Optional.of(activity));

        assertThat(eventId.toString()).isEqualTo(activityEventId.toString());

        var title = sessionCreateDtoWithDifferentDuration.getTitle();
        when(sessionRepository.existsByTitleIgnoreCaseAndActivityId(title, activityId)).thenReturn(Boolean.FALSE);

        var start = sessionCreateDtoWithDifferentDuration.getSessionSchedules().get(0).getExecutionStart();
        var end = sessionCreateDtoWithDifferentDuration.getSessionSchedules().get(0).getExecutionEnd();
        var sessionDuration = Duration.between(start, end).getSeconds();
        var activityDuration = activity.getDurationInSeconds() + activity.getSetupTimeInSeconds() * sessionCreateDtoWithDifferentDuration.getSessionSchedules().size();

        assertThat(sessionDuration).isNotEqualTo(activityDuration);

        SessionRuleException exception = (SessionRuleException) catchThrowable(
                () -> sessionService.create(eventId, activityId, sessionCreateDtoWithDifferentDuration)
        );
        assertThat(exception).isInstanceOf(SessionRuleException.class);
        assertThat(exception.getRuleType()).isEqualTo(SessionRuleType.SESSION_DURATION);
    }

    @Test
    public void create_ThrowsException_WhenSeatsAreNotDefined() {
        UUID eventId = event.getId();
        UUID activityEventId = activity.getEvent().getId();
        UUID activityId = activity.getId();

        SessionCreateDto sessionCreateDtoWithNoSeats = getSampleSessionCreateDtoWithNoSeats();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(jwtUserDetailsAdmin);

        when(activityRepository.findById(any(UUID.class))).thenReturn(Optional.of(activity));

        assertThat(eventId.toString()).isEqualTo(activityEventId.toString());

        var title = sessionCreateDtoWithNoSeats.getTitle();
        when(sessionRepository.existsByTitleIgnoreCaseAndActivityId(title, activityId)).thenReturn(Boolean.FALSE);

        assertThat(sessionCreateDtoWithNoSeats.getSeats()).isEqualTo(0);

        SessionRuleException exception = (SessionRuleException) catchThrowable(
                () -> sessionService.create(eventId, activityId, sessionCreateDtoWithNoSeats)
        );
        assertThat(exception).isInstanceOf(SessionRuleException.class);
        assertThat(exception.getRuleType()).isEqualTo(SessionRuleType.SEATS_NOT_DEFINED);
    }

    @Test
    public void create_ThrowsException_WhenEventsRegistrationPeriodHasEnded() {
        UUID eventId = event.getId();
        UUID activityEventId = activity.getEvent().getId();
        UUID activityId = activity.getId();

        activity.getEvent().setRegistrationPeriod(
                new Period(
                        LocalDate.of(2023, 1, 1),
                        LocalDate.of(2022, 1, 31))
                );

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(jwtUserDetailsAdmin);

        when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));

        var title = sessionCreateDto.getTitle();
        when(sessionRepository.existsByTitleIgnoreCaseAndActivityId(title, activityId)).thenReturn(Boolean.FALSE);

        assertThat(eventId.toString()).isEqualTo(activityEventId.toString());

        assertThat(activity.getEvent().isRegistrationPeriodEnded()).isTrue();

        SessionRuleException exception = (SessionRuleException) catchThrowable(
                () -> sessionService.create(eventId, activityId, sessionCreateDto)
        );
        assertThat(exception).isInstanceOf(SessionRuleException.class);
        assertThat(exception.getRuleType()).isEqualTo(SessionRuleType.REGISTRATION_PERIOD_ENDED);
    }

    @Test
    public void create_ThrowsException_WhenExecutionPeriodHasEnded() {
        UUID eventId = event.getId();
        UUID activityEventId = activity.getEvent().getId();
        UUID activityId = activity.getId();
        activity.setNeedRegistration(false);

        activity.getEvent().setExecutionPeriod(
                new Period(
                        LocalDate.of(2023, 1, 1),
                        LocalDate.of(2022, 1, 31))
        );

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(jwtUserDetailsAdmin);

        when(activityRepository.findById(any(UUID.class))).thenReturn(Optional.of(activity));

        assertThat(eventId.toString()).isEqualTo(activityEventId.toString());

        var title = sessionCreateDto.getTitle();
        when(sessionRepository.existsByTitleIgnoreCaseAndActivityId(title, activityId)).thenReturn(Boolean.FALSE);

        assertThat(activity.getEvent().isExecutionPeriodEnded()).isTrue();

        SessionRuleException exception = (SessionRuleException) catchThrowable(
                () -> sessionService.create(eventId, activityId, sessionCreateDto)
        );
        assertThat(exception).isInstanceOf(SessionRuleException.class);
        assertThat(exception.getRuleType()).isEqualTo(SessionRuleType.EXECUTION_PERIOD_ENDED);
    }

    @Test
    public void create_ThrowsException_WhenLocationIsNotDefined() {
        UUID eventId = event.getId();
        UUID activityEventId = activity.getEvent().getId();
        UUID activityId = activity.getId();

        SessionCreateDto sessionCreateDtoWithoutLocationId = getSampleSessionCreateDtoWithNoLocationId();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(jwtUserDetailsAdmin);

        when(activityRepository.findById(any(UUID.class))).thenReturn(Optional.of(activity));

        assertThat(eventId.toString()).isEqualTo(activityEventId.toString());

        var title = sessionCreateDtoWithoutLocationId.getTitle();
        when(sessionRepository.existsByTitleIgnoreCaseAndActivityId(title, activityId)).thenReturn(Boolean.FALSE);

        assertThat(sessionCreateDtoWithoutLocationId.getSessionSchedules().get(0).getLocationId()).isNull();

        SessionRuleException exception = (SessionRuleException) catchThrowable(
                () -> sessionService.create(eventId, activityId, sessionCreateDtoWithoutLocationId)
        );
        assertThat(exception).isInstanceOf(SessionRuleException.class);
        assertThat(exception.getRuleType()).isEqualTo(SessionRuleType.LOCATION_NOT_DEFINED);
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

    private SessionCreateDto getSampleSessionCreateDtoWithInvalidPeriodStartIsAfterEnd() {
        return new SessionCreateDto(
                "Sessão 1",
                20,
                List.of(
                        new SessionScheduleCreateDto(
                                LocalDateTime.of(2023, 1, 9, 11, 45, 0),
                                LocalDateTime.of(2023, 1, 9, 10, 0, 0),
                                "",
                                UUID.fromString("6af7fd0b-84c7-440a-9159-7a1fb26bbb47"),
                                UUID.fromString("d2bf49f1-4ef5-4cf4-90e5-c72a0ea58cef"),
                                UUID.fromString("8215f714-1bd5-4a17-bbef-6aa9396775a8")
                        )
                )
        );
    }

    private SessionCreateDto getSampleSessionCreateDtoWithInvalidPeriodStartIsEqualToEnd() {
        return new SessionCreateDto(
                "Sessão 1",
                20,
                List.of(
                        new SessionScheduleCreateDto(
                                LocalDateTime.of(2023, 1, 9, 10, 0, 0),
                                LocalDateTime.of(2023, 1, 9, 10, 0, 0),
                                "",
                                UUID.fromString("6af7fd0b-84c7-440a-9159-7a1fb26bbb47"),
                                UUID.fromString("d2bf49f1-4ef5-4cf4-90e5-c72a0ea58cef"),
                                UUID.fromString("8215f714-1bd5-4a17-bbef-6aa9396775a8")
                        )
                )
        );
    }

    private SessionCreateDto getSampleSessionCreateDtoWithInvalidPeriodInThePast() {
        return new SessionCreateDto(
                "Sessão 1",
                20,
                List.of(
                        new SessionScheduleCreateDto(
                                LocalDateTime.of(2022, 1, 9, 10, 0, 0),
                                LocalDateTime.of(2022, 1, 9, 11, 45, 0),
                                "",
                                UUID.fromString("6af7fd0b-84c7-440a-9159-7a1fb26bbb47"),
                                UUID.fromString("d2bf49f1-4ef5-4cf4-90e5-c72a0ea58cef"),
                                UUID.fromString("8215f714-1bd5-4a17-bbef-6aa9396775a8")
                        )
                )
        );
    }

    private SessionCreateDto getSampleSessionCreateDtoWithScheduleInterception() {
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
                        ),
                        new SessionScheduleCreateDto(
                                LocalDateTime.of(2023, 1, 9, 10, 0, 0),
                                LocalDateTime.of(2023, 1, 9, 11, 45, 0),
                                "",
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                UUID.randomUUID()
                        )
                )
        );
    }

    private SessionCreateDto getSampleSessionCreateDtoWithSessionScheduleOutsideEventOrSubeventPeriod() {
        return new SessionCreateDto(
                "Sessão 1",
                20,
                List.of(
                        new SessionScheduleCreateDto(
                                LocalDateTime.of(2023, 2, 9, 10, 0, 0),
                                LocalDateTime.of(2023, 2, 9, 11, 45, 0),
                                "",
                                UUID.fromString("6af7fd0b-84c7-440a-9159-7a1fb26bbb47"),
                                UUID.fromString("d2bf49f1-4ef5-4cf4-90e5-c72a0ea58cef"),
                                UUID.fromString("8215f714-1bd5-4a17-bbef-6aa9396775a8")
                        )
                )
        );
    }

    private SessionCreateDto getSampleSessionCreateDtoWithDifferentDuration() {
        return new SessionCreateDto(
                "Sessão 1",
                20,
                List.of(
                        new SessionScheduleCreateDto(
                                LocalDateTime.of(2023, 1, 9, 10, 0, 0),
                                LocalDateTime.of(2023, 1, 9, 11, 45, 1),
                                "",
                                UUID.fromString("6af7fd0b-84c7-440a-9159-7a1fb26bbb47"),
                                UUID.fromString("d2bf49f1-4ef5-4cf4-90e5-c72a0ea58cef"),
                                UUID.fromString("8215f714-1bd5-4a17-bbef-6aa9396775a8")
                        )
                )
        );
    }

    private SessionCreateDto getSampleSessionCreateDtoWithNoSeats() {
        return new SessionCreateDto(
                "Sessão 1",
                0,
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

    private SessionCreateDto getSampleSessionCreateDtoWithNoLocationId() {
        return new SessionCreateDto(
                "Sessão 1",
                20,
                List.of(
                        new SessionScheduleCreateDto(
                                LocalDateTime.of(2023, 1, 9, 10, 0, 0),
                                LocalDateTime.of(2023, 1, 9, 11, 45, 0),
                                "https://sample.url.com",
                                null,
                                UUID.fromString("d2bf49f1-4ef5-4cf4-90e5-c72a0ea58cef"),
                                UUID.fromString("8215f714-1bd5-4a17-bbef-6aa9396775a8")
                        )
                )
        );
    }
}
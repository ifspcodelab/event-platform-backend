package br.edu.ifsp.spo.eventos.eventplatformbackend.registration;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.ActivityFactory;
import br.edu.ifsp.spo.eventos.eventplatformbackend.attendance.AttendanceRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.Period;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.email.EmailService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.BusinessRuleException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.BusinessRuleType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtUserDetails;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.validators.JwtUserDetailsFactory;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus;
import br.edu.ifsp.spo.eventos.eventplatformbackend.organizer_authorization.OrganizerAuthorizationException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.organizer_authorization.OrganizerAuthorizationExceptionType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.Session;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.SessionRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.SessionSchedule;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.SessionScheduleFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RunWith(SpringRunner.class)
@WithMockUser
public class RegistrationServiceTest {
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private RegistrationRepository registrationRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private EmailService emailService;
    @Value("${registration.email-confirmation-time}")
    private String emailConfirmationTime;
    @InjectMocks
    private RegistrationService registrationService;
    private Registration registrationConfirmedStatus;
    private Registration registrationWaitingListStatus;
    private RegistrationCreateDto registrationCreateDto;

    @BeforeEach
    public void setUp() {
        registrationConfirmedStatus = RegistrationFactory.sampleRegistrationWithConfirmedStatus();
        registrationWaitingListStatus = RegistrationFactory.sampleRegistrationWithWaitingListStatus();
        registrationCreateDto = sampleRegistrationCreateDto();

    }

    @Test
    public void create_ThrowsException_WhenCurrentUserHasNoPermissionForEvent() {
        UUID eventId = registrationConfirmedStatus.getSession().getActivity().getEvent().getId();
        UUID activityId = registrationConfirmedStatus.getSession().getActivity().getId();
        UUID sessionId = registrationConfirmedStatus.getSession().getId();
        mocksAuthenticationWithCurrentUserThatIsNotAuthorized();

        OrganizerAuthorizationException exception = (OrganizerAuthorizationException) catchThrowable(
                () -> registrationService.create(registrationCreateDto, eventId, activityId, sessionId)
        );

        assertThat(exception).isInstanceOf(OrganizerAuthorizationException.class);
        assertThat(exception.getOrganizerAuthorizationExceptionType())
                .isEqualTo(OrganizerAuthorizationExceptionType.UNAUTHORIZED_EVENT);
        assertThat(exception.getResourceId()).isEqualTo(eventId);
    }

    @Test
    public void create_ThrowsException_WhenAccountDoesNotExist() {
        UUID eventId = registrationConfirmedStatus.getSession().getActivity().getEvent().getId();
        UUID activityId = registrationConfirmedStatus.getSession().getActivity().getId();
        UUID sessionId = registrationConfirmedStatus.getSession().getId();
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = (NoSuchElementException) catchThrowable(
                () -> registrationService.create(registrationCreateDto, eventId, activityId, sessionId)
        );

        assertThat(exception).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void create_ThrowsException_WhenSessionDoesNotExist() {
        UUID eventId = registrationConfirmedStatus.getSession().getActivity().getEvent().getId();
        UUID activityId = registrationConfirmedStatus.getSession().getActivity().getId();
        UUID sessionId = registrationConfirmedStatus.getSession().getId();
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = (NoSuchElementException) catchThrowable(
                () -> registrationService.create(registrationCreateDto, eventId, activityId, sessionId)
        );

        assertThat(exception).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void create_ThrowsException_WhenEventIsNotAssociateToActivity() {
        UUID eventId = registrationConfirmedStatus.getSession().getActivity().getEvent().getId();
        UUID activityId = UUID.randomUUID();
        UUID sessionId = registrationConfirmedStatus.getSession().getId();
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getSession()));

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.create(registrationCreateDto, eventId, activityId, sessionId)
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.SESSION_IS_NOT_ASSOCIATED_TO_ACTIVITY);
    }

    @Test
    public void create_ThrowsException_WhenActivityIsNotAssociateToSession() {
        UUID eventId = registrationConfirmedStatus.getSession().getActivity().getEvent().getId();
        UUID activityId = ActivityFactory.sampleActivity2().getId();
        UUID sessionId = registrationConfirmedStatus.getSession().getId();
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getSession()));

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.create(registrationCreateDto, eventId, activityId, sessionId)
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.SESSION_IS_NOT_ASSOCIATED_TO_ACTIVITY);
    }

    @Test
    public void create_ThrowsException_WhenSessionIsCancelled() {
        UUID eventId = registrationConfirmedStatus.getSession().getActivity().getEvent().getId();
        UUID activityId =  registrationConfirmedStatus.getSession().getActivity().getId();
        Session sessionCancelled = registrationConfirmedStatus.getSession();
        sessionCancelled.setCanceled(true);
        sessionCancelled.setCancellationMessage("Mensagem de cancelamento de exemplo");
        UUID sessionId = sessionCancelled.getId();
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(sessionCancelled));

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.create(registrationCreateDto, eventId, activityId, sessionId)
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_CREATE_WITH_CANCELED_SESSION);
    }

    @Test
    public void create_ThrowsException_WhenAccountAlreadyHasARegistrationInActivity() {
        UUID eventId = registrationConfirmedStatus.getSession().getActivity().getEvent().getId();
        UUID activityId =  registrationConfirmedStatus.getSession().getActivity().getId();
        UUID sessionId = registrationConfirmedStatus.getSession().getId();
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getSession()));
        when(registrationRepository.existsByAccountIdAndActivityId(any(UUID.class), any(UUID.class)))
                .thenReturn(true);

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.create(registrationCreateDto, eventId, activityId, sessionId)
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_CREATE_ACCOUNT_ALREADY_HAS_REGISTRATION_IN_ACTIVITY);
    }

    @Test
    public void create_ThrowsException_WhenAccountAlreadyHasARegistrationInSession() {
        UUID eventId = registrationConfirmedStatus.getSession().getActivity().getEvent().getId();
        UUID activityId =  registrationConfirmedStatus.getSession().getActivity().getId();
        UUID sessionId = registrationConfirmedStatus.getSession().getId();
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getSession()));
        when(registrationRepository.existsByAccountIdAndActivityId(any(UUID.class), any(UUID.class)))
                .thenReturn(false);
        when(registrationRepository.existsBySessionIdAndAccountIdAndRegistrationStatusIn(
                any(UUID.class),
                any(UUID.class),
                anyList()
        )).thenReturn(true);

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.create(registrationCreateDto, eventId, activityId, sessionId)
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_CREATE_ALREADY_EXISTS);
    }

    @Test
    public void create_ThrowsException_WhenExistsAnyRegistrationWithConflict() {
        UUID eventId = registrationConfirmedStatus.getSession().getActivity().getEvent().getId();
        UUID activityId =  registrationConfirmedStatus.getSession().getActivity().getId();
        UUID sessionId = registrationConfirmedStatus.getSession().getId();
        List<Registration> registrationsInConflict = List.of(registrationConfirmedStatus);
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getSession()));
        when(registrationRepository.existsByAccountIdAndActivityId(any(UUID.class), any(UUID.class)))
                .thenReturn(false);
        when(registrationRepository.existsBySessionIdAndAccountIdAndRegistrationStatusIn(
                any(UUID.class),
                any(UUID.class),
                anyList()
        )).thenReturn(false);
        when(registrationRepository.findAllByAccountIdAndRegistrationStatusInAndDate(
                any(UUID.class),
                any(LocalDateTime.class),
                anyList()
        )).thenReturn(registrationsInConflict);

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.create(registrationCreateDto, eventId, activityId, sessionId)
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_CREATE_HAS_SCHEDULE_CONFLICT);
    }

    @Test
    public void create_ThrowsException_WhenSessionIsFull() {
        UUID eventId = registrationConfirmedStatus.getSession().getActivity().getEvent().getId();
        UUID activityId =  registrationConfirmedStatus.getSession().getActivity().getId();
        UUID sessionId = registrationConfirmedStatus.getSession().getId();
        List<Registration> registrations = List.of(
                RegistrationFactory.sampleRegistrationWithConfirmedStatusInOtherSession()
        );
        registrationConfirmedStatus.getSession().setConfirmedSeats(
                registrationConfirmedStatus.getSession().getSeats()
        );
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getSession()));
        when(registrationRepository.existsByAccountIdAndActivityId(any(UUID.class), any(UUID.class)))
                .thenReturn(false);
        when(registrationRepository.existsBySessionIdAndAccountIdAndRegistrationStatusIn(
                any(UUID.class),
                any(UUID.class),
                anyList()
        )).thenReturn(false);
        when(registrationRepository.findAllByAccountIdAndRegistrationStatusInAndDate(
                any(UUID.class),
                any(LocalDateTime.class),
                anyList()
        )).thenReturn(registrations);

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.create(registrationCreateDto, eventId, activityId, sessionId)
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_CREATE_WITH_NO_SEATS_AVAILABLE);
    }

    @Test
    public void create_ReturnsRegistration_WhenSuccessful() {
        UUID eventId = registrationConfirmedStatus.getSession().getActivity().getEvent().getId();
        UUID activityId =  registrationConfirmedStatus.getSession().getActivity().getId();
        UUID sessionId = registrationConfirmedStatus.getSession().getId();
        List<Registration> registrations = List.of(
                RegistrationFactory.sampleRegistrationWithConfirmedStatusInOtherSession()
        );
        List<Registration> registrationsInWaitingList = List.of(
                RegistrationFactory.sampleRegistrationWithWaitingListStatus()
        );
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getSession()));
        when(registrationRepository.existsByAccountIdAndActivityId(any(UUID.class), any(UUID.class)))
                .thenReturn(false);
        when(registrationRepository.existsBySessionIdAndAccountIdAndRegistrationStatusIn(
                any(UUID.class),
                any(UUID.class),
                anyList()
        )).thenReturn(false);
        when(registrationRepository.findAllByAccountIdAndRegistrationStatusInAndDate(
                any(UUID.class),
                any(LocalDateTime.class),
                anyList()
        )).thenReturn(registrations).thenReturn(registrationsInWaitingList);
        when(registrationRepository.save(any(Registration.class)))
                .thenReturn(registrationConfirmedStatus);
        when(registrationRepository.findAllByAccountIdAndSessionIdIn(
                any(UUID.class),
                anyList()
        )).thenReturn(registrationsInWaitingList);

        Registration registrationCreated = registrationService.create(
                registrationCreateDto,
                eventId,
                activityId,
                sessionId
        );

        verify(registrationRepository, times(1)).save(any(Registration.class));
        verify(sessionRepository, times(1)).save(any(Session.class));
        verify(registrationRepository, times(1)).saveAll(anyList());
        assertThat(registrationCreated).isNotNull();
        assertThat(registrationCreated.getId()).isEqualTo(registrationConfirmedStatus.getId());
        assertThat(registrationCreated.getAccount().getId())
                .isEqualTo(registrationConfirmedStatus.getAccount().getId());
        assertThat(registrationCreated.getSession().getId())
                .isEqualTo(registrationConfirmedStatus.getSession().getId());
        assertThat(registrationCreated.getSession().getConfirmedSeats())
                .isEqualTo(registrationConfirmedStatus.getSession().getConfirmedSeats());
        assertThat(registrationCreated.getRegistrationStatus())
                .isEqualTo(registrationConfirmedStatus.getRegistrationStatus());
        assertThat(registrationCreated.getDate())
                .isEqualTo(registrationConfirmedStatus.getDate());
        assertThat(registrationsInWaitingList)
                .extracting(Registration::getRegistrationStatus)
                .contains(RegistrationStatus.CANCELED_BY_SYSTEM);
    }

    @Test
    public void create3_ThrowsException_WhenAccountDoesNotExist() {
        UUID sessionId = registrationConfirmedStatus.getSession().getId();
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = (NoSuchElementException) catchThrowable(
                () -> registrationService.create(registrationCreateDto, sessionId)
        );

        assertThat(exception).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void create3_ThrowsException_WhenSessionDoesNotExist() {
        UUID sessionId = registrationConfirmedStatus.getSession().getId();
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = (NoSuchElementException) catchThrowable(
                () -> registrationService.create(registrationCreateDto, sessionId)
        );

        assertThat(exception).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void create3_ThrowsException_WhenSessionIsCancelled() {
        Session sessionCancelled = registrationConfirmedStatus.getSession();
        sessionCancelled.setCanceled(true);
        sessionCancelled.setCancellationMessage("Mensagem de cancelamento de exemplo");
        UUID sessionId = sessionCancelled.getId();
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(sessionCancelled));

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.create(registrationCreateDto, sessionId)
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_CREATE_WITH_CANCELED_SESSION);
    }

    @Test
    public void create3_ThrowsException_WhenActivityIsCanceled() {
        //Não faz sentido essa verificação
        registrationConfirmedStatus.getSession().getActivity().setStatus(EventStatus.CANCELED);
        registrationConfirmedStatus.getSession().getActivity()
                .setCancellationMessage("Mensagem de cancelamento de exemplo");
        UUID sessionId = registrationConfirmedStatus.getSession().getId();
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getSession()));

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.create(registrationCreateDto, sessionId)
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_CREATE_WITH_ACTIVITY_CANCELED);
    }

    @Test
    public void create3_ThrowsException_WhenItIsOutOfRegistrationPeriod() {
        //Datas podem deixar o teste incorreto
        UUID sessionId = registrationConfirmedStatus.getSession().getId();
        Period period = new Period(
                LocalDate.of(2024, 11, 29),
                LocalDate.of(2024, 11, 30)
        );
        registrationConfirmedStatus.getSession().getActivity().getEvent()
                .setRegistrationPeriod(period);
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getSession()));

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.create(registrationCreateDto, sessionId)
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_CREATE_WITH_EVENT_OUT_OF_REGISTRATION_PERIOD);
    }

    @Test
    public void create3_ThrowsException_WhenAccountAlreadyHasARegistrationInActivity() {
        UUID sessionId = registrationConfirmedStatus.getSession().getId();
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getSession()));
        when(registrationRepository.existsByAccountIdAndActivityId(any(UUID.class), any(UUID.class)))
                .thenReturn(true);

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.create(registrationCreateDto, sessionId)
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_CREATE_ACCOUNT_ALREADY_HAS_REGISTRATION_IN_ACTIVITY);
    }

    @Test
    public void create3_ThrowsException_WhenSessionHasAlreadyStarted() {
        UUID sessionId = registrationConfirmedStatus.getSession().getId();
        List<SessionSchedule> sessionSchedulesStarted = List.of(
                SessionScheduleFactory.sampleSessionScheduleThatStarted()
        );
        registrationConfirmedStatus.getSession().setSessionSchedules(sessionSchedulesStarted);
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getSession()));
        when(registrationRepository.existsByAccountIdAndActivityId(any(UUID.class), any(UUID.class)))
                .thenReturn(false);

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.create(registrationCreateDto, sessionId)
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_CREATE_WITH_SESSION_STARTED);
    }

    @Test
    public void create3_ThrowsException_WhenAccountAlreadyHasARegistrationInSession() {
        UUID sessionId = registrationConfirmedStatus.getSession().getId();
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getSession()));
        when(registrationRepository.existsByAccountIdAndActivityId(any(UUID.class), any(UUID.class)))
                .thenReturn(false);
        when(registrationRepository.existsBySessionIdAndAccountIdAndRegistrationStatusIn(
                any(UUID.class),
                any(UUID.class),
                anyList()
        )).thenReturn(true);

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.create(registrationCreateDto, sessionId)
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_CREATE_ALREADY_EXISTS);
    }

    @Test
    public void create3_ThrowsException_WhenExistsAnyRegistrationWithConflict() {
        UUID sessionId = registrationConfirmedStatus.getSession().getId();
        List<Registration> registrationsInConflict = List.of(registrationConfirmedStatus);
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getSession()));
        when(registrationRepository.existsByAccountIdAndActivityId(any(UUID.class), any(UUID.class)))
                .thenReturn(false);
        when(registrationRepository.existsBySessionIdAndAccountIdAndRegistrationStatusIn(
                any(UUID.class),
                any(UUID.class),
                anyList()
        )).thenReturn(false);
        when(registrationRepository.findAllByAccountIdAndRegistrationStatusInAndDate(
                any(UUID.class),
                any(LocalDateTime.class),
                anyList()
        )).thenReturn(registrationsInConflict);

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.create(registrationCreateDto, sessionId)
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_CREATE_HAS_SCHEDULE_CONFLICT);
    }

    @Test
    public void create3_ThrowsException_WhenSessionIsFull() {
        UUID eventId = registrationConfirmedStatus.getSession().getActivity().getEvent().getId();
        UUID activityId =  registrationConfirmedStatus.getSession().getActivity().getId();
        UUID sessionId = registrationConfirmedStatus.getSession().getId();
        List<Registration> registrations = List.of(
                RegistrationFactory.sampleRegistrationWithConfirmedStatusInOtherSession()
        );
        registrationConfirmedStatus.getSession().setConfirmedSeats(
                registrationConfirmedStatus.getSession().getSeats()
        );
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getSession()));
        when(registrationRepository.existsByAccountIdAndActivityId(any(UUID.class), any(UUID.class)))
                .thenReturn(false);
        when(registrationRepository.existsBySessionIdAndAccountIdAndRegistrationStatusIn(
                any(UUID.class),
                any(UUID.class),
                anyList()
        )).thenReturn(false);
        when(registrationRepository.findAllByAccountIdAndRegistrationStatusInAndDate(
                any(UUID.class),
                any(LocalDateTime.class),
                anyList()
        )).thenReturn(registrations);

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.create(registrationCreateDto, eventId, activityId, sessionId)
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_CREATE_WITH_NO_SEATS_AVAILABLE);
    }

    @Test
    public void create3_ReturnsRegistration_WhenSuccessful() {
        UUID sessionId = registrationConfirmedStatus.getSession().getId();
        List<Registration> registrations = List.of(
                RegistrationFactory.sampleRegistrationWithConfirmedStatusInOtherSession()
        );
        List<Registration> registrationsInWaitingList = List.of(
                RegistrationFactory.sampleRegistrationWithWaitingListStatus()
        );
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getSession()));
        when(registrationRepository.existsByAccountIdAndActivityId(any(UUID.class), any(UUID.class)))
                .thenReturn(false);
        when(registrationRepository.existsBySessionIdAndAccountIdAndRegistrationStatusIn(
                any(UUID.class),
                any(UUID.class),
                anyList()
        )).thenReturn(false);
        when(registrationRepository.findAllByAccountIdAndRegistrationStatusInAndDate(
                any(UUID.class),
                any(LocalDateTime.class),
                anyList()
        )).thenReturn(registrations).thenReturn(registrationsInWaitingList);
        when(registrationRepository.save(any(Registration.class)))
                .thenReturn(registrationConfirmedStatus);
        when(registrationRepository.findAllByAccountIdAndSessionIdIn(
                any(UUID.class),
                anyList()
        )).thenReturn(registrationsInWaitingList);

        Registration registrationCreated = registrationService.create(registrationCreateDto, sessionId);

        verify(registrationRepository, times(1)).save(any(Registration.class));
        verify(sessionRepository, times(1)).save(any(Session.class));
        verify(registrationRepository, times(1)).saveAll(anyList());
        assertThat(registrationCreated).isNotNull();
        assertThat(registrationCreated.getId()).isEqualTo(registrationConfirmedStatus.getId());
        assertThat(registrationCreated.getAccount().getId())
                .isEqualTo(registrationConfirmedStatus.getAccount().getId());
        assertThat(registrationCreated.getSession().getId())
                .isEqualTo(registrationConfirmedStatus.getSession().getId());
        assertThat(registrationCreated.getSession().getConfirmedSeats())
                .isEqualTo(registrationConfirmedStatus.getSession().getConfirmedSeats());
        assertThat(registrationCreated.getRegistrationStatus())
                .isEqualTo(registrationConfirmedStatus.getRegistrationStatus());
        assertThat(registrationCreated.getDate())
                .isEqualTo(registrationConfirmedStatus.getDate());
        assertThat(registrationsInWaitingList)
                .extracting(Registration::getRegistrationStatus)
                .contains(RegistrationStatus.CANCELED_BY_SYSTEM);
    }

    private void mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(UUID eventId) {
        JwtUserDetails userDetails = JwtUserDetailsFactory.sampleJwtUserDetailsThatIsOrganizer(eventId);
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
    }

    private void mocksAuthenticationWithCurrentUserThatIsNotAuthorized() {
        JwtUserDetails userDetails = JwtUserDetailsFactory.sampleJwtUserDetailsThatIsNotOrganizer();
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
    }

    private RegistrationCreateDto sampleRegistrationCreateDto() {
        return new RegistrationCreateDto(
                UUID.randomUUID()
        );
    }
}

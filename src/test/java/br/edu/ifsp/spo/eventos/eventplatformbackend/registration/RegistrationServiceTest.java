package br.edu.ifsp.spo.eventos.eventplatformbackend.registration;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.ActivityFactory;
import br.edu.ifsp.spo.eventos.eventplatformbackend.attendance.AttendanceRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.email.EmailService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.BusinessRuleException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.BusinessRuleType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceName;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceNotFoundException;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
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
//    @Mock
//    private EmailConfirmationTime emailConfirmationTime;
    @InjectMocks
    private RegistrationService registrationService;
    private Registration registrationConfirmedStatus;
    private Registration registrationWaitingListStatus;
    private Registration registrationCanceledByAdmin;
    private Registration registrationWaitingConfirmation;
    private RegistrationCreateDto registrationCreateDto;

    @BeforeEach
    public void setUp() {
        registrationConfirmedStatus = RegistrationFactory.sampleRegistrationWithConfirmedStatus();
        registrationWaitingListStatus = RegistrationFactory.sampleRegistrationWithWaitingListStatus();
        registrationCanceledByAdmin = RegistrationFactory.sampleRegistrationWithCanceledByAdminStatus();
        registrationWaitingConfirmation = RegistrationFactory.sampleRegistrationWithWaitingConfirmationStatus();
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
        UUID eventId = UUID.randomUUID();
        UUID activityId = registrationConfirmedStatus.getSession().getActivity().getId();
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
                .isEqualTo(BusinessRuleType.ACTIVITY_IS_NOT_ASSOCIATED_TO_EVENT);
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

//    @Test
//    public void create3_ThrowsException_WhenItIsOutOfRegistrationPeriod() {
//        //TODO: Utilizar Clock fixo para as datas
//        UUID sessionId = registrationConfirmedStatus.getSession().getId();
//        Period period = new Period(
//                LocalDate.of(2024, 11, 29),
//                LocalDate.of(2024, 11, 30)
//        );
//        registrationConfirmedStatus.getSession().getActivity().getEvent()
//                .setRegistrationPeriod(period);
//        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
//                .thenReturn(Optional.of(registrationConfirmedStatus.getAccount()));
//        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
//                .thenReturn(Optional.of(registrationConfirmedStatus.getSession()));
//
//        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
//                () -> registrationService.create(registrationCreateDto, sessionId)
//        );
//
//        assertThat(exception).isInstanceOf(BusinessRuleException.class);
//        assertThat(exception.getBusinessRuleType())
//                .isEqualTo(BusinessRuleType.REGISTRATION_CREATE_WITH_EVENT_OUT_OF_REGISTRATION_PERIOD);
//    }

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

    @Test
    public void createRegistrationInWaitList_ThrowsException_WhenCurrentUserHasNoPermissionForEvent() {
        UUID eventId = registrationWaitingListStatus.getSession().getActivity().getEvent().getId();
        UUID activityId = registrationWaitingListStatus.getSession().getActivity().getId();
        UUID sessionId = registrationWaitingListStatus.getSession().getId();
        mocksAuthenticationWithCurrentUserThatIsNotAuthorized();

        OrganizerAuthorizationException exception = (OrganizerAuthorizationException) catchThrowable(
                () -> registrationService.createRegistrationInWaitList(
                        registrationCreateDto,
                        eventId,
                        activityId,
                        sessionId
                )
        );

        assertThat(exception).isInstanceOf(OrganizerAuthorizationException.class);
        assertThat(exception.getOrganizerAuthorizationExceptionType())
                .isEqualTo(OrganizerAuthorizationExceptionType.UNAUTHORIZED_EVENT);
        assertThat(exception.getResourceId()).isEqualTo(eventId);
    }

    @Test
    public void createRegistrationInWaitList_ThrowsException_WhenAccountDoesNotExist() {
        UUID eventId = registrationWaitingListStatus.getSession().getActivity().getEvent().getId();
        UUID activityId = registrationWaitingListStatus.getSession().getActivity().getId();
        UUID sessionId = registrationWaitingListStatus.getSession().getId();
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = (NoSuchElementException) catchThrowable(
                () -> registrationService.createRegistrationInWaitList(
                        registrationCreateDto,
                        eventId,
                        activityId,
                        sessionId
                )
        );

        assertThat(exception).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void createRegistrationInWaitList_ThrowsException_WhenSessionDoesNotExist() {
        UUID eventId = registrationWaitingListStatus.getSession().getActivity().getEvent().getId();
        UUID activityId = registrationWaitingListStatus.getSession().getActivity().getId();
        UUID sessionId = registrationWaitingListStatus.getSession().getId();
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = (NoSuchElementException) catchThrowable(
                () -> registrationService.createRegistrationInWaitList(
                        registrationCreateDto,
                        eventId,
                        activityId,
                        sessionId
                )
        );

        assertThat(exception).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void createRegistrationInWaitList_ThrowsException_WhenEventIsNotAssociateToActivity() {
        UUID eventId = UUID.randomUUID();
        UUID activityId = registrationWaitingListStatus.getSession().getActivity().getId();
        UUID sessionId = registrationWaitingListStatus.getSession().getId();
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getSession()));

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.createRegistrationInWaitList(
                        registrationCreateDto,
                        eventId,
                        activityId,
                        sessionId
                )
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.ACTIVITY_IS_NOT_ASSOCIATED_TO_EVENT);
    }

    @Test
    public void createRegistrationInWaitList_ThrowsException_WhenActivityIsNotAssociateToSession() {
        UUID eventId = registrationWaitingListStatus.getSession().getActivity().getEvent().getId();
        UUID activityId = ActivityFactory.sampleActivity2().getId();
        UUID sessionId = registrationWaitingListStatus.getSession().getId();
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getSession()));

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.createRegistrationInWaitList(
                        registrationCreateDto,
                        eventId,
                        activityId,
                        sessionId
                )
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.SESSION_IS_NOT_ASSOCIATED_TO_ACTIVITY);
    }

    @Test
    public void createRegistrationInWaitList_ThrowsException_WhenSessionIsCancelled() {
        UUID eventId = registrationWaitingListStatus.getSession().getActivity().getEvent().getId();
        UUID activityId =  registrationWaitingListStatus.getSession().getActivity().getId();
        Session sessionCancelled = registrationWaitingListStatus.getSession();
        sessionCancelled.setCanceled(true);
        sessionCancelled.setCancellationMessage("Mensagem de cancelamento de exemplo");
        UUID sessionId = sessionCancelled.getId();
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(sessionCancelled));

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.createRegistrationInWaitList(
                        registrationCreateDto,
                        eventId,
                        activityId,
                        sessionId
                )
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_CREATE_WITH_CANCELED_SESSION);
    }

    @Test
    public void createRegistrationInWaitList_ThrowsException_WhenActivityIsCanceled() {
        UUID eventId = registrationWaitingListStatus.getSession().getActivity().getEvent().getId();
        UUID activityId = registrationWaitingListStatus.getSession().getActivity().getId();
        UUID sessionId = registrationWaitingListStatus.getSession().getId();
        registrationWaitingListStatus.getSession().getActivity().setStatus(EventStatus.CANCELED);
        registrationWaitingListStatus.getSession().getActivity()
                .setCancellationMessage("Mensagem de cancelamento de exemplo");
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getSession()));

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.createRegistrationInWaitList(
                        registrationCreateDto,
                        eventId,
                        activityId,
                        sessionId
                )
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_CREATE_WITH_ACTIVITY_CANCELED);
    }

//    @Test
//    public void createRegistrationInWaitList_ThrowsException_WhenItIsOutOfRegistrationPeriod() {
//        //TODO: Utilizar Clock fixo para as datas
//        UUID eventId = registrationWaitingListStatus.getSession().getActivity().getEvent().getId();
//        UUID activityId = registrationWaitingListStatus.getSession().getActivity().getId();
//        UUID sessionId = registrationWaitingListStatus.getSession().getId();
//        Period period = new Period(
//                LocalDate.of(2024, 11, 29),
//                LocalDate.of(2024, 11, 30)
//        );
//        registrationWaitingListStatus.getSession().getActivity().getEvent()
//                .setRegistrationPeriod(period);
//        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
//        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
//                .thenReturn(Optional.of(registrationWaitingListStatus.getAccount()));
//        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
//                .thenReturn(Optional.of(registrationWaitingListStatus.getSession()));
//
//        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
//                () -> registrationService.createRegistrationInWaitList(
//                        registrationCreateDto,
//                        eventId,
//                        activityId,
//                        sessionId
//                )
//        );
//
//        assertThat(exception).isInstanceOf(BusinessRuleException.class);
//        assertThat(exception.getBusinessRuleType())
//                .isEqualTo(BusinessRuleType.REGISTRATION_CREATE_WITH_EVENT_OUT_OF_REGISTRATION_PERIOD);
//    }

    @Test
    public void createRegistrationInWaitList_ThrowsException_WhenAccountAlreadyHasARegistrationInActivity() {
        UUID eventId = registrationWaitingListStatus.getSession().getActivity().getEvent().getId();
        UUID activityId =  registrationWaitingListStatus.getSession().getActivity().getId();
        UUID sessionId = registrationWaitingListStatus.getSession().getId();
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getSession()));
        when(registrationRepository.existsByAccountIdAndActivityId(any(UUID.class), any(UUID.class)))
                .thenReturn(true);

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.createRegistrationInWaitList(
                        registrationCreateDto,
                        eventId,
                        activityId,
                        sessionId
                )
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_CREATE_ACCOUNT_ALREADY_HAS_REGISTRATION_IN_ACTIVITY);
    }

    @Test
    public void createRegistrationInWaitList_ThrowsException_WhenSessionHasAlreadyStarted() {
        UUID eventId = registrationWaitingListStatus.getSession().getActivity().getEvent().getId();
        UUID activityId =  registrationWaitingListStatus.getSession().getActivity().getId();
        UUID sessionId = registrationWaitingListStatus.getSession().getId();
        List<SessionSchedule> sessionSchedulesStarted = List.of(
                SessionScheduleFactory.sampleSessionScheduleThatStarted()
        );
        registrationWaitingListStatus.getSession().setSessionSchedules(sessionSchedulesStarted);
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getSession()));
        when(registrationRepository.existsByAccountIdAndActivityId(any(UUID.class), any(UUID.class)))
                .thenReturn(false);

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.createRegistrationInWaitList(
                        registrationCreateDto,
                        eventId,
                        activityId,
                        sessionId
                )
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_CREATE_WITH_SESSION_STARTED);
    }

    @Test
    public void createRegistrationInWaitList_ThrowsException_WhenAccountAlreadyHasARegistrationInSession() {
        UUID eventId = registrationWaitingListStatus.getSession().getActivity().getEvent().getId();
        UUID activityId =  registrationWaitingListStatus.getSession().getActivity().getId();
        UUID sessionId = registrationWaitingListStatus.getSession().getId();
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getSession()));
        when(registrationRepository.existsByAccountIdAndActivityId(any(UUID.class), any(UUID.class)))
                .thenReturn(false);
        when(registrationRepository.existsBySessionIdAndAccountIdAndRegistrationStatusIn(
                any(UUID.class),
                any(UUID.class),
                anyList()
        )).thenReturn(true);

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () ->  registrationService.createRegistrationInWaitList(
                        registrationCreateDto,
                        eventId,
                        activityId,
                        sessionId
                )
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_CREATE_ALREADY_EXISTS);
    }

    @Test
    public void createRegistrationInWaitList_ThrowsException_WhenExistsAnyRegistrationWithConflict() {
        UUID eventId = registrationWaitingListStatus.getSession().getActivity().getEvent().getId();
        UUID activityId =  registrationWaitingListStatus.getSession().getActivity().getId();
        UUID sessionId = registrationWaitingListStatus.getSession().getId();
        List<Registration> registrationsInConflict = List.of(registrationWaitingListStatus);
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getSession()));
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
                () -> registrationService.createRegistrationInWaitList(
                        registrationCreateDto,
                        eventId,
                        activityId,
                        sessionId
                )
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_CREATE_HAS_SCHEDULE_CONFLICT);
    }

    @Test
    public void createRegistrationInWaitList_ThrowsException_WhenSessionIsNotFull() {
        UUID eventId = registrationWaitingListStatus.getSession().getActivity().getEvent().getId();
        UUID activityId =  registrationWaitingListStatus.getSession().getActivity().getId();
        UUID sessionId = registrationWaitingListStatus.getSession().getId();
        List<Registration> registrations = List.of(
                RegistrationFactory.sampleRegistrationWithConfirmedStatusInOtherSession()
        );
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getSession()));
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
                () -> registrationService.createRegistrationInWaitList(
                        registrationCreateDto,
                        eventId,
                        activityId,
                        sessionId
                )
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_CREATE_IN_WAIT_LIST_WITH_SEATS_VAILABLE);
    }

    @Test
    public void createRegistrationInWaitList_ReturnsRegistration_WhenSuccessful() {
        UUID eventId = registrationWaitingListStatus.getSession().getActivity().getEvent().getId();
        UUID activityId =  registrationWaitingListStatus.getSession().getActivity().getId();
        UUID sessionId = registrationWaitingListStatus.getSession().getId();
        List<Registration> registrations = List.of(
                RegistrationFactory.sampleRegistrationWithConfirmedStatusInOtherSession()
        );
        registrationWaitingListStatus.getSession().setConfirmedSeats(
                registrationWaitingListStatus.getSession().getSeats()
        );
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getSession()));
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
        when(registrationRepository.save(any(Registration.class)))
                .thenReturn(registrationWaitingListStatus);

        Registration registrationCreated = registrationService.createRegistrationInWaitList(
                registrationCreateDto,
                eventId,
                activityId,
                sessionId
        );

        verify(registrationRepository, times(1)).save(any(Registration.class));
        assertThat(registrationCreated).isNotNull();
        assertThat(registrationCreated.getId()).isEqualTo(registrationWaitingListStatus.getId());
        assertThat(registrationCreated.getAccount().getId())
                .isEqualTo(registrationWaitingListStatus.getAccount().getId());
        assertThat(registrationCreated.getSession().getId())
                .isEqualTo(registrationWaitingListStatus.getSession().getId());
        assertThat(registrationCreated.getRegistrationStatus())
                .isEqualTo(registrationWaitingListStatus.getRegistrationStatus());
        assertThat(registrationCreated.getDate())
                .isEqualTo(registrationWaitingListStatus.getDate());
    }

    @Test
    public void createRegistrationInWaitList3_ThrowsException_WhenAccountDoesNotExist() {
        UUID sessionId = registrationWaitingListStatus.getSession().getId();
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = (NoSuchElementException) catchThrowable(
                () -> registrationService.createRegistrationInWaitList(
                        registrationCreateDto,
                        sessionId
                )
        );

        assertThat(exception).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void createRegistrationInWaitList3_ThrowsException_WhenSessionDoesNotExist() {
        UUID sessionId = registrationWaitingListStatus.getSession().getId();
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = (NoSuchElementException) catchThrowable(
                () -> registrationService.createRegistrationInWaitList(
                        registrationCreateDto,
                        sessionId
                )
        );

        assertThat(exception).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void createRegistrationInWaitList3_ThrowsException_WhenSessionIsCancelled() {
        Session sessionCancelled = registrationWaitingListStatus.getSession();
        sessionCancelled.setCanceled(true);
        sessionCancelled.setCancellationMessage("Mensagem de cancelamento de exemplo");
        UUID sessionId = sessionCancelled.getId();
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(sessionCancelled));

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.createRegistrationInWaitList(
                        registrationCreateDto,
                        sessionId
                )
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_CREATE_WITH_CANCELED_SESSION);
    }

    @Test
    public void createRegistrationInWaitList3_ThrowsException_WhenActivityIsCanceled() {
        registrationWaitingListStatus.getSession().getActivity().setStatus(EventStatus.CANCELED);
        registrationWaitingListStatus.getSession().getActivity()
                .setCancellationMessage("Mensagem de cancelamento de exemplo");
        UUID sessionId = registrationWaitingListStatus.getSession().getId();
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getSession()));

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () ->  registrationService.createRegistrationInWaitList(
                        registrationCreateDto,
                        sessionId
                )
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_CREATE_WITH_ACTIVITY_CANCELED);
    }

//    @Test
//    public void createRegistrationInWaitList3_ThrowsException_WhenItIsOutOfRegistrationPeriod() {
//        //TODO: Utilizar Clock fixo para as datas
//        UUID sessionId = registrationWaitingListStatus.getSession().getId();
//        Period period = new Period(
//                LocalDate.of(2024, 11, 29),
//                LocalDate.of(2024, 11, 30)
//        );
//        registrationWaitingListStatus.getSession().getActivity().getEvent()
//                .setRegistrationPeriod(period);
//        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
//                .thenReturn(Optional.of(registrationWaitingListStatus.getAccount()));
//        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
//                .thenReturn(Optional.of(registrationWaitingListStatus.getSession()));
//
//        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
//                () -> registrationService.createRegistrationInWaitList(
//                        registrationCreateDto,
//                        sessionId
//                )
//        );
//
//        assertThat(exception).isInstanceOf(BusinessRuleException.class);
//        assertThat(exception.getBusinessRuleType())
//                .isEqualTo(BusinessRuleType.REGISTRATION_CREATE_WITH_EVENT_OUT_OF_REGISTRATION_PERIOD);
//    }

    @Test
    public void createRegistrationInWaitList3_ThrowsException_WhenAccountAlreadyHasARegistrationInSession() {
        UUID sessionId = registrationWaitingListStatus.getSession().getId();
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getSession()));
        when(registrationRepository.existsBySessionIdAndAccountIdAndRegistrationStatusIn(
                any(UUID.class),
                any(UUID.class),
                anyList()
        )).thenReturn(true);

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.createRegistrationInWaitList(
                        registrationCreateDto,
                        sessionId
                )
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_CREATE_ALREADY_EXISTS);
    }

    @Test
    public void createRegistrationInWaitList3_ThrowsException_WhenAccountAlreadyHasARegistrationInActivity() {
        UUID sessionId = registrationWaitingListStatus.getSession().getId();
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getSession()));
        when(registrationRepository.existsBySessionIdAndAccountIdAndRegistrationStatusIn(
                any(UUID.class),
                any(UUID.class),
                anyList()
        )).thenReturn(false);
        when(registrationRepository.existsByAccountIdAndActivityId(any(UUID.class), any(UUID.class)))
                .thenReturn(true);

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.createRegistrationInWaitList(
                        registrationCreateDto,
                        sessionId
                )
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_CREATE_ACCOUNT_ALREADY_HAS_REGISTRATION_IN_ACTIVITY);
    }

    @Test
    public void createRegistrationInWaitList3_ThrowsException_WhenSessionHasAlreadyStarted() {
        UUID sessionId = registrationWaitingListStatus.getSession().getId();
        List<SessionSchedule> sessionSchedulesStarted = List.of(
                SessionScheduleFactory.sampleSessionScheduleThatStarted()
        );
        registrationWaitingListStatus.getSession().setSessionSchedules(sessionSchedulesStarted);
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getSession()));
        when(registrationRepository.existsBySessionIdAndAccountIdAndRegistrationStatusIn(
                any(UUID.class),
                any(UUID.class),
                anyList()
        )).thenReturn(false);
        when(registrationRepository.existsByAccountIdAndActivityId(any(UUID.class), any(UUID.class)))
                .thenReturn(false);

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.createRegistrationInWaitList(
                        registrationCreateDto,
                        sessionId
                )
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_CREATE_WITH_SESSION_STARTED);
    }

    @Test
    public void createRegistrationInWaitList3_ThrowsException_WhenExistsAnyRegistrationWithConflict() {
        UUID sessionId = registrationWaitingListStatus.getSession().getId();
        List<Registration> registrationsInConflict = List.of(registrationWaitingListStatus);
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getSession()));
        when(registrationRepository.existsBySessionIdAndAccountIdAndRegistrationStatusIn(
                any(UUID.class),
                any(UUID.class),
                anyList()
        )).thenReturn(false);
        when(registrationRepository.existsByAccountIdAndActivityId(any(UUID.class), any(UUID.class)))
                .thenReturn(false);
        when(registrationRepository.findAllByAccountIdAndRegistrationStatusInAndDate(
                any(UUID.class),
                any(LocalDateTime.class),
                anyList()
        )).thenReturn(registrationsInConflict);

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.createRegistrationInWaitList(
                        registrationCreateDto,
                        sessionId
                )
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_CREATE_HAS_SCHEDULE_CONFLICT);
    }

    @Test
    public void createRegistrationInWaitList3_ThrowsException_WhenSessionIsNotFull() {
        UUID sessionId = registrationWaitingListStatus.getSession().getId();
        List<Registration> registrations = List.of(
                RegistrationFactory.sampleRegistrationWithConfirmedStatusInOtherSession()
        );
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getSession()));
        when(registrationRepository.existsBySessionIdAndAccountIdAndRegistrationStatusIn(
                any(UUID.class),
                any(UUID.class),
                anyList()
        )).thenReturn(false);
        when(registrationRepository.existsByAccountIdAndActivityId(any(UUID.class), any(UUID.class)))
                .thenReturn(false);
        when(registrationRepository.findAllByAccountIdAndRegistrationStatusInAndDate(
                any(UUID.class),
                any(LocalDateTime.class),
                anyList()
        )).thenReturn(registrations);

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.createRegistrationInWaitList(
                        registrationCreateDto,
                        sessionId
                )
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_CREATE_IN_WAIT_LIST_WITH_SEATS_VAILABLE);
    }

    @Test
    public void createRegistrationInWaitList3_ReturnsRegistration_WhenSuccessful() {
        UUID sessionId = registrationWaitingListStatus.getSession().getId();
        List<Registration> registrations = List.of(
                RegistrationFactory.sampleRegistrationWithConfirmedStatusInOtherSession()
        );
        registrationWaitingListStatus.getSession().setConfirmedSeats(
                registrationWaitingListStatus.getSession().getSeats()
        );
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getAccount()));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getSession()));
        when(registrationRepository.existsBySessionIdAndAccountIdAndRegistrationStatusIn(
                any(UUID.class),
                any(UUID.class),
                anyList()
        )).thenReturn(false);
        when(registrationRepository.existsByAccountIdAndActivityId(any(UUID.class), any(UUID.class)))
                .thenReturn(false);
        when(registrationRepository.findAllByAccountIdAndRegistrationStatusInAndDate(
                any(UUID.class),
                any(LocalDateTime.class),
                anyList()
        )).thenReturn(registrations);
        when(registrationRepository.save(any(Registration.class)))
                .thenReturn(registrationWaitingListStatus);

        Registration registrationCreated = registrationService.createRegistrationInWaitList(
                        registrationCreateDto,
                        sessionId
        );

        verify(registrationRepository, times(1)).save(any(Registration.class));
        assertThat(registrationCreated).isNotNull();
        assertThat(registrationCreated.getId()).isEqualTo(registrationWaitingListStatus.getId());
        assertThat(registrationCreated.getAccount().getId())
                .isEqualTo(registrationWaitingListStatus.getAccount().getId());
        assertThat(registrationCreated.getSession().getId())
                .isEqualTo(registrationWaitingListStatus.getSession().getId());
        assertThat(registrationCreated.getRegistrationStatus())
                .isEqualTo(registrationWaitingListStatus.getRegistrationStatus());
        assertThat(registrationCreated.getDate())
                .isEqualTo(registrationWaitingListStatus.getDate());
    }

    @Test
    public void cancel_ThrowsException_WhenCurrentUserHasNoPermissionForEvent() {
        UUID eventId = registrationConfirmedStatus.getSession().getActivity().getEvent().getId();
        UUID activityId = registrationConfirmedStatus.getSession().getActivity().getId();
        UUID sessionId = registrationConfirmedStatus.getSession().getId();
        UUID registrationId = registrationConfirmedStatus.getId();
        mocksAuthenticationWithCurrentUserThatIsNotAuthorized();

        OrganizerAuthorizationException exception = (OrganizerAuthorizationException) catchThrowable(
                () -> registrationService.cancel(eventId, activityId, sessionId, registrationId)
        );

        assertThat(exception).isInstanceOf(OrganizerAuthorizationException.class);
        assertThat(exception.getOrganizerAuthorizationExceptionType())
                .isEqualTo(OrganizerAuthorizationExceptionType.UNAUTHORIZED_EVENT);
        assertThat(exception.getResourceId()).isEqualTo(eventId);
    }

    @Test
    public void cancel_ThrowsException_WhenRegistrationDoesNotExist() {
        UUID eventId = registrationConfirmedStatus.getSession().getActivity().getEvent().getId();
        UUID activityId = registrationConfirmedStatus.getSession().getActivity().getId();
        UUID sessionId = registrationConfirmedStatus.getSession().getId();
        UUID registrationId = registrationConfirmedStatus.getId();
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(registrationRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        ResourceNotFoundException exception = (ResourceNotFoundException) catchThrowable(
                () -> registrationService.cancel(eventId, activityId, sessionId, registrationId)
        );

        assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
        assertThat(exception.getResourceName()).isEqualTo(ResourceName.REGISTRATION);
        assertThat(exception.getResourceId()).isEqualTo(registrationId.toString());
    }

    @Test
    public void cancel_ThrowsException_WhenEventIsNotAssociateToActivity() {
        UUID eventId = UUID.randomUUID();
        UUID activityId = registrationConfirmedStatus.getSession().getActivity().getId();
        UUID sessionId = registrationConfirmedStatus.getSession().getId();
        UUID registrationId = registrationConfirmedStatus.getId();
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(registrationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getSession()));

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.cancel(eventId, activityId, sessionId, registrationId)
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.ACTIVITY_IS_NOT_ASSOCIATED_TO_EVENT);
    }

    @Test
    public void cancel_ThrowsException_WhenSessionIsNotAssociateToRegistration() {
        UUID eventId = registrationConfirmedStatus.getSession().getActivity().getEvent().getId();
        UUID activityId = registrationConfirmedStatus.getSession().getActivity().getId();
        UUID sessionId = UUID.randomUUID();
        UUID registrationId = registrationConfirmedStatus.getId();
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(registrationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getSession()));

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.cancel(eventId, activityId, sessionId, registrationId)
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_IS_NOT_ASSOCIATED_TO_SESSION);
    }

    @Test
    public void cancel_ThrowsException_WhenRegistrationHasAttendanceAssociated() {
        UUID eventId = registrationConfirmedStatus.getSession().getActivity().getEvent().getId();
        UUID activityId = registrationConfirmedStatus.getSession().getActivity().getId();
        UUID sessionId = registrationConfirmedStatus.getSession().getId();
        UUID registrationId = registrationConfirmedStatus.getId();
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(registrationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getSession()));
        when(attendanceRepository.existsByRegistrationId(any(UUID.class)))
                .thenReturn(true);

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.cancel(eventId, activityId, sessionId, registrationId)
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_HAS_ATTENDANCE);
    }

    @Test
    public void cancel_ReturnsRegistrationCanceled_WhenRegistrationIsInWaitingList() {
        UUID eventId = registrationWaitingListStatus.getSession().getActivity().getEvent().getId();
        UUID activityId = registrationWaitingListStatus.getSession().getActivity().getId();
        UUID sessionId = registrationWaitingListStatus.getSession().getId();
        UUID registrationId = registrationWaitingListStatus.getId();
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(registrationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getSession()));
        when(attendanceRepository.existsByRegistrationId(any(UUID.class)))
                .thenReturn(false);
        when(registrationRepository.save(any(Registration.class)))
                .thenReturn(registrationWaitingListStatus);

        Registration registrationCreated = registrationService.cancel(
                eventId,
                activityId,
                sessionId,
                registrationId
        );

        verify(registrationRepository, times(1)).save(any(Registration.class));
        assertThat(registrationCreated).isNotNull();
        assertThat(registrationCreated.getId()).isEqualTo(registrationWaitingListStatus.getId());
        assertThat(registrationCreated.getAccount().getId())
                .isEqualTo(registrationWaitingListStatus.getAccount().getId());
        assertThat(registrationCreated.getSession().getId())
                .isEqualTo(registrationWaitingListStatus.getSession().getId());
        assertThat(registrationCreated.getRegistrationStatus())
                .isEqualTo(RegistrationStatus.CANCELED_BY_ADMIN);
        assertThat(registrationCreated.getDate())
                .isEqualTo(registrationWaitingListStatus.getDate());
    }

    @Test
    public void cancel_ReturnsRegistrationCanceled_WhenSessionHasStartedAndRegistrationIsConfirmed() {
        UUID eventId = registrationConfirmedStatus.getSession().getActivity().getEvent().getId();
        UUID activityId = registrationConfirmedStatus.getSession().getActivity().getId();
        UUID sessionId = registrationConfirmedStatus.getSession().getId();
        UUID registrationId = registrationConfirmedStatus.getId();
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        List<SessionSchedule> sessionSchedulesStarted = List.of(
                SessionScheduleFactory.sampleSessionScheduleThatStarted()
        );
        registrationConfirmedStatus.getSession().setSessionSchedules(sessionSchedulesStarted);
        when(registrationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getSession()));
        when(attendanceRepository.existsByRegistrationId(any(UUID.class)))
                .thenReturn(false);
        when(registrationRepository.save(any(Registration.class)))
                .thenReturn(registrationConfirmedStatus);

        Registration registrationCreated = registrationService.cancel(
                eventId,
                activityId,
                sessionId,
                registrationId
        );

        verify(registrationRepository, times(1)).save(any(Registration.class));
        verify(sessionRepository, times(1)).save(any(Session.class));
        assertThat(registrationCreated).isNotNull();
        assertThat(registrationCreated.getId()).isEqualTo(registrationConfirmedStatus.getId());
        assertThat(registrationCreated.getAccount().getId())
                .isEqualTo(registrationConfirmedStatus.getAccount().getId());
        assertThat(registrationCreated.getSession().getId())
                .isEqualTo(registrationConfirmedStatus.getSession().getId());
        assertThat(registrationCreated.getSession().getConfirmedSeats())
                .isEqualTo(registrationConfirmedStatus.getSession().getConfirmedSeats());
        assertThat(registrationCreated.getRegistrationStatus())
                .isEqualTo(RegistrationStatus.CANCELED_BY_ADMIN);
        assertThat(registrationCreated.getDate())
                .isEqualTo(registrationConfirmedStatus.getDate());
    }

    @Test
    public void cancel_ReturnsRegistrationCanceled_WhenSessionHasStartedAndRegistrationIsCanceled() {
        UUID eventId = registrationCanceledByAdmin.getSession().getActivity().getEvent().getId();
        UUID activityId = registrationCanceledByAdmin.getSession().getActivity().getId();
        UUID sessionId = registrationCanceledByAdmin.getSession().getId();
        UUID registrationId = registrationCanceledByAdmin.getId();
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        List<SessionSchedule> sessionSchedulesStarted = List.of(
                SessionScheduleFactory.sampleSessionScheduleThatStarted()
        );
        registrationCanceledByAdmin.getSession().setSessionSchedules(sessionSchedulesStarted);
        when(registrationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(registrationCanceledByAdmin));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationCanceledByAdmin.getSession()));
        when(attendanceRepository.existsByRegistrationId(any(UUID.class)))
                .thenReturn(false);
        when(registrationRepository.save(any(Registration.class)))
                .thenReturn(registrationCanceledByAdmin);

        Registration registrationCreated = registrationService.cancel(
                eventId,
                activityId,
                sessionId,
                registrationId
        );

        verify(registrationRepository, times(1)).save(any(Registration.class));
        assertThat(registrationCreated).isNotNull();
        assertThat(registrationCreated.getId()).isEqualTo(registrationCanceledByAdmin.getId());
        assertThat(registrationCreated.getAccount().getId())
                .isEqualTo(registrationCanceledByAdmin.getAccount().getId());
        assertThat(registrationCreated.getSession().getId())
                .isEqualTo(registrationCanceledByAdmin.getSession().getId());
        assertThat(registrationCreated.getRegistrationStatus())
                .isEqualTo(RegistrationStatus.CANCELED_BY_ADMIN);
        assertThat(registrationCreated.getDate())
                .isEqualTo(registrationCanceledByAdmin.getDate());
    }

    @Test
    public void cancel_ReturnsRegistrationCanceled_WhenExistAnyRegistrationInWaitingListAndAccountHasAllowEmail() {
        //Nesse caso, a sessão não começou, existe inscrição na lista de espera,
        // conta tem e-mail autorizado e o e-mail é enviado
        try {
            UUID eventId = registrationConfirmedStatus.getSession().getActivity().getEvent().getId();
            UUID activityId = registrationConfirmedStatus.getSession().getActivity().getId();
            UUID sessionId = registrationConfirmedStatus.getSession().getId();
            UUID registrationId = registrationConfirmedStatus.getId();
            mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
            when(registrationRepository.findById(any(UUID.class)))
                    .thenReturn(Optional.of(registrationConfirmedStatus));
            when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                    .thenReturn(Optional.of(registrationConfirmedStatus.getSession()));
            when(attendanceRepository.existsByRegistrationId(any(UUID.class)))
                    .thenReturn(false);
            when(registrationRepository.existsBySessionIdAndRegistrationStatus(
                    any(UUID.class),
                    any(RegistrationStatus.class)
            )).thenReturn(true);
            when(registrationRepository.getFirstBySessionIdAndRegistrationStatusOrderByDate(
                    any(UUID.class),
                    any(RegistrationStatus.class)
            )).thenReturn(Optional.of(registrationWaitingListStatus));
            doNothing().when(emailService).sendEmailToConfirmRegistration(
                    any(Account.class),
                    any(Registration.class)
            );
            when(registrationRepository.save(any(Registration.class)))
                    .thenReturn(registrationConfirmedStatus);

            Registration registrationCreated = registrationService.cancel(
                    eventId,
                    activityId,
                    sessionId,
                    registrationId
            );

            verify(registrationRepository, times(2)).save(any(Registration.class));
            verify(emailService, times(1))
                    .sendEmailToConfirmRegistration(any(Account.class), any(Registration.class));
            assertThat(registrationCreated).isNotNull();
            assertThat(registrationCreated.getId()).isEqualTo(registrationConfirmedStatus.getId());
            assertThat(registrationCreated.getAccount().getId())
                    .isEqualTo(registrationConfirmedStatus.getAccount().getId());
            assertThat(registrationCreated.getSession().getId())
                    .isEqualTo(registrationConfirmedStatus.getSession().getId());
            assertThat(registrationCreated.getRegistrationStatus())
                    .isEqualTo(RegistrationStatus.CANCELED_BY_ADMIN);
            assertThat(registrationCreated.getDate())
                    .isEqualTo(registrationConfirmedStatus.getDate());
            assertThat(registrationWaitingListStatus.getRegistrationStatus())
                    .isEqualTo(RegistrationStatus.WAITING_CONFIRMATION);
        } catch (MessagingException e) {
        }
    }

//    @Test
//    public void cancel_ReturnsRegistrationCanceled_WhenExistAnyRegistrationInWaitingListAndEmailIsNotSend() {
//        //TODO:  Pegar exceção lançada ao não conseguir enviar e-mail
//        //Nesse caso, a sessão não começou, existe inscrição na lista de espera,
//        // conta tem e-mail autorizado e o e-mail não é enviado.
//        try {
//            UUID eventId = registrationConfirmedStatus.getSession().getActivity().getEvent().getId();
//            UUID activityId = registrationConfirmedStatus.getSession().getActivity().getId();
//            UUID sessionId = registrationConfirmedStatus.getSession().getId();
//            UUID registrationId = registrationConfirmedStatus.getId();
//            mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
//            when(registrationRepository.findById(any(UUID.class)))
//                    .thenReturn(Optional.of(registrationConfirmedStatus));
//            when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
//                    .thenReturn(Optional.of(registrationConfirmedStatus.getSession()));
//            when(attendanceRepository.existsByRegistrationId(any(UUID.class)))
//                    .thenReturn(false);
//            when(registrationRepository.existsBySessionIdAndRegistrationStatus(
//                    any(UUID.class),
//                    any(RegistrationStatus.class)
//            )).thenReturn(true);
//            when(registrationRepository.getFirstBySessionIdAndRegistrationStatusOrderByDate(
//                    any(UUID.class),
//                    any(RegistrationStatus.class)
//            )).thenReturn(Optional.of(registrationWaitingListStatus));
//            doThrow(new MessagingException()).when(emailService).sendEmailToConfirmRegistration(
//                    any(Account.class),
//                    any(Registration.class)
//            );
//
//            when(registrationRepository.save(any(Registration.class)))
//                    .thenReturn(registrationConfirmedStatus);
//
//            Registration registrationCreated = registrationService.cancel(
//                    eventId,
//                    activityId,
//                    sessionId,
//                    registrationId
//            );
//
//            verify(registrationRepository, times(2)).save(any(Registration.class));
//            verify(emailService, times(1))
//                    .sendEmailToConfirmRegistration(any(Account.class), any(Registration.class));
//            assertThat(registrationCreated).isNotNull();
//            assertThat(registrationCreated.getId()).isEqualTo(registrationConfirmedStatus.getId());
//            assertThat(registrationCreated.getAccount().getId())
//                    .isEqualTo(registrationConfirmedStatus.getAccount().getId());
//            assertThat(registrationCreated.getSession().getId())
//                    .isEqualTo(registrationConfirmedStatus.getSession().getId());
//            assertThat(registrationCreated.getRegistrationStatus())
//                    .isEqualTo(RegistrationStatus.CANCELED_BY_ADMIN);
//            assertThat(registrationCreated.getDate())
//                    .isEqualTo(registrationConfirmedStatus.getDate());
//            assertThat(registrationWaitingListStatus.getRegistrationStatus())
//                    .isEqualTo(RegistrationStatus.WAITING_CONFIRMATION);
//        } catch (MessagingException e) {
//        }
//    }

    @Test
    public void cancel_ReturnsRegistrationCanceled_WhenExistAnyRegistrationInWaitingListAndAccountHasNotAllowEmail() {
        //Nesse caso, a sessão não começou, existe inscrição na lista de espera e
        //a conta não tem e-mail autorizado.
        UUID eventId = registrationConfirmedStatus.getSession().getActivity().getEvent().getId();
        UUID activityId = registrationConfirmedStatus.getSession().getActivity().getId();
        UUID sessionId = registrationConfirmedStatus.getSession().getId();
        UUID registrationId = registrationConfirmedStatus.getId();
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        registrationWaitingListStatus.getAccount().setAllowEmail(false);
        when(registrationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getSession()));
        when(attendanceRepository.existsByRegistrationId(any(UUID.class)))
                .thenReturn(false);
        when(registrationRepository.existsBySessionIdAndRegistrationStatus(
                any(UUID.class),
                any(RegistrationStatus.class)
        )).thenReturn(true);
        when(registrationRepository.getFirstBySessionIdAndRegistrationStatusOrderByDate(
                any(UUID.class),
                any(RegistrationStatus.class)
        )).thenReturn(Optional.of(registrationWaitingListStatus));
        when(registrationRepository.save(any(Registration.class)))
                .thenReturn(registrationConfirmedStatus);

        Registration registrationCreated = registrationService.cancel(
                eventId,
                activityId,
                sessionId,
                registrationId
        );

        verify(registrationRepository, times(2)).save(any(Registration.class));
        assertThat(registrationCreated).isNotNull();
        assertThat(registrationCreated.getId()).isEqualTo(registrationConfirmedStatus.getId());
        assertThat(registrationCreated.getAccount().getId())
                .isEqualTo(registrationConfirmedStatus.getAccount().getId());
        assertThat(registrationCreated.getSession().getId())
                .isEqualTo(registrationConfirmedStatus.getSession().getId());
        assertThat(registrationCreated.getRegistrationStatus())
                .isEqualTo(RegistrationStatus.CANCELED_BY_ADMIN);
        assertThat(registrationCreated.getDate())
                .isEqualTo(registrationConfirmedStatus.getDate());
        assertThat(registrationWaitingListStatus.getRegistrationStatus())
                .isEqualTo(RegistrationStatus.WAITING_CONFIRMATION);
    }

    @Test
    public void cancel_ReturnsRegistrationCanceled_WhenThereIsNoRegistrationInWaitingList() {
        // Nesse caso, a sessão não começou, não existe inscrição na lista de espera e
        // a inscrição é garantida/confirmada.
        UUID eventId = registrationConfirmedStatus.getSession().getActivity().getEvent().getId();
        UUID activityId = registrationConfirmedStatus.getSession().getActivity().getId();
        UUID sessionId = registrationConfirmedStatus.getSession().getId();
        UUID registrationId = registrationConfirmedStatus.getId();
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(registrationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getSession()));
        when(attendanceRepository.existsByRegistrationId(any(UUID.class)))
                .thenReturn(false);
        when(registrationRepository.existsBySessionIdAndRegistrationStatus(
                any(UUID.class),
                any(RegistrationStatus.class)
        )).thenReturn(false);
        when(registrationRepository.save(any(Registration.class)))
                .thenReturn(registrationConfirmedStatus);

        Registration registrationCreated = registrationService.cancel(
                eventId,
                activityId,
                sessionId,
                registrationId
        );

        verify(registrationRepository, times(1)).save(any(Registration.class));
        verify(sessionRepository, times(1)).save(any(Session.class));
        assertThat(registrationCreated).isNotNull();
        assertThat(registrationCreated.getId()).isEqualTo(registrationConfirmedStatus.getId());
        assertThat(registrationCreated.getAccount().getId())
                .isEqualTo(registrationConfirmedStatus.getAccount().getId());
        assertThat(registrationCreated.getSession().getId())
                .isEqualTo(registrationConfirmedStatus.getSession().getId());
        assertThat(registrationCreated.getSession().getConfirmedSeats())
                .isEqualTo(registrationConfirmedStatus.getSession().getConfirmedSeats());
        assertThat(registrationCreated.getRegistrationStatus())
                .isEqualTo(RegistrationStatus.CANCELED_BY_ADMIN);
        assertThat(registrationCreated.getDate())
                .isEqualTo(registrationConfirmedStatus.getDate());
    }

    @Test
    public void cancel_ReturnsRegistrationCanceled_WhenThereIsNoRegistrationInWaitingListAndRegistrationIsCanceled() {
        // Nesse caso, a sessão não começou, não existe inscrição na lista de espera e
        // a inscrição está cancelada.
        UUID eventId = registrationCanceledByAdmin.getSession().getActivity().getEvent().getId();
        UUID activityId = registrationCanceledByAdmin.getSession().getActivity().getId();
        UUID sessionId = registrationCanceledByAdmin.getSession().getId();
        UUID registrationId = registrationCanceledByAdmin.getId();
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(registrationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(registrationCanceledByAdmin));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationCanceledByAdmin.getSession()));
        when(attendanceRepository.existsByRegistrationId(any(UUID.class)))
                .thenReturn(false);
        when(registrationRepository.existsBySessionIdAndRegistrationStatus(
                any(UUID.class),
                any(RegistrationStatus.class)
        )).thenReturn(false);
        when(registrationRepository.save(any(Registration.class)))
                .thenReturn(registrationCanceledByAdmin);

        Registration registrationCreated = registrationService.cancel(
                eventId,
                activityId,
                sessionId,
                registrationId
        );

        verify(registrationRepository, times(1)).save(any(Registration.class));
        assertThat(registrationCreated).isNotNull();
        assertThat(registrationCreated.getId()).isEqualTo(registrationCanceledByAdmin.getId());
        assertThat(registrationCreated.getAccount().getId())
                .isEqualTo(registrationCanceledByAdmin.getAccount().getId());
        assertThat(registrationCreated.getSession().getId())
                .isEqualTo(registrationCanceledByAdmin.getSession().getId());
        assertThat(registrationCreated.getRegistrationStatus())
                .isEqualTo(RegistrationStatus.CANCELED_BY_ADMIN);
        assertThat(registrationCreated.getDate())
                .isEqualTo(registrationCanceledByAdmin.getDate());
    }

    @Test
    public void cancel3_ThrowsException_WhenRegistrationDoesNotExist() {
        UUID accountId = registrationConfirmedStatus.getAccount().getId();
        UUID registrationId = registrationConfirmedStatus.getId();
        when(registrationRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        ResourceNotFoundException exception = (ResourceNotFoundException) catchThrowable(
                () -> registrationService.cancel(accountId, registrationId)
        );

        assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
        assertThat(exception.getResourceName()).isEqualTo(ResourceName.REGISTRATION);
        assertThat(exception.getResourceId()).isEqualTo(registrationId.toString());
    }

    @Test
    public void cancel3_ThrowsException_WhenAccountIsNotAssociatedToRegistration() {
        UUID accountId = UUID.randomUUID();
        UUID registrationId = registrationConfirmedStatus.getId();
        when(registrationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getSession()));

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.cancel(accountId, registrationId)
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_IS_NOT_ASSOCIATED_TO_ACCOUNT);
    }

    @Test
    public void cancel3_ThrowsException_WhenRegistrationHasAttendanceAssociated() {
        UUID accountId =  registrationConfirmedStatus.getAccount().getId();
        UUID registrationId = registrationConfirmedStatus.getId();
        when(registrationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getSession()));
        when(attendanceRepository.existsByRegistrationId(any(UUID.class)))
                .thenReturn(true);

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.cancel(accountId, registrationId)
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_HAS_ATTENDANCE);
    }

    @Test
    public void cancel3_ReturnsRegistrationCanceled_WhenRegistrationIsInWaitingList() {
        UUID accountId =  registrationWaitingListStatus.getAccount().getId();
        UUID registrationId = registrationWaitingListStatus.getId();
        when(registrationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getSession()));
        when(attendanceRepository.existsByRegistrationId(any(UUID.class)))
                .thenReturn(false);
        when(registrationRepository.save(any(Registration.class)))
                .thenReturn(registrationWaitingListStatus);

        Registration registrationCreated = registrationService.cancel(accountId, registrationId);

        verify(registrationRepository, times(1)).save(any(Registration.class));
        assertThat(registrationCreated).isNotNull();
        assertThat(registrationCreated.getId()).isEqualTo(registrationWaitingListStatus.getId());
        assertThat(registrationCreated.getAccount().getId())
                .isEqualTo(registrationWaitingListStatus.getAccount().getId());
        assertThat(registrationCreated.getSession().getId())
                .isEqualTo(registrationWaitingListStatus.getSession().getId());
        assertThat(registrationCreated.getRegistrationStatus())
                .isEqualTo(RegistrationStatus.CANCELED_BY_USER);
        assertThat(registrationCreated.getDate())
                .isEqualTo(registrationWaitingListStatus.getDate());
    }

    @Test
    public void cancel3_ReturnsRegistrationCanceled_WhenSessionHasStartedAndRegistrationIsConfirmed() {
        UUID accountId =  registrationConfirmedStatus.getAccount().getId();
        UUID registrationId = registrationConfirmedStatus.getId();
        List<SessionSchedule> sessionSchedulesStarted = List.of(
                SessionScheduleFactory.sampleSessionScheduleThatStarted()
        );
        registrationConfirmedStatus.getSession().setSessionSchedules(sessionSchedulesStarted);
        when(registrationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getSession()));
        when(attendanceRepository.existsByRegistrationId(any(UUID.class)))
                .thenReturn(false);
        when(registrationRepository.save(any(Registration.class)))
                .thenReturn(registrationConfirmedStatus);

        Registration registrationCreated = registrationService.cancel(accountId, registrationId);

        verify(registrationRepository, times(1)).save(any(Registration.class));
        verify(sessionRepository, times(1)).save(any(Session.class));
        assertThat(registrationCreated).isNotNull();
        assertThat(registrationCreated.getId()).isEqualTo(registrationConfirmedStatus.getId());
        assertThat(registrationCreated.getAccount().getId())
                .isEqualTo(registrationConfirmedStatus.getAccount().getId());
        assertThat(registrationCreated.getSession().getId())
                .isEqualTo(registrationConfirmedStatus.getSession().getId());
        assertThat(registrationCreated.getSession().getConfirmedSeats())
                .isEqualTo(registrationConfirmedStatus.getSession().getConfirmedSeats());
        assertThat(registrationCreated.getRegistrationStatus())
                .isEqualTo(RegistrationStatus.CANCELED_BY_USER);
        assertThat(registrationCreated.getDate())
                .isEqualTo(registrationConfirmedStatus.getDate());
    }

    @Test
    public void cancel3_ReturnsRegistrationCanceled_WhenExistAnyRegistrationInWaitingListAndAccountHasAllowEmail() {
        //Nesse caso, a sessão não começou, existe inscrição na lista de espera,
        // conta tem e-mail autorizado e o e-mail é enviado
        try {
            UUID accountId =  registrationConfirmedStatus.getAccount().getId();
            UUID registrationId = registrationConfirmedStatus.getId();
            when(registrationRepository.findById(any(UUID.class)))
                    .thenReturn(Optional.of(registrationConfirmedStatus));
            when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                    .thenReturn(Optional.of(registrationConfirmedStatus.getSession()));
            when(attendanceRepository.existsByRegistrationId(any(UUID.class)))
                    .thenReturn(false);
            when(registrationRepository.existsBySessionIdAndRegistrationStatus(
                    any(UUID.class),
                    any(RegistrationStatus.class)
            )).thenReturn(true);
            when(registrationRepository.getFirstBySessionIdAndRegistrationStatusOrderByDate(
                    any(UUID.class),
                    any(RegistrationStatus.class)
            )).thenReturn(Optional.of(registrationWaitingListStatus));
            doNothing().when(emailService).sendEmailToConfirmRegistration(
                    any(Account.class),
                    any(Registration.class)
            );
            when(registrationRepository.save(any(Registration.class)))
                    .thenReturn(registrationConfirmedStatus);

            Registration registrationCreated = registrationService.cancel(accountId, registrationId);

            verify(registrationRepository, times(2)).save(any(Registration.class));
            verify(emailService, times(1))
            .sendEmailToConfirmRegistration(any(Account.class), any(Registration.class));
            assertThat(registrationCreated).isNotNull();
            assertThat(registrationCreated.getId()).isEqualTo(registrationConfirmedStatus.getId());
            assertThat(registrationCreated.getAccount().getId())
                    .isEqualTo(registrationConfirmedStatus.getAccount().getId());
            assertThat(registrationCreated.getSession().getId())
                    .isEqualTo(registrationConfirmedStatus.getSession().getId());
            assertThat(registrationCreated.getRegistrationStatus())
                    .isEqualTo(RegistrationStatus.CANCELED_BY_USER);
            assertThat(registrationCreated.getDate())
                    .isEqualTo(registrationConfirmedStatus.getDate());
            assertThat(registrationWaitingListStatus.getRegistrationStatus())
                    .isEqualTo(RegistrationStatus.WAITING_CONFIRMATION);
        } catch (MessagingException e) {
        }
    }

//    @Test
//    public void cancel3_ReturnsRegistrationCanceled_WhenExistAnyRegistrationInWaitingListAndEmailIsNotSend() {
//        //TODO:  Pegar exceção lançada ao não conseguir enviar e-mail
//        //Nesse caso, a sessão não começou, existe inscrição na lista de espera,
//        // conta tem e-mail autorizado e o e-mail não é enviado.
//        try {
//            UUID accountId =  registrationConfirmedStatus.getAccount().getId();
//            UUID registrationId = registrationConfirmedStatus.getId();
//            when(registrationRepository.findById(any(UUID.class)))
//                    .thenReturn(Optional.of(registrationConfirmedStatus));
//            when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
//                    .thenReturn(Optional.of(registrationConfirmedStatus.getSession()));
//            when(attendanceRepository.existsByRegistrationId(any(UUID.class)))
//                    .thenReturn(false);
//            when(registrationRepository.existsBySessionIdAndRegistrationStatus(
//                    any(UUID.class),
//                    any(RegistrationStatus.class)
//            )).thenReturn(true);
//            when(registrationRepository.getFirstBySessionIdAndRegistrationStatusOrderByDate(
//                    any(UUID.class),
//                    any(RegistrationStatus.class)
//            )).thenReturn(Optional.of(registrationWaitingListStatus));
//            doThrow(new MessagingException()).when(emailService).sendEmailToConfirmRegistration(
//                    any(Account.class),
//                    any(Registration.class)
//            );
//            when(registrationRepository.save(any(Registration.class)))
//                    .thenReturn(registrationConfirmedStatus);
//
//            Registration registrationCreated = registrationService.cancel(accountId, registrationId);
//
//            verify(registrationRepository, times(2)).save(any(Registration.class));
//            verify(emailService, times(1))
//                    .sendEmailToConfirmRegistration(any(Account.class), any(Registration.class));
//            assertThat(registrationCreated).isNotNull();
//            assertThat(registrationCreated.getId()).isEqualTo(registrationConfirmedStatus.getId());
//            assertThat(registrationCreated.getAccount().getId())
//                    .isEqualTo(registrationConfirmedStatus.getAccount().getId());
//            assertThat(registrationCreated.getSession().getId())
//                    .isEqualTo(registrationConfirmedStatus.getSession().getId());
//            assertThat(registrationCreated.getRegistrationStatus())
//                    .isEqualTo(RegistrationStatus.CANCELED_BY_USER);
//            assertThat(registrationCreated.getDate())
//                    .isEqualTo(registrationConfirmedStatus.getDate());
//            assertThat(registrationWaitingListStatus.getRegistrationStatus())
//                    .isEqualTo(RegistrationStatus.WAITING_CONFIRMATION);
//        } catch (MessagingException e) {
//        }
//    }

    @Test
    public void cancel3_ReturnsRegistrationCanceled_WhenExistAnyRegistrationInWaitingListAndAccountHasNotAllowEmail() {
        //Nesse caso, a sessão não começou, existe inscrição na lista de espera e
        //a conta não tem e-mail autorizado.
        UUID accountId =  registrationConfirmedStatus.getAccount().getId();
        UUID registrationId = registrationConfirmedStatus.getId();
        registrationWaitingListStatus.getAccount().setAllowEmail(false);
        when(registrationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getSession()));
        when(attendanceRepository.existsByRegistrationId(any(UUID.class)))
                .thenReturn(false);
        when(registrationRepository.existsBySessionIdAndRegistrationStatus(
                any(UUID.class),
                any(RegistrationStatus.class)
        )).thenReturn(true);
        when(registrationRepository.getFirstBySessionIdAndRegistrationStatusOrderByDate(
                any(UUID.class),
                any(RegistrationStatus.class)
        )).thenReturn(Optional.of(registrationWaitingListStatus));
        when(registrationRepository.save(any(Registration.class)))
                .thenReturn(registrationConfirmedStatus);

        Registration registrationCreated = registrationService.cancel(accountId, registrationId);

        verify(registrationRepository, times(2)).save(any(Registration.class));
        assertThat(registrationCreated).isNotNull();
        assertThat(registrationCreated.getId()).isEqualTo(registrationConfirmedStatus.getId());
        assertThat(registrationCreated.getAccount().getId())
                .isEqualTo(registrationConfirmedStatus.getAccount().getId());
        assertThat(registrationCreated.getSession().getId())
                .isEqualTo(registrationConfirmedStatus.getSession().getId());
        assertThat(registrationCreated.getRegistrationStatus())
                .isEqualTo(RegistrationStatus.CANCELED_BY_USER);
        assertThat(registrationCreated.getDate())
                .isEqualTo(registrationConfirmedStatus.getDate());
        assertThat(registrationWaitingListStatus.getRegistrationStatus())
                .isEqualTo(RegistrationStatus.WAITING_CONFIRMATION);
    }

    @Test
    public void cancel3_ReturnsRegistrationCanceled_WhenThereIsNoRegistrationInWaitingList() {
        // Nesse caso, a sessão não começou E não existe inscrição na lista de espera.
        UUID accountId =  registrationConfirmedStatus.getAccount().getId();
        UUID registrationId = registrationConfirmedStatus.getId();
        when(registrationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationConfirmedStatus.getSession()));
        when(attendanceRepository.existsByRegistrationId(any(UUID.class)))
                .thenReturn(false);
        when(registrationRepository.existsBySessionIdAndRegistrationStatus(
                any(UUID.class),
                any(RegistrationStatus.class)
        )).thenReturn(false);
        when(registrationRepository.save(any(Registration.class)))
                .thenReturn(registrationConfirmedStatus);

        Registration registrationCreated = registrationService.cancel(accountId, registrationId);

        verify(registrationRepository, times(1)).save(any(Registration.class));
        verify(sessionRepository, times(1)).save(any(Session.class));
        assertThat(registrationCreated).isNotNull();
        assertThat(registrationCreated.getId()).isEqualTo(registrationConfirmedStatus.getId());
        assertThat(registrationCreated.getAccount().getId())
                .isEqualTo(registrationConfirmedStatus.getAccount().getId());
        assertThat(registrationCreated.getSession().getId())
                .isEqualTo(registrationConfirmedStatus.getSession().getId());
        assertThat(registrationCreated.getSession().getConfirmedSeats())
                .isEqualTo(registrationConfirmedStatus.getSession().getConfirmedSeats());
        assertThat(registrationCreated.getRegistrationStatus())
                .isEqualTo(RegistrationStatus.CANCELED_BY_USER);
        assertThat(registrationCreated.getDate())
                .isEqualTo(registrationConfirmedStatus.getDate());
    }

    @Test
    public void confirmWaitingList_ThrowsException_WhenCurrentUserHasNoPermissionForEvent() {
        UUID eventId = registrationWaitingListStatus.getSession().getActivity().getEvent().getId();
        UUID activityId = registrationWaitingListStatus.getSession().getActivity().getId();
        UUID sessionId = registrationWaitingListStatus.getSession().getId();
        UUID registrationId = registrationWaitingListStatus.getId();
        mocksAuthenticationWithCurrentUserThatIsNotAuthorized();

        OrganizerAuthorizationException exception = (OrganizerAuthorizationException) catchThrowable(
                () -> registrationService.confirmWaitingList(
                        eventId, activityId, sessionId, registrationId
                )
        );

        assertThat(exception).isInstanceOf(OrganizerAuthorizationException.class);
        assertThat(exception.getOrganizerAuthorizationExceptionType())
                .isEqualTo(OrganizerAuthorizationExceptionType.UNAUTHORIZED_EVENT);
        assertThat(exception.getResourceId()).isEqualTo(eventId);
    }

    @Test
    public void confirmWaitingList_ThrowsException_When() {
        UUID eventId = registrationWaitingListStatus.getSession().getActivity().getEvent().getId();
        UUID activityId = registrationWaitingListStatus.getSession().getActivity().getId();
        UUID sessionId = registrationWaitingListStatus.getSession().getId();
        UUID registrationId = registrationWaitingListStatus.getId();
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(registrationRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        ResourceNotFoundException exception = (ResourceNotFoundException) catchThrowable(
                () -> registrationService.confirmWaitingList(
                        eventId, activityId, sessionId, registrationId
                )
        );

        assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
        assertThat(exception.getResourceName()).isEqualTo(ResourceName.REGISTRATION);
        assertThat(exception.getResourceId()).isEqualTo(registrationId.toString());
    }

    @Test
    public void confirmWaitingList_ThrowsException_WhenSessionDoesNotExist() {
        UUID eventId = registrationWaitingListStatus.getSession().getActivity().getEvent().getId();
        UUID activityId = registrationWaitingListStatus.getSession().getActivity().getId();
        UUID sessionId = registrationWaitingListStatus.getSession().getId();
        UUID registrationId = registrationWaitingListStatus.getId();
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(registrationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = (NoSuchElementException) catchThrowable(
                () -> registrationService.confirmWaitingList(
                        eventId, activityId, sessionId, registrationId
                )
        );

        assertThat(exception).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void confirmWaitingList_ThrowsException_WhenEventIsNotAssociateToActivity() {
        UUID eventId = UUID.randomUUID();
        UUID activityId = registrationWaitingListStatus.getSession().getActivity().getId();
        UUID sessionId = registrationWaitingListStatus.getSession().getId();
        UUID registrationId = registrationWaitingListStatus.getId();
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(registrationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getSession()));

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.confirmWaitingList(
                        eventId, activityId, sessionId, registrationId
                )
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.ACTIVITY_IS_NOT_ASSOCIATED_TO_EVENT);
    }

    @Test
    public void confirmWaitingList_ThrowsException_WhenActivityIsNotAssociateToSession() {
        UUID eventId = registrationWaitingListStatus.getSession().getActivity().getEvent().getId();
        UUID activityId = UUID.randomUUID();
        UUID sessionId = registrationWaitingListStatus.getSession().getId();
        UUID registrationId = registrationWaitingListStatus.getId();
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(registrationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getSession()));

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.confirmWaitingList(
                        eventId, activityId, sessionId, registrationId
                )
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.SESSION_IS_NOT_ASSOCIATED_TO_ACTIVITY);
    }

    @Test
    public void confirmWaitingList_ThrowsException_WhenSessionIsNotAssociateToRegistration() {
        UUID eventId = registrationWaitingListStatus.getSession().getActivity().getEvent().getId();
        UUID activityId = registrationWaitingListStatus.getSession().getActivity().getId();
        UUID sessionId = UUID.randomUUID();
        UUID registrationId = registrationWaitingListStatus.getId();
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        when(registrationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getSession()));

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.confirmWaitingList(
                        eventId, activityId, sessionId, registrationId
                )
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_IS_NOT_ASSOCIATED_TO_SESSION);
    }

    @Test
    public void confirmWaitingList_ThrowsException_WhenEmailWasAnswered() {
        UUID eventId = registrationWaitingListStatus.getSession().getActivity().getEvent().getId();
        UUID activityId = registrationWaitingListStatus.getSession().getActivity().getId();
        UUID sessionId = registrationWaitingListStatus.getSession().getId();
        UUID registrationId = registrationWaitingListStatus.getId();
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        registrationWaitingListStatus.setEmailReplyDate(
                LocalDateTime.of(2022, 12, 2, 0, 0, 0)
        );
        when(registrationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getSession()));

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.confirmWaitingList(
                        eventId, activityId, sessionId, registrationId
                )
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_ALREADY_WAS_ANSWERED);
    }

    @Test
    public void confirmWaitingList_ThrowsException_WhenRegistrationIsInWaitingListAndSessionIsFull() {
        UUID eventId = registrationWaitingListStatus.getSession().getActivity().getEvent().getId();
        UUID activityId = registrationWaitingListStatus.getSession().getActivity().getId();
        UUID sessionId = registrationWaitingListStatus.getSession().getId();
        UUID registrationId = registrationWaitingListStatus.getId();
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        registrationWaitingListStatus.getSession().setConfirmedSeats(
                registrationWaitingListStatus.getSession().getSeats()
        );
        when(registrationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getSession()));

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.confirmWaitingList(
                        eventId, activityId, sessionId, registrationId
                )
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_CREATE_WITH_NO_SEATS_AVAILABLE);
    }

    @Test
    public void confirmWaitingList_ReturnsRegistrationConfirmed_WhenSuccessful() {
        UUID eventId = registrationWaitingListStatus.getSession().getActivity().getEvent().getId();
        UUID activityId = registrationWaitingListStatus.getSession().getActivity().getId();
        UUID sessionId = registrationWaitingListStatus.getSession().getId();
        UUID registrationId = registrationWaitingListStatus.getId();
        mocksAuthenticationWithCurrentUserThatIsOrganizerOfEvent(eventId);
        List<Registration> registrations = List.of(
                RegistrationFactory.sampleRegistrationWithConfirmedStatusInOtherSession()
        );
        List<Registration> registrationsInWaitingList = List.of(
                RegistrationFactory.sampleRegistrationWithWaitingListStatus()
        );
        when(registrationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingListStatus.getSession()));
        when(registrationRepository.save(any(Registration.class)))
                .thenReturn(registrationWaitingListStatus);
        when(registrationRepository.findAllByAccountIdAndRegistrationStatusInAndDate(
                any(UUID.class),
                any(LocalDateTime.class),
                anyList()
        )).thenReturn(registrations);
        when(registrationRepository.findAllByAccountIdAndSessionIdIn(
                any(UUID.class),
                anyList()
        )).thenReturn(registrationsInWaitingList);

        Registration registrationCreated = registrationService.confirmWaitingList(
                        eventId, activityId, sessionId, registrationId
        );

        verify(registrationRepository, times(1)).save(any(Registration.class));
        verify(registrationRepository, times(1)).saveAll(anyList());
        assertThat(registrationCreated).isNotNull();
        assertThat(registrationCreated.getId()).isEqualTo(registrationWaitingListStatus.getId());
        assertThat(registrationCreated.getAccount().getId())
                .isEqualTo(registrationWaitingListStatus.getAccount().getId());
        assertThat(registrationCreated.getSession().getId())
                .isEqualTo(registrationWaitingListStatus.getSession().getId());
        assertThat(registrationCreated.getRegistrationStatus())
                .isEqualTo(RegistrationStatus.CONFIRMED);
        assertThat(registrationCreated.getDate())
                .isEqualTo(registrationWaitingListStatus.getDate());
        assertThat(registrationsInWaitingList)
                .extracting(Registration::getRegistrationStatus)
                .contains(RegistrationStatus.CANCELED_BY_SYSTEM);
    }

    @Test
    public void acceptSessionSeat_ThrowsException_WhenRegistrationDoesNotExist() {
        UUID accountId = registrationWaitingConfirmation.getAccount().getId();
        UUID registrationId = registrationWaitingConfirmation.getId();
        when(registrationRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        ResourceNotFoundException exception = (ResourceNotFoundException) catchThrowable(
                () -> registrationService.acceptSessionSeat(accountId, registrationId)
        );

        assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
        assertThat(exception.getResourceName()).isEqualTo(ResourceName.REGISTRATION);
        assertThat(exception.getResourceId()).isEqualTo(registrationId.toString());
    }

    @Test
    public void acceptSessionSeat_ThrowsException_WhenAccountDoesNotExist() {
        UUID accountId = registrationWaitingConfirmation.getAccount().getId();
        UUID registrationId = registrationWaitingConfirmation.getId();
        when(registrationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingConfirmation));
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = (NoSuchElementException) catchThrowable(
                () -> registrationService.acceptSessionSeat(accountId, registrationId)
        );

        assertThat(exception).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void acceptSessionSeat_ThrowsException_WhenAccountIsNotAssociateToRegistration() {
        UUID accountId = UUID.randomUUID();
        UUID registrationId = registrationWaitingConfirmation.getId();
        when(registrationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingConfirmation));
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingConfirmation.getAccount()));

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.acceptSessionSeat(accountId, registrationId)
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_IS_NOT_ASSOCIATED_TO_ACCOUNT);
    }

    @Test
    public void acceptSessionSeat_ThrowsException_WhenEmailWasAnswered() {
        UUID accountId = registrationWaitingConfirmation.getAccount().getId();
        UUID registrationId = registrationWaitingConfirmation.getId();
        registrationWaitingConfirmation.setEmailReplyDate(
                LocalDateTime.of(2022, 12, 2, 0, 0, 0)
        );
        when(registrationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingConfirmation));
        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingConfirmation.getAccount()));

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.acceptSessionSeat(accountId, registrationId)
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_ALREADY_WAS_ANSWERED);
    }

//    @Test
//    public void acceptSessionSeat_ThrowsException_WhenSolicitationIsExpired() {
//        //TODO: Criar classe contendo as configurações de e-mail
//        UUID accountId = registrationWaitingConfirmation.getAccount().getId();
//        UUID registrationId = registrationWaitingConfirmation.getId();
//        String timeToConfirmEmail = "12";
//        registrationWaitingConfirmation.setTimeEmailWasSent(
//                LocalDateTime.of(2022, 11, 30, 0, 0, 0)
//        );
//        when(registrationRepository.findById(any(UUID.class)))
//                .thenReturn(Optional.of(registrationWaitingConfirmation));
//        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
//                .thenReturn(Optional.of(registrationWaitingConfirmation.getAccount()));
//        when(emailConfirmationTime.getEmailConfirmationTime()).thenReturn(timeToConfirmEmail);
//
//        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
//                () -> registrationService.acceptSessionSeat(accountId, registrationId)
//        );
//
//        assertThat(exception).isInstanceOf(BusinessRuleException.class);
//        assertThat(exception.getBusinessRuleType())
//                .isEqualTo(BusinessRuleType.REGISTRATION_ACCEPT_WITH_EXPIRED_HOURS);
//    }

//    @Test
//    public void acceptSessionSeat_ReturnsRegistrationConfirmed_WhenSuccessful() {
//        //TODO: Criar classe contendo as configurações de e-mail
//        UUID accountId = registrationWaitingConfirmation.getAccount().getId();
//        UUID registrationId = registrationWaitingConfirmation.getId();
//        String timeToConfirmEmail = "12";
//        registrationWaitingConfirmation.setTimeEmailWasSent(
//                LocalDateTime.now()
//        );
//        List<Registration> registrationsInWaitingList = List.of(
//                RegistrationFactory.sampleRegistrationWithWaitingListStatus()
//        );
//        when(registrationRepository.findById(any(UUID.class)))
//                .thenReturn(Optional.of(registrationWaitingConfirmation));
//        when(accountRepository.findByIdWithPessimisticLock(any(UUID.class)))
//                .thenReturn(Optional.of(registrationWaitingConfirmation.getAccount()));
//        when(emailConfirmationTime.getEmailConfirmationTime()).thenReturn(timeToConfirmEmail);
//        when(registrationRepository.findAllByAccountIdAndRegistrationStatusInAndDate(
//                any(UUID.class),
//                any(LocalDateTime.class),
//                anyList()
//        )).thenReturn(registrationsInWaitingList);
//        when(registrationRepository.findAllByAccountIdAndSessionIdIn(
//                any(UUID.class),
//                anyList()
//        )).thenReturn(registrationsInWaitingList);
//        when(registrationRepository.save(any(Registration.class)))
//                .thenReturn(registrationConfirmedStatus);
//
//        Registration registrationCreated = registrationService.acceptSessionSeat(accountId, registrationId);
//
//        verify(registrationRepository, times(1)).save(any(Registration.class));
//        verify(registrationRepository, times(1)).saveAll(anyList());
//        assertThat(registrationCreated).isNotNull();
//        assertThat(registrationCreated.getId()).isEqualTo(registrationConfirmedStatus.getId());
//        assertThat(registrationCreated.getAccount().getId())
//                .isEqualTo(registrationConfirmedStatus.getAccount().getId());
//        assertThat(registrationCreated.getSession().getId())
//                .isEqualTo(registrationConfirmedStatus.getSession().getId());
//        assertThat(registrationCreated.getRegistrationStatus())
//                .isEqualTo(registrationConfirmedStatus.getRegistrationStatus());
//        assertThat(registrationCreated.getDate())
//                .isEqualTo(registrationConfirmedStatus.getDate());
//        assertThat(registrationsInWaitingList)
//                .extracting(Registration::getRegistrationStatus)
//                .contains(RegistrationStatus.CANCELED_BY_SYSTEM);
//    }

    @Test
    public void denySessionSeat_ThrowsException_WhenRegistrationDoesNotExist() {
        UUID accountId = registrationWaitingConfirmation.getAccount().getId();
        UUID registrationId = registrationWaitingConfirmation.getId();
        when(registrationRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        ResourceNotFoundException exception = (ResourceNotFoundException) catchThrowable(
                () -> registrationService.denySessionSeat(accountId, registrationId)
        );

        assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
        assertThat(exception.getResourceName()).isEqualTo(ResourceName.REGISTRATION);
        assertThat(exception.getResourceId()).isEqualTo(registrationId.toString());
    }

    @Test
    public void denySessionSeat_ThrowsException_WhenAccountIsNotAssociateToRegistration() {
        UUID accountId = UUID.randomUUID();
        UUID registrationId = registrationWaitingConfirmation.getId();
        when(registrationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingConfirmation));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingConfirmation.getSession()));

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.denySessionSeat(accountId, registrationId)
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_IS_NOT_ASSOCIATED_TO_ACCOUNT);
    }

    @Test
    public void denySessionSeat_ThrowsException_WhenEmailWasAnswered() {
        UUID accountId = registrationWaitingConfirmation.getAccount().getId();
        UUID registrationId = registrationWaitingConfirmation.getId();
        registrationWaitingConfirmation.setEmailReplyDate(
                LocalDateTime.of(2022, 12, 2, 0, 0, 0)
        );
        when(registrationRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingConfirmation));
        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
                .thenReturn(Optional.of(registrationWaitingConfirmation.getSession()));

        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
                () -> registrationService.denySessionSeat(accountId, registrationId)
        );

        assertThat(exception).isInstanceOf(BusinessRuleException.class);
        assertThat(exception.getBusinessRuleType())
                .isEqualTo(BusinessRuleType.REGISTRATION_ALREADY_WAS_ANSWERED);
    }

//    @Test
//    public void denySessionSeat_ThrowsException_WhenSolicitationIsExpired() {
//        //TODO: Criar classe contendo as configurações de e-mail
//        UUID accountId = registrationWaitingConfirmation.getAccount().getId();
//        UUID registrationId = registrationWaitingConfirmation.getId();
//        String timeToConfirmEmail = "12";
//        registrationWaitingConfirmation.setTimeEmailWasSent(
//                LocalDateTime.of(2022, 11, 30, 0, 0, 0)
//        );
//        when(registrationRepository.findById(any(UUID.class)))
//                .thenReturn(Optional.of(registrationWaitingConfirmation));
//        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
//                .thenReturn(Optional.of(registrationWaitingConfirmation.getSession()));
//        when(emailConfirmationTime.getEmailConfirmationTime()).thenReturn(timeToConfirmEmail);
//
//        BusinessRuleException exception = (BusinessRuleException) catchThrowable(
//                () -> registrationService.denySessionSeat(accountId, registrationId)
//        );
//
//        assertThat(exception).isInstanceOf(BusinessRuleException.class);
//        assertThat(exception.getBusinessRuleType())
//                .isEqualTo(BusinessRuleType.REGISTRATION_DENY_WITH_EXPIRED_HOURS);
//    }

//    @Test
//    public void denySessionSeat_ReturnsRegistrationCanceled_WhenSessionHasStarted() {
//        //TODO: Criar classe contendo as configurações de e-mail
//        UUID accountId = registrationWaitingConfirmation.getAccount().getId();
//        UUID registrationId = registrationWaitingConfirmation.getId();
//        String timeToConfirmEmail = "12";
//        registrationWaitingConfirmation.setTimeEmailWasSent(
//                LocalDateTime.now()
//        );
//        List<SessionSchedule> sessionSchedulesStarted = List.of(
//                SessionScheduleFactory.sampleSessionScheduleThatStarted()
//        );
//        registrationWaitingConfirmation.getSession().setSessionSchedules(sessionSchedulesStarted);
//        when(registrationRepository.findById(any(UUID.class)))
//                .thenReturn(Optional.of(registrationWaitingConfirmation));
//        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
//                .thenReturn(Optional.of(registrationWaitingConfirmation.getSession()));
//        when(emailConfirmationTime.getEmailConfirmationTime()).thenReturn(timeToConfirmEmail);
//        when(registrationRepository.save(any(Registration.class))).thenReturn(registrationWaitingConfirmation);
//
//        Registration registrationCreated = registrationService.denySessionSeat(accountId, registrationId);
//
//        verify(registrationRepository, times(1)).save(any(Registration.class));
//        verify(sessionRepository, times(1)).save(any(Session.class));
//        assertThat(registrationCreated).isNotNull();
//        assertThat(registrationCreated.getId()).isEqualTo(registrationWaitingConfirmation.getId());
//        assertThat(registrationCreated.getAccount().getId())
//                .isEqualTo(registrationWaitingConfirmation.getAccount().getId());
//        assertThat(registrationCreated.getSession().getId())
//                .isEqualTo(registrationWaitingConfirmation.getSession().getId());
//        assertThat(registrationCreated.getRegistrationStatus())
//                .isEqualTo(RegistrationStatus.CANCELED_BY_USER);
//        assertThat(registrationCreated.getDate())
//                .isEqualTo(registrationWaitingConfirmation.getDate());
//    }

//    @Test
//    public void denySessionSeat_ReturnsRegistrationCanceled_WhenExistAnyRegistrationInWaitingListAndAccountHasAllowEmail() {
//        //TODO: Criar classe contendo as configurações de e-mail
//        //Nesse caso, a sessão não começou, existe inscrição na lista de espera,
//        // conta tem e-mail autorizado e o e-mail é enviado
//        try {
//            UUID accountId = registrationWaitingConfirmation.getAccount().getId();
//            UUID registrationId = registrationWaitingConfirmation.getId();
//            String timeToConfirmEmail = "12";
//            registrationWaitingConfirmation.setTimeEmailWasSent(
//                    LocalDateTime.now()
//            );
//            when(registrationRepository.findById(any(UUID.class)))
//                    .thenReturn(Optional.of(registrationWaitingConfirmation));
//            when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
//                    .thenReturn(Optional.of(registrationWaitingConfirmation.getSession()));
//            when(emailConfirmationTime.getEmailConfirmationTime()).thenReturn(timeToConfirmEmail);
//            when(registrationRepository.existsBySessionIdAndRegistrationStatus(
//                    any(UUID.class),
//                    any(RegistrationStatus.class)
//            )).thenReturn(true);
//            when(registrationRepository.getFirstBySessionIdAndRegistrationStatusOrderByDate(
//                    any(UUID.class),
//                    any(RegistrationStatus.class)
//            )).thenReturn(Optional.of(registrationWaitingListStatus));
//            doNothing().when(emailService).sendEmailToConfirmRegistration(
//                    any(Account.class),
//                    any(Registration.class)
//            );
//            when(registrationRepository.save(any(Registration.class)))
//                    .thenReturn(registrationWaitingConfirmation);
//
//            Registration registrationCreated = registrationService.denySessionSeat(accountId, registrationId);
//
//            verify(registrationRepository, times(2)).save(any(Registration.class));
//            verify(emailService, times(1))
//                    .sendEmailToConfirmRegistration(any(Account.class), any(Registration.class));
//            assertThat(registrationCreated).isNotNull();
//            assertThat(registrationCreated.getId()).isEqualTo(registrationWaitingConfirmation.getId());
//            assertThat(registrationCreated.getAccount().getId())
//                    .isEqualTo(registrationWaitingConfirmation.getAccount().getId());
//            assertThat(registrationCreated.getSession().getId())
//                    .isEqualTo(registrationWaitingConfirmation.getSession().getId());
//            assertThat(registrationCreated.getRegistrationStatus())
//                    .isEqualTo(RegistrationStatus.CANCELED_BY_USER);
//            assertThat(registrationCreated.getDate())
//                    .isEqualTo(registrationWaitingConfirmation.getDate());
//            assertThat(registrationWaitingListStatus.getRegistrationStatus())
//                    .isEqualTo(RegistrationStatus.WAITING_CONFIRMATION);
//        } catch (MessagingException e) {
//        }
//    }

//    @Test
//    public void denySessionSeat_ReturnsRegistrationCanceled_WhenExistAnyRegistrationInWaitingListAndEmailIsNotSend() {
//        //TODO: Criar classe contendo as configurações de e-mail
//        //TODO: Pegar exceção lançada ao não conseguir enviar e-mail
//        //Nesse caso, a sessão não começou, existe inscrição na lista de espera,
//        // conta tem e-mail autorizado e o e-mail não é enviado.
//        try {
//            UUID accountId = registrationWaitingConfirmation.getAccount().getId();
//            UUID registrationId = registrationWaitingConfirmation.getId();
//            String timeToConfirmEmail = "12";
//            registrationWaitingConfirmation.setTimeEmailWasSent(
//                    LocalDateTime.now()
//            );
//            when(registrationRepository.findById(any(UUID.class)))
//                    .thenReturn(Optional.of(registrationWaitingConfirmation));
//            when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
//                    .thenReturn(Optional.of(registrationWaitingConfirmation.getSession()));
//            when(emailConfirmationTime.getEmailConfirmationTime()).thenReturn(timeToConfirmEmail);
//            when(registrationRepository.existsBySessionIdAndRegistrationStatus(
//                    any(UUID.class),
//                    any(RegistrationStatus.class)
//            )).thenReturn(true);
//            when(registrationRepository.getFirstBySessionIdAndRegistrationStatusOrderByDate(
//                    any(UUID.class),
//                    any(RegistrationStatus.class)
//            )).thenReturn(Optional.of(registrationWaitingListStatus));
//            doThrow(new MessagingException()).when(emailService).sendEmailToConfirmRegistration(
//                    any(Account.class),
//                    any(Registration.class)
//            );
//            when(registrationRepository.save(any(Registration.class)))
//                    .thenReturn(registrationWaitingConfirmation);
//
//            Registration registrationCreated = registrationService.denySessionSeat(accountId, registrationId);
//
//            verify(registrationRepository, times(2)).save(any(Registration.class));
//            verify(emailService, times(1))
//                    .sendEmailToConfirmRegistration(any(Account.class), any(Registration.class));
//            assertThat(registrationCreated).isNotNull();
//            assertThat(registrationCreated.getId()).isEqualTo(registrationWaitingConfirmation.getId());
//            assertThat(registrationCreated.getAccount().getId())
//                    .isEqualTo(registrationWaitingConfirmation.getAccount().getId());
//            assertThat(registrationCreated.getSession().getId())
//                    .isEqualTo(registrationWaitingConfirmation.getSession().getId());
//            assertThat(registrationCreated.getRegistrationStatus())
//                    .isEqualTo(RegistrationStatus.CANCELED_BY_USER);
//            assertThat(registrationCreated.getDate())
//                    .isEqualTo(registrationWaitingConfirmation.getDate());
//            assertThat(registrationWaitingListStatus.getRegistrationStatus())
//                    .isEqualTo(RegistrationStatus.WAITING_CONFIRMATION);
//        } catch (MessagingException e) {
//        }
//    }

//    @Test
//    public void denySessionSeat_ReturnsRegistrationCanceled_WhenExistAnyRegistrationInWaitingListAndAccountHasNotAllowEmail() {
//        //TODO: Criar classe contendo as configurações de e-mail
//        //Nesse caso, a sessão não começou, existe inscrição na lista de espera e
//        //a conta não tem e-mail autorizado.
//        UUID accountId = registrationWaitingConfirmation.getAccount().getId();
//        UUID registrationId = registrationWaitingConfirmation.getId();
//        registrationWaitingConfirmation.getAccount().setAllowEmail(false);
//        String timeToConfirmEmail = "12";
//        registrationWaitingConfirmation.setTimeEmailWasSent(
//                LocalDateTime.now()
//        );
//        when(registrationRepository.findById(any(UUID.class)))
//                .thenReturn(Optional.of(registrationWaitingConfirmation));
//        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
//                .thenReturn(Optional.of(registrationWaitingConfirmation.getSession()));
//        when(emailConfirmationTime.getEmailConfirmationTime()).thenReturn(timeToConfirmEmail);
//        when(registrationRepository.existsBySessionIdAndRegistrationStatus(
//                any(UUID.class),
//                any(RegistrationStatus.class)
//        )).thenReturn(true);
//        when(registrationRepository.getFirstBySessionIdAndRegistrationStatusOrderByDate(
//                any(UUID.class),
//                any(RegistrationStatus.class)
//        )).thenReturn(Optional.of(registrationWaitingListStatus));
//        when(registrationRepository.save(any(Registration.class)))
//                .thenReturn(registrationWaitingConfirmation);
//
//        Registration registrationCreated = registrationService.denySessionSeat(accountId, registrationId);
//
//        verify(registrationRepository, times(2)).save(any(Registration.class));
//        assertThat(registrationCreated).isNotNull();
//        assertThat(registrationCreated.getId()).isEqualTo(registrationWaitingConfirmation.getId());
//        assertThat(registrationCreated.getAccount().getId())
//                .isEqualTo(registrationWaitingConfirmation.getAccount().getId());
//        assertThat(registrationCreated.getSession().getId())
//                .isEqualTo(registrationWaitingConfirmation.getSession().getId());
//        assertThat(registrationCreated.getRegistrationStatus())
//                .isEqualTo(RegistrationStatus.CANCELED_BY_USER);
//        assertThat(registrationCreated.getDate())
//                .isEqualTo(registrationWaitingConfirmation.getDate());
//        assertThat(registrationWaitingListStatus.getRegistrationStatus())
//                .isEqualTo(RegistrationStatus.WAITING_CONFIRMATION);
//    }

//    @Test
//    public void denySessionSeat_ReturnsRegistrationCanceled_WhenThereIsNoRegistrationInWaitingList() {
//        //TODO: Criar classe contendo as configurações de e-mail
//
//        // Nesse caso, a sessão não começou e não existe inscrição na lista de espera.
//        UUID accountId = registrationWaitingConfirmation.getAccount().getId();
//        UUID registrationId = registrationWaitingConfirmation.getId();
//        String timeToConfirmEmail = "12";
//        registrationWaitingConfirmation.setTimeEmailWasSent(
//                LocalDateTime.now()
//        );
//        when(registrationRepository.findById(any(UUID.class)))
//                .thenReturn(Optional.of(registrationWaitingConfirmation));
//        when(sessionRepository.findByIdWithPessimisticLock(any(UUID.class)))
//                .thenReturn(Optional.of(registrationWaitingConfirmation.getSession()));
//        when(emailConfirmationTime.getEmailConfirmationTime()).thenReturn(timeToConfirmEmail);
//        when(registrationRepository.existsBySessionIdAndRegistrationStatus(
//                any(UUID.class),
//                any(RegistrationStatus.class)
//        )).thenReturn(false);
//        when(registrationRepository.save(any(Registration.class)))
//                .thenReturn(registrationWaitingConfirmation);
//
//        Registration registrationCreated = registrationService.denySessionSeat(accountId, registrationId);
//
//        verify(registrationRepository, times(1)).save(any(Registration.class));
//        verify(sessionRepository, times(1)).save(any(Session.class));
//        assertThat(registrationCreated).isNotNull();
//        assertThat(registrationCreated.getId()).isEqualTo(registrationWaitingConfirmation.getId());
//        assertThat(registrationCreated.getAccount().getId())
//                .isEqualTo(registrationWaitingConfirmation.getAccount().getId());
//        assertThat(registrationCreated.getSession().getId())
//                .isEqualTo(registrationWaitingConfirmation.getSession().getId());
//        assertThat(registrationCreated.getSession().getConfirmedSeats())
//                .isEqualTo(registrationWaitingConfirmation.getSession().getConfirmedSeats());
//        assertThat(registrationCreated.getRegistrationStatus())
//                .isEqualTo(RegistrationStatus.CANCELED_BY_USER);
//        assertThat(registrationCreated.getDate())
//                .isEqualTo(registrationWaitingConfirmation.getDate());
//    }

    @Test
    public void cancelAllRegistrationInWaitConfirmation_DoNotCancelRegistrations_WhenThereAreNotRegistrationsInWaitConfirmation() {
        when(registrationRepository.findAllByRegistrationStatus(
                any(LocalDateTime.class),
                any(RegistrationStatus.class)
        )).thenReturn(List.of());

        registrationService.cancelAllRegistrationInWaitConfirmation();

        verify(sessionRepository, times(0))
                .findByIdWithPessimisticLock(any(UUID.class));
        verify(registrationRepository, times(0))
                .save(any(Registration.class));
    }

//    @Test
//    public void cancelAllRegistrationInWaitConfirmation_DoNotCancelRegistrations_WhenThereAreNotRegistrationsExpired() {
        //TODO: Criar classe contendo as configurações de e-mail
//        //Nesse caso, não há inscrições esperando confirmação já expiradas.
//        String timeToConfirmEmail = "12";
//        registrationWaitingConfirmation.setTimeEmailWasSent(
//                LocalDateTime.now()
//        );
//        when(registrationRepository.findAllByRegistrationStatus(
//                any(LocalDateTime.class),
//                any(RegistrationStatus.class)
//        )).thenReturn(List.of(registrationWaitingConfirmation));
//        when(emailConfirmationTime.getEmailConfirmationTime()).thenReturn(timeToConfirmEmail);
//
//        registrationService.cancelAllRegistrationInWaitConfirmation();
//
//        verify(sessionRepository, times(0))
//                .findByIdWithPessimisticLock(any(UUID.class));
//        verify(registrationRepository, times(0))
//                .save(any(Registration.class));
//    }

//    @Test
//    public void cancelAllRegistrationInWaitConfirmation_CancelRegistrations_WhenSessionHasStarted() {
//        //TODO: Criar classe contendo as configurações de e-mail
//        String timeToConfirmEmail = "12";
//        registrationWaitingConfirmation.setTimeEmailWasSent(
//                LocalDateTime.of(2022, 11, 30, 0, 0, 0)
//        );
//        List<SessionSchedule> sessionSchedulesStarted = List.of(
//                SessionScheduleFactory.sampleSessionScheduleThatStarted()
//        );
//        registrationWaitingConfirmation.getSession().setSessionSchedules(sessionSchedulesStarted);
//        when(registrationRepository.findAllByRegistrationStatus(
//                any(LocalDateTime.class),
//                any(RegistrationStatus.class)
//        )).thenReturn(List.of(registrationWaitingConfirmation));
//        when(emailConfirmationTime.getEmailConfirmationTime()).thenReturn(timeToConfirmEmail);
//        when(sessionRepository.findByIdWithPessimisticLock(
//                any(UUID.class)
//        )).thenReturn(Optional.of(registrationWaitingConfirmation.getSession()));
//
//        registrationService.cancelAllRegistrationInWaitConfirmation();
//
//        verify(sessionRepository, times(1))
//                .save(any(Session.class));
//        verify(registrationRepository, times(1))
//                .save(any(Registration.class));
//        assertThat(registrationWaitingConfirmation.getRegistrationStatus())
//                .isEqualTo(RegistrationStatus.CANCELED_BY_SYSTEM);
//    }

//    @Test
//    public void cancelAllRegistrationInWaitConfirmation_CancelRegistrations_WhenExistAnyRegistrationInWaitingListAndAccountHasAllowEmail() {
//        //TODO: Criar classe contendo as configurações de e-mail
//        //Nesse caso, a sessão não começou, existe inscrição na lista de espera,
//        // conta tem e-mail autorizado e o e-mail é enviado
//        try {
//            String timeToConfirmEmail = "12";
//            registrationWaitingConfirmation.setTimeEmailWasSent(
//                    LocalDateTime.of(2022, 11, 30, 0, 0, 0)
//            );
//            when(registrationRepository.findAllByRegistrationStatus(
//                    any(LocalDateTime.class),
//                    any(RegistrationStatus.class)
//            )).thenReturn(List.of(registrationWaitingConfirmation));
//            when(emailConfirmationTime.getEmailConfirmationTime()).thenReturn(timeToConfirmEmail);
//            when(sessionRepository.findByIdWithPessimisticLock(
//                    any(UUID.class)
//            )).thenReturn(Optional.of(registrationWaitingConfirmation.getSession()));
//            when(registrationRepository.existsBySessionIdAndRegistrationStatus(
//                    any(UUID.class),
//                    any(RegistrationStatus.class)
//            )).thenReturn(true);
//            when(registrationRepository.getFirstBySessionIdAndRegistrationStatusOrderByDate(
//                    any(UUID.class),
//                    any(RegistrationStatus.class)
//            )).thenReturn(Optional.of(registrationWaitingListStatus));
//            doNothing().when(emailService).sendEmailToConfirmRegistration(
//                    any(Account.class),
//                    any(Registration.class)
//            );
//
//            registrationService.cancelAllRegistrationInWaitConfirmation();
//
//            verify(registrationRepository, times(2)).save(any(Registration.class));
//            verify(emailService, times(1))
//                    .sendEmailToConfirmRegistration(any(Account.class), any(Registration.class));
//            assertThat(registrationWaitingListStatus.getRegistrationStatus())
//                    .isEqualTo(RegistrationStatus.WAITING_CONFIRMATION);
//            assertThat(registrationWaitingConfirmation.getRegistrationStatus())
//                    .isEqualTo(RegistrationStatus.CANCELED_BY_SYSTEM);
//        } catch (MessagingException e) {
//        }
//    }

//    @Test
//    public void cancelAllRegistrationInWaitConfirmation_CancelRegistrations_WhenExistAnyRegistrationInWaitingListAndEmailIsNotSend() {
//        //TODO: Criar classe contendo as configurações de e-mail
//        //TODO: Pegar exceção lançada ao não conseguir enviar e-mail
//        //Nesse caso, a sessão não começou, existe inscrição na lista de espera,
//        // conta tem e-mail autorizado e o e-mail não é enviado.
//        try {
//            String timeToConfirmEmail = "12";
//            registrationWaitingConfirmation.setTimeEmailWasSent(
//                    LocalDateTime.of(2022, 11, 30, 0, 0, 0)
//            );
//            when(registrationRepository.findAllByRegistrationStatus(
//                    any(LocalDateTime.class),
//                    any(RegistrationStatus.class)
//            )).thenReturn(List.of(registrationWaitingConfirmation));
//            when(emailConfirmationTime.getEmailConfirmationTime()).thenReturn(timeToConfirmEmail);
//            when(sessionRepository.findByIdWithPessimisticLock(
//                    any(UUID.class)
//            )).thenReturn(Optional.of(registrationWaitingConfirmation.getSession()));
//            when(registrationRepository.existsBySessionIdAndRegistrationStatus(
//                    any(UUID.class),
//                    any(RegistrationStatus.class)
//            )).thenReturn(true);
//            when(registrationRepository.getFirstBySessionIdAndRegistrationStatusOrderByDate(
//                    any(UUID.class),
//                    any(RegistrationStatus.class)
//            )).thenReturn(Optional.of(registrationWaitingListStatus));
//            doThrow(new MessagingException()).when(emailService).sendEmailToConfirmRegistration(
//                    any(Account.class),
//                    any(Registration.class)
//            );
//
//            registrationService.cancelAllRegistrationInWaitConfirmation();
//
//            verify(registrationRepository, times(2)).save(any(Registration.class));
//            verify(emailService, times(1))
//                    .sendEmailToConfirmRegistration(any(Account.class), any(Registration.class));
//            assertThat(registrationWaitingListStatus.getRegistrationStatus())
//                    .isEqualTo(RegistrationStatus.WAITING_CONFIRMATION);
//            assertThat(registrationWaitingConfirmation.getRegistrationStatus())
//                    .isEqualTo(RegistrationStatus.CANCELED_BY_SYSTEM);
//        } catch (MessagingException e) {
//        }
//    }

//    @Test
//    public void cancelAllRegistrationInWaitConfirmation_CancelRegistrations_WhenExistAnyRegistrationInWaitingListAndAccountHasNotAllowEmail() {
//        //TODO: Criar classe contendo as configurações de e-mail
//        //Nesse caso, a sessão não começou, existe inscrição na lista de espera e
//        //a conta não tem e-mail autorizado.
//
//        String timeToConfirmEmail = "12";
//        registrationWaitingConfirmation.setTimeEmailWasSent(
//                LocalDateTime.of(2022, 11, 30, 0, 0, 0)
//        );
//        registrationWaitingConfirmation.getAccount().setAllowEmail(false);
//        when(registrationRepository.findAllByRegistrationStatus(
//                any(LocalDateTime.class),
//                any(RegistrationStatus.class)
//        )).thenReturn(List.of(registrationWaitingConfirmation));
//        when(emailConfirmationTime.getEmailConfirmationTime()).thenReturn(timeToConfirmEmail);
//        when(sessionRepository.findByIdWithPessimisticLock(
//                any(UUID.class)
//        )).thenReturn(Optional.of(registrationWaitingConfirmation.getSession()));
//        when(registrationRepository.existsBySessionIdAndRegistrationStatus(
//                any(UUID.class),
//                any(RegistrationStatus.class)
//        )).thenReturn(true);
//        when(registrationRepository.getFirstBySessionIdAndRegistrationStatusOrderByDate(
//                any(UUID.class),
//                any(RegistrationStatus.class)
//        )).thenReturn(Optional.of(registrationWaitingListStatus));
//
//        registrationService.cancelAllRegistrationInWaitConfirmation();
//
//        verify(registrationRepository, times(2)).save(any(Registration.class));
//        assertThat(registrationWaitingListStatus.getRegistrationStatus())
//                .isEqualTo(RegistrationStatus.WAITING_CONFIRMATION);
//        assertThat(registrationWaitingConfirmation.getRegistrationStatus())
//                .isEqualTo(RegistrationStatus.CANCELED_BY_SYSTEM);
//    }

//    @Test
//    public void cancelAllRegistrationInWaitConfirmation_CancelRegistrations_WhenThereIsNoRegistrationInWaitingList() {
//        //TODO: Criar classe contendo as configurações de e-mail.
//
//        // Nesse caso, a sessão não começou e não existe inscrição na lista de espera.
//        String timeToConfirmEmail = "12";
//        registrationWaitingConfirmation.setTimeEmailWasSent(
//                LocalDateTime.of(2022, 11, 30, 0, 0, 0)
//        );
//        when(registrationRepository.findAllByRegistrationStatus(
//                any(LocalDateTime.class),
//                any(RegistrationStatus.class)
//        )).thenReturn(List.of(registrationWaitingConfirmation));
//        when(emailConfirmationTime.getEmailConfirmationTime()).thenReturn(timeToConfirmEmail);
//        when(sessionRepository.findByIdWithPessimisticLock(
//                any(UUID.class)
//        )).thenReturn(Optional.of(registrationWaitingConfirmation.getSession()));
//        when(registrationRepository.existsBySessionIdAndRegistrationStatus(
//                any(UUID.class),
//                any(RegistrationStatus.class)
//        )).thenReturn(false);
//
//        registrationService.cancelAllRegistrationInWaitConfirmation();
//
//        verify(registrationRepository, times(1)).save(any(Registration.class));
//        verify(sessionRepository, times(1)).save(any(Session.class));
//        assertThat(registrationWaitingConfirmation.getRegistrationStatus())
//                .isEqualTo(RegistrationStatus.CANCELED_BY_SYSTEM);
//    }

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

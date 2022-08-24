package br.edu.ifsp.spo.eventos.eventplatformbackend.registration;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.Activity;
import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.ActivityRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.*;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.Session;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.SessionRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.Subevent;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.SubeventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class RegistrationService {
    private final EventRepository eventRepository;
    private final SubeventRepository subeventRepository;
    private final ActivityRepository activityRepository;
    private final SessionRepository sessionRepository;
    private final RegistrationRepository registrationRepository;
    private final AccountRepository accountRepository;

    //TODO: Método que verifica se uma sessão acabou para poder finalziar as inscrições (alterar o status)

    @Transactional
    public Registration create(RegistrationCreateDto registrationCreateDto, UUID eventId, UUID activityId, UUID sessionId) {
        var account = getAccount(registrationCreateDto.getAccountId());
        var session = getSession(sessionId);
        var activity = session.getActivity();
        var event = activity.getEvent();
        checksIfEventIsAssociateToActivity(eventId, activity);
        checksIfActivityIsAssociateToSession(activityId, session);
        checkIfSessionIsCancelled(session);
        checkIfActivityIsNotPublished(activity);
        checkIfTodayIsOutOfRegistrationPeriodOfEvent(event);

        Account accountLock = accountRepository.findByIdWithPessimisticLock(account.getId()).get();

        checkIfAccountHasARegistrationInSession(accountLock.getId(), sessionId);

        var registrations = registrationRepository.findAllByAccountIdAndRegistrationStatus(
            accountLock.getId(),
            RegistrationStatus.CONFIRMED
        );

        registrations.stream()
            .flatMap(registration -> registration.getSession().getSessionsSchedules().stream())
            .forEach(schedule -> {
                if(session.getSessionsSchedules().stream().anyMatch(s -> s.hasIntersection(schedule))) {
                    throw new BusinessRuleException(BusinessRuleType.REGISTRATION_CREATE_HAS_SCHEDULE_CONFLICT);
                }
            });

        Session sessionLock = sessionRepository.findByIdWithPessimisticLock(sessionId).get();

        checkIfSessionLockIsFull(sessionLock);

        sessionLock.incrementNumberOfConfirmedSeats();
        sessionRepository.save(sessionLock);

        return registrationRepository.save(Registration.createWithConfirmedStatus(accountLock,sessionLock));
    }

    @Transactional
    public Registration create(RegistrationCreateDto registrationCreateDto, UUID eventId, UUID subeventId, UUID activityId, UUID sessionId) {
        var account = getAccount(registrationCreateDto.getAccountId());
        var session = getSession(sessionId);
        var activity = session.getActivity();
        var subevent = activity.getSubevent();
        var event = subevent.getEvent();
        checkIfEventIsAssociateToSubevent(eventId, subevent);
        checksIfSubeventIsAssociateToActivity(subeventId, activity);
        checksIfEventIsAssociateToActivity(eventId, activity);
        checksIfActivityIsAssociateToSession(activityId, session);
        checkIfSessionIsCancelled(session);
        checkIfActivityIsNotPublished(activity);
        checkIfTodayIsOutOfRegistrationPeriodOfEvent(event);

        Account accountLock = accountRepository.findByIdWithPessimisticLock(account.getId()).get();

        checkIfAccountHasARegistrationInSession(accountLock.getId(), sessionId);

        var registrations = registrationRepository.findAllByAccountIdAndRegistrationStatus(
                accountLock.getId(),
                RegistrationStatus.CONFIRMED
        );

        registrations.stream()
            .flatMap(registration -> registration.getSession().getSessionsSchedules().stream())
            .forEach(schedule -> {
                if(session.getSessionsSchedules().stream().anyMatch(s -> s.hasIntersection(schedule))) {
                    throw new BusinessRuleException(BusinessRuleType.REGISTRATION_CREATE_HAS_SCHEDULE_CONFLICT);
                }
            });

        Session sessionLock = sessionRepository.findByIdWithPessimisticLock(sessionId).get();

        checkIfSessionLockIsFull(sessionLock);

        sessionLock.incrementNumberOfConfirmedSeats();
        sessionRepository.save(sessionLock);

        return registrationRepository.save(Registration.createWithConfirmedStatus(accountLock, sessionLock));
    }

    @Transactional
    public Registration create(RegistrationCreateDto registrationCreateDto, UUID sessionId) {
        var account = getAccount(registrationCreateDto.getAccountId());
        var session = getSession(sessionId);
        var activity = session.getActivity();
        var event = activity.getEvent();
        checkIfSessionIsCancelled(session);
        checkIfActivityIsNotPublished(activity);
        checkIfTodayIsOutOfRegistrationPeriodOfEvent(event);

        Account accountLock = accountRepository.findByIdWithPessimisticLock(account.getId()).get();

        checkIfAccountHasARegistrationInSession(accountLock.getId(), sessionId);

        var registrations = registrationRepository.findAllByAccountIdAndRegistrationStatus(
                accountLock.getId(),
                RegistrationStatus.CONFIRMED
        );

        registrations.stream()
            .flatMap(registration -> registration.getSession().getSessionsSchedules().stream())
            .forEach(schedule -> {
                if(session.getSessionsSchedules().stream().anyMatch(s -> s.hasIntersection(schedule))) {
                    throw new BusinessRuleException(BusinessRuleType.REGISTRATION_CREATE_HAS_SCHEDULE_CONFLICT);
                }
            });

        Session sessionLock = sessionRepository.findByIdWithPessimisticLock(sessionId).get();

        checkIfSessionLockIsFull(sessionLock);

        sessionLock.incrementNumberOfConfirmedSeats();
        sessionRepository.save(sessionLock);

        return registrationRepository.save(Registration.createWithConfirmedStatus(accountLock, sessionLock));
    }

    @Transactional
    public Registration createRegistrationInWaitList(RegistrationCreateDto registrationCreateDto, UUID eventId, UUID activityId, UUID sessionId) {
        var account = getAccount(registrationCreateDto.getAccountId());
        var session = getSession(sessionId);
        var activity = session.getActivity();
        var event = activity.getEvent();
        checksIfEventIsAssociateToActivity(eventId, activity);
        checksIfActivityIsAssociateToSession(activityId, session);
        checkIfSessionIsCancelled(session);
        checkIfActivityIsNotPublished(activity);
        checkIfTodayIsOutOfRegistrationPeriodOfEvent(event);

        Account accountLock = accountRepository.findByIdWithPessimisticLock(account.getId()).get();

        checkIfAccountHasARegistrationInSession(accountLock.getId(), sessionId);

        Session sessionLock = sessionRepository.findByIdWithPessimisticLock(sessionId).get();

        checkIfSessionIsNotFull(sessionLock);

        return registrationRepository.save(Registration.createWithWaitingListdStatus(accountLock, sessionLock));
    }

    @Transactional
    public Registration createRegistrationInWaitList(RegistrationCreateDto registrationCreateDto, UUID eventId, UUID subeventId, UUID activityId, UUID sessionId) {
        var account = getAccount(registrationCreateDto.getAccountId());
        var session = getSession(sessionId);
        var activity = session.getActivity();
        var subevent = activity.getSubevent();
        var event = subevent.getEvent();
        checkIfEventIsAssociateToSubevent(eventId, subevent);
        checksIfSubeventIsAssociateToActivity(subeventId, activity);
        checksIfEventIsAssociateToActivity(eventId, activity);
        checksIfActivityIsAssociateToSession(activityId, session);
        checkIfSessionIsCancelled(session);
        checkIfActivityIsNotPublished(activity);
        checkIfTodayIsOutOfRegistrationPeriodOfEvent(event);

        Account accountLock = accountRepository.findByIdWithPessimisticLock(account.getId()).get();

        checkIfAccountHasARegistrationInSession(accountLock.getId(), sessionId);

        Session sessionLock = sessionRepository.findByIdWithPessimisticLock(sessionId).get();

        checkIfSessionIsNotFull(sessionLock);

        return registrationRepository.save(Registration.createWithWaitingListdStatus(accountLock, sessionLock));
    }

    @Transactional
    public Registration createRegistrationInWaitList(RegistrationCreateDto registrationCreateDto, UUID sessionId) {
        var account = getAccount(registrationCreateDto.getAccountId());
        var session = getSession(sessionId);
        var activity = session.getActivity();
        var event = activity.getEvent();

        Account accountLock = accountRepository.findByIdWithPessimisticLock(account.getId()).get();

        checkIfSessionIsCancelled(session);
        checkIfActivityIsNotPublished(activity);
        checkIfTodayIsOutOfRegistrationPeriodOfEvent(event);
        checkIfAccountHasARegistrationInSession(accountLock.getId(), sessionId);
        Session sessionLock = sessionRepository.findByIdWithPessimisticLock(sessionId).get();

        checkIfSessionIsNotFull(sessionLock);

        return registrationRepository.save(Registration.createWithWaitingListdStatus(accountLock, sessionLock));
    }

    public Registration cancel(UUID eventId, UUID activityId, UUID sessionId, UUID registrationId) {
        var registration = getRegistration(registrationId);
        var session = registration.getSession();
        var activity = session.getActivity();
        checksIfEventIsAssociateToActivity(eventId, activity);
        checksIfActivityIsAssociateToSession(activityId, session);
        checksIfSessionIsAssociateToRegistration(sessionId, registration);

        Session sessionLock = sessionRepository.findByIdWithPessimisticLock(sessionId).get();

        if(sessionLock.isFull() && checkIfExistAnyRegistrationInWaitList()) {
           var firstRegistrationInWaitList = registrationRepository.getFirstByRegistrationStatus(RegistrationStatus.WAITING_LIST).get();
           firstRegistrationInWaitList.setRegistrationStatus(RegistrationStatus.WAITING_CONFIRMATION);
           registrationRepository.save(firstRegistrationInWaitList);
           //email
        }

        registration.setRegistrationStatus(RegistrationStatus.CANCELED_BY_ADMIN);
        log.info("Registration cancelled: date={}, status={}", LocalDateTime.now(), registration.getRegistrationStatus());

        return registrationRepository.save(registration);
    }

    public Registration cancel(UUID eventId, UUID subeventId, UUID activityId, UUID sessionId, UUID registrationId) {
        var registration = getRegistration(registrationId);
        var session = registration.getSession();
        var activity = session.getActivity();
        var subevent = activity.getSubevent();
        checkIfEventIsAssociateToSubevent(eventId, subevent);
        checksIfSubeventIsAssociateToActivity(subeventId, activity);
        checksIfEventIsAssociateToActivity(eventId, activity);
        checksIfActivityIsAssociateToSession(activityId, session);
        checksIfSessionIsAssociateToRegistration(sessionId, registration);

        Session sessionLock = sessionRepository.findByIdWithPessimisticLock(sessionId).get();

        if(sessionLock.isFull() && checkIfExistAnyRegistrationInWaitList()) {
            var firstRegistrationInWaitList = registrationRepository.getFirstByRegistrationStatus(RegistrationStatus.WAITING_LIST).get();
            firstRegistrationInWaitList.setRegistrationStatus(RegistrationStatus.WAITING_CONFIRMATION);
            registrationRepository.save(firstRegistrationInWaitList);
            //email
        }

        registration.setRegistrationStatus(RegistrationStatus.CANCELED_BY_ADMIN);
        log.info("Registration cancelled: date={}, status={}", LocalDateTime.now(), registration.getRegistrationStatus());

        return registrationRepository.save(registration);
    }

    public Registration cancel(UUID accountId, UUID registrationId) {
        var registration = getRegistration(registrationId);
        var session = registration.getSession();
        checksIfAccountIsAssociateToRegistration(accountId, registration);

        Session sessionLock = sessionRepository.findByIdWithPessimisticLock(session.getId()).get();

        if(sessionLock.isFull() && checkIfExistAnyRegistrationInWaitList()) {
            var firstRegistrationInWaitList = registrationRepository.getFirstByRegistrationStatus(RegistrationStatus.WAITING_LIST).get();
            firstRegistrationInWaitList.setRegistrationStatus(RegistrationStatus.WAITING_CONFIRMATION);
            registrationRepository.save(firstRegistrationInWaitList);
            //email
        }

        registration.setRegistrationStatus(RegistrationStatus.CANCELED_BY_USER);
        log.info("Registration cancelled: date={}, status={}", LocalDateTime.now(), registration.getRegistrationStatus());

        return registrationRepository.save(registration);
    }

    public List<Registration> findAll(UUID eventId, UUID activityId, UUID sessionId) {
        var session = getSession(sessionId);
        var activity = session.getActivity();
        checksIfEventIsAssociateToActivity(eventId, activity);
        checksIfActivityIsAssociateToSession(activityId, session);

        return registrationRepository.findAllBySessionId(sessionId);
    }

    public List<Registration> findAll(UUID eventId, UUID subeventId, UUID activityId, UUID sessionId) {
        var session = getSession(sessionId);
        var activity = session.getActivity();
        var subevent = activity.getSubevent();
        checkIfEventIsAssociateToSubevent(eventId, subevent);
        checksIfSubeventIsAssociateToActivity(subeventId, activity);
        checksIfEventIsAssociateToActivity(eventId, activity);
        checksIfActivityIsAssociateToSession(activityId, session);

        return registrationRepository.findAllBySessionId(sessionId);
    }

    public List<Registration> findAll(UUID accountId) {
        var account = getAccount(accountId);
        return registrationRepository.findAllByAccountIdAndRegistrationStatusIn(
            accountId,
            List.of(RegistrationStatus.CONFIRMED,
                    RegistrationStatus.WAITING_LIST,
                    RegistrationStatus.WAITING_CONFIRMATION
            )
        );
    }

    private boolean checkIfExistAnyRegistrationInWaitList() {
        return registrationRepository.existsByRegistrationStatus(RegistrationStatus.WAITING_LIST);
    }

    private void checkIfSessionLockIsFull(Session sessionLock) {
        if(sessionLock.isFull()) {
            throw new BusinessRuleException(BusinessRuleType.REGISTRATION_CREATE_WITH_NO_SEATS_AVAILABLE);
        }
    }

    private void checkIfSessionIsNotFull(Session session) {
        if(!session.isFull()) {
            throw new BusinessRuleException(BusinessRuleType.REGISTRATION_CREATE_IN_WAIT_LIST_WITH_SEATS_VAILABLE);
        }
    }

    private void checkIfTodayIsOutOfRegistrationPeriodOfEvent(Event event) {
        if(event.getRegistrationPeriod().todayIsOutOfPeriod()) {
            throw new BusinessRuleException(BusinessRuleType.REGISTRATION_CREATE_WITH_EVENT_OUT_OF_REGISTRATION_PERIOD);
        }
    }

    private void checkIfActivityIsNotPublished(Activity activity) {
        if(activity.getStatus() != EventStatus.PUBLISHED) {
            throw new BusinessRuleException(BusinessRuleType.REGISTRATION_CREATE_WITH_ACTIVITY_NOT_PUBLISHED);
        }
    }

    private void checkIfSessionIsCancelled(Session session) {
        if(session.isCanceled()) {
            throw new BusinessRuleException(BusinessRuleType.REGISTRATION_CREATE_WITH_CANCELED_SESSION);
        }
    }

    private void checkIfAccountHasARegistrationInSession(UUID accountId, UUID sessionId) {
        if(registrationRepository.existsBySessionIdAndAccountIdAndRegistrationStatusIn(
            sessionId,
            accountId,
            List.of(RegistrationStatus.CONFIRMED, RegistrationStatus.WAITING_LIST)
        )) {
            throw new BusinessRuleException(BusinessRuleType.REGISTRATION_CREATE_ALREADY_EXISTS);
        }
    }

    private void checkIfEventIsAssociateToSubevent(UUID eventId, Subevent subevent) {
        if (!subevent.getEvent().getId().equals(eventId)) {
            throw new ResourceReferentialIntegrityException(ResourceName.SUBEVENT, ResourceName.EVENT);
        }
    }

    private void checksIfSubeventIsAssociateToActivity(UUID subeventId, Activity activity) {
        if (!activity.getSubevent().getId().equals(subeventId)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_IS_NOT_ASSOCIATED_TO_SUBEVENT);
        }
    }

    private void checksIfEventIsAssociateToActivity(UUID eventId, Activity activity) {
        if (!activity.getEvent().getId().equals(eventId)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_IS_NOT_ASSOCIATED_TO_EVENT);
        }
    }

    private void checksIfActivityIsAssociateToSession(UUID activityId, Session session) {
        if (!session.getActivity().getId().equals(activityId)) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_IS_NOT_ASSOCIATED_TO_ACTIVITY);
        }
    }

    private void checksIfSessionIsAssociateToRegistration(UUID sessionId, Registration registration) {
        if (!registration.getSession().getId().equals(sessionId)) {
            throw new BusinessRuleException(BusinessRuleType.REGISTRATION_IS_NOT_ASSOCIATED_TO_SESSION);
        }
    }

    private void checksIfAccountIsAssociateToRegistration(UUID accountId, Registration registration) {
        if (!registration.getSession().getId().equals(accountId)) {
            throw new BusinessRuleException(BusinessRuleType.REGISTRATION_IS_NOT_ASSOCIATED_TO_ACCOUNT);
        }
    }

    private Account getAccount(UUID accountId) {
        return accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException(ResourceName.ACCOUNT, accountId));
    }

    private Event getEvent(UUID eventId) {
        return eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException(ResourceName.EVENT, eventId));
    }

    private Subevent getSubevent(UUID subeventId) {
        return subeventRepository.findById(subeventId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.SUBEVENT, subeventId));
    }

    private Activity getActivity(UUID activityId) {
        return activityRepository.findById(activityId)
            .orElseThrow(() -> new ResourceNotFoundException(ResourceName.ACTIVITY, activityId));
    }

    private Session getSession(UUID sessionId) {
        return sessionRepository.findById(sessionId)
            .orElseThrow(() -> new ResourceNotFoundException(ResourceName.SESSION, sessionId));
    }

    private Registration getRegistration(UUID registrationId) {
        return registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.REGISTRATION, registrationId));
    }
}

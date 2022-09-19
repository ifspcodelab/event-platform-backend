package br.edu.ifsp.spo.eventos.eventplatformbackend.registration;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.Activity;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.email.EmailService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.*;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtUserDetails;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus;
import br.edu.ifsp.spo.eventos.eventplatformbackend.organizer_authorization.OrganizerAuthorizationException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.organizer_authorization.OrganizerAuthorizationExceptionType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.Session;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.SessionRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.SessionSchedule;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.Subevent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {
    private final SessionRepository sessionRepository;
    private final RegistrationRepository registrationRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    @Value("${registration.email-confirmation-time}")
    private String emailConfirmationTime;

    private void checkUserEventPermission(UUID eventId) {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(jwtUserDetails.isAdmin()){
            return;
        }

        if(!jwtUserDetails.hasPermissionForEvent(eventId)) {
            throw new OrganizerAuthorizationException(OrganizerAuthorizationExceptionType.UNAUTHORIZED_EVENT, jwtUserDetails.getUsername(), eventId);
        }
    }

    private void checkUserSubEventPermission(UUID subEventId) {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(jwtUserDetails.isAdmin()){
            return;
        }

        if(!jwtUserDetails.hasPermissionForSubEvent(subEventId)) {
            throw new OrganizerAuthorizationException(OrganizerAuthorizationExceptionType.UNAUTHORIZED_SUBEVENT, jwtUserDetails.getUsername(), subEventId);
        }
    }

    @Transactional
    public Registration create(RegistrationCreateDto registrationCreateDto, UUID eventId, UUID activityId, UUID sessionId) {
        checkUserEventPermission(eventId);
        Account account = accountRepository.findByIdWithPessimisticLock(registrationCreateDto.getAccountId()).get();
        Session session = sessionRepository.findByIdWithPessimisticLock(sessionId).get();

        checksIfEventIsAssociateToActivity(eventId, session.getActivity());
        checksIfActivityIsAssociateToSession(activityId, session);
        checkIfSessionIsCancelled(session);
        checksIfAccountHasARegistrationInActivity(account.getId(), activityId);
        checkIfAccountHasARegistrationInSession(account.getId(), sessionId);
        checkIfExistsAnyRegistrationWithConflict(account, session);
        checkIfSessionLockIsFull(session);

        Registration registration = registrationRepository.save(Registration.createWithConfirmedStatus(account, session));
        session.incrementNumberOfConfirmedSeats();
        sessionRepository.save(session);

        cancellAllRegistrationsInWaitListWithConflict(account, session);

        return registration;
    }

    @Transactional
    public Registration create(RegistrationCreateDto registrationCreateDto, UUID eventId, UUID subeventId, UUID activityId, UUID sessionId) {
        checkUserSubEventPermission(subeventId);
        Account account = accountRepository.findByIdWithPessimisticLock(registrationCreateDto.getAccountId()).get();
        Session session = sessionRepository.findByIdWithPessimisticLock(sessionId).get();

        var activity = session.getActivity();
        var subevent = activity.getSubevent();
        checkIfEventIsAssociateToSubevent(eventId, subevent);
        checksIfSubeventIsAssociateToActivity(subeventId, activity);
        checksIfEventIsAssociateToActivity(eventId, activity);
        checksIfActivityIsAssociateToSession(activityId, session);
        checkIfSessionIsCancelled(session);
        checksIfAccountHasARegistrationInActivity(account.getId(), activityId);
        checkIfAccountHasARegistrationInSession(account.getId(), sessionId);
        checkIfExistsAnyRegistrationWithConflict(account, session);
        checkIfSessionLockIsFull(session);

        Registration registration = registrationRepository.save(Registration.createWithConfirmedStatus(account, session));
        session.incrementNumberOfConfirmedSeats();
        sessionRepository.save(session);

        cancellAllRegistrationsInWaitListWithConflict(account, session);

        return registration;
    }

    @Transactional
    public Registration create(RegistrationCreateDto registrationCreateDto, UUID sessionId) {
        Account account = accountRepository.findByIdWithPessimisticLock(registrationCreateDto.getAccountId()).get();
        Session session = sessionRepository.findByIdWithPessimisticLock(sessionId).get();
        var activity = session.getActivity();
        var event = activity.getEvent();
        checkIfSessionIsCancelled(session);
        checkIfActivityIsCanceled(activity);
        checksIfItIsOutOfRegistrationPeriod(event);
        checksIfAccountHasARegistrationInActivity(account.getId(), activity.getId());
        checkIfSessionStarted(session);
        checkIfAccountHasARegistrationInSession(account.getId(), sessionId);
        checkIfExistsAnyRegistrationWithConflict(account, session);
        checkIfSessionLockIsFull(session);

        session.incrementNumberOfConfirmedSeats();
        sessionRepository.save(session);

        cancellAllRegistrationsInWaitListWithConflict(account, session);

        return registrationRepository.save(Registration.createWithConfirmedStatus(account, session));

    }

    @Transactional
    public Registration createRegistrationInWaitList(RegistrationCreateDto registrationCreateDto, UUID eventId, UUID activityId, UUID sessionId) {
        checkUserEventPermission(eventId);
        Account account = accountRepository.findByIdWithPessimisticLock(registrationCreateDto.getAccountId()).get();
        Session session = sessionRepository.findByIdWithPessimisticLock(sessionId).get();
        var activity = session.getActivity();
        var event = activity.getEvent();
        checksIfEventIsAssociateToActivity(eventId, activity);
        checksIfActivityIsAssociateToSession(activityId, session);
        checkIfSessionIsCancelled(session);
        checkIfActivityIsCanceled(activity);
        checksIfItIsOutOfRegistrationPeriod(event);
        checksIfAccountHasARegistrationInActivity(account.getId(), activityId);
        checkIfSessionStarted(session);
        checkIfAccountHasARegistrationInSession(account.getId(), sessionId);
        checkIfExistsAnyRegistrationWithConflict(account, session);
        checkIfSessionIsNotFull(session);
        return registrationRepository.save(Registration.createWithWaitingListdStatus(account, session));
    }

    @Transactional
    public Registration createRegistrationInWaitList(RegistrationCreateDto registrationCreateDto, UUID eventId, UUID subeventId, UUID activityId, UUID sessionId) {
        checkUserSubEventPermission(subeventId);
        Account account = accountRepository.findByIdWithPessimisticLock(registrationCreateDto.getAccountId()).get();
        Session session = sessionRepository.findByIdWithPessimisticLock(sessionId).get();
        var activity = session.getActivity();
        var subevent = activity.getSubevent();
        var event = subevent.getEvent();
        checkIfEventIsAssociateToSubevent(eventId, subevent);
        checksIfSubeventIsAssociateToActivity(subeventId, activity);
        checksIfEventIsAssociateToActivity(eventId, activity);
        checksIfActivityIsAssociateToSession(activityId, session);
        checkIfSessionIsCancelled(session);
        checkIfActivityIsCanceled(activity);
        checksIfItIsOutOfRegistrationPeriod(event);
        checksIfAccountHasARegistrationInActivity(account.getId(), activityId);
        checkIfSessionStarted(session);
        checkIfAccountHasARegistrationInSession(account.getId(), sessionId);
        checkIfExistsAnyRegistrationWithConflict(account, session);
        checkIfSessionIsNotFull(session);

        return registrationRepository.save(Registration.createWithWaitingListdStatus(account, session));
    }

    @Transactional
    public Registration createRegistrationInWaitList(RegistrationCreateDto registrationCreateDto, UUID sessionId) {
        Account account = accountRepository.findByIdWithPessimisticLock(registrationCreateDto.getAccountId()).get();
        Session session = sessionRepository.findByIdWithPessimisticLock(sessionId).get();
        var activity = session.getActivity();
        var event = activity.getEvent();

        checkIfSessionIsCancelled(session);
        checkIfActivityIsCanceled(activity);
        checksIfItIsOutOfRegistrationPeriod(event);
        checkIfAccountHasARegistrationInSession(account.getId(), sessionId);
        checksIfAccountHasARegistrationInActivity(account.getId(), activity.getId());
        checkIfSessionStarted(session);
        checkIfExistsAnyRegistrationWithConflict(account, session);

        checkIfSessionIsNotFull(session);

        return registrationRepository.save(Registration.createWithWaitingListdStatus(account, session));
    }

    @Transactional
    public Registration cancel(UUID eventId, UUID activityId, UUID sessionId, UUID registrationId) {
        checkUserEventPermission(eventId);
        var registration = getRegistration(registrationId);
        Session session = sessionRepository.findByIdWithPessimisticLock(sessionId).get();
        var activity = session.getActivity();
        checksIfEventIsAssociateToActivity(eventId, activity);
        checksIfActivityIsAssociateToSession(activityId, session);
        checksIfSessionIsAssociateToRegistration(sessionId, registration);

        if(registration.getRegistrationStatus().equals(RegistrationStatus.WAITING_LIST)) {
            registration.setRegistrationStatus(RegistrationStatus.CANCELED_BY_ADMIN);
            log.info("Registration in wait list cancelled: date={}, status={}", LocalDateTime.now(), registration.getRegistrationStatus());
            return registrationRepository.save(registration);
        }

        if(isSessionStarted(session)) {
            session.decrementNumberOfConfirmedSeats();
            sessionRepository.save(session);
        }
        else if(checkIfExistAnyRegistrationInWaitListBySessionId(session.getId())) {
            var firstRegistrationInWaitList = registrationRepository.getFirstBySessionIdAndRegistrationStatusOrderByDate(sessionId, RegistrationStatus.WAITING_LIST).get();
            firstRegistrationInWaitList.setRegistrationStatus(RegistrationStatus.WAITING_CONFIRMATION);
            firstRegistrationInWaitList.setTimeEmailWasSent(LocalDateTime.now());
            registrationRepository.save(firstRegistrationInWaitList);
            sendEmailToConfirmRegistration(firstRegistrationInWaitList.getAccount(), firstRegistrationInWaitList);
        }
        else {
            session.decrementNumberOfConfirmedSeats();
            sessionRepository.save(session);
        }

        registration.setRegistrationStatus(RegistrationStatus.CANCELED_BY_ADMIN);
        log.info("Registration cancelled: date={}, status={}", LocalDateTime.now(), registration.getRegistrationStatus());

        return registrationRepository.save(registration);
    }

    @Transactional
    public Registration cancel(UUID eventId, UUID subeventId, UUID activityId, UUID sessionId, UUID registrationId) {
        checkUserSubEventPermission(subeventId);
        var registration = getRegistration(registrationId);
        Session session = sessionRepository.findByIdWithPessimisticLock(sessionId).get();
        var activity = session.getActivity();
        var subevent = activity.getSubevent();
        checkIfEventIsAssociateToSubevent(eventId, subevent);
        checksIfSubeventIsAssociateToActivity(subeventId, activity);
        checksIfEventIsAssociateToActivity(eventId, activity);
        checksIfActivityIsAssociateToSession(activityId, session);
        checksIfSessionIsAssociateToRegistration(sessionId, registration);

        if(registration.getRegistrationStatus().equals(RegistrationStatus.WAITING_LIST)) {
            registration.setRegistrationStatus(RegistrationStatus.CANCELED_BY_ADMIN);
            log.info("Registration in wait list cancelled: date={}, status={}", LocalDateTime.now(), registration.getRegistrationStatus());
            return registrationRepository.save(registration);
        }

        if(isSessionStarted(session)) {
            session.decrementNumberOfConfirmedSeats();
            sessionRepository.save(session);
        }

        else if(checkIfExistAnyRegistrationInWaitListBySessionId(session.getId())) {
            var firstRegistrationInWaitList = registrationRepository.getFirstBySessionIdAndRegistrationStatusOrderByDate(sessionId, RegistrationStatus.WAITING_LIST).get();
            firstRegistrationInWaitList.setRegistrationStatus(RegistrationStatus.WAITING_CONFIRMATION);
            firstRegistrationInWaitList.setTimeEmailWasSent(LocalDateTime.now());
            registrationRepository.save(firstRegistrationInWaitList);

            // Verificar o horário da sessão antes de enviar o e-mail
            sendEmailToConfirmRegistration(firstRegistrationInWaitList.getAccount(), firstRegistrationInWaitList);
        }

        else {
            session.decrementNumberOfConfirmedSeats();
            sessionRepository.save(session);
        }

        registration.setRegistrationStatus(RegistrationStatus.CANCELED_BY_ADMIN);
        log.info("Registration cancelled: date={}, status={}", LocalDateTime.now(), registration.getRegistrationStatus());

        return registrationRepository.save(registration);
    }

    @Transactional
    public Registration confirmWaitingList(UUID eventId, UUID activityId, UUID sessionId, UUID registrationId) {
        checkUserEventPermission(eventId);
        var registration = getRegistration(registrationId);
        Session session = sessionRepository.findByIdWithPessimisticLock(sessionId).get();
        var activity = session.getActivity();
        checksIfEventIsAssociateToActivity(eventId, activity);
        checksIfActivityIsAssociateToSession(activityId, session);
        checksIfSessionIsAssociateToRegistration(sessionId, registration);
        checksIfEmailWasAnswered(registration);
        cancellAllRegistrationsInWaitListWithConflict(registration.getAccount(), session);
        checkIfSessionLockIsFull(session);

        registration.setRegistrationStatus(RegistrationStatus.CONFIRMED);

        session.incrementNumberOfConfirmedSeats();
        sessionRepository.save(session);

        return registrationRepository.save(registration);
    }

    @Transactional
    public Registration confirmWaitingList(UUID eventId, UUID subeventId, UUID activityId, UUID sessionId, UUID registrationId) {
        checkUserSubEventPermission(subeventId);
        var registration = getRegistration(registrationId);
        Session session = sessionRepository.findByIdWithPessimisticLock(sessionId).get();
        var activity = session.getActivity();
        var subevent = activity.getSubevent();
        checkIfEventIsAssociateToSubevent(eventId, subevent);
        checksIfSubeventIsAssociateToActivity(subeventId, activity);
        checksIfEventIsAssociateToActivity(eventId, activity);
        checksIfActivityIsAssociateToSession(activityId, session);
        checksIfSessionIsAssociateToRegistration(sessionId, registration);
        checksIfEmailWasAnswered(registration);
        cancellAllRegistrationsInWaitListWithConflict(registration.getAccount(), session);
        checkIfSessionLockIsFull(session);

        registration.setRegistrationStatus(RegistrationStatus.CONFIRMED);

        session.incrementNumberOfConfirmedSeats();
        sessionRepository.save(session);

        return registrationRepository.save(registration);
    }

    @Transactional
    public Registration cancel(UUID accountId, UUID registrationId) {
        var registration = getRegistration(registrationId);
        Session session = sessionRepository.findByIdWithPessimisticLock(registration.getSession().getId()).get();
        checksIfAccountIsAssociateToRegistration(accountId, registration);

        if(registration.getRegistrationStatus().equals(RegistrationStatus.WAITING_LIST)) {
            registration.setRegistrationStatus(RegistrationStatus.CANCELED_BY_USER);
            log.info("Registration in wait list cancelled by user: date={}, status={}", LocalDateTime.now(), registration.getRegistrationStatus());
            return registrationRepository.save(registration);
        }

        if(isSessionStarted(session)) {
            session.decrementNumberOfConfirmedSeats();
            sessionRepository.save(session);
        }

        else if(checkIfExistAnyRegistrationInWaitListBySessionId(session.getId())) {
            var firstRegistrationInWaitList = registrationRepository.getFirstBySessionIdAndRegistrationStatusOrderByDate(session.getId(), RegistrationStatus.WAITING_LIST).get();
            firstRegistrationInWaitList.setRegistrationStatus(RegistrationStatus.WAITING_CONFIRMATION);
            firstRegistrationInWaitList.setTimeEmailWasSent(LocalDateTime.now());
            registrationRepository.save(firstRegistrationInWaitList);
            sendEmailToConfirmRegistration(firstRegistrationInWaitList.getAccount(), firstRegistrationInWaitList);
        }

        else {
            session.decrementNumberOfConfirmedSeats();
            sessionRepository.save(session);
        }
        registration.setRegistrationStatus(RegistrationStatus.CANCELED_BY_USER);
        log.info("Registration cancelled: date={}, status={}", LocalDateTime.now(), registration.getRegistrationStatus());

        return registrationRepository.save(registration);
    }

    public List<Registration> findAll(UUID eventId, UUID activityId, UUID sessionId) {
        checkUserEventPermission(eventId);
        var session = getSession(sessionId);
        var activity = session.getActivity();
        checksIfEventIsAssociateToActivity(eventId, activity);
        checksIfActivityIsAssociateToSession(activityId, session);

        return registrationRepository.findAllBySessionId(sessionId);
    }

    public List<Registration> findAll(UUID eventId, UUID subeventId, UUID activityId, UUID sessionId) {
        checkUserSubEventPermission(subeventId);
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
        return registrationRepository.findAllByAccountId(accountId);
    }

    @Transactional
    public Registration acceptSessionSeat(UUID accountId, UUID registrationId) {
        var registration = getRegistration(registrationId);
        Account account = accountRepository.findByIdWithPessimisticLock(accountId).get();
        var session = registration.getSession();
        checksIfAccountIsAssociateToRegistration(accountId, registration);

        //verificar se o usuário ja aceitou ou se o sistema negou (data resposta)
        checksIfEmailWasAnswered(registration);

        if(registration.getTimeEmailWasSent() != null &&
            registration.getTimeEmailWasSent().plusHours(Long.parseLong(emailConfirmationTime)).isBefore(LocalDateTime.now())
        ) {
            throw new BusinessRuleException(BusinessRuleType.REGISTRATION_ACCEPT_WITH_EXPIRED_HOURS);
        }

        cancellAllRegistrationsInWaitListWithConflict(account, session);

        registration.setRegistrationStatus(RegistrationStatus.CONFIRMED);
        registration.setEmailReplyDate(LocalDateTime.now());

        return registrationRepository.save(registration);
    }

    @Transactional
    public Registration denySessionSeat(UUID accountId, UUID registrationId) {
        var registration = getRegistration(registrationId);
        Session session = sessionRepository.findByIdWithPessimisticLock(registration.getSession().getId()).get();
        checksIfAccountIsAssociateToRegistration(accountId, registration);
        // Verificar o primeiro horário da sessão ja iniciou caso sim, não enviar e-mail
        //verificar se o usuário ja negou ou sistema negou
        checksIfEmailWasAnswered(registration);

        if(registration.getTimeEmailWasSent().plusHours(Long.parseLong(emailConfirmationTime)).isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException(BusinessRuleType.REGISTRATION_DENY_WITH_EXPIRED_HOURS);
        }

        if(isSessionStarted(session)) {
            session.decrementNumberOfConfirmedSeats();
            sessionRepository.save(session);
        }

        else if(checkIfExistAnyRegistrationInWaitListBySessionId(session.getId())) {
            var firstRegistrationInWaitList = registrationRepository.getFirstBySessionIdAndRegistrationStatusOrderByDate(session.getId(), RegistrationStatus.WAITING_LIST).get();
            firstRegistrationInWaitList.setRegistrationStatus(RegistrationStatus.WAITING_CONFIRMATION);
            firstRegistrationInWaitList.setTimeEmailWasSent(LocalDateTime.now());
            registrationRepository.save(firstRegistrationInWaitList);
            sendEmailToConfirmRegistration(firstRegistrationInWaitList.getAccount(), firstRegistrationInWaitList);
        }

        else {
            session.decrementNumberOfConfirmedSeats();
            sessionRepository.save(session);
        }

        registration.setRegistrationStatus(RegistrationStatus.CANCELED_BY_USER);
        registration.setEmailReplyDate(LocalDateTime.now());
        log.info("Registration cancelled: date={}, status={}", LocalDateTime.now(), registration.getRegistrationStatus());

        return registrationRepository.save(registration);
    }

    @Transactional
    public void cancelAllRegistrationInWaitConfirmation() {
        List<Registration> registrations = registrationRepository.findAllByRegistrationStatus(LocalDateTime.now(), RegistrationStatus.WAITING_CONFIRMATION);

        registrations.stream().filter(registration -> registration.getTimeEmailWasSent().plusHours(Long.parseLong(emailConfirmationTime)).isBefore(LocalDateTime.now()))
            .forEach(registration -> {
                registration.setRegistrationStatus(RegistrationStatus.CANCELED_BY_SYSTEM);
                log.info("Registration cancelled: date={}, status={}", LocalDateTime.now(), registration.getRegistrationStatus());

                Session sessionLock = sessionRepository.findByIdWithPessimisticLock(registration.getSession().getId()).get();

                if(isSessionStarted(sessionLock)) {
                    sessionLock.decrementNumberOfConfirmedSeats();
                    sessionRepository.save(sessionLock);
                }

                else if(checkIfExistAnyRegistrationInWaitListBySessionId(registration.getSession().getId())) {
                    var firstRegistrationInWaitList = registrationRepository.getFirstBySessionIdAndRegistrationStatusOrderByDate(registration.getSession().getId(), RegistrationStatus.WAITING_LIST).get();
                    firstRegistrationInWaitList.setRegistrationStatus(RegistrationStatus.WAITING_CONFIRMATION);
                    firstRegistrationInWaitList.setTimeEmailWasSent(LocalDateTime.now());
                    registrationRepository.save(firstRegistrationInWaitList);
                    sendEmailToConfirmRegistration(firstRegistrationInWaitList.getAccount(), firstRegistrationInWaitList);
                }

                else {
                    sessionLock.decrementNumberOfConfirmedSeats();
                    sessionRepository.save(sessionLock);
                }

                registrationRepository.save(registration);
            });
    }

    public List<AccountEventQueryDto> findAllEventsByAccount(UUID accountId) {
        return registrationRepository.findEventsByAccount(accountId);
    }

    private void checksIfEmailWasAnswered(Registration registration) {
        if(registration.getEmailReplyDate() != null) {
            throw new BusinessRuleException(BusinessRuleType.REGISTRATION_ALREADY_WAS_ANSWERED);
        }
    }

    private boolean isSessionStarted(Session session) {
        SessionSchedule firstSessionSchedule = session.getSessionSchedules().stream()
            .min(Comparator.comparing(SessionSchedule::getExecutionStart))
            .get();

        if(firstSessionSchedule.getExecutionStart().isBefore(LocalDateTime.now()) ||
            firstSessionSchedule.getExecutionStart().isBefore(LocalDateTime.now())
        ) {
            return true;
        }

        return false;
    }

    private void checkIfSessionStarted(Session session) {
        SessionSchedule firstSessionSchedule = session.getSessionSchedules().stream()
            .min(Comparator.comparing(SessionSchedule::getExecutionStart))
            .get();

        if(firstSessionSchedule.getExecutionStart().isBefore(LocalDateTime.now()) ||
                firstSessionSchedule.getExecutionStart().isBefore(LocalDateTime.now())
        ) {
            throw new BusinessRuleException(BusinessRuleType.REGISTRATION_CREATE_WITH_SESSION_STARTED);
        }
    }

    private void checksIfAccountHasARegistrationInActivity(UUID accountId, UUID activityId) {
        if(registrationRepository.existsByAccountIdAndActivityId(accountId, activityId)) {
            throw new BusinessRuleException(BusinessRuleType.REGISTRATION_CREATE_ACCOUNT_ALREADY_HAS_REGISTRATION_IN_ACTIVITY);
        }
    }

    private void sendEmailToConfirmRegistration(Account account, Registration registration) {
        if(account.getAllowEmail()) {
            try {
                emailService.sendEmailToConfirmRegistration(account, registration);
                log.info("To Confirm Registration e-mail was sent to {}", account.getEmail());
            } catch (MessagingException ex) {
                log.error("Error when trying to confirm registration e-mail to {}",account.getEmail(), ex);
            }
        } else {
            log.info("To Confirm Registration e-mail not send to {} - account e-mail not allow {}", account.getEmail());
        }
    }

    private void checkIfExistsAnyRegistrationWithConflict(Account accountLock, Session session) {
        //TODO: pegar data de hj e verificar os registros de hj e do futuro
        var registrations = registrationRepository.findAllByAccountIdAndRegistrationStatusInAndDate(
            accountLock.getId(),
            LocalDateTime.now(),
            List.of(RegistrationStatus.CONFIRMED, RegistrationStatus.WAITING_CONFIRMATION)
        );

        registrations.stream()
            .flatMap(registration -> registration.getSession().getSessionSchedules().stream())
            .forEach(schedule -> {
                if(session.getSessionSchedules().stream().anyMatch(s -> s.hasIntersection(schedule))) {
                    throw new BusinessRuleException(BusinessRuleType.REGISTRATION_CREATE_HAS_SCHEDULE_CONFLICT);
                }
            });
    }

    private void cancellAllRegistrationsInWaitListWithConflict(Account accountLock, Session session) {
        var registrationsInWaitList = registrationRepository.findAllByAccountIdAndRegistrationStatusInAndDate(
            accountLock.getId(),
            LocalDateTime.now(),
            List.of(RegistrationStatus.WAITING_LIST)
        );

        List<UUID> sessionsId = new ArrayList<>();

        registrationsInWaitList.stream()
            .flatMap(registrationInWaitList -> registrationInWaitList.getSession().getSessionSchedules().stream())
            .forEach(schedule -> {
                if(session.getSessionSchedules().stream().anyMatch(s -> s.hasIntersection(schedule))) {
                    sessionsId.add(schedule.getSession().getId());
                }
            });

        List<Registration> registrationsToBeCancel = registrationRepository.findAllByAccountIdAndSessionIdIn(accountLock.getId(), sessionsId);
        registrationsToBeCancel.forEach(s -> s.setRegistrationStatus(RegistrationStatus.CANCELED_BY_SYSTEM));
        registrationRepository.saveAll(registrationsToBeCancel);
    }

    private boolean checkIfExistAnyRegistrationInWaitListBySessionId(UUID sessionId) {
        return registrationRepository.existsBySessionIdAndRegistrationStatus(sessionId, RegistrationStatus.WAITING_LIST);
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

    private void checksIfItIsOutOfRegistrationPeriod(Event event) {
        if(event.getRegistrationPeriod().todayIsOutOfPeriod()) {
            throw new BusinessRuleException(BusinessRuleType.REGISTRATION_CREATE_WITH_EVENT_OUT_OF_REGISTRATION_PERIOD);
        }
    }

    private void checkIfActivityIsCanceled(Activity activity) {
        if(activity.getStatus() == EventStatus.CANCELED) {
            throw new BusinessRuleException(BusinessRuleType.REGISTRATION_CREATE_WITH_ACTIVITY_CANCELED);
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
                List.of(RegistrationStatus.CONFIRMED, RegistrationStatus.WAITING_CONFIRMATION, RegistrationStatus.WAITING_LIST)
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
        if (!registration.getAccount().getId().equals(accountId)) {
            throw new BusinessRuleException(BusinessRuleType.REGISTRATION_IS_NOT_ASSOCIATED_TO_ACCOUNT);
        }
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

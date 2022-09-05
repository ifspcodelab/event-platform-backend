package br.edu.ifsp.spo.eventos.eventplatformbackend.registration;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.signup.EmailService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.Activity;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.*;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.Session;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.SessionRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.SessionSchedule;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.Subevent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Transactional
    public Registration create(RegistrationCreateDto registrationCreateDto, UUID eventId, UUID activityId, UUID sessionId) {
        var account = getAccount(registrationCreateDto.getAccountId());
        var session = getSession(sessionId);
        var activity = session.getActivity();
        checksIfEventIsAssociateToActivity(eventId, activity);
        checksIfActivityIsAssociateToSession(activityId, session);
        checkIfSessionIsCancelled(session);

        //Verificar se já tem uma inscrição nessa atividade
        //flag se requer inscrição ou não

        checksIfAccountHasARegistrationInActivity(account.getId(), activityId);

        Account accountLock = accountRepository.findByIdWithPessimisticLock(account.getId()).get();

        checkIfAccountHasARegistrationInSession(accountLock.getId(), sessionId);

        checkIfExistsAnyRegistrationWithConflict(accountLock, session);

        Session sessionLock = sessionRepository.findByIdWithPessimisticLock(sessionId).get();

        checkIfSessionLockIsFull(sessionLock);

        Registration registration = registrationRepository.save(Registration.createWithConfirmedStatus(accountLock, sessionLock));

        sessionLock.incrementNumberOfConfirmedSeats();
        sessionRepository.save(sessionLock);

        cancellAllRegistrationsInWaitListWithConflict(accountLock, sessionLock);

        return registration;
    }

    @Transactional
    public Registration create(RegistrationCreateDto registrationCreateDto, UUID eventId, UUID subeventId, UUID activityId, UUID sessionId) {
        var account = getAccount(registrationCreateDto.getAccountId());
        var session = getSession(sessionId);
        var activity = session.getActivity();
        var subevent = activity.getSubevent();
        checkIfEventIsAssociateToSubevent(eventId, subevent);
        checksIfSubeventIsAssociateToActivity(subeventId, activity);
        checksIfEventIsAssociateToActivity(eventId, activity);
        checksIfActivityIsAssociateToSession(activityId, session);
        checkIfSessionIsCancelled(session);
        checksIfAccountHasARegistrationInActivity(account.getId(), activityId);
        //Verificar se já tem uma inscrição nessa atividade
        // flag se requer inscrição ou não

        Account accountLock = accountRepository.findByIdWithPessimisticLock(account.getId()).get();

        checkIfAccountHasARegistrationInSession(accountLock.getId(), sessionId);

        checkIfExistsAnyRegistrationWithConflict(accountLock, session);

        Session sessionLock = sessionRepository.findByIdWithPessimisticLock(sessionId).get();

        checkIfSessionLockIsFull(sessionLock);

        Registration registration = registrationRepository.save(Registration.createWithConfirmedStatus(accountLock, sessionLock));

        sessionLock.incrementNumberOfConfirmedSeats();
        sessionRepository.save(sessionLock);

        cancellAllRegistrationsInWaitListWithConflict(accountLock, sessionLock);

        return registration;
    }

    @Transactional
    public Registration create(RegistrationCreateDto registrationCreateDto, UUID sessionId) {
        var account = getAccount(registrationCreateDto.getAccountId());
        var session = getSession(sessionId);
        var activity = session.getActivity();
        var event = activity.getEvent();
        checkIfSessionIsCancelled(session);
        checkIfActivityIsCanceled(activity);
        checksIfItIsOutOfRegistrationPeriod(event);
        checksIfAccountHasARegistrationInActivity(account.getId(), activity.getId());
        checkIfSessionStarted(session);
        //Verificar se já tem uma inscrição nessa atividade
        // flag se requer inscrição ou não

        Account accountLock = accountRepository.findByIdWithPessimisticLock(account.getId()).get();

        checkIfAccountHasARegistrationInSession(accountLock.getId(), sessionId);

        checkIfExistsAnyRegistrationWithConflict(accountLock, session);

        // Verificar se a sesssao ja iniciou (so para participantes, admin pode)

        Session sessionLock = sessionRepository.findByIdWithPessimisticLock(sessionId).get();

        checkIfSessionLockIsFull(sessionLock);

        sessionLock.incrementNumberOfConfirmedSeats();
        sessionRepository.save(sessionLock);

        cancellAllRegistrationsInWaitListWithConflict(accountLock, sessionLock);

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
        checkIfActivityIsCanceled(activity);
        checksIfItIsOutOfRegistrationPeriod(event);
        checksIfAccountHasARegistrationInActivity(account.getId(), activityId);
        checkIfSessionStarted(session);
        // Verificar se a sesssao ja iniciou


        Account accountLock = accountRepository.findByIdWithPessimisticLock(account.getId()).get();

        checkIfAccountHasARegistrationInSession(accountLock.getId(), sessionId);

        checkIfExistsAnyRegistrationWithConflict(accountLock, session);

        Session sessionLock = sessionRepository.findByIdWithPessimisticLock(sessionId).get();

        checkIfSessionIsNotFull(sessionLock);

        //metodo
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
        checkIfActivityIsCanceled(activity);
        checksIfItIsOutOfRegistrationPeriod(event);
        checksIfAccountHasARegistrationInActivity(account.getId(), activityId);
        checkIfSessionStarted(session);
        // Verificar se a sesssao ja iniciou

        Account accountLock = accountRepository.findByIdWithPessimisticLock(account.getId()).get();

        checkIfAccountHasARegistrationInSession(accountLock.getId(), sessionId);

        checkIfExistsAnyRegistrationWithConflict(accountLock, session);

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

        // Verificar se a sesssao ja iniciou

        checkIfSessionIsCancelled(session);
        checkIfActivityIsCanceled(activity);
        checksIfItIsOutOfRegistrationPeriod(event);
        checkIfAccountHasARegistrationInSession(accountLock.getId(), sessionId);
        checksIfAccountHasARegistrationInActivity(account.getId(), activity.getId());
        checkIfSessionStarted(session);
        checkIfExistsAnyRegistrationWithConflict(accountLock, session);

        Session sessionLock = sessionRepository.findByIdWithPessimisticLock(sessionId).get();

        checkIfSessionIsNotFull(sessionLock);

        return registrationRepository.save(Registration.createWithWaitingListdStatus(accountLock, sessionLock));
    }

    @Transactional
    public Registration cancel(UUID eventId, UUID activityId, UUID sessionId, UUID registrationId) {
        var registration = getRegistration(registrationId);
        var session = registration.getSession();
        var activity = session.getActivity();
        checksIfEventIsAssociateToActivity(eventId, activity);
        checksIfActivityIsAssociateToSession(activityId, session);
        checksIfSessionIsAssociateToRegistration(sessionId, registration);

        Session sessionLock = sessionRepository.findByIdWithPessimisticLock(sessionId).get();

        // Verificar o primeiro horário da sessão ja iniciou caso sim, não enviar e-mail
        if(isSessionStarted(sessionLock)) {
            sessionLock.decrementNumberOfConfirmedSeats();
            sessionRepository.save(sessionLock);
        }
        else if(checkIfExistAnyRegistrationInWaitListBySessionId(session.getId())) {
            //TODO: garantir que está vindo por data, orderByDate ou ascendente no final
            var firstRegistrationInWaitList = registrationRepository.getFirstBySessionIdAndRegistrationStatusOrderByDate(sessionId, RegistrationStatus.WAITING_LIST).get();
            firstRegistrationInWaitList.setRegistrationStatus(RegistrationStatus.WAITING_CONFIRMATION);
            firstRegistrationInWaitList.setTimeEmailWasSent(LocalDateTime.now());
            registrationRepository.save(firstRegistrationInWaitList);
            sendEmailToConfirmRegistration(firstRegistrationInWaitList.getAccount(), registration);
        }
        else {
            sessionLock.decrementNumberOfConfirmedSeats();
            sessionRepository.save(sessionLock);
        }

        registration.setRegistrationStatus(RegistrationStatus.CANCELED_BY_ADMIN);
        log.info("Registration cancelled: date={}, status={}", LocalDateTime.now(), registration.getRegistrationStatus());

        return registrationRepository.save(registration);
    }

    @Transactional
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

        if(isSessionStarted(sessionLock)) {
            sessionLock.decrementNumberOfConfirmedSeats();
            sessionRepository.save(sessionLock);
        }

        else if(checkIfExistAnyRegistrationInWaitListBySessionId(session.getId())) {
            var firstRegistrationInWaitList = registrationRepository.getFirstBySessionIdAndRegistrationStatusOrderByDate(sessionId, RegistrationStatus.WAITING_LIST).get();
            firstRegistrationInWaitList.setRegistrationStatus(RegistrationStatus.WAITING_CONFIRMATION);
            firstRegistrationInWaitList.setTimeEmailWasSent(LocalDateTime.now());
            registrationRepository.save(firstRegistrationInWaitList);

            // Verificar o horário da sessão antes de enviar o e-mail
            sendEmailToConfirmRegistration(firstRegistrationInWaitList.getAccount(), registration);
        }

        else {
            sessionLock.decrementNumberOfConfirmedSeats();
            sessionRepository.save(sessionLock);
        }

        registration.setRegistrationStatus(RegistrationStatus.CANCELED_BY_ADMIN);
        log.info("Registration cancelled: date={}, status={}", LocalDateTime.now(), registration.getRegistrationStatus());

        return registrationRepository.save(registration);
    }

    @Transactional
    public Registration cancel(UUID accountId, UUID registrationId) {
        var registration = getRegistration(registrationId);
        var session = registration.getSession();
        checksIfAccountIsAssociateToRegistration(accountId, registration);

        Session sessionLock = sessionRepository.findByIdWithPessimisticLock(session.getId()).get();
        if(isSessionStarted(sessionLock)) {
            sessionLock.decrementNumberOfConfirmedSeats();
            sessionRepository.save(sessionLock);
        }

        else if(checkIfExistAnyRegistrationInWaitListBySessionId(session.getId())) {
            var firstRegistrationInWaitList = registrationRepository.getFirstBySessionIdAndRegistrationStatusOrderByDate(session.getId(), RegistrationStatus.WAITING_LIST).get();
            firstRegistrationInWaitList.setRegistrationStatus(RegistrationStatus.WAITING_CONFIRMATION);
            firstRegistrationInWaitList.setTimeEmailWasSent(LocalDateTime.now());
            registrationRepository.save(firstRegistrationInWaitList);
            sendEmailToConfirmRegistration(firstRegistrationInWaitList.getAccount(), registration);
        }

        else {
            sessionLock.decrementNumberOfConfirmedSeats();
            sessionRepository.save(sessionLock);
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
        return registrationRepository.findAllByAccountIdAndRegistrationStatusIn(
            accountId,
            List.of(
                RegistrationStatus.CONFIRMED,
                RegistrationStatus.WAITING_LIST,
                RegistrationStatus.WAITING_CONFIRMATION
            )
        );
    }

    @Transactional
    public Registration acceptSessionSeat(UUID accountId, UUID registrationId) {
        log.info(emailConfirmationTime);
        var registration = getRegistration(registrationId);
        var account = registration.getAccount();
        var session = registration.getSession();
        checksIfAccountIsAssociateToRegistration(accountId, registration);

        //verificar se o usuário ja aceitou ou se o sistema negou (data resposta)
        checksIfEmailWasAnswered(registration);

        if(registration.getTimeEmailWasSent() != null &&
            registration.getTimeEmailWasSent().plusHours(Long.parseLong(emailConfirmationTime)).isBefore(LocalDateTime.now())
        ) {
            throw new BusinessRuleException(BusinessRuleType.REGISTRATION_ACCEPT_WITH_EXPIRED_HOURS);
        }

        Account accountLock = accountRepository.findByIdWithPessimisticLock(account.getId()).get();

        cancellAllRegistrationsInWaitListWithConflict(accountLock, session);

        registration.setRegistrationStatus(RegistrationStatus.CONFIRMED);
        registration.setEmailReplyDate(LocalDateTime.now());

        return registrationRepository.save(registration);
    }

    @Transactional
    public Registration denySessionSeat(UUID accountId, UUID registrationId) {
        var registration = getRegistration(registrationId);
        var session = registration.getSession();
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
            sendEmailToConfirmRegistration(firstRegistrationInWaitList.getAccount(), registration);
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
                    sendEmailToConfirmRegistration(firstRegistrationInWaitList.getAccount(), registration);
                }

                else {
                    sessionLock.decrementNumberOfConfirmedSeats();
                    sessionRepository.save(sessionLock);
                }

                registrationRepository.save(registration);
            });
    }

//    private void checksIfActivityDoesNotNeedRegistration(Activity activity) {
//        if(activity.needRegistration) {
//            throw new BusinessRuleException(BusinessRuleType.REGISTRATION_CREATE_WITH_ACTIVITY_DOES_NOT_NEED_REGISTRATION);
//        }
//    }

    private void checksIfEmailWasAnswered(Registration registration) {
        if(registration.getEmailReplyDate() != null) {
            throw new BusinessRuleException(BusinessRuleType.REGISTRATION_ALREADY_WAS_ANSWERED);
        }
    }

    private boolean isSessionStarted(Session session) {
        SessionSchedule firstSessionSchedule = session.getSessionsSchedules().stream()
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
        SessionSchedule firstSessionSchedule = session.getSessionsSchedules().stream()
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
        try {
            emailService.sendEmailToConfirmRegistration(account, registration);
            log.info("To Confirm Registration e-mail was sent to {}", account.getEmail());
        } catch (MessagingException ex) {
            log.error("Error when trying to confirm registration e-mail to {}",account.getEmail(), ex);
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
            .flatMap(registration -> registration.getSession().getSessionsSchedules().stream())
            .forEach(schedule -> {
                if(session.getSessionsSchedules().stream().anyMatch(s -> s.hasIntersection(schedule))) {
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
            .flatMap(registrationInWaitList -> registrationInWaitList.getSession().getSessionsSchedules().stream())
            .forEach(schedule -> {
                if(session.getSessionsSchedules().stream().anyMatch(s -> s.hasIntersection(schedule))) {
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

    private Account getAccount(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.ACCOUNT, accountId));
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

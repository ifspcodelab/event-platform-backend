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
import java.time.LocalDate;
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
    private final AccountRepository accountRepository;
    private final RegistrationRepository registrationRepository;

    public Registration create(UUID accountId, UUID eventId, UUID activityId, UUID sessionId) {
        var account = getAccount(accountId);
        var event = getEvent(eventId);
        var activity = getActivity(activityId);
        var session = getSession(sessionId);
        checksIfEventIsAssociateToActivity(eventId, activity);
        checksIfActivityIsAssociateToSession(activityId, session);

        if(registrationRepository.existsBySessionIdAndAccountIdAndRegistrationStatus(sessionId, accountId, RegistrationStatus.CONFIRMED)
            || registrationRepository.existsBySessionIdAndAccountIdAndRegistrationStatus(sessionId, accountId, RegistrationStatus.WAITING_LIST)
        ) {
            throw new BusinessRuleException(BusinessRuleType.REGISTRATION_CREATE_ALREADY_EXISTS);
        }

        if(session.isCanceled()) {
            throw new BusinessRuleException(BusinessRuleType.REGISTRATION_CREATE_WITH_CANCELED_SESSION);
        }

        if(activity.getStatus() != EventStatus.PUBLISHED) {
            throw new BusinessRuleException(BusinessRuleType.REGISTRATION_CREATE_WITH_ACTIVITY_NOT_PUBLISHED);
        }

        if(event.getRegistrationPeriod().getStartDate().isAfter(LocalDate.now()) ||
            event.getRegistrationPeriod().getEndDate().isBefore(LocalDate.now())
        ) {
            throw new BusinessRuleException(BusinessRuleType.REGISTRATION_CREATE_WITH_EVENT_OUT_OF_REGISTRATION_PERIOD);
        }

        if(session.getSeats().equals(registrationRepository.countRegistrationsBySessionIdAndRegistrationStatus(
            sessionId,
            RegistrationStatus.CONFIRMED))) {
            return registrationRepository.save(Registration.createWithWaitingListdStatus(account,session));
        }

        return registrationRepository.save(Registration.createWithConfirmedStatus(account,session));
    }

    public Registration create(UUID accountId, UUID eventId, UUID subeventId, UUID activityId, UUID sessionId) {
        var account = getAccount(accountId);
        var event = getEvent(eventId);
        var subevent = getSubevent(subeventId);
        var activity = getActivity(activityId);
        var session = getSession(sessionId);
        checkIfEventIsAssociateToSubevent(eventId, subevent);
        checksIfSubeventIsAssociateToActivity(subeventId, activity);
        checksIfEventIsAssociateToActivity(eventId, activity);
        checksIfActivityIsAssociateToSession(activityId, session);

        if(registrationRepository.existsBySessionIdAndAccountIdAndRegistrationStatus(sessionId, accountId, RegistrationStatus.CONFIRMED)
            || registrationRepository.existsBySessionIdAndAccountIdAndRegistrationStatus(sessionId, accountId, RegistrationStatus.WAITING_LIST)
        ) {
            throw new BusinessRuleException(BusinessRuleType.REGISTRATION_CREATE_ALREADY_EXISTS);
        }

        if(session.isCanceled()) {
            throw new BusinessRuleException(BusinessRuleType.REGISTRATION_CREATE_WITH_CANCELED_SESSION);
        }

        if(activity.getStatus() != EventStatus.PUBLISHED) {
            throw new BusinessRuleException(BusinessRuleType.REGISTRATION_CREATE_WITH_ACTIVITY_NOT_PUBLISHED);
        }

        if(event.getRegistrationPeriod().getStartDate().isAfter(LocalDate.now()) ||
            event.getRegistrationPeriod().getEndDate().isBefore(LocalDate.now())
        ) {
            throw new BusinessRuleException(BusinessRuleType.REGISTRATION_CREATE_WITH_EVENT_OUT_OF_REGISTRATION_PERIOD);
        }

        if(session.getSeats().equals(registrationRepository.countRegistrationsBySessionIdAndRegistrationStatus(
            sessionId,
            RegistrationStatus.CONFIRMED))) {
            return registrationRepository.save(Registration.createWithWaitingListdStatus(account,session));
        }

        return registrationRepository.save(Registration.createWithConfirmedStatus(account,session));
    }

    public Registration cancel(UUID accountId, UUID eventId, UUID activityId, UUID sessionId, UUID registrationId) {
        return null;
    }

    public Registration cancel(UUID accountId, UUID eventId, UUID subeventId, UUID activityId, UUID sessionId, UUID registrationId) {
        return null;
    }

    public List<Registration> findAll(UUID eventId, UUID activityId, UUID sessionId) {
        var activity = getActivity(activityId);
        var session = getSession(sessionId);
        checksIfEventIsAssociateToActivity(eventId, activity);
        checksIfActivityIsAssociateToSession(activityId, session);

        return registrationRepository.findAllBySessionId(sessionId);
    }

    public List<Registration> findAll(UUID eventId, UUID subeventId, UUID activityId, UUID sessionId) {
        var subevent = getSubevent(subeventId);
        var activity = getActivity(activityId);
        var session = getSession(sessionId);
        checkIfEventIsAssociateToSubevent(eventId, subevent);
        checksIfSubeventIsAssociateToActivity(subeventId, activity);
        checksIfEventIsAssociateToActivity(eventId, activity);
        checksIfActivityIsAssociateToSession(activityId, session);

        return registrationRepository.findAllBySessionId(sessionId);
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
}

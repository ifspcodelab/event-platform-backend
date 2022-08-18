package br.edu.ifsp.spo.eventos.eventplatformbackend.registration;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.AccountRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.Activity;
import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.ActivityRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.BusinessRuleException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.BusinessRuleType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceName;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceNotFoundException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.Session;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.SessionRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.SubeventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
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

    public Registration create(UUID accountId, UUID eventId, UUID subeventId, UUID activityId, UUID sessionId) {
        var account = getAccount(accountId);
        var event = getEvent(eventId);
        var activity = getActivity(activityId);
        var session = getSession(sessionId);

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

    public Registration cancel(UUID accountId, UUID eventId, UUID subeventId, UUID activityId, UUID sessionId, UUID registrationId) {
        return null;
    }

    private Account getAccount(UUID accountId) {
        return accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException(ResourceName.ACCOUNT, accountId));
    }

    private Event getEvent(UUID eventId) {
        return eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException(ResourceName.EVENT, eventId));
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

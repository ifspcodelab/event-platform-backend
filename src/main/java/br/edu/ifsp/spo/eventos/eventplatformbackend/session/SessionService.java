package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.Activity;
import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.ActivityRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.Area;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.AreaRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.dto.CancellationMessageCreateDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.*;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.Location;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.LocationRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.space.Space;
import br.edu.ifsp.spo.eventos.eventplatformbackend.space.SpaceRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.Subevent;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.SubeventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final ActivityRepository activityRepository;
    private final EventRepository eventRepository;
    private final SubeventRepository subeventRepository;
    private final LocationRepository locationRepository;
    private final AreaRepository areaRepository;
    private final SpaceRepository spaceRepository;

    public Session create(UUID eventId, UUID activityId, SessionCreateDto dto) {
        Activity activity = getActivity(activityId);

        Session session = new Session(
                dto.getTitle(),
                dto.getSeats(),
                activity,
                getSessionSchedules(dto)
        );

        return sessionRepository.save(session);
    }

    public Session create(UUID eventId, UUID subeventId, UUID activityId, SessionCreateDto dto) {
        Activity activity = getActivity(activityId);

        Session session = new Session(
                dto.getTitle(),
                dto.getSeats(),
                activity,
                getSessionSchedules(dto)
        );

        return sessionRepository.save(session);
    }

    public Session update(UUID eventId, UUID activityId, UUID sessionId, SessionCreateDto dto) {
        Session session = getSession(sessionId);
        List<SessionSchedule> sessionSchedule = getSessionSchedules(dto); // do DTO

        session.setTitle(dto.getTitle());
        session.setSeats(dto.getSeats());

        session.setSessionsSchedules(sessionSchedule);

        return sessionRepository.save(session);
    }

    public Session update(UUID eventId, UUID subeventId, UUID activityId, UUID sessionId, SessionCreateDto dto) {
        Session session = getSession(sessionId);
        List<SessionSchedule> sessionSchedule = getSessionSchedules(dto); // do DTO

        session.setTitle(dto.getTitle());
        session.setSeats(dto.getSeats());

        session.setSessionsSchedules(sessionSchedule);

        return sessionRepository.save(session);
    }

    // com sessions schedules
    public List<Session> findAll(UUID eventId, UUID activityId) {
        return sessionRepository.findAllByActivityId(activityId);
    }

    // com sessions schedules
    public List<Session> findAll(UUID eventId, UUID subeventId, UUID activityId) {
        return sessionRepository.findAllByActivityId(activityId);
    }

    public Session cancel(UUID eventId, UUID activityId, UUID sessionId, CancellationMessageCreateDto cancellationMessageCreateDto) {
        Event event = getEvent(eventId);
        Activity activity = getActivity(activityId);
        Session session = getSession(sessionId);
        checksIfEventIsAssociateToActivity(eventId, activity);
        checksIfActivityIsAssociateToSession(activityId, session);

        if(session.isCanceled()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_WITH_CANCELED_STATUS);
        }

        if(event.getStatus().equals(EventStatus.DRAFT)) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_WITH_AN_EVENT_WITH_DRAFT_STATUS);
        }

        if(activity.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_WITH_AN_ACTIVITY_WITH_CANCELED_STATUS);
        }

        if(activity.getStatus().equals(EventStatus.DRAFT)) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_WITH_AN_ACTIVITY_WITH_DRAFT_STATUS);
        }

        if(event.getExecutionPeriod().getEndDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_AFTER_EVENT_EXECUTION_PERIOD);
        }

        session.setCanceled(true);
        session.setCancellationMessage(cancellationMessageCreateDto.getReason());
        return sessionRepository.save(session);
    }

    public Session cancel(UUID eventId, UUID subeventId, UUID activityId, UUID sessionId, CancellationMessageCreateDto cancellationMessageCreateDto) {
        Event event = getEvent(eventId);
        Subevent subevent = getSubEvent(subeventId);
        Activity activity = getActivity(activityId);
        Session session = getSession(sessionId);
        checkIfEventIsAssociateToSubevent(eventId, subevent);
        checksIfSubeventIsAssociateToActivity(subeventId, activity);
        checksIfActivityIsAssociateToSession(activityId, session);

        if(session.isCanceled()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_WITH_CANCELED_STATUS);
        }

        // se ao cancelar um evento e os subeventos sao cancelados também, entao não precisa verificar se o evento está cncelado...
        if(subevent.getStatus().equals(EventStatus.DRAFT)) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_WITH_A_SUBEVENT_WITH_DRAFT_STATUS);
        }

        if(subevent.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_WITH_A_SUBEVENT_WITH_CANCELED_STATUS);
        }

        if(activity.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_WITH_AN_ACTIVITY_WITH_CANCELED_STATUS);
        }

        if(activity.getStatus().equals(EventStatus.DRAFT)) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_WITH_AN_ACTIVITY_WITH_DRAFT_STATUS);
        }

        if(event.getExecutionPeriod().getEndDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_AFTER_EVENT_EXECUTION_PERIOD);
        }

        if(activity.getStatus().equals(EventStatus.PUBLISHED)) {
            if (subevent.getExecutionPeriod().getEndDate().isBefore(LocalDate.now())) {
                throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_WITH_ACTIVITY_PUBLISHED_STATUS_AFTER_SUBEVENT_EXECUTION_PERIOD);
            }
        }

        session.setCanceled(true);
        session.setCancellationMessage(cancellationMessageCreateDto.getReason());
        return sessionRepository.save(session);
    }

    public void delete(UUID eventId, UUID activityId, UUID sessionId) {
        Event event = getEvent(eventId);
        Activity activity = getActivity(activityId);
        Session session = getSession(sessionId);
        checksIfEventIsAssociateToActivity(eventId, activity);
        checksIfActivityIsAssociateToSession(activityId, session);

        if(session.isCanceled()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_DELETE_WITH_STATUS_CANCELED);
        }

        if(activity.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_DELETE_WITH_ACTIVITY_STATUS_CANCELED);
        }

        if(activity.getStatus().equals(EventStatus.PUBLISHED)) {
            if(event.getRegistrationPeriod().getStartDate().isBefore(LocalDate.now())) {
                throw new BusinessRuleException(BusinessRuleType.SESSION_DELETE_WITH_ACTIVITY_PUBLISHED_STATUS_AND_AFTER_REGISTRATION_PERIOD_START);
            }
        }

        // TODO PRECISA DOS METODOS QUE CANCELA AS ENTIDADES FILHAS

        sessionRepository.delete(session);
    }

    public void delete(UUID eventId, UUID subeventId, UUID activityId, UUID sessionId) {
        Event event = getEvent(eventId);
        Subevent subevent = getSubEvent(subeventId);
        Activity activity = getActivity(activityId);
        Session session = getSession(sessionId);
        checkIfEventIsAssociateToSubevent(eventId, subevent);
        checksIfSubeventIsAssociateToActivity(subeventId, activity);
        checksIfActivityIsAssociateToSession(activityId, session);

        if(session.isCanceled()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_DELETE_WITH_STATUS_CANCELED);
        }

        if(activity.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_DELETE_WITH_ACTIVITY_STATUS_CANCELED);
        }

        if(activity.getStatus().equals(EventStatus.PUBLISHED)) {
            if(event.getRegistrationPeriod().getStartDate().isBefore(LocalDate.now())) {
                throw new BusinessRuleException(BusinessRuleType.SESSION_DELETE_WITH_ACTIVITY_PUBLISHED_STATUS_AND_AFTER_REGISTRATION_PERIOD_START);
            }
        }

        if(activity.getStatus().equals(EventStatus.PUBLISHED)) {
            if (subevent.getExecutionPeriod().getEndDate().isBefore(LocalDate.now())) {
                throw new BusinessRuleException(BusinessRuleType.SESSION_DELETE_WITH_ACTIVITY_PUBLISHED_STATUS_AFTER_SUBEVENT_EXECUTION_PERIOD);
            }
        }

        // TODO PRECISA DOS METODOS QUE CANCELA AS ENTIDADES FILHAS, ISSO EXISTE??
        sessionRepository.delete(session);
    }

    private Event getEvent(UUID eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.EVENT, eventId));
    }

    private void checksIfSubeventIsAssociateToActivity(UUID subeventId, Activity activity) {
        if (!activity.getSubevent().getId().equals(subeventId)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_IS_NOT_ASSOCIATED_TO_SUBEVENT);
        }
    }

    private void checkIfEventIsAssociateToSubevent(UUID eventId, Subevent subevent) {
        if (!subevent.getEvent().getId().equals(eventId)) {
            throw new ResourceReferentialIntegrityException(ResourceName.SUBEVENT, ResourceName.EVENT);
        }
    }
    private Subevent getSubEvent(UUID subeventId) {
        return subeventRepository.findById(subeventId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.SUBEVENT, subeventId));
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

    private Location getLocation(UUID locationId) {
        return locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.LOCATION, locationId));
    }

    private Area getArea(UUID areaId) {
        return areaRepository.findById(areaId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.AREA, areaId));
    }

    private Space getSpace(UUID spaceId) {
        return spaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.SPACE, spaceId));
    }

    private Activity getActivity(UUID activityId) {
        return activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.ACTIVITY, activityId));
    }

    private Session getSession(UUID sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.SESSION, sessionId));
    }

    private List<SessionSchedule> getSessionSchedules(SessionCreateDto dto) {
        return dto.getSessionsSchedules().stream()
                .map(s -> {
                    Location location = s.getLocationId() != null ? getLocation(s.getLocationId()) : null;
                    Area area = s.getAreaId() != null ? getArea(s.getAreaId()) : null;
                    Space space = s.getSpaceId() != null ? getSpace(s.getSpaceId()) : null;

                    return new SessionSchedule(
                            s.getExecution_start(),
                            s.getExecution_end(),
                            s.getUrl(),
                            location,
                            area,
                            space
                    );
                }).toList();
    }
}

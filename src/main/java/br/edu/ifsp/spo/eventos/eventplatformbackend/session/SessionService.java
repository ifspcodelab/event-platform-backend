package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.AuditService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.Activity;
import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.ActivityRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.Area;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.AreaRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.dto.CancellationMessageCreateDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.*;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.Location;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.LocationRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.space.Space;
import br.edu.ifsp.spo.eventos.eventplatformbackend.space.SpaceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.DiffResult;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class SessionService {
    private final SessionRepository sessionRepository;
    private final SessionScheduleRepository sessionScheduleRepository;
    private final ActivityRepository activityRepository;
    private final LocationRepository locationRepository;
    private final AreaRepository areaRepository;
    private final SpaceRepository spaceRepository;
    private final AuditService auditService;

    public Session create(UUID eventId, UUID activityId, SessionCreateDto dto) {
        Activity activity = getActivity(activityId);
        checksIfEventIsAssociateToActivity(eventId, activity);
        checksIfSessionTitleExists(dto, activityId);

        if(activity.isCanceled()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CREATE_WITH_AN_ACTIVITY_WITH_CANCELED_STATUS);
        }

        if(activity.isNeedRegistration() && activity.getEvent().isRegistrationPeriodEnded()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CREATE_WITH_EVENT_REGISTRATION_PERIOD_BEFORE_TODAY);
        }

        if(!activity.isNeedRegistration() && !activity.getEvent().isExecutionPeriodEnded()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CREATE_WITH_EVENT_EXECUTION_PERIOD_BEFORE_TODAY);
        }

        List<SessionSchedule> sessionsSchedule = getSessionsSchedule(activity, dto, true);

        Session session = new Session(dto.getTitle(), dto.getSeats(), activity, sessionsSchedule);

        return sessionRepository.save(session);
    }

    public Session create(UUID eventId, UUID subeventId, UUID activityId, SessionCreateDto dto) {
        Activity activity = getActivity(activityId);
        checksIfEventIsAssociateToActivity(eventId, activity);
        checksIfSubeventIsAssociateToActivity(subeventId, activity);
        checksIfSessionTitleExists(dto, activityId);

        if(activity.isEventCanceled()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CREATE_WITH_AN_EVENT_WITH_CANCELED_STATUS);
        }

        if(activity.getSubevent().getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CREATE_WITH_A_SUBEVENT_WITH_CANCELED_STATUS);
        }

        if(activity.isCanceled()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CREATE_WITH_AN_ACTIVITY_WITH_CANCELED_STATUS);
        }

        if(activity.isNeedRegistration() && activity.getEvent().isRegistrationPeriodEnded()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CREATE_WITH_EVENT_REGISTRATION_PERIOD_BEFORE_TODAY);
        }

        if(!activity.isNeedRegistration() && activity.getEvent().isExecutionPeriodEnded()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CREATE_WITH_EVENT_EXECUTION_PERIOD_BEFORE_TODAY);
        }

        List<SessionSchedule> sessionsSchedule = getSessionsSchedule(activity, dto, true);

        Session session = new Session(
                dto.getTitle(),
                dto.getSeats(),
                activity,
                sessionsSchedule
        );

        return sessionRepository.save(session);
    }

    public Session update(UUID eventId, UUID activityId, UUID sessionId, SessionCreateDto dto) {
        Session session = getSession(sessionId);
        checksIfEventIsAssociateToSession(eventId, session);
        checksIfActivityIsAssociateToSession(activityId, session);
        checksIfSessionTitleExistsExcludedId(dto, activityId, sessionId);

        if(session.isCanceled()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_UPDATE_WITH_CANCELED_STATUS);
        }

        if(session.getActivity().isCanceled()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_UPDATE_WITH_AN_ACTIVITY_WITH_CANCELED_STATUS);
        }

        if(session.getActivity().isPublished() && session.getActivity().getEvent().isExecutionPeriodEnded()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_UPDATE_WITH_AN_ACTIVITY_PUBLISHED_STATUS_AFTER_EVENT_EXECUTION_PERIOD);
        }

        List<SessionSchedule> sessionSchedule = getSessionsSchedule(session.getActivity(), dto, false);

        if(session.getActivity().getEvent().isRegistrationPeriodStarted()) {
            var executionStartList = session.getSessionSchedules().stream()
                    .map(SessionSchedule::getExecutionStart).collect(Collectors.toList());

            var newExecutionStartList = sessionSchedule.stream()
                    .map(SessionSchedule::getExecutionStart).collect(Collectors.toList());

            if(!executionStartList.equals(newExecutionStartList)) {
                throw new BusinessRuleException(BusinessRuleType.SESSION_UPDATE_WITH_SESSION_SCHEDULE_EXECUTION_IN_REGISTRATION_PERIOD);
            }

            var executionEndList = session.getSessionSchedules().stream()
                    .map(SessionSchedule::getExecutionEnd).collect(Collectors.toList());

            var newExecutionEndList = sessionSchedule.stream()
                    .map(SessionSchedule::getExecutionEnd).collect(Collectors.toList());

            if(!executionEndList.equals(newExecutionEndList)) {
                throw new BusinessRuleException(BusinessRuleType.SESSION_UPDATE_WITH_SESSION_SCHEDULE_EXECUTION_IN_REGISTRATION_PERIOD);
            }
        }

        Session currentSession = new Session();
        currentSession.setTitle(session.getTitle());
        currentSession.setSeats(session.getSeats());
        currentSession.setSessionSchedules(session.getSessionSchedules());

        session.setTitle(dto.getTitle());
        session.setSeats(dto.getSeats());
        session.setSessionSchedules(sessionSchedule);

        sessionRepository.save(session);

        DiffResult<?> diffResult = currentSession.diff(session);
        auditService.logAdminUpdate(ResourceName.SESSION, diffResult.getDiffs().toString(), sessionId);

        return session;
    }

    public Session update(UUID eventId, UUID subeventId, UUID activityId, UUID sessionId, SessionCreateDto dto) {
        Session session = getSession(sessionId);
        checksIfEventIsAssociateToSession(eventId, session);
        checksIfSubeventIsAssociateToSession(subeventId, session);
        checksIfActivityIsAssociateToSession(activityId, session);
        checksIfSessionTitleExistsExcludedId(dto, activityId, sessionId);

        if(session.isCanceled()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_UPDATE_WITH_CANCELED_STATUS);
        }

        if(session.getActivity().isCanceled()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_UPDATE_WITH_AN_ACTIVITY_WITH_CANCELED_STATUS);
        }

        if(session.getActivity().isPublished()) {
            if (session.getActivity().getSubevent().isExecutionPeriodEnded()) {
                throw new BusinessRuleException(BusinessRuleType.SESSION_UPDATE_WITH_AN_ACTIVITY_PUBLISHED_STATUS_AFTER_SUBEVENT_EXECUTION_PERIOD);
            }
        }

        List<SessionSchedule> sessionSchedule = getSessionsSchedule(session.getActivity(), dto, false);

        if(session.getActivity().getEvent().isRegistrationPeriodStarted()) {
            var executionStartList = session.getSessionSchedules().stream()
                    .map(SessionSchedule::getExecutionStart).collect(Collectors.toList());

            var newExecutionStartList = sessionSchedule.stream()
                    .map(SessionSchedule::getExecutionStart).collect(Collectors.toList());

            if(!executionStartList.equals(newExecutionStartList)) {
                throw new BusinessRuleException(BusinessRuleType.SESSION_UPDATE_WITH_SESSION_SCHEDULE_EXECUTION_IN_REGISTRATION_PERIOD);
            }

            var executionEndList = session.getSessionSchedules().stream()
                    .map(SessionSchedule::getExecutionEnd).collect(Collectors.toList());

            var newExecutionEndList = sessionSchedule.stream()
                    .map(SessionSchedule::getExecutionEnd).collect(Collectors.toList());

            if(!executionEndList.equals(newExecutionEndList)) {
                throw new BusinessRuleException(BusinessRuleType.SESSION_UPDATE_WITH_SESSION_SCHEDULE_EXECUTION_IN_REGISTRATION_PERIOD);
            }
        }

        Session currentSession = new Session();
        currentSession.setTitle(session.getTitle());
        currentSession.setSeats(session.getSeats());
        currentSession.setSessionSchedules(session.getSessionSchedules());

        session.setTitle(dto.getTitle());
        session.setSeats(dto.getSeats());
        session.setSessionSchedules(sessionSchedule);

        sessionRepository.save(session);

        DiffResult<?> diffResult = currentSession.diff(session);
        auditService.logAdminUpdate(ResourceName.SESSION, diffResult.getDiffs().toString(), sessionId);

        return session;
    }

    public List<Session> findAll(UUID eventId, UUID activityId) {
        checksIfEventIsAssociateToActivity(eventId, getActivity(activityId));
        return sessionRepository.findAllByActivityId(activityId);
    }

    public List<Session> findAll(UUID eventId, UUID subeventId, UUID activityId) {
        Activity activity = getActivity(activityId);
        checksIfEventIsAssociateToActivity(eventId, activity);
        checksIfSubeventIsAssociateToActivity(subeventId, activity);
        return sessionRepository.findAllByActivityId(activityId);
    }

    public Session findById(UUID eventId, UUID activityId, UUID sessionId) {
        Session session = getSession(sessionId);
        checksIfEventIsAssociateToSession(eventId, session);
        checksIfActivityIsAssociateToSession(activityId, session);
        return session;
    }

    public Session findById(UUID eventId, UUID subeventId, UUID activityId, UUID sessionId) {
        Session session = getSession(sessionId);
        checksIfEventIsAssociateToSession(eventId, session);
        checksIfSubeventIsAssociateToSession(subeventId, session);
        checksIfActivityIsAssociateToSession(activityId, session);
        return session;
    }

    public Session cancel(UUID eventId, UUID activityId, UUID sessionId, CancellationMessageCreateDto cancellationMessageCreateDto) {
        Session session = getSession(sessionId);
        checksIfEventIsAssociateToSession(eventId, session);
        checksIfActivityIsAssociateToSession(activityId, session);

        if(session.isCanceled()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_WITH_CANCELED_STATUS);
        }

        if(session.getActivity().isEventDraft()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_WITH_AN_EVENT_WITH_DRAFT_STATUS);
        }

        if(session.getActivity().isDraft()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_WITH_AN_ACTIVITY_WITH_DRAFT_STATUS);
        }

        if(session.getActivity().isCanceled()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_WITH_AN_ACTIVITY_WITH_CANCELED_STATUS);
        }

        if(session.getActivity().getEvent().isExecutionPeriodEnded()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_AFTER_EVENT_EXECUTION_PERIOD);
        }

        if(session.getActivity().getEvent().isRegistrationPeriodNotStart()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_BEFORE_EVENT_REGISTRATION_PERIOD);
        }

        session.setCanceled(true);
        session.setCancellationMessage(cancellationMessageCreateDto.getReason());
        log.info("Session canceled: id={}, title={}", sessionId, session.getTitle());
        return sessionRepository.save(session);
    }

    public Session cancel(UUID eventId, UUID subeventId, UUID activityId, UUID sessionId, CancellationMessageCreateDto cancellationMessageCreateDto) {
        Session session = getSession(sessionId);
        checksIfEventIsAssociateToSession(eventId, session);
        checksIfSubeventIsAssociateToSession(subeventId, session);
        checksIfActivityIsAssociateToSession(activityId, session);

        if(session.isCanceled()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_WITH_CANCELED_STATUS);
        }

        if(session.getActivity().getSubevent().getStatus().equals(EventStatus.DRAFT)) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_WITH_A_SUBEVENT_WITH_DRAFT_STATUS);
        }

        if(session.getActivity().isCanceled()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_WITH_AN_ACTIVITY_WITH_CANCELED_STATUS);
        }

        if(session.getActivity().isDraft()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_WITH_AN_ACTIVITY_WITH_DRAFT_STATUS);
        }

        if(session.getActivity().getEvent().isExecutionPeriodEnded()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_AFTER_EVENT_EXECUTION_PERIOD);
        }

        if(session.getActivity().isPublished()) {
            if(session.getActivity().getSubevent().isExecutionPeriodEnded()) {
                throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_WITH_ACTIVITY_PUBLISHED_STATUS_AFTER_SUBEVENT_EXECUTION_PERIOD);
            }
        }

        session.setCanceled(true);
        session.setCancellationMessage(cancellationMessageCreateDto.getReason());
        log.info("Session canceled: id={}, title={}", sessionId, session.getTitle());
        return sessionRepository.save(session);
    }

    public void delete(UUID eventId, UUID activityId, UUID sessionId) {
        Session session = getSession(sessionId);
        checksIfEventIsAssociateToSession(eventId, session);
        checksIfActivityIsAssociateToSession(activityId, session);

        if(session.isCanceled()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_DELETE_WITH_CANCELED_STATUS);
        }

        if(session.getActivity().isCanceled()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_DELETE_WITH_AN_ACTIVITY_WITH_CANCELED_STATUS);
        }

        if(session.getActivity().isPublished()) {
            if(session.getActivity().getEvent().isRegistrationPeriodStarted()) {
                throw new BusinessRuleException(BusinessRuleType.SESSION_DELETE_WITH_AN_ACTIVITY_WITH_PUBLISHED_STATUS_AND_AFTER_EVENT_REGISTRATION_PERIOD_START);
            }
        }

        sessionRepository.delete(session);
        log.info("Session deleted: id={}, title={}", sessionId, session.getTitle());
        auditService.logAdminDelete(ResourceName.SESSION, sessionId);
    }

    public void delete(UUID eventId, UUID subeventId, UUID activityId, UUID sessionId) {
        Session session = getSession(sessionId);
        checksIfEventIsAssociateToSession(eventId, session);
        checksIfSubeventIsAssociateToSession(subeventId, session);
        checksIfActivityIsAssociateToSession(activityId, session);

        if(session.isCanceled()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_DELETE_WITH_CANCELED_STATUS);
        }

        if(session.getActivity().isCanceled()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_DELETE_WITH_AN_ACTIVITY_WITH_CANCELED_STATUS);
        }

        if(session.getActivity().isPublished()) {
            if(session.getActivity().getEvent().isRegistrationPeriodStarted()) {
                throw new BusinessRuleException(BusinessRuleType.SESSION_DELETE_WITH_AN_ACTIVITY_WITH_PUBLISHED_STATUS_AND_AFTER_EVENT_REGISTRATION_PERIOD_START);
            }
        }

        sessionRepository.delete(session);
        log.info("Session deleted: id={}, title={}", sessionId, session.getTitle());
        auditService.logAdminDelete(ResourceName.SESSION, sessionId);
    }

    @Transactional
    public void cancelAllByActivityId(UUID eventId, UUID activityId) {

        List<Session> sessions = new ArrayList<>();
        for (Session session : this.findAll(eventId, activityId)) {

            if(!session.isCanceled())
            {
                session.setCanceled(true);
                sessions.add(session);
            }
        }
        sessionRepository.saveAll(sessions);
    }

    @Transactional
    public void cancelAllByActivityId(UUID eventId, UUID subeventId, UUID activityId) {

        List<Session> sessions = new ArrayList<>();
        for (Session session : this.findAll(eventId, subeventId, activityId)) {

            if(!session.isCanceled())
            {
                session.setCanceled(true);
                sessions.add(session);
            }
        }
        sessionRepository.saveAll(sessions);
    }

    private void checksIfEventIsAssociateToSession(UUID eventId, Session session) {
        if(!session.getActivity().getEvent().getId().equals(eventId)) {
            throw new ResourceNotExistsAssociationException(ResourceName.SESSION, ResourceName.EVENT);
        }
    }

    private void checksIfSubeventIsAssociateToSession(UUID subeventId, Session session) {
        if(!session.getActivity().getSubevent().getId().equals(subeventId)) {
            throw new ResourceNotExistsAssociationException(ResourceName.SESSION, ResourceName.SUBEVENT);
        }
    }

    private void checksIfActivityIsAssociateToSession(UUID activityId, Session session) {
        if(!session.getActivity().getId().equals(activityId)) {
            throw new ResourceNotExistsAssociationException(ResourceName.SESSION, ResourceName.ACTIVITY);
        }
    }

    private void checksIfEventIsAssociateToActivity(UUID eventId, Activity activity) {
        if(!activity.getEvent().getId().equals(eventId)) {
            throw new ResourceReferentialIntegrityException(ResourceName.ACTIVITY, ResourceName.EVENT);
        }
    }

    private void checksIfSubeventIsAssociateToActivity(UUID subeventId, Activity activity) {
        if(!activity.getSubevent().getId().equals(subeventId)) {
            throw new ResourceReferentialIntegrityException(ResourceName.ACTIVITY, ResourceName.SUBEVENT);
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

    private void checksIfSessionTitleExists(SessionCreateDto dto, UUID activityId) {
        if (sessionRepository.existsByTitleIgnoreCaseAndActivityId(dto.getTitle(), activityId)) {
            throw new ResourceAlreadyExistsException(ResourceName.SESSION, "title", dto.getTitle());
        }
    }

    private void checksIfSessionTitleExistsExcludedId(SessionCreateDto dto, UUID activityId, UUID sessionId) {
        if (sessionRepository.existsByTitleIgnoreCaseAndActivityIdAndIdNot(dto.getTitle(), activityId, sessionId)) {
            throw new ResourceAlreadyExistsException(ResourceName.SESSION, "title", dto.getTitle());
        }
    }

    private List<SessionSchedule> getSessionsSchedule(Activity activity, SessionCreateDto dto, boolean isCreate) {
        return getValidSessionSchedules(dto).stream()
                .map(sessionSchedule -> {
                    var event = activity.getEvent();

                    if(sessionSchedule.getExecutionStart().isAfter(sessionSchedule.getExecutionEnd())) {
                        throw  new BusinessRuleException(BusinessRuleType.SESSION_SCHEDULE_EXECUTION_START_IS_AFTER_EXECUTION_END);
                    }

                    if(sessionSchedule.getExecutionStart().equals(sessionSchedule.getExecutionEnd())) {
                        throw new BusinessRuleException(BusinessRuleType.SESSION_SCHEDULE_EXECUTION_START_IS_EQUALS_TO_EXECUTION_END);
                    }

                    if(sessionSchedule.getExecutionStart().isBefore(LocalDateTime.now()) || sessionSchedule.getExecutionEnd().isBefore(LocalDateTime.now())) {
                        throw new BusinessRuleException(BusinessRuleType.SESSION_SCHEDULES_EXECUTION_PERIOD_BEFORE_TODAY);
                    }

                    if(activity.getSubevent() != null) {
                        if(!sessionSchedule.isInsidePeriod(activity.getSubevent().getExecutionPeriod())) {
                            throw new BusinessRuleException(BusinessRuleType.SESSION_SCHEDULE_EXECUTION_BEFORE_EVENT_EXECUTION);
                        }
                    }

                    if(event.isExecutionPeriodEnded()) {
                        throw new BusinessRuleException(BusinessRuleType.SESSION_SCHEDULE_EXECUTION_AFTER_EVENT_EXECUTION);
                    }

                    if(!sessionSchedule.isInsidePeriod(event.getExecutionPeriod())) {
                        throw new BusinessRuleException(BusinessRuleType.SESSION_SCHEDULE_EXECUTION_BEFORE_EVENT_EXECUTION);
                    }

                    if(activity.isNeedRegistration() && event.isRegistrationPeriodEnded()) {
                        throw new BusinessRuleException(BusinessRuleType.SESSION_SCHEDULE_EXECUTION_AFTER_EVENT_EXECUTION);
                    }


                    if(sessionSchedule.getSpace() != null){
                        var sessionScheduleWithSpace = sessionScheduleRepository
                                .findAllBySpaceIdAndExecutionStartGreaterThanEqual(sessionSchedule.getSpace().getId(), LocalDateTime.now());

//                        if(isCreate) {
//                            sessionScheduleWithSpace = sessionScheduleWithSpace.stream().filter(s -> {})
//                        }
                        for(SessionSchedule s: sessionScheduleWithSpace) {
                            if(s.hasIntersection(sessionSchedule)) {
                                throw new ResourceAlreadyReservedInTheSpaceException(s);
                            }
                        }
                    }

                    return sessionSchedule;
                }).collect(Collectors.toList());
    }

    private List<SessionSchedule> getValidSessionSchedules(SessionCreateDto dto) {
        List<SessionSchedule> sessionScheduleCreate = dto.getSessionSchedules().stream()
                .map(this::getValidSessionSchedule).collect(Collectors.toList());

        for (SessionSchedule outerSession: sessionScheduleCreate) {
            for (SessionSchedule innerSession: sessionScheduleCreate) {
                if(!outerSession.equals(innerSession) && outerSession.hasIntersection(innerSession)) {
                    throw new ResourceIntersectionInExecutionTimesException(
                            outerSession.getExecutionStart(), outerSession.getExecutionEnd(),
                            innerSession.getExecutionStart(), innerSession.getExecutionEnd(),
                            innerSession.getSpace().getName());
                }
            }
        }

        return sessionScheduleCreate;
    }

    private SessionSchedule getValidSessionSchedule(SessionScheduleCreateDto dto) {
        Location location = null;
        Area area = null;
        Space space = null;

        if(dto.getLocationId() == null) {
            if(dto.getAreaId() != null || dto.getSpaceId() != null) {
                throw new BusinessRuleException(BusinessRuleType.SESSION_SCHEDULE_ADD_AREA_OR_SPACE_IN_A_NULL_LOCATION);
            }
        }

        if(dto.getLocationId() != null) {
            if(dto.getAreaId() == null && dto.getSpaceId() != null) {
                throw new BusinessRuleException(BusinessRuleType.SESSION_SCHEDULE_ADD_SPACE_IN_A_NULL_AREA);
            }
        }

        if(dto.getLocationId() != null) {
            location = getLocation(dto.getLocationId());

            if(dto.getAreaId() != null) {
                area = getArea(dto.getAreaId());

                if(dto.getSpaceId() != null) {
                    space = getSpace(dto.getSpaceId());
                }
            }
        }

        return new SessionSchedule(dto.getExecutionStart(), dto.getExecutionEnd(), dto.getUrl(), location, area, space);
    }
}

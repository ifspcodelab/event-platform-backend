package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

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
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public Session create(UUID eventId, UUID activityId, SessionCreateDto dto) {
        Activity activity = getActivity(activityId);
        checksIfEventIsAssociateToActivity(eventId, activity);
        checksIfSessionTitleExists(dto, activityId);

        if(activity.isEventCanceled()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CREATE_WITH_AN_EVENT_WITH_CANCELED_STATUS);
        }

        if(activity.isCanceled()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CREATE_WITH_AN_ACTIVITY_WITH_CANCELED_STATUS);
        }

        if(activity.getEvent().isRegistrationPeriodEnded()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CREATE_WITH_EVENT_REGISTRATION_PERIOD_BEFORE_TODAY);
        }

        List<SessionSchedule> sessionsSchedule = getSessionsSchedule(activity, dto);

        Session session = new Session(
                dto.getTitle(),
                dto.getSeats(),
                activity,
                sessionsSchedule
        );

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

        if(activity.getEvent().isRegistrationPeriodEnded()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CREATE_WITH_EVENT_REGISTRATION_PERIOD_BEFORE_TODAY);
        }

        List<SessionSchedule> sessionsSchedule = getSessionsSchedule(activity, dto);

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

        List<SessionSchedule> sessionSchedule = getSessionsSchedule(session.getActivity(), dto);

        if(session.getActivity().getEvent().isRegistrationPeriodStarted()) {
            var executionStartList = session.getSessionsSchedule().stream()
                    .map(SessionSchedule::getExecutionStart).toList();

            var newExecutionStartList = sessionSchedule.stream()
                    .map(SessionSchedule::getExecutionStart).toList();

            if(!executionStartList.equals(newExecutionStartList)) {
                throw new BusinessRuleException(BusinessRuleType.SESSION_UPDATE_WITH_SESSION_SCHEDULE_EXECUTION_IN_REGISTRATION_PERIOD);
            }

            var executionEndList = session.getSessionsSchedule().stream()
                    .map(SessionSchedule::getExecutionEnd).toList();

            var newExecutionEndList = sessionSchedule.stream()
                    .map(SessionSchedule::getExecutionEnd).toList();

            if(!executionEndList.equals(newExecutionEndList)) {
                throw new BusinessRuleException(BusinessRuleType.SESSION_UPDATE_WITH_SESSION_SCHEDULE_EXECUTION_IN_REGISTRATION_PERIOD);
            }
        }

        session.setTitle(dto.getTitle());
        session.setSeats(dto.getSeats());
        session.setSessionsSchedule(sessionSchedule);

        return sessionRepository.save(session);
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

        List<SessionSchedule> sessionSchedule = getSessionsSchedule(session.getActivity(), dto);

        if(session.getActivity().getEvent().isRegistrationPeriodStarted()) {
            var executionStartList = session.getSessionsSchedule().stream()
                    .map(SessionSchedule::getExecutionStart).toList();

            var newExecutionStartList = sessionSchedule.stream()
                    .map(SessionSchedule::getExecutionStart).toList();

            if(!executionStartList.equals(newExecutionStartList)) {
                throw new BusinessRuleException(BusinessRuleType.SESSION_UPDATE_WITH_SESSION_SCHEDULE_EXECUTION_IN_REGISTRATION_PERIOD);
            }

            var executionEndList = session.getSessionsSchedule().stream()
                    .map(SessionSchedule::getExecutionEnd).toList();

            var newExecutionEndList = sessionSchedule.stream()
                    .map(SessionSchedule::getExecutionEnd).toList();

            if(!executionEndList.equals(newExecutionEndList)) {
                throw new BusinessRuleException(BusinessRuleType.SESSION_UPDATE_WITH_SESSION_SCHEDULE_EXECUTION_IN_REGISTRATION_PERIOD);
            }
        }

        session.setTitle(dto.getTitle());
        session.setSeats(dto.getSeats());
        session.setSessionsSchedule(sessionSchedule);

        return sessionRepository.save(session);
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

    private List<SessionSchedule> getSessionsSchedule(Activity activity, SessionCreateDto dto) {
        // TODO Validação: não pode registrar uma sessão no mesmo ESPAÇO e horário
        return dto.getSessionsSchedule().stream()
                .map(s -> {
                    Location location = null;
                    Area area = null;
                    Space space = null;

                    if(s.getLocationId() == null) {
                        if(s.getAreaId() != null || s.getSpaceId() != null) {
                            throw new BusinessRuleException(BusinessRuleType.SESSION_SCHEDULE_ADD_AREA_OR_SPACE_IN_A_NULL_LOCATION);
                        }
                    }

                    if(s.getLocationId() != null) {
                        if(s.getAreaId() == null && s.getSpaceId() != null) {
                            throw new BusinessRuleException(BusinessRuleType.SESSION_SCHEDULE_ADD_SPACE_IN_A_NULL_AREA);
                        }
                    }

                    if(s.getLocationId() != null) {
                         location = getLocation(s.getLocationId());

                        if(s.getAreaId() != null) {
                             area = getArea(s.getAreaId());

                            if(s.getSpaceId() != null){
                                 space = getSpace(s.getSpaceId());

                                // TODO - retornar apenas execution start e end

                                var sessionScheduleWithSpace = sessionScheduleRepository.findAllWithSpaceId(space.getId());

                                var sessionScheduleWithSpaceExecutionStartList = sessionScheduleWithSpace.stream().map(SessionSchedule::getExecutionStart).toList();
                                var sessionScheduleWithSpaceExecutionEndList = sessionScheduleWithSpace.stream().map(SessionSchedule::getExecutionStart).toList();

                                // TODO - precisar comparar também somente com o que foi passado
                                for(int i = 0; i <sessionScheduleWithSpaceExecutionStartList.size(); i++) {
                                    if(sessionScheduleWithSpaceExecutionStartList.get(i).isAfter(s.getExecutionStart())
                                            && sessionScheduleWithSpaceExecutionStartList.get(i).isBefore(s.getExecutionStart())){
                                        // TODO - mudar para uma exception
                                        throw new BusinessRuleException(BusinessRuleType.SESSION_SCHEDULE_ALREADY_RESERVED_IN_THE_SPACE);
                                    }

                                    if(sessionScheduleWithSpaceExecutionEndList.get(i).isBefore(s.getExecutionEnd())
                                            && sessionScheduleWithSpaceExecutionEndList.get(i).isAfter(s.getExecutionStart())){
                                        // TODO - mudar para uma exception
                                        throw new BusinessRuleException(BusinessRuleType.SESSION_SCHEDULE_ALREADY_RESERVED_IN_THE_SPACE);
                                    }
                                }
                            }
                        }
                    }

                    if(s.getExecutionStart().isAfter(s.getExecutionEnd())) {
                        throw  new BusinessRuleException(BusinessRuleType.SESSION_SCHEDULE_EXECUTION_START_IS_AFTER_EXECUTION_END);
                    }

                    if(s.getExecutionStart().equals(s.getExecutionEnd())) {
                        throw new BusinessRuleException(BusinessRuleType.SESSION_SCHEDULE_EXECUTION_START_IS_EQUALS_TO_EXECUTION_END);
                    }

                    if(s.getExecutionStart().isBefore(LocalDateTime.now()) ||
                            s.getExecutionEnd().isBefore(LocalDateTime.now())
                    ) {
                        throw new BusinessRuleException(BusinessRuleType.SESSION_SCHEDULES_EXECUTION_PERIOD_BEFORE_TODAY);
                    }

                    if(s.getExecutionStart().toLocalDate().isBefore(activity.getEvent().getExecutionPeriod().getStartDate())) {
                        throw new BusinessRuleException(BusinessRuleType.SESSION_SCHEDULE_EXECUTION_BEFORE_EVENT_EXECUTION);
                    }

                    if(s.getExecutionEnd().toLocalDate().isAfter(activity.getEvent().getExecutionPeriod().getEndDate())) {
                        throw new BusinessRuleException(BusinessRuleType.SESSION_SCHEDULE_EXECUTION_AFTER_EVENT_EXECUTION);
                    }

                    if(activity.getSubevent() != null) {
                        if(s.getExecutionStart().toLocalDate().isBefore(activity.getSubevent().getExecutionPeriod().getStartDate())) {
                            throw new BusinessRuleException(BusinessRuleType.SESSION_SCHEDULE_EXECUTION_BEFORE_SUBEVENT_EXECUTATION);
                        }

                        if(s.getExecutionEnd().toLocalDate().isAfter(activity.getSubevent().getExecutionPeriod().getEndDate())) {
                            throw new BusinessRuleException(BusinessRuleType.SESSION_SCHEDULE_EXECUTION_AFTER_SUBEVENT_EXECUTATION);
                        }
                    }

                    return new SessionSchedule(
                            s.getExecutionStart(),
                            s.getExecutionEnd(),
                            s.getUrl(),
                            location,
                            area,
                            space
                    );
                }).toList();
    }
}

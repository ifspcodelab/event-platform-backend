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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final ActivityRepository activityRepository;
    private final LocationRepository locationRepository;
    private final AreaRepository areaRepository;
    private final SpaceRepository spaceRepository;

    public Session create(UUID eventId, UUID activityId, SessionCreateDto dto) {
        Activity activity = getActivity(activityId);
        checksIfEventIsAssociateToActivity(eventId, activity);

        if (sessionRepository.existsByTitleIgnoreCaseAndActivityId(dto.getTitle(), activityId)) {
            throw new ResourceAlreadyExistsException(ResourceName.SESSION, "title", dto.getTitle());
        }

        if (activity.getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CREATE_WITH_ACTIVITY_CANCELED_STATUS);
        }

        if (activity.getEvent().getRegistrationPeriod().getEndDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CREATE_WITH_EVENT_REGISTRATION_PERIOD_BEFORE_TODAY);
        }

        List<SessionSchedule> sessionSchedules = getSessionSchedules(activity, dto);


        Session session = new Session(
                dto.getTitle(),
                dto.getSeats(),
                activity,
                sessionSchedules

        );

        return sessionRepository.save(session);
    }

//
//    public Session create(UUID eventId, UUID subeventId, UUID activityId, SessionCreateDto dto) {
//        Activity activity = getActivity(activityId);
//
//        Session session = new Session(
//                dto.getTitle(),
//                dto.getSeats(),
//                activity,
//                getSessionSchedules(dto)
//        );
//
//        return sessionRepository.save(session);
//    }
//
//    public Session update(UUID eventId, UUID activityId, UUID sessionId, SessionCreateDto dto) {
//        Session session = getSession(sessionId);
//        List<SessionSchedule> sessionSchedule = getSessionSchedules(dto); // do DTO
//
//        session.setTitle(dto.getTitle());
//        session.setSeats(dto.getSeats());
//
//        session.setSessionsSchedules(sessionSchedule);
//
//        return sessionRepository.save(session);
//    }
//
//    public Session update(UUID eventId, UUID subeventId, UUID activityId, UUID sessionId, SessionCreateDto dto) {
//        Session session = getSession(sessionId);
//        List<SessionSchedule> sessionSchedule = getSessionSchedules(dto); // do DTO
//
//        session.setTitle(dto.getTitle());
//        session.setSeats(dto.getSeats());
//
//        session.setSessionsSchedules(sessionSchedule);
//
//        return sessionRepository.save(session);
//    }

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

    public Session cancel(UUID eventId, UUID activityId, UUID sessionId, CancellationMessageCreateDto cancellationMessageCreateDto) {
        Session session = getSession(sessionId);
        checksIfEventIsAssociateToSession(eventId, session);
        checksIfActivityIsAssociateToSession(activityId, session);

        if(session.isCanceled()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_WITH_CANCELED_STATUS);
        }

        if(session.getActivity().getEvent().getStatus().equals(EventStatus.DRAFT)) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_WITH_AN_EVENT_WITH_DRAFT_STATUS);
        }

        if(session.getActivity().getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_WITH_AN_ACTIVITY_WITH_CANCELED_STATUS);
        }

        if(session.getActivity().getStatus().equals(EventStatus.DRAFT)) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_WITH_AN_ACTIVITY_WITH_DRAFT_STATUS);
        }

        if(session.getActivity().getEvent().getExecutionPeriod().getEndDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_AFTER_EVENT_EXECUTION_PERIOD);
        }

        session.setCanceled(true);
        session.setCancellationMessage(cancellationMessageCreateDto.getReason());
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

        // se ao cancelar um evento e os subeventos sao cancelados também, entao não precisa verificar se o evento está cncelado...
        if(session.getActivity().getSubevent().getStatus().equals(EventStatus.DRAFT)) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_WITH_A_SUBEVENT_WITH_DRAFT_STATUS);
        }

        if(session.getActivity().getSubevent().getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_WITH_A_SUBEVENT_WITH_CANCELED_STATUS);
        }

        if(session.getActivity().getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_WITH_AN_ACTIVITY_WITH_CANCELED_STATUS);
        }

        if(session.getActivity().getStatus().equals(EventStatus.DRAFT)) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_WITH_AN_ACTIVITY_WITH_DRAFT_STATUS);
        }

        if(session.getActivity().getEvent().getExecutionPeriod().getEndDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_AFTER_EVENT_EXECUTION_PERIOD);
        }

        if(session.getActivity().getStatus().equals(EventStatus.PUBLISHED)) {
            if (session.getActivity().getSubevent().getExecutionPeriod().getEndDate().isBefore(LocalDate.now())) {
                throw new BusinessRuleException(BusinessRuleType.SESSION_CANCEL_WITH_ACTIVITY_PUBLISHED_STATUS_AFTER_SUBEVENT_EXECUTION_PERIOD);
            }
        }

        session.setCanceled(true);
        session.setCancellationMessage(cancellationMessageCreateDto.getReason());
        return sessionRepository.save(session);
    }

    public void delete(UUID eventId, UUID activityId, UUID sessionId) {
        Session session = getSession(sessionId);
        checksIfEventIsAssociateToSession(eventId, session);
        checksIfActivityIsAssociateToSession(activityId, session);

        if(session.isCanceled()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_DELETE_WITH_STATUS_CANCELED);
        }

        if(session.getActivity().getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_DELETE_WITH_ACTIVITY_STATUS_CANCELED);
        }

        if(session.getActivity().getStatus().equals(EventStatus.PUBLISHED)) {
            if(session.getActivity().getEvent().getRegistrationPeriod().getStartDate().isBefore(LocalDate.now())) {
                throw new BusinessRuleException(BusinessRuleType.SESSION_DELETE_WITH_ACTIVITY_PUBLISHED_STATUS_AND_AFTER_EVENT_REGISTRATION_PERIOD_START);
            }
        }

        sessionRepository.delete(session);
    }

    public void delete(UUID eventId, UUID subeventId, UUID activityId, UUID sessionId) {
        Session session = getSession(sessionId);
        checksIfEventIsAssociateToSession(eventId, session);
        checksIfSubeventIsAssociateToSession(subeventId, session);
        checksIfActivityIsAssociateToSession(activityId, session);

        if(session.isCanceled()) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_DELETE_WITH_STATUS_CANCELED);
        }

        if(session.getActivity().getStatus().equals(EventStatus.CANCELED)) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_DELETE_WITH_ACTIVITY_STATUS_CANCELED);
        }

        if(session.getActivity().getStatus().equals(EventStatus.PUBLISHED)) {
            if(session.getActivity().getEvent().getRegistrationPeriod().getStartDate().isBefore(LocalDate.now())) {
                throw new BusinessRuleException(BusinessRuleType.SESSION_DELETE_WITH_ACTIVITY_PUBLISHED_STATUS_AND_AFTER_EVENT_REGISTRATION_PERIOD_START);
            }
        }

        if(session.getActivity().getStatus().equals(EventStatus.PUBLISHED)) {
            if (session.getActivity().getSubevent().getExecutionPeriod().getEndDate().isBefore(LocalDate.now())) {
                throw new BusinessRuleException(BusinessRuleType.SESSION_DELETE_WITH_ACTIVITY_PUBLISHED_STATUS_AFTER_SUBEVENT_EXECUTION_PERIOD);
            }
        }

        sessionRepository.delete(session);
    }

    private void checksIfEventIsAssociateToSession(UUID eventId, Session session) {
        if (!session.getActivity().getEvent().getId().equals(eventId)) {
            throw new ResourceNotExistsAssociationException(ResourceName.SESSION, ResourceName.EVENT);
        }
    }

    private void checksIfSubeventIsAssociateToSession(UUID subeventId, Session session) {
        if (!session.getActivity().getSubevent().getId().equals(subeventId)) {
            throw new ResourceNotExistsAssociationException(ResourceName.SESSION, ResourceName.SUBEVENT);
        }
    }

    private void checksIfActivityIsAssociateToSession(UUID activityId, Session session) {
        if (!session.getActivity().getId().equals(activityId)) {
            throw new ResourceNotExistsAssociationException(ResourceName.SESSION, ResourceName.ACTIVITY);
        }
    }

    private void checksIfEventIsAssociateToActivity(UUID eventId, Activity activity) {
        if (!activity.getEvent().getId().equals(eventId)) {
            throw new ResourceReferentialIntegrityException(ResourceName.ACTIVITY, ResourceName.EVENT);
        }
    }

    private void checksIfSubeventIsAssociateToActivity(UUID subeventId, Activity activity) {
        if (!activity.getSubevent().getId().equals(subeventId)) {
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

//    @Transactional
//    public void cancelAllByActivityId(UUID eventId, UUID activityId) {
//
//        List<Session> sessions = new ArrayList<>();
//        for (Session session : this.findAll(eventId, activityId)) {
//
//            if(!session.isCanceled())
//             {
//                session.setCanceled(true);
//                sessions.add(session);
//            }
//        }
//        sessionRepository.saveAll(sessions);
//    }
//
//    @Transactional
//    public void cancelAllByActivityId(UUID eventId, UUID subeventId, UUID activityId) {
//
//        List<Session> sessions = new ArrayList<>();
//        for (Session session : this.findAll(eventId, subeventId, activityId)) {
//
//            if(!session.isCanceled())
//            {
//                session.setCanceled(true);
//                sessions.add(session);
//            }
//        }
//        sessionRepository.saveAll(sessions);
//    }

//    // TODO MUDAR O NOME DO EXECUTION DE SESSION SCHEDULE
//    // SEM
//    private List<SessionSchedule> validationsSessionSchedules(Activity activity, List<SessionSchedule> sessionSchedules) {
//        sessionSchedules.stream()
//                .map(s -> {
//                    if (s.getExecution_start().isBefore(LocalDateTime.now()) ||
//                            s.getExecution_end().isBefore(LocalDateTime.now())
//                    ) {
//                        throw new BusinessRuleException(BusinessRuleType.SESSION_SCHEDULES_EXECUTION_PERIOD_BEFORE_TODAY);
//                    }
//
//                    if(s.getExecution_start().toLocalDate().isBefore(activity.getEvent().getExecutionPeriod().getStartDate())) {
//                        throw new BusinessRuleException(BusinessRuleType.SESSION_SCHEDULE_EXECUTION_BEFORE_EVENT);
//                    }
//
//                    if(s.getExecution_end().toLocalDate().isAfter(activity.getEvent().getExecutionPeriod().getEndDate())) {
//                        throw new BusinessRuleException(BusinessRuleType.SESSION_SCHEDULE_EXECUTION_AFTER_EVENT);
//                    }
//                }
//    }

    private List<SessionSchedule> getSessionSchedules(Activity activity, SessionCreateDto dto) {
        return dto.getSessionsSchedules().stream()
                .map(s -> {
                    Location location = s.getLocationId() != null ? getLocation(s.getLocationId()) : null;
                    Area area = s.getAreaId() != null ? getArea(s.getAreaId()) : null;
                    Space space = s.getSpaceId() != null ? getSpace(s.getSpaceId()) : null;
                    /* TODO - PRECISA CONFIRMAR, POR EXEMPLO:
                       QUE NÃO VAI TER UMA LOCATION E UM SPACE, SEM A AREA
                       OU SÓ UMA AREA || SPACE
                       OU SOMENTE UMA AREA E SPACE
                     */

                        if (s.getExecution_start().isBefore(LocalDateTime.now()) ||
                                s.getExecution_end().isBefore(LocalDateTime.now())
                        ) {
                            throw new BusinessRuleException(BusinessRuleType.SESSION_SCHEDULES_EXECUTION_PERIOD_BEFORE_TODAY);
                        }

                        if (s.getExecution_start().toLocalDate().isBefore(activity.getEvent().getExecutionPeriod().getStartDate())) {
                            throw new BusinessRuleException(BusinessRuleType.SESSION_SCHEDULE_EXECUTION_BEFORE_EVENT);
                        }

                        if (s.getExecution_end().toLocalDate().isAfter(activity.getEvent().getExecutionPeriod().getEndDate())) {
                            throw new BusinessRuleException(BusinessRuleType.SESSION_SCHEDULE_EXECUTION_AFTER_EVENT);
                        }

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


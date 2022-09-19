package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.Action;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.AuditService;
import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.Activity;
import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.ActivityRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.Area;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.AreaRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.dto.CancellationMessageCreateDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.*;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.security.JwtUserDetails;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.Location;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.LocationRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.organizer_authorization.OrganizerAuthorizationException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.organizer_authorization.OrganizerAuthorizationExceptionType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.registration.Registration;
import br.edu.ifsp.spo.eventos.eventplatformbackend.registration.RegistrationRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.registration.RegistrationStatus;
import br.edu.ifsp.spo.eventos.eventplatformbackend.space.Space;
import br.edu.ifsp.spo.eventos.eventplatformbackend.space.SpaceRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.Subevent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
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
    private final RegistrationRepository registrationRepository;

    public Session create(UUID eventId, UUID activityId, SessionCreateDto dto) {
        checkUserIsAdmin();
        Activity activity = getActivity(activityId);
        checksIfEventIsAssociateToActivity(eventId, activity);
        checksIfSessionTitleExists(dto, activityId);
        checkIfEventSubEventActivityIsNotCancelled(activity);
        checkIfSchedulesTimesAreValid(activity, dto);
        checkIfSchedulesHasNotIntersections(dto);
        checkIfSchedulesTimesAreInsideExecutionPeriod(activity, dto);
        checkIfSchedulesDurationIsTheSameAsActivity(activity, dto);
        checkIfActivityRegistrationRequirementsPass(activity, dto);
        checkIfActivityModalityRequirementsPass(activity, dto);

        List<SessionSchedule> sessionSchedules = getValidSessionSchedules(dto);
        checkIfSpaceIsAvailable(sessionSchedules);

        Session session = new Session(dto.getTitle(), dto.getSeats(), activity, sessionSchedules);
        return sessionRepository.save(session);
    }

    public Session create(UUID eventId, UUID subeventId, UUID activityId, SessionCreateDto dto) {
        checkUserIsAdmin();
        Activity activity = getActivity(activityId);
        checksIfEventIsAssociateToActivity(eventId, activity);
        checksIfSubeventIsAssociateToActivity(subeventId, activity);
        checksIfSessionTitleExists(dto, activityId);
        checkIfEventSubEventActivityIsNotCancelled(activity);
        checkIfSchedulesTimesAreValid(activity, dto);
        checkIfSchedulesHasNotIntersections(dto);
        checkIfSchedulesTimesAreInsideExecutionPeriod(activity, dto);
        checkIfSchedulesDurationIsTheSameAsActivity(activity, dto);
        checkIfActivityRegistrationRequirementsPass(activity, dto);
        checkIfActivityModalityRequirementsPass(activity, dto);

        List<SessionSchedule> sessionSchedules = getValidSessionSchedules(dto);
        checkIfSpaceIsAvailable(sessionSchedules);

        Session session = new Session(dto.getTitle(), dto.getSeats(), activity, sessionSchedules);
        return sessionRepository.save(session);
    }

    @Transactional
    public Session cancel(UUID eventId, UUID activityId, UUID sessionId, CancellationMessageCreateDto cancellationMessageCreateDto) {
        checkUserIsAdmin();
        Session session = getSessionWithLock(sessionId);
        checksIfEventIsAssociateToSession(eventId, session);
        checksIfActivityIsAssociateToSession(activityId, session);
        checkIfEventSubEventActivityIsNotCancelled(session.getActivity());
        checkIfSessionIsNotCanceled(session);

        registrationRepository.findAllBySessionId(sessionId).stream()
            .filter(Registration::canBeCanceled)
            .forEach(registration -> {
                registration.setRegistrationStatus(RegistrationStatus.CANCELED_BY_ADMIN);
                registrationRepository.save(registration);
                log.info("Registration canceled by admin: id={}, email={}", registration.getId(), registration.getAccount().getEmail());
        });

        session.setConfirmedSeats(0);
        session.setCanceled(true);
        session.setCancellationMessage(cancellationMessageCreateDto.getReason());
        log.info("Session canceled: id={}, title={}", sessionId, session.getTitle());
        auditService.logAdmin(Action.CANCEL, ResourceName.SESSION, sessionId);
        return sessionRepository.save(session);
    }

    @Transactional
    public Session cancel(UUID eventId, UUID subeventId, UUID activityId, UUID sessionId, CancellationMessageCreateDto cancellationMessageCreateDto) {
        checkUserIsAdmin();
        Session session = getSessionWithLock(sessionId);
        checksIfEventIsAssociateToSession(eventId, session);
        checksIfSubeventIsAssociateToSession(subeventId, session);
        checksIfActivityIsAssociateToSession(activityId, session);
        checkIfEventSubEventActivityIsNotCancelled(session.getActivity());
        checkIfSessionIsNotCanceled(session);

        registrationRepository.findAllBySessionId(sessionId).stream()
            .filter(Registration::canBeCanceled)
            .forEach(registration -> {
                registration.setRegistrationStatus(RegistrationStatus.CANCELED_BY_ADMIN);
                registrationRepository.save(registration);
                log.info("Registration canceled by admin: id={}, email={}", registration.getId(), registration.getAccount().getEmail());
            });

        session.setConfirmedSeats(0);
        session.setCanceled(true);
        session.setCancellationMessage(cancellationMessageCreateDto.getReason());
        log.info("Session canceled: id={}, title={}", sessionId, session.getTitle());
        auditService.logAdmin(Action.CANCEL, ResourceName.SESSION, sessionId);
        return sessionRepository.save(session);
    }

    public void delete(UUID eventId, UUID activityId, UUID sessionId) {
        checkUserIsAdmin();
        Session session = getSession(sessionId);
        checksIfEventIsAssociateToSession(eventId, session);
        checksIfActivityIsAssociateToSession(activityId, session);
        checkIfEventSubEventActivityIsNotCancelled(session.getActivity());
        checkIfSessionIsNotCanceled(session);
        checkIfRegistrationPeriodNotStarted(session);
        sessionRepository.delete(session);
        log.info("Session deleted: id={}, title={}", sessionId, session.getTitle());
        auditService.logAdminDelete(ResourceName.SESSION, sessionId);
    }

    public void delete(UUID eventId, UUID subeventId, UUID activityId, UUID sessionId) {
        checkUserIsAdmin();
        Session session = getSession(sessionId);
        checksIfEventIsAssociateToSession(eventId, session);
        checksIfSubeventIsAssociateToSession(subeventId, session);
        checksIfActivityIsAssociateToSession(activityId, session);
        checkIfEventSubEventActivityIsNotCancelled(session.getActivity());
        checkIfSessionIsNotCanceled(session);
        checkIfRegistrationPeriodNotStarted(session);
        sessionRepository.delete(session);
        log.info("Session deleted: id={}, title={}", sessionId, session.getTitle());
        auditService.logAdminDelete(ResourceName.SESSION, sessionId);
    }

    private void checkIfEventSubEventActivityIsNotCancelled(Activity activity) {
        if(activity.getEvent().isCanceled()) {
            throw new SessionRuleException(SessionRuleType.CANCELED_EVENT);
        }

        if(activity.getSubevent() != null) {
            if(activity.getSubevent().isCanceled()) {
                throw new SessionRuleException(SessionRuleType.CANCELED_SUBEVENT);
            }
        }

        if(activity.isCanceled()) {
            throw new SessionRuleException(SessionRuleType.CANCELED_ACTIVITY);
        }
    }

    private void checkIfSessionIsNotCanceled(Session session) {
        if(session.isCanceled()) {
            throw new SessionRuleException(SessionRuleType.CANCELED_SESSION);
        }
    }

    private void checkIfSchedulesTimesAreValid(Activity activity, SessionCreateDto dto) {
        dto.getSessionSchedules().forEach(s -> {
            if(s.getExecutionStart().isAfter(s.getExecutionEnd())) {
                throw new SessionRuleException(SessionRuleType.SCHEDULE_INVALID_PERIOD);
            }

            if(s.getExecutionStart().isEqual(s.getExecutionEnd())) {
                throw new SessionRuleException(SessionRuleType.SCHEDULE_INVALID_PERIOD);
            }

            if(s.getExecutionStart().isBefore(LocalDateTime.now()) || s.getExecutionEnd().isBefore(LocalDateTime.now())) {
                throw new SessionRuleException(SessionRuleType.SCHEDULE_IN_PAST);
            }
        });
    }

    private void checkIfSchedulesHasNotIntersections(SessionCreateDto dto) {
        for (SessionScheduleCreateDto outerSession: dto.getSessionSchedules()) {
            for (SessionScheduleCreateDto innerSession: dto.getSessionSchedules()) {
                if(!outerSession.equals(innerSession) && outerSession.hasIntersection(innerSession)) {
                    throw new SessionRuleException(SessionRuleType.SCHEDULE_HAS_INTERSECTIONS);
                }
            }
        }
    }

    private void checkIfSchedulesTimesAreInsideExecutionPeriod(Activity activity, SessionCreateDto dto) {
        dto.getSessionSchedules().forEach(s -> {
            Event event = activity.getEvent();
            Subevent subevent = activity.getSubevent();

            boolean outSideExecutionPeriod = true;

            if(subevent != null) {
                outSideExecutionPeriod =
                    s.getExecutionStart().isBefore(subevent.getExecutionPeriod().getStartDate().atStartOfDay()) ||
                    s.getExecutionEnd().isAfter(subevent.getExecutionPeriod().getEndDate().plusDays(1).atStartOfDay());
            } else {
                outSideExecutionPeriod =
                    s.getExecutionStart().isBefore(event.getExecutionPeriod().getStartDate().atStartOfDay()) ||
                    s.getExecutionEnd().isAfter(event.getExecutionPeriod().getEndDate().plusDays(1).atStartOfDay());
            }

            if(outSideExecutionPeriod) {
                throw new SessionRuleException(SessionRuleType.OUTSIDE_EXECUTION_PERIOD);
            }
        });
    }

    private void checkIfSchedulesDurationIsTheSameAsActivity(Activity activity, SessionCreateDto dto) {
        Long sessionDurationInSeconds = dto.getSessionSchedules().stream()
            .map(s -> Duration.between(s.getExecutionStart(), s.getExecutionEnd()).getSeconds())
            .reduce(0L, Long::sum);

        Long activityCredentialTimeTotalDuration = (long) activity.getSetupTimeInSeconds() * dto.getSessionSchedules().size();

        Long activityDurationPlusCredentialTime = activity.getDurationInSeconds() + activityCredentialTimeTotalDuration;

        if(!activityDurationPlusCredentialTime.equals(sessionDurationInSeconds)) {
            throw new SessionRuleException(SessionRuleType.SESSION_DURATION);
        }
    }

    private void checkIfActivityRegistrationRequirementsPass(Activity activity, SessionCreateDto dto) {
        if(activity.isNeedRegistration()) {
            if(dto.getSeats() == 0) {
                throw new SessionRuleException(SessionRuleType.SEATS_NOT_DEFINED);
            }

            if(activity.getEvent().isRegistrationPeriodEnded()) {
                throw new SessionRuleException(SessionRuleType.REGISTRATION_PERIOD_ENDED);
            }
        } else {
            if(activity.getEvent().isExecutionPeriodEnded()) {
                throw new SessionRuleException(SessionRuleType.EXECUTION_PERIOD_ENDED);
            }
        }
    }

    private void checkIfActivityModalityRequirementsPass(Activity activity, SessionCreateDto dto) {
        boolean locationIsNotPresent = dto.getSessionSchedules().stream().anyMatch(s -> s.getLocationId() == null);
        boolean urlIsNotPresent = dto.getSessionSchedules().stream().anyMatch(s -> s.getUrl().isBlank());

        if(activity.isInPerson()) {
            if(locationIsNotPresent) {
                throw new SessionRuleException(SessionRuleType.LOCATION_NOT_DEFINED);
            }
        }

        if(activity.isOnline()) {
            if(urlIsNotPresent) {
                throw new SessionRuleException(SessionRuleType.URL_NOT_DEFINED);
            }
        }

        if(activity.isHibrid()) {
            dto.getSessionSchedules().stream()
                .filter(s -> !s.getUrl().isBlank())
                .findAny()
                .orElseThrow(() -> new SessionRuleException(SessionRuleType.URL_OR_LOCATION_NOT_DEFINED));

            dto.getSessionSchedules().stream()
                .filter(s -> s.getLocationId() != null)
                .findAny()
                .orElseThrow(() -> new SessionRuleException(SessionRuleType.URL_OR_LOCATION_NOT_DEFINED));
        }
    }

    private void checkIfRegistrationPeriodNotStarted(Session session) {
        if(session.getActivity().getEvent().isRegistrationPeriodStarted()) {
            throw new SessionRuleException(SessionRuleType.REGISTRATION_PERIOD_STARTED);
        }
    }

    private List<SessionSchedule> getValidSessionSchedules(SessionCreateDto dto) {
       return dto.getSessionSchedules().stream()
            .map(this::toSessionSchedule)
            .collect(Collectors.toList());
    }

    private SessionSchedule toSessionSchedule(SessionScheduleCreateDto dto) {
        Location location = null;
        Area area = null;
        Space space = null;

        if(dto.getLocationId() == null) {
            if(dto.getAreaId() != null || dto.getSpaceId() != null) {
                throw new SessionRuleException(SessionRuleType.AREA_OR_SPACE_NULL_LOCATION);
            }
        }

        if(dto.getLocationId() != null) {
            if(dto.getAreaId() == null && dto.getSpaceId() != null) {
                throw new SessionRuleException(SessionRuleType.SPACE_NULL_AREA);
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

    private void checkIfSpaceIsAvailable(List<SessionSchedule> sessionSchedules) {
        sessionSchedules.forEach(sessionSchedule -> {
            if (sessionSchedule.getSpace() != null) {
                var sessionSchedulesAtSpace = sessionScheduleRepository
                    .findAllBySpaceIdAndExecutionStartGreaterThanEqual(sessionSchedule.getSpace().getId(), LocalDateTime.now());

                for (SessionSchedule s : sessionSchedulesAtSpace) {
                    if (s.hasIntersection(sessionSchedule) && !s.getSession().isCanceled()) {
                        throw new ResourceAlreadyReservedInTheSpaceException(s);
                    }
                }
            }
        });
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

    @Transactional
    public void cancelAllByActivityId(UUID eventId, UUID activityId, String reason) {
        this.findAll(eventId, activityId).forEach(session -> {
            var cancellationMessageCreateDto = new CancellationMessageCreateDto();
            cancellationMessageCreateDto.setReason(reason);
            this.cancel(eventId, activityId, session.getId(), cancellationMessageCreateDto);
        });
    }

    @Transactional
    public void cancelAllByActivityId(UUID eventId, UUID subeventId, UUID activityId, String reason) {
        this.findAll(eventId, subeventId, activityId).forEach(session -> {
            var cancellationMessageCreateDto = new CancellationMessageCreateDto();
            cancellationMessageCreateDto.setReason(reason);
            this.cancel(eventId, activityId, session.getId(), cancellationMessageCreateDto);
        });
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

    private Session getSessionWithLock(UUID sessionId) {
        return sessionRepository.findByIdWithPessimisticLock(sessionId)
            .orElseThrow(() -> new ResourceNotFoundException(ResourceName.SESSION, sessionId));
    }

    private void checksIfSessionTitleExists(SessionCreateDto dto, UUID activityId) {
        if (sessionRepository.existsByTitleIgnoreCaseAndActivityId(dto.getTitle(), activityId)) {
            throw new ResourceAlreadyExistsException(ResourceName.SESSION, "title", dto.getTitle());
        }
    }

    private void checkUserIsAdmin() {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!jwtUserDetails.isAdmin()) {
            throw new BusinessRuleException(BusinessRuleType.UNAUTHORIZED_ACTION);
        }
    }
}

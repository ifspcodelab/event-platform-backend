package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.Activity;
import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.ActivityRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.Area;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.AreaRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.dto.CancellationMessageCreateDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceName;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceNotFoundException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.Location;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.LocationRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.space.Space;
import br.edu.ifsp.spo.eventos.eventplatformbackend.space.SpaceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import javax.transaction.Transactional;
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

//    public Session update(UUID eventId, UUID activityId, UUID sessionId, SessionCreateDto dto) {
//        Session session = getSession(sessionId);
//
//        List<SessionSchedule> sessionSchedulesAux = dto.getSessionsSchedules().stream()
//                .map(s -> {
//                    Location location = s.getLocationId() != null ? getLocation(s.getLocationId()) : null;
//                    Area area = s.getAreaId() != null ? getArea(s.getAreaId()) : null;
//                    Space space = s.getSpaceId() != null ? getSpace(s.getSpaceId()) : null;
//
//                    return new SessionSchedule(
//                            s.getExecution_start(),
//                            s.getExecution_end(),
//                            s.getUrl(),
//                            location,
//                            area,
//                            space
//                    );
//
//                }).toList();
//        List<SessionSchedule> sessionSchedules = session.getSessionsSchedules().stream()
//                .map(s -> {
//                    s.setLocation(sessionSchedulesAux);
//                })
//    }

    // com sessions schedules
    public List<Session> findAll(UUID eventId, UUID activityId) {
        return sessionRepository.findAllByActivityId(activityId);
    }

    // com sessions schedules
    public List<Session> findAll(UUID eventId, UUID subeventId, UUID activityId) {
        return sessionRepository.findAllByActivityId(activityId);
    }

    public Session cancel(UUID eventId, UUID activityId, UUID sessionId, CancellationMessageCreateDto cancellationMessageCreateDto) {
        Session session = getSession(sessionId);

        session.setCanceled(true);
        session.setCancellationMessage(cancellationMessageCreateDto.getReason());
        return sessionRepository.save(session);
    }

    public Session cancel(UUID eventId, UUID subeventId, UUID activityId, UUID sessionId, CancellationMessageCreateDto cancellationMessageCreateDto) {
        Session session = getSession(sessionId);

        session.setCanceled(true);
        session.setCancellationMessage(cancellationMessageCreateDto.getReason());
        return sessionRepository.save(session);
    }

    public void delete(UUID eventId, UUID activityId, UUID sessionId) {
        Session session = getSession(sessionId);

        sessionRepository.delete(session);
    }

    public void delete(UUID eventId, UUID subeventId, UUID activityId, UUID sessionId) {
        Session session = getSession(sessionId);

        sessionRepository.delete(session);
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
        List<SessionSchedule> sessionSchedules = dto.getSessionsSchedules().stream()
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

        return sessionSchedules;
    }
}

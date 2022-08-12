package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.Activity;
import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.ActivityRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.Area;
import br.edu.ifsp.spo.eventos.eventplatformbackend.area.AreaRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceName;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceNotFoundException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.Location;
import br.edu.ifsp.spo.eventos.eventplatformbackend.location.LocationRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.space.Space;
import br.edu.ifsp.spo.eventos.eventplatformbackend.space.SpaceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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

        Session session = new Session(
                dto.getTitle(),
                dto.getSeats(),
                activity,
                sessionSchedules
        );

        return sessionRepository.save(session);
    }

    public Session create(UUID eventId, UUID subeventId, UUID activityId, SessionCreateDto dto) {
        Activity activity = getActivity(activityId);

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

        Session session = new Session(
                dto.getTitle(),
                dto.getSeats(),
                activity,
                sessionSchedules
        );

        return sessionRepository.save(session);
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
}

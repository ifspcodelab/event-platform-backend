package br.edu.ifsp.spo.eventos.eventplatformbackend.site;

import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.ActivityModality;
import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.ActivityRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.ActivityType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceName;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceNotFoundException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus;
import br.edu.ifsp.spo.eventos.eventplatformbackend.organizer.OrganizerRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.organizer_subevent.OrganizerSubeventRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.site.dtos.*;
import br.edu.ifsp.spo.eventos.eventplatformbackend.site.mappers.EventSiteMapper;
import br.edu.ifsp.spo.eventos.eventplatformbackend.site.mappers.SubeventSiteMapper;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.Subevent;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.SubeventRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/for-site")
@AllArgsConstructor
public class SiteController {
    private final EventRepository eventRepository;
    private final EventSiteMapper eventSiteMapper;
    private final SubeventRepository subeventRepository;
    private final SubeventSiteMapper subeventSiteMapper;
    private final OrganizerRepository organizerRepository;
    private final OrganizerSubeventRepository organizerSubeventRepository;
    private final ActivityRepository activityRepository;

    @Cacheable(value = "events")
    @GetMapping("events")
    public ResponseEntity<List<EventSiteDto>> home() {
        return ResponseEntity.ok(eventSiteMapper.to(eventRepository.findAllByStatus(EventStatus.PUBLISHED)));
    }

    @Cacheable(value = "event", key = "#eventSlug")
    @GetMapping("events/{eventSlug}")
    public ResponseEntity<EventSiteDto> event(@PathVariable String eventSlug) {
        Optional<Event> optionalEvent = eventRepository.findBySlugAndStatus(eventSlug, EventStatus.PUBLISHED);
        if(optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(eventSiteMapper.to(optionalEvent.get()));
    }

    @Cacheable(value = "event_subevents", key = "#eventSlug")
    @GetMapping("events/{eventSlug}/sub-events")
    public ResponseEntity<List<SubeventSiteDto>> subEventList(@PathVariable String eventSlug) {
        return ResponseEntity.ok(
            subeventSiteMapper.to(subeventRepository.findAllByEventSlugAndStatus(eventSlug, EventStatus.PUBLISHED))
        );
    }

    @Cacheable(value = "subevent", key = "#subeventSlug")
    @GetMapping("events/{eventSlug}/sub-events/{subeventSlug}")
    public ResponseEntity<SubeventSiteDto> subEvent(@PathVariable String eventSlug, @PathVariable String subeventSlug) {
        Optional<Subevent> subeventOptional =
            subeventRepository.findByEventSlugAndSlugAndStatus(eventSlug, subeventSlug, EventStatus.PUBLISHED);

        if(subeventOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(subeventSiteMapper.to(subeventOptional.get()));
    }

    @Cacheable(value = "event_organizers", key = "#eventId")
    @GetMapping("events/{eventId}/organizers")
    public ResponseEntity<List<OrganizerSiteDto>> eventOrganizers(@PathVariable UUID eventId) {
        return ResponseEntity.ok(organizerRepository.findAllOrganizerByEventId(eventId));
    }

    @Cacheable(value = "subevent_organizers", key = "#subeventId")
    @GetMapping("events/{eventId}/sub-events/{subeventId}/organizers")
    public ResponseEntity<List<OrganizerSubEventSiteDto>> subeventOrganizers(@PathVariable UUID eventId, @PathVariable UUID subeventId) {
        return ResponseEntity.ok(organizerSubeventRepository.findAllOrganizerBySubEventId(subeventId));
    }

    record SessionSiteDto(
        UUID eventId,
        UUID subEventId,
        UUID activityId,
        String activityTitle,
        String activitySlug,
        ActivityType activityType,
        ActivityModality activityModality,
        String activityDescription,
        Set<String> speakers,
        UUID sessionId,
        String sessionTitle,
        UUID sessionScheduleId,
        LocalDateTime sessionScheduleExecutionStart,
        LocalDateTime sessionScheduleExecutionEnd
    ) {}

    record SessionsGroupByDate(String day, List<SessionSiteDto> sessions) {}

    @Cacheable(value = "event_activities", key = "#eventId")
    @GetMapping("events/{eventId}/activities")
    public ResponseEntity<List<SessionsGroupByDate>> eventActivities(@PathVariable UUID eventId) {
        List<ActivitySiteDto> sessions = activityRepository.findAllForSiteByEventId(eventId);

        List<SessionsGroupByDate> sessionsGroupByDate = sessions.stream()
            .collect(Collectors.groupingBy(ActivitySiteDto::getSessionScheduleExecutionStartDate))
            .entrySet().stream().map(entry ->
                new SessionsGroupByDate(
                    entry.getKey().toString(),
                    entry.getValue().stream().map(s ->
                        new SessionSiteDto(
                            s.getEventId(),
                            s.getSubEventId(),
                            s.getActivityId(),
                            s.getActivityTitle(),
                            s.getActivitySlug(),
                            s.getActivityType(),
                            s.getActivityModality(),
                            s.getActivityDescription(),
                            getSpeakersMap(sessions).get(s.getActivityId()),
                            s.getSessionId(),
                            s.getSessionTitle(),
                            s.getSessionScheduleId(),
                            s.getSessionScheduleExecutionStart(),
                            s.getSessionScheduleExecutionEnd()
                        )
                    )
                    .collect(Collectors.toSet())
                    .stream().sorted(Comparator.comparing(SessionSiteDto::sessionScheduleExecutionStart))
                    .collect(Collectors.toList())
                )
            )
            .sorted(Comparator.comparing(SessionsGroupByDate::day))
            .collect(Collectors.toList());

        return ResponseEntity.ok(sessionsGroupByDate);
    }

    @Cacheable(value = "subevent_activities", key = "#subeventId")
    @GetMapping("events/{eventId}/sub-events/{subeventId}/activities")
    public ResponseEntity<List<SessionsGroupByDate>> subeventActivities(@PathVariable UUID eventId, @PathVariable UUID subeventId) {
        List<ActivitySiteDto> sessions = activityRepository.findAllForSiteByEventIdAndSubeventId(eventId, subeventId);

        List<SessionsGroupByDate> sessionsGroupByDate = sessions.stream()
            .collect(Collectors.groupingBy(ActivitySiteDto::getSessionScheduleExecutionStartDate))
            .entrySet().stream().map(entry ->
                new SessionsGroupByDate(
                    entry.getKey().toString(),
                    entry.getValue().stream().map(s ->
                        new SessionSiteDto(
                            s.getEventId(),
                            s.getSubEventId(),
                            s.getActivityId(),
                            s.getActivityTitle(),
                            s.getActivitySlug(),
                            s.getActivityType(),
                            s.getActivityModality(),
                            s.getActivityDescription(),
                            getSpeakersMap(sessions).get(s.getActivityId()),
                            s.getSessionId(),
                            s.getSessionTitle(),
                            s.getSessionScheduleId(),
                            s.getSessionScheduleExecutionStart(),
                            s.getSessionScheduleExecutionEnd()
                        )
                    ).collect(Collectors.toSet())
                     .stream().sorted(Comparator.comparing(SessionSiteDto::sessionScheduleExecutionStart))
                     .collect(Collectors.toList())
                )
            )
            .sorted(Comparator.comparing(SessionsGroupByDate::day))
            .collect(Collectors.toList());

        return ResponseEntity.ok(sessionsGroupByDate);
    }


    @Cacheable(value = "event_activity", key = "#activitySlug")
    @GetMapping("events/{eventId}/activities/{activitySlug}")
    public ResponseEntity<?> eventActivity(@PathVariable UUID eventId, @PathVariable String activitySlug) {
        List<SessionSiteQueryDto> sessions = activityRepository.findByEventIdAndSlugForSite(eventId, activitySlug);

        ActivityForSiteDto activity = sessions.stream()
            .map(s ->
                s.toActivityForSiteDto(
                    getSpeakersMapForActivity(sessions).get(s.getActivityId()),
                    getSessionsMap(sessions).get(s.getActivityId())
                )
            )
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException(ResourceName.ACTIVITY, activitySlug));

        return ResponseEntity.ok(activity);
    }

    @Cacheable(value = "subevent_activity", key = "#activitySlug")
    @GetMapping("events/{eventId}/sub-events/{subeventId}/activities/{activitySlug}")
    public ResponseEntity<?> subeventActivity(@PathVariable UUID eventId, @PathVariable UUID subeventId, @PathVariable String activitySlug) {
        List<SessionSiteQueryDto> sessions = activityRepository.findBySubEventIdAndSlugForSite(eventId, subeventId, activitySlug);

        ActivityForSiteDto activity = sessions.stream()
            .map(s ->
                s.toActivityForSiteDto(
                    getSpeakersMapForActivity(sessions).get(s.getActivityId()),
                    getSessionsMap(sessions).get(s.getActivityId())
                )
            )
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException(ResourceName.ACTIVITY, activitySlug));

        return ResponseEntity.ok(activity);
    }

    private Map<UUID, Set<String>> getSpeakersMapForActivity(List<SessionSiteQueryDto> sessions) {
        return sessions.stream()
            .collect(
                Collectors.groupingBy(
                    SessionSiteQueryDto::getActivityId,
                    Collectors.mapping(SessionSiteQueryDto::getSpeakerName, Collectors.toSet())
                )
            );
    }

    private Map<UUID, Set<String>> getSpeakersMap(List<ActivitySiteDto> sessions) {
        return sessions.stream()
            .collect(
                Collectors.groupingBy(
                    ActivitySiteDto::getActivityId,
                    Collectors.mapping(ActivitySiteDto::getSpeakerName, Collectors.toSet())
                )
            );
    }

    private Map<UUID, Set<SessionForSiteDto>> getSessionsMap(List<SessionSiteQueryDto> sessions) {
        return sessions.stream()
            .collect(
                Collectors.groupingBy(
                    SessionSiteQueryDto::getActivityId,
                    Collectors.mapping(
                        a -> a.toSessionForSiteDto(getSessionsScheduleMap(sessions).get(a.getSessionId())),
                        Collectors.toSet()
                    )
                )
            );
    }

    private Map<UUID, Set<SessionScheduleForSiteDto>> getSessionsScheduleMap(List<SessionSiteQueryDto> sessions) {
        return sessions.stream()
            .collect(
                Collectors.groupingBy(
                    SessionSiteQueryDto::getSessionId,
                    Collectors.mapping(
                        SessionSiteQueryDto::toSessionScheduleForSiteDto,
                        Collectors.toSet()
                    )
                )
            );
    }
}

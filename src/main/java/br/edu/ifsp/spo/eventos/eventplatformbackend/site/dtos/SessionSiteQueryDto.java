package br.edu.ifsp.spo.eventos.eventplatformbackend.site.dtos;

import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.ActivityModality;
import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.ActivityType;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionSiteQueryDto {
    UUID eventId;
    UUID subEventId;
    UUID activityId;
    String activityTitle;
    String activitySlug;
    ActivityType activityType;
    EventStatus activityStatus;
    ActivityModality activityModality;
    boolean needRegistration;
    Integer setupTime;
    Integer duration;
    String activityDescription;
    String speakerName;
    UUID sessionId;
    String sessionTitle;
    Integer sessionSeats;
    UUID sessionScheduleId;
    LocalDateTime sessionScheduleExecutionStart;
    LocalDateTime sessionScheduleExecutionEnd;
    String sessionScheduleUrl;
    String sessionLocationName;
    String sessionAreaName;
    String sessionSpaceName;

    public ActivityForSiteDto toActivityForSiteDto(Set<String> speakers, Set<SessionForSiteDto> sessions) {
        return new ActivityForSiteDto(
            this.getActivityId(),
            this.getActivityTitle(),
            this.getActivitySlug(),
            this.getActivityDescription(),
            this.getActivityType(),
            this.getActivityStatus(),
            this.getActivityModality(),
            this.isNeedRegistration(),
            this.getSetupTime(),
            this.getDuration(),
            speakers,
            sessions
        );
    }

    public SessionForSiteDto toSessionForSiteDto(Set<SessionScheduleForSiteDto> sessionSchedules) {
        return new SessionForSiteDto(
            this.getSessionId(),
            this.getSessionTitle(),
            this.getSessionSeats(),
            sessionSchedules
        );
    }

    public SessionScheduleForSiteDto toSessionScheduleForSiteDto() {
        return new SessionScheduleForSiteDto(
            this.getSessionScheduleId(),
            this.getSessionScheduleExecutionStart(),
            this.getSessionScheduleExecutionEnd(),
            this.getSessionScheduleUrl(),
            this.getSessionLocationName(),
            this.getSessionAreaName(),
            this.getSessionSpaceName()
        );
    }
}

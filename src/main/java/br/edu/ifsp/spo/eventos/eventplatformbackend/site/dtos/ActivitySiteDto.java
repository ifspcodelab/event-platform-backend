package br.edu.ifsp.spo.eventos.eventplatformbackend.site.dtos;

import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.ActivityModality;
import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivitySiteDto {
    UUID eventId;
    UUID subEventId;
    UUID activityId;
    String activityTitle;
    String activitySlug;
    ActivityType activityType;
    ActivityModality activityModality;
    String activityDescription;
    String speakerName;
    UUID sessionId;
    String sessionTitle;
    UUID sessionScheduleId;
    LocalDateTime sessionScheduleExecutionStart;
    LocalDateTime sessionScheduleExecutionEnd;

    public LocalDate getSessionScheduleExecutionStartDate() {
        return sessionScheduleExecutionStart.toLocalDate();
    }
}

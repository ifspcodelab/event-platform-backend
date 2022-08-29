package br.edu.ifsp.spo.eventos.eventplatformbackend.activity;

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
    UUID activityid;
    String activityTitle;
    String activitySlug;
    ActivityType activityType;
    String activityDescription;
    String speakerName;
    String sessionTitle;
    UUID sessionScheduleId;
    LocalDateTime sessionScheduleExecutionStart;
    LocalDateTime sessionScheduleExecutionEnd;

    public LocalDate getSessionScheduleExecutionStartDate() {
        return sessionScheduleExecutionStart.toLocalDate();
    }

}

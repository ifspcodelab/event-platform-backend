package br.edu.ifsp.spo.eventos.eventplatformbackend.attendance;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("attendance")
@Getter
@Setter
public class AttendanceConfig {
    private Integer periodInDaysToRegisterAttendance;
}

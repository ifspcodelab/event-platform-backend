package br.edu.ifsp.spo.eventos.eventplatformbackend.attendance;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events/{eventId}")
@AllArgsConstructor
@CrossOrigin ( "*" )
public class AttendanceController {
    private final AttendanceService attendanceService;
    private final AttendanceMapper attendanceMapper;

    @PostMapping("/activities/{activityId}/sessions/{sessionId}/sessions-schedules/{sessionScheduleId}/attendances")
    public ResponseEntity<AttendanceDto> create(@PathVariable UUID eventId, @PathVariable UUID activityId, @PathVariable UUID sessionId, @PathVariable UUID sessionScheduleId, @RequestBody AttendanceCreateDto attendanceCreateDto) {
        Attendance attendance = attendanceService.create(eventId, activityId, sessionId, sessionScheduleId, attendanceCreateDto);
        AttendanceDto attendanceDto = attendanceMapper.to(attendance);
        return new ResponseEntity<>(attendanceDto, HttpStatus.CREATED);
    }
}

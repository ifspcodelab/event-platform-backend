package br.edu.ifsp.spo.eventos.eventplatformbackend.attendance;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceName;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.ResourceNotFoundException;
import br.edu.ifsp.spo.eventos.eventplatformbackend.registration.Registration;
import br.edu.ifsp.spo.eventos.eventplatformbackend.registration.RegistrationRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.SessionSchedule;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.SessionScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final RegistrationRepository registrationRepository;
    private final SessionScheduleRepository sessionScheduleRepository;
    private final AttendanceRepository attendanceRepository;

    public Attendance create(UUID eventId, UUID activityId, UUID sessionId, UUID sessionScheduleId, AttendanceCreateDto dto) {
        Attendance attendance = new Attendance(getRegistration(dto.getRegistrationId()), getSessionSchedule(sessionScheduleId) );
        return attendanceRepository.save(attendance);
    }

    public void delete(UUID eventId, UUID activityId, UUID sessionId, UUID sessionScheduleId, UUID attendanceId) {
        Attendance attendance = getAttendance(attendanceId);
        attendanceRepository.delete(attendance);
    }

    private Attendance getAttendance(UUID attendanceId) {
        return attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.ATTENDANCE, attendanceId));
    }

    private Registration getRegistration(UUID registrationId) {
        return registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.REGISTRATION, registrationId));
    }

    private SessionSchedule getSessionSchedule(UUID sessionScheduleId) {
        return sessionScheduleRepository.findById(sessionScheduleId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.SESSION_SCHEDULE, sessionScheduleId)); // mudar reso name
    }
}

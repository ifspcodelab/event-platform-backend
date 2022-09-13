package br.edu.ifsp.spo.eventos.eventplatformbackend.attendance;

import br.edu.ifsp.spo.eventos.eventplatformbackend.activity.Activity;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions.*;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus;
import br.edu.ifsp.spo.eventos.eventplatformbackend.registration.Registration;
import br.edu.ifsp.spo.eventos.eventplatformbackend.registration.RegistrationRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.registration.RegistrationStatus;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.Session;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.SessionSchedule;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.SessionScheduleRepository;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.Subevent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final RegistrationRepository registrationRepository;
    private final SessionScheduleRepository sessionScheduleRepository;
    private final AttendanceRepository attendanceRepository;

    public Attendance create(UUID eventId, UUID activityId, UUID sessionId, UUID sessionScheduleId, AttendanceCreateDto dto) {
        SessionSchedule sessionSchedule = getSessionSchedule(sessionScheduleId);
        checksIfSessionIsAssociateToSessionSchedules(sessionId, sessionSchedule);
        checksIfActivityIsAssociateToSession(activityId, sessionSchedule.getSession());
        checksIfEventIsAssociateToActivity(eventId, sessionSchedule.getSession().getActivity());
        checkIfActivityIsCanceled(sessionSchedule.getSession().getActivity());
        checkIfSessionIsCancelled(sessionSchedule.getSession());

        Registration registration = getRegistration(dto.registrationId);

        if(attendanceRepository.existsByRegistrationIdAndSessionScheduleId(registration.getId(), sessionScheduleId)) {
            throw new BusinessRuleException(BusinessRuleType.ATTENDANCE_ALREADY_EXISTS); // mudar para uma exception ?
        }

        if(registration.getRegistrationStatus() != RegistrationStatus.CONFIRMED) {
            throw new BusinessRuleException(BusinessRuleType.ATTENDANCE_CREATE_WITH_REGISTRATION_STATUS_NOT_CONFIRMED);
        }

        Attendance attendance = new Attendance(registration, getSessionSchedule(sessionScheduleId));
        return attendanceRepository.save(attendance);
    }

    public void delete(UUID eventId, UUID activityId, UUID sessionId, UUID sessionScheduleId, UUID attendanceId) {
        Attendance attendance = getAttendance(attendanceId);
        attendanceRepository.delete(attendance);
    }

    public List<Attendance> findAll(UUID eventId, UUID activityId, UUID sessionId, UUID sessionScheduleId) {
        return attendanceRepository.findAllBySessionScheduleId(sessionScheduleId);
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

    private void checkIfEventIsAssociateToSubevent(UUID eventId, Subevent subevent) {
        if (!subevent.getEvent().getId().equals(eventId)) {
            throw new ResourceReferentialIntegrityException(ResourceName.SUBEVENT, ResourceName.EVENT);
        }
    }

    private void checksIfSubeventIsAssociateToActivity(UUID subeventId, Activity activity) {
        if (!activity.getSubevent().getId().equals(subeventId)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_IS_NOT_ASSOCIATED_TO_SUBEVENT);
        }
    }

    private void checksIfEventIsAssociateToActivity(UUID eventId, Activity activity) {
        if (!activity.getEvent().getId().equals(eventId)) {
            throw new BusinessRuleException(BusinessRuleType.ACTIVITY_IS_NOT_ASSOCIATED_TO_EVENT);
        }
    }

    private void checksIfActivityIsAssociateToSession(UUID activityId, Session session) {
        if (!session.getActivity().getId().equals(activityId)) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_IS_NOT_ASSOCIATED_TO_ACTIVITY);
        }
    }

    private void checksIfSessionIsAssociateToSessionSchedules(UUID sessionId, SessionSchedule sessionScheduleId) {
        if (!sessionScheduleId.getSession().getId().equals(sessionId)) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_IS_NOT_ASSOCIATED_TO_ACTIVITY); // mudar business rule
        }
    }

    private void checkIfActivityIsCanceled(Activity activity) {
        if(activity.getStatus() == EventStatus.CANCELED) {
            throw new BusinessRuleException(BusinessRuleType.ATTENDANCE_CREATE_WITH_ACTIVITY_CANCELED);
        }
    }

    private void checkIfSessionIsCancelled(Session session) {
        if(session.isCanceled()) {
            throw new BusinessRuleException(BusinessRuleType.ATTENDANCE_CREATE_WITH_CANCELED_SESSION);
        }
    }
}

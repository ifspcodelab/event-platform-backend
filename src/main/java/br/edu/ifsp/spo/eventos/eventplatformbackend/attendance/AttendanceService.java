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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {
    private final RegistrationRepository registrationRepository;
    private final SessionScheduleRepository sessionScheduleRepository;
    private final AttendanceRepository attendanceRepository;
    private final AttendanceConfig attendanceConfig;

    public Attendance create(UUID eventId, UUID activityId, UUID sessionId, UUID sessionScheduleId, AttendanceCreateDto dto) {
        SessionSchedule sessionSchedule = getSessionSchedule(sessionScheduleId);
        Registration registration = getRegistration(dto.registrationId);
        checksIfSessionIsAssociateToRegistration(sessionId, registration);
        checksIfSessionIsAssociateToSessionSchedules(sessionId, sessionSchedule);
        checksIfActivityIsAssociateToSession(activityId, sessionSchedule.getSession());
        checksIfEventIsAssociateToActivity(eventId, sessionSchedule.getSession().getActivity());
        checkIfActivityIsCanceled(sessionSchedule.getSession().getActivity());
        checkIfSessionIsCancelled(sessionSchedule.getSession());

        if(attendanceRepository.existsByRegistrationIdAndSessionScheduleId(registration.getId(), sessionScheduleId)) {
            throw new BusinessRuleException(BusinessRuleType.ATTENDANCE_ALREADY_EXISTS);
        }

        if(registration.getRegistrationStatus() != RegistrationStatus.CONFIRMED) {
            throw new BusinessRuleException(BusinessRuleType.ATTENDANCE_CREATE_WITH_REGISTRATION_STATUS_NOT_CONFIRMED);
        }

        if(sessionSchedule.getExecutionStart().toLocalDate().isAfter(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.ATTENDANCE_CREATE_WITH_SESSION_SCHEDULE_NOT_STARTED);
        }

        if(sessionSchedule.getSession().getActivity().getEvent().getExecutionPeriod().getEndDate().plusDays(attendanceConfig.getPeriodInDaysToRegisterAttendance()).isBefore(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.ATTENDANCE_CREATE_AFTER_EVENT_EXECUTION_PERIOD);
        }

        Attendance attendance = new Attendance(registration, getSessionSchedule(sessionScheduleId));
        return attendanceRepository.save(attendance);
    }

    public Attendance create(UUID eventId, UUID subeventId, UUID activityId, UUID sessionId, UUID sessionScheduleId, AttendanceCreateDto dto) {
        SessionSchedule sessionSchedule = getSessionSchedule(sessionScheduleId);
        Registration registration = getRegistration(dto.registrationId);
        checksIfSessionIsAssociateToRegistration(sessionId, registration);
        checksIfSessionIsAssociateToSessionSchedules(sessionId, sessionSchedule);
        checksIfActivityIsAssociateToSession(activityId, sessionSchedule.getSession());
        checksIfSubeventIsAssociateToActivity(subeventId, sessionSchedule.getSession().getActivity());
        checkIfEventIsAssociateToSubevent(eventId, sessionSchedule.getSession().getActivity().getSubevent());
        checkIfActivityIsCanceled(sessionSchedule.getSession().getActivity());
        checkIfSessionIsCancelled(sessionSchedule.getSession());

        if(attendanceRepository.existsByRegistrationIdAndSessionScheduleId(registration.getId(), sessionScheduleId)) {
            throw new BusinessRuleException(BusinessRuleType.ATTENDANCE_ALREADY_EXISTS);
        }

        if(registration.getRegistrationStatus() != RegistrationStatus.CONFIRMED) {
            throw new BusinessRuleException(BusinessRuleType.ATTENDANCE_CREATE_WITH_REGISTRATION_STATUS_NOT_CONFIRMED);
        }

        if(sessionSchedule.getExecutionStart().toLocalDate().isAfter(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.ATTENDANCE_CREATE_WITH_SESSION_SCHEDULE_NOT_STARTED);
        }

         if(sessionSchedule.getSession().getActivity().getSubevent().getExecutionPeriod().getEndDate().plusDays(attendanceConfig.getPeriodInDaysToRegisterAttendance()).isBefore(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.ATTENDANCE_CREATE_AFTER_SUBEVENT_EXECUTION_PERIOD);
        }

        Attendance attendance = new Attendance(registration, getSessionSchedule(sessionScheduleId));
        return attendanceRepository.save(attendance);
    }

    public void delete(UUID eventId, UUID activityId, UUID sessionId, UUID sessionScheduleId, UUID attendanceId) {
        SessionSchedule sessionSchedule = getSessionSchedule(sessionScheduleId);
        Attendance attendance = getAttendance(attendanceId);
        checksIfSessionIsAssociateToRegistration(sessionId, attendance.getRegistration());
        checksIfSessionIsAssociateToSessionSchedules(sessionId, sessionSchedule);
        checksIfActivityIsAssociateToSession(activityId, sessionSchedule.getSession());
        checksIfEventIsAssociateToActivity(eventId, sessionSchedule.getSession().getActivity());
        checkIfActivityIsCanceled(sessionSchedule.getSession().getActivity());
        checkIfSessionIsCancelled(sessionSchedule.getSession());

        if(sessionSchedule.getExecutionStart().toLocalDate().isAfter(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.ATTENDANCE_CREATE_WITH_SESSION_SCHEDULE_NOT_STARTED);
        }

        if(sessionSchedule.getSession().getActivity().getEvent().getExecutionPeriod().getEndDate().plusDays(attendanceConfig.getPeriodInDaysToRegisterAttendance()).isBefore(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.ATTENDANCE_DELETE_AFTER_EVENT_EXECUTION_END);
        }

        log.info("Attendance removed: id={}, account name={}", attendanceId, attendance.getRegistration().getAccount().getName());
        attendanceRepository.delete(attendance);
    }

    public void delete(UUID eventId, UUID subeventId, UUID activityId, UUID sessionId, UUID sessionScheduleId, UUID attendanceId) {
        SessionSchedule sessionSchedule = getSessionSchedule(sessionScheduleId);
        Attendance attendance = getAttendance(attendanceId);
        checksIfSessionIsAssociateToRegistration(sessionId, attendance.getRegistration());
        checksIfSessionIsAssociateToSessionSchedules(sessionId, sessionSchedule);
        checksIfActivityIsAssociateToSession(activityId, sessionSchedule.getSession());
        checksIfSubeventIsAssociateToActivity(subeventId, sessionSchedule.getSession().getActivity());
        checkIfEventIsAssociateToSubevent(eventId, sessionSchedule.getSession().getActivity().getSubevent());
        checkIfActivityIsCanceled(sessionSchedule.getSession().getActivity());
        checkIfSessionIsCancelled(sessionSchedule.getSession());

        if(sessionSchedule.getExecutionStart().toLocalDate().isAfter(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.ATTENDANCE_CREATE_WITH_SESSION_SCHEDULE_NOT_STARTED);
        }

        if(sessionSchedule.getSession().getActivity().getSubevent().getExecutionPeriod().getEndDate().plusDays(attendanceConfig.getPeriodInDaysToRegisterAttendance()).isBefore(LocalDate.now())) {
            throw new BusinessRuleException(BusinessRuleType.ATTENDANCE_DELETE_AFTER_SUBEVENT_EXECUTION_END);
        }

        log.info("Attendance removed: id={}, account name={}", attendanceId, attendance.getRegistration().getAccount().getName());
        attendanceRepository.delete(attendance);
    }

    public List<Attendance> findAll(UUID eventId, UUID activityId, UUID sessionId, UUID sessionScheduleId) {
        SessionSchedule sessionSchedule = getSessionSchedule(sessionScheduleId);
        checksIfSessionIsAssociateToSessionSchedules(sessionId, sessionSchedule);
        checksIfActivityIsAssociateToSession(activityId, sessionSchedule.getSession());
        checksIfEventIsAssociateToActivity(eventId, sessionSchedule.getSession().getActivity());
        checkIfActivityIsCanceled(sessionSchedule.getSession().getActivity());
        checkIfSessionIsCancelled(sessionSchedule.getSession());

        return attendanceRepository.findAllBySessionScheduleId(sessionScheduleId);
    }

    public List<Attendance> findAll(UUID eventId, UUID subeventId, UUID activityId, UUID sessionId, UUID sessionScheduleId) {
        SessionSchedule sessionSchedule = getSessionSchedule(sessionScheduleId);
        checksIfSessionIsAssociateToSessionSchedules(sessionId, sessionSchedule);
        checksIfActivityIsAssociateToSession(activityId, sessionSchedule.getSession());
        checksIfSubeventIsAssociateToActivity(subeventId, sessionSchedule.getSession().getActivity());
        checkIfEventIsAssociateToSubevent(eventId, sessionSchedule.getSession().getActivity().getSubevent());
        checkIfActivityIsCanceled(sessionSchedule.getSession().getActivity());
        checkIfSessionIsCancelled(sessionSchedule.getSession());

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
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.SESSION_SCHEDULE, sessionScheduleId));
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

    private void checksIfSessionIsAssociateToSessionSchedules(UUID sessionId, SessionSchedule sessionSchedule) {
        if (!sessionSchedule.getSession().getId().equals(sessionId)) {
            throw new BusinessRuleException(BusinessRuleType.SESSION_IS_NOT_ASSOCIATED_TO_SESSION_SCHEDULE);
        }
    }

    private void checksIfSessionIsAssociateToRegistration(UUID sessionId, Registration registration) {
        if (!registration.getSession().getId().equals(sessionId)) {
            throw new BusinessRuleException(BusinessRuleType.REGISTRATION_IS_NOT_ASSOCIATED_TO_SESSION);
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

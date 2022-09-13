package br.edu.ifsp.spo.eventos.eventplatformbackend.attendance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {
    List<Attendance> findAllBySessionScheduleId(UUID sessionSchedule);
    boolean existsByRegistrationIdAndSessionScheduleId(UUID registrationId, UUID sessionScheduleId);
}

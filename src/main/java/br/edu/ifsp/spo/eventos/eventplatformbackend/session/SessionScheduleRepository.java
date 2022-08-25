package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SessionScheduleRepository extends JpaRepository<SessionSchedule, UUID> {
    List<SessionSchedule> findAllBySpaceIdAndExecutionStartGreaterThanEqual(UUID spaceId, LocalDateTime now);
}
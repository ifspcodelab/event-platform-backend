package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SessionScheduleRepository extends JpaRepository<SessionSchedule, UUID> {
    // TODO - retornar apenas execution start e end, colocando em uma classe
//    @Query("select u from SessionSchedule u where u.space.id = :spaceId")
//    List<SessionSchedule> findAllWithSpaceId(UUID spaceId);

    List<SessionSchedule> findAllBySpaceIdAndExecutionStartGreaterThanEqual(UUID spaceId, LocalDateTime now);
}

// 10/09  11/09  12/09
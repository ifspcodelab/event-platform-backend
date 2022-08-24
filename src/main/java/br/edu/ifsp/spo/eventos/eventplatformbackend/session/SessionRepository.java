package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {
    @Query("select u from Session u join fetch u.sessionsSchedules where u.activity.id = :activityId")
    List<Session> findAllByActivityId(@Param("activityId") UUID activityId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Session s where s.id = :id")
    Optional<Session> findByIdWithPessimisticLock(UUID id);
}

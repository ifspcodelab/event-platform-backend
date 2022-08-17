package br.edu.ifsp.spo.eventos.eventplatformbackend.session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {
    @Query("select u from Session u join fetch u.sessionsSchedules where u.activity.id = :activityId")
    List<Session> findAllByActivityId(@Param("activityId") UUID activityId);
    boolean existsByTitleIgnoreCaseAndActivityId(String title, UUID activityId);
}

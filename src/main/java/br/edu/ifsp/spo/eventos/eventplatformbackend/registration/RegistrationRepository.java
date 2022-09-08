package br.edu.ifsp.spo.eventos.eventplatformbackend.registration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, UUID> {
    @Query("select count(r) > 0 from Registration r join Activity as a on a.id = r.session.activity.id where a.id = ?2 and r.account.id = ?1 " +
            "and (r.registrationStatus = br.edu.ifsp.spo.eventos.eventplatformbackend.registration.RegistrationStatus.CONFIRMED " +
            "or r.registrationStatus = br.edu.ifsp.spo.eventos.eventplatformbackend.registration.RegistrationStatus.WAITING_CONFIRMATION)")
    boolean existsByAccountIdAndActivityId(UUID accountId, UUID activityId);
    boolean existsBySessionIdAndAccountIdAndRegistrationStatusIn(UUID sessionId, UUID accountId, List<RegistrationStatus> registrationStatus);
    boolean existsBySessionIdAndRegistrationStatus(UUID sessionId, RegistrationStatus registrationStatus);
    List<Registration> findAllByAccountIdAndRegistrationStatusIn(UUID accountId, List<RegistrationStatus> registrationsStatus);
    @Query("select distinct (r) from Registration r join Session as s on s.id = r.session.id join SessionSchedule as ss on ss.session.id = s.id where r.account.id = ?1 and ss.executionStart > ?2 and r.registrationStatus in ?3")
    List<Registration> findAllByAccountIdAndRegistrationStatusInAndDate(UUID accountId, LocalDateTime now, List<RegistrationStatus> registrationsStatus);
    @Query("select distinct (r) from Registration r join Session as s on s.id = r.session.id join SessionSchedule as ss on ss.session.id = s.id where ss.executionStart > ?1 and r.registrationStatus = ?2")
    List<Registration> findAllByRegistrationStatus(LocalDateTime now, RegistrationStatus registrationStatus);
    List<Registration> findAllBySessionId(UUID sessionId);
    Optional<Registration> getFirstBySessionIdAndRegistrationStatusOrderByDate(UUID sessionId, RegistrationStatus registrationStatus);
    List<Registration> findAllByAccountIdAndSessionIdIn(UUID accountId, List<UUID> sessionsId);
    List<Registration> findAllByAccountId(UUID accountId);

    @Query("SELECT DISTINCT new br.edu.ifsp.spo.eventos.eventplatformbackend.registration.AccountEventQueryDto(evt.id, evt.title, sub.id, sub.title) \n" +
        "FROM Event evt " +
        "INNER JOIN Activity act ON act.event = evt " +
        "LEFT JOIN Subevent sub ON act.subevent = sub " +
        "INNER JOIN Session ses ON act = ses.activity " +
        "INNER JOIN Registration reg ON ses = reg.session " +
        "INNER JOIN Account acc ON reg.account = acc " +
        "WHERE acc.id = ?1")
    List<AccountEventQueryDto> findEventsByAccount(UUID accountId);


}

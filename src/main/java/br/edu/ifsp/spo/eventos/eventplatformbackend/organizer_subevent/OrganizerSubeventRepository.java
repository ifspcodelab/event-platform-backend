package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer_subevent;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.Session;
import br.edu.ifsp.spo.eventos.eventplatformbackend.site.dtos.OrganizerSubEventSiteDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.Subevent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrganizerSubeventRepository extends JpaRepository<OrganizerSubevent, UUID> {
    List<OrganizerSubevent> findAllBySubeventId(UUID subeventId);
    boolean existsByAccountAndSubeventId(Account account, UUID subeventId);

    @Query("SELECT new br.edu.ifsp.spo.eventos.eventplatformbackend.site.dtos.OrganizerSubEventSiteDto(o.id, a.name, o.type)\n" +
        "FROM Subevent se\n" +
        "JOIN OrganizerSubevent o ON se.id = o.subevent.id\n" +
        "JOIN Account a on a.id = o.account.id\n" +
        "WHERE se.id = ?1\n" +
        "ORDER BY a.name")
    List<OrganizerSubEventSiteDto> findAllOrganizerBySubEventId(UUID subeventId);

    @Query("select distinct (se) from Subevent se join OrganizerSubevent as os on os.subevent = se.id where os.account.id = :accountId")
    List<Subevent> findAllSubeventsByAccountId(UUID accountId);

    boolean existsByAccountId(UUID id);

    @Query("select distinct (os.subevent.id) from OrganizerSubevent os where os.account.id = :accountId")
    List<UUID> findAllSubeventIdByAccountId(UUID accountId);

    @Query("select distinct (s) from Session s \n" +
            "join fetch s.sessionSchedules schedules \n" +
            "join fetch schedules.location \n" +
            "join fetch schedules.area \n" +
            "join fetch schedules.space \n" +
            "join Activity a on s.activity.id = a.id \n" +
            "join OrganizerSubevent os on a.subevent.id = os.subevent.id \n" +
            "where os.account.id = :accountId")
    List<Session> findAllSessionsByAccountId(UUID accountId);
}

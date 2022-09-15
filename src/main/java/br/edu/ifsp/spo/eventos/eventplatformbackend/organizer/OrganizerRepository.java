package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.session.Session;
import br.edu.ifsp.spo.eventos.eventplatformbackend.site.dtos.OrganizerSiteDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrganizerRepository extends JpaRepository<Organizer, UUID> {
    boolean existsByAccountAndEventId(Account account, UUID eventId);
    List<Organizer> findAllByEventId(UUID eventId);

    @Query("SELECT new br.edu.ifsp.spo.eventos.eventplatformbackend.site.dtos.OrganizerSiteDto(o.id, a.name, o.type)\n" +
        "FROM Event e\n" +
        "JOIN Organizer o ON e.id = o.event.id\n" +
        "JOIN Account a on a.id = o.account.id\n" +
        "WHERE e.id = ?1\n" +
        "ORDER BY a.name")
    List<OrganizerSiteDto> findAllOrganizerByEventId(UUID eventId);

    @Query("select distinct (e) from Event e join Organizer as o on o.event = e.id where o.account.id = :accountId")
    List<Event> findAllEventsByAccountId(UUID accountId);

    @Query("select o.event.id from Organizer o where o.account.id = :accountId and o.type = :organizerType")
    List<String> findAllEventIdsByAccountIdAndOrganizerType(UUID accountId, OrganizerType organizerType);

    @Query("select distinct (s) from Session s \n" +
            "join fetch s.sessionSchedules schedules \n" +
            "join fetch schedules.location \n" +
            "join fetch schedules.area \n" +
            "join fetch schedules.space \n" +
            "join fetch s.activity a \n" +
            "join fetch a.event \n" +
            "join Organizer o on a.event.id = o.event.id \n" +
            "where o.account.id = :accountId \n" +
            "and a.event.id = :eventId \n" +
            "and a.subevent.id is null")
    List<Session> findAllSessionsByAccountIdAndEventId(UUID accountId, UUID eventId);
}

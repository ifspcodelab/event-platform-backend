package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrganizerRepository extends JpaRepository<Organizer, UUID> {
    boolean existsByAccountAndEventId(Account account, UUID eventId);
    List<Organizer> findAllByEventId(UUID eventId);

    @Query("SELECT new br.edu.ifsp.spo.eventos.eventplatformbackend.organizer.OrganizerSiteDto(o.id, a.name, o.type)\n" +
        "FROM Event e\n" +
        "JOIN Organizer o ON e.id = o.event.id\n" +
        "JOIN Account a on a.id = o.account.id\n" +
        "WHERE e.id = ?1\n" +
        "ORDER BY a.name")
    List<OrganizerSiteDto> findAllOrganizerByEventId(UUID eventId);
}

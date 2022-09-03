package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer_subevent;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.site.dtos.OrganizerSubEventSiteDto;
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
}

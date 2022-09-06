package br.edu.ifsp.spo.eventos.eventplatformbackend.organizer_authorization;

import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import br.edu.ifsp.spo.eventos.eventplatformbackend.organizer.Organizer;
import br.edu.ifsp.spo.eventos.eventplatformbackend.subevent.Subevent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrganizerAuthorizationRepository extends JpaRepository<Organizer, UUID> {
    @Query("select distinct (e) from Event e join Organizer as o on o.event = e.id where o.account.id = :accountId")
    List<Event> findAllEventsByOrganizerAccountId(UUID accountId);
}

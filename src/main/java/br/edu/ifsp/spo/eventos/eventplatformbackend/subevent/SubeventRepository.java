package br.edu.ifsp.spo.eventos.eventplatformbackend.subevent;

import br.edu.ifsp.spo.eventos.eventplatformbackend.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface SubeventRepository extends JpaRepository<Subevent, UUID> {
    boolean existsByTitleAndEvent(String title, Event event);
    boolean existsBySlugAndEvent(String Slug, Event event);
    boolean existsByEventId(UUID eventId);
}

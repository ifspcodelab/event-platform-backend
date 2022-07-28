package br.edu.ifsp.spo.eventos.eventplatformbackend.subevent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubeventRepository extends JpaRepository<Subevent, UUID> {
    boolean existsByTitleAndEventId(String title, UUID eventId);
    boolean existsByTitleAndEventIdAndIdNot(String title, UUID eventId, UUID subeventId);
    boolean existsBySlugAndEventId(String Slug, UUID eventId);
    boolean existsBySlugAndEventIdAndIdNot(String Slug, UUID eventId, UUID subeventId);
    List<Subevent> findAllByEventId(UUID eventId);
    boolean existsByEventId(UUID eventId);
}

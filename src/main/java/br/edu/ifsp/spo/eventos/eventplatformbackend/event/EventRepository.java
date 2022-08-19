package br.edu.ifsp.spo.eventos.eventplatformbackend.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    boolean existsByTitle(String title);
    boolean existsByTitleAndIdNot(String title, UUID eventId);
    boolean existsBySlug(String slug);
    boolean existsBySlugAndIdNot(String slug, UUID eventId);
    Optional<Event> findEventBySlug(String slug);
}

package br.edu.ifsp.spo.eventos.eventplatformbackend.activity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, UUID> {
    List<Activity> findAllByEventId (UUID eventId);
    boolean existsByTitleIgnoreCaseAndEventId(String title, UUID eventId);
    boolean existsByTitleIgnoreCaseAndEventIdAndIdNot(String title, UUID eventId, UUID activityId);
    boolean existsBySlugAndEventId(String slug, UUID eventId);
    boolean existsBySlugAndEventIdAndIdNot(String Slug, UUID eventId, UUID activityId);
}

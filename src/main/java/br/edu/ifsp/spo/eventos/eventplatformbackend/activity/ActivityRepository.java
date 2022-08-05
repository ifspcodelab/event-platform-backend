package br.edu.ifsp.spo.eventos.eventplatformbackend.activity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, UUID> {
    List<Activity> findAllByEventIdAndSubeventNull(UUID eventId);
    List<Activity> findAllBySubeventId(UUID subeventId);
    boolean existsByTitleIgnoreCaseAndEventId(String title, UUID eventId);
    boolean existsByTitleIgnoreCaseAndSubeventId(String title, UUID subeventId);
    boolean existsByTitleIgnoreCaseAndEventIdAndIdNot(String title, UUID eventId, UUID activityId);
    boolean existsByTitleIgnoreCaseAndSubeventIdAndIdNot(String title, UUID subeventId, UUID activityId);
    boolean existsBySlugAndEventId(String slug, UUID eventId);
    boolean existsBySlugAndSubeventId(String slug, UUID subeventId);
    boolean existsBySlugAndEventIdAndIdNot(String Slug, UUID eventId, UUID activityId);
    boolean existsBySlugAndSubeventIdAndIdNot(String Slug, UUID subeventId, UUID activityId);
}
package br.edu.ifsp.spo.eventos.eventplatformbackend.activity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

//    @Query("SELECT new br.edu.ifsp.spo.eventos.eventplatformbackend.activity.ActivitySiteDto(a.id, a.title, a.slug) FROM Activity a WHERE a.event.id = ?1 AND a.subevent.id = NULL")
//    List<ActivitySiteDto> findAllForSiteByEventId(UUID eventId);

    @Query("SELECT new br.edu.ifsp.spo.eventos.eventplatformbackend.activity.ActivitySiteDto(act.id, act.title, act.slug, act.type, act.description, spe.name, ses.title, sch.id, sch.executionStart, sch.executionEnd) \n" +
           "FROM Activity act\n" +
           "JOIN ActivitySpeaker act_spe ON act = act_spe.activity\n" +
           "JOIN Speaker spe ON act_spe.speaker = spe\n" +
           "JOIN Session ses ON act = ses.activity\n" +
           "JOIN SessionSchedule sch ON ses = sch.session\n" +
           "WHERE act.event.id = ?1 AND act.subevent = null\n" +
           "ORDER BY sch.executionStart, act.title, ses.title, spe.name")
    List<ActivitySiteDto> findAllForSiteByEventId(UUID eventId);

    @Query("SELECT new br.edu.ifsp.spo.eventos.eventplatformbackend.activity.ActivitySiteDto(act.id, act.title, act.slug, act.type, spe.name, act.description, ses.title, sch.id, sch.executionStart, sch.executionEnd) \n" +
           "FROM Activity act\n" +
           "JOIN ActivitySpeaker act_spe ON act = act_spe.activity\n" +
           "JOIN Speaker spe ON act_spe.speaker = spe\n" +
           "JOIN Session ses ON act = ses.activity\n" +
           "JOIN SessionSchedule sch ON ses = sch.session\n" +
           "WHERE act.event.id = ?1 AND act.subevent.id = ?2\n" +
           "ORDER BY sch.executionStart, act.title, ses.title, spe.name")
    List<ActivitySiteDto> findAllForSiteByEventIdAndSubeventId(UUID eventId, UUID subeventId);

}

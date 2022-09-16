package br.edu.ifsp.spo.eventos.eventplatformbackend.activity;

import br.edu.ifsp.spo.eventos.eventplatformbackend.site.dtos.ActivitySiteDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.site.dtos.SessionSiteQueryDto;
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

    @Query("SELECT new br.edu.ifsp.spo.eventos.eventplatformbackend.site.dtos.ActivitySiteDto(act.event.id, act.subevent.id, act.id, act.title, act.slug, act.type, act.modality, act.description, spe.name, ses.id, ses.title, sch.id, sch.executionStart, sch.executionEnd) \n" +
           "FROM Activity act\n" +
           "JOIN ActivitySpeaker act_spe ON act = act_spe.activity\n" +
           "JOIN Speaker spe ON act_spe.speaker = spe\n" +
           "JOIN Session ses ON act = ses.activity\n" +
           "JOIN SessionSchedule sch ON ses = sch.session\n" +
           "WHERE act.event.id = ?1 AND act.subevent = null AND act.status = br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus.PUBLISHED AND ses.canceled = false")
    List<ActivitySiteDto> findAllForSiteByEventId(UUID eventId);

    @Query("SELECT new br.edu.ifsp.spo.eventos.eventplatformbackend.site.dtos.ActivitySiteDto(act.event.id, act.subevent.id, act.id, act.title, act.slug, act.type, act.modality, act.description, spe.name, ses.id, ses.title, sch.id, sch.executionStart, sch.executionEnd) \n" +
           "FROM Activity act\n" +
           "JOIN ActivitySpeaker act_spe ON act = act_spe.activity\n" +
           "JOIN Speaker spe ON act_spe.speaker = spe\n" +
           "JOIN Session ses ON act = ses.activity\n" +
           "JOIN SessionSchedule sch ON ses = sch.session\n" +
           "WHERE act.event.id = ?1 AND act.subevent.id = ?2 AND act.status = br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus.PUBLISHED AND ses.canceled = false")
    List<ActivitySiteDto> findAllForSiteByEventIdAndSubeventId(UUID eventId, UUID subeventId);


    @Query("SELECT new br.edu.ifsp.spo.eventos.eventplatformbackend.site.dtos.SessionSiteQueryDto(act.event.id, act.subevent.id, act.id, act.title, act.slug, act.type, act.status, act.modality, act.needRegistration, act.setupTime, act.duration, act.description, spe.name,  ses.id, ses.title, ses.seats, ses.confirmedSeats, sch.id, sch.executionStart, sch.executionEnd, sch.url, loc.name, loc.address, are.name, spa.name, spa.type) \n" +
        "FROM Activity act\n" +
        "INNER JOIN ActivitySpeaker act_spe ON act = act_spe.activity\n" +
        "INNER JOIN Speaker spe ON act_spe.speaker = spe\n" +
        "INNER JOIN Session ses ON act = ses.activity\n" +
        "INNER JOIN SessionSchedule sch ON ses = sch.session\n" +
        "LEFT JOIN Location loc ON sch.location = loc\n" +
        "LEFT JOIN Area are ON sch.area = are\n" +
        "LEFT JOIN Space spa ON sch.space = spa\n" +
        "WHERE act.event.id = ?1 AND act.subevent = null AND act.status = br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus.PUBLISHED AND act.slug = ?2 AND ses.canceled = false")
    List<SessionSiteQueryDto> findByEventIdAndSlugForSite(UUID eventId, String activitySlug);

    @Query("SELECT new br.edu.ifsp.spo.eventos.eventplatformbackend.site.dtos.SessionSiteQueryDto(act.event.id, act.subevent.id, act.id, act.title, act.slug, act.type, act.status, act.modality, act.needRegistration, act.setupTime, act.duration, act.description, spe.name,  ses.id, ses.title, ses.seats, ses.confirmedSeats, sch.id, sch.executionStart, sch.executionEnd, sch.url, loc.name, loc.address, are.name, spa.name, spa.type) \n" +
        "FROM Activity act\n" +
        "INNER JOIN ActivitySpeaker act_spe ON act = act_spe.activity\n" +
        "INNER JOIN Speaker spe ON act_spe.speaker = spe\n" +
        "INNER JOIN Session ses ON act = ses.activity\n" +
        "INNER JOIN SessionSchedule sch ON ses = sch.session\n" +
        "LEFT JOIN Location loc ON sch.location = loc\n" +
        "LEFT JOIN Area are ON sch.area = are\n" +
        "LEFT JOIN Space spa ON sch.space = spa\n" +
        "WHERE act.event.id = ?1 AND act.subevent.id = ?2 AND act.status = br.edu.ifsp.spo.eventos.eventplatformbackend.event.EventStatus.PUBLISHED AND act.slug = ?3 AND ses.canceled = false")
    List<SessionSiteQueryDto> findBySubEventIdAndSlugForSite(UUID eventId, UUID subEventId, String activitySlug);
}

package br.edu.ifsp.spo.eventos.eventplatformbackend.activity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ActivitySpeakerRepository extends JpaRepository<ActivitySpeaker, UUID> {
    List<ActivitySpeaker> findAllByActivityId(UUID activityId);
    boolean existsBySpeakerIdAndActivityId(UUID speakerId, UUID activityId);
}

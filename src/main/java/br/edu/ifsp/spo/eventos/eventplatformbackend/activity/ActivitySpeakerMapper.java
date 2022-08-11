package br.edu.ifsp.spo.eventos.eventplatformbackend.activity;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ActivitySpeakerMapper {
    ActivitySpeakerDto to(ActivitySpeaker activitySpeaker);
    List<ActivitySpeakerDto> to(List<ActivitySpeaker> activitySpeakers);
}

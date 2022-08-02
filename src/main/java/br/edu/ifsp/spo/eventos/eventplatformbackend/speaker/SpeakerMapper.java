package br.edu.ifsp.spo.eventos.eventplatformbackend.speaker;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SpeakerMapper {
    SpeakerDto to(Speaker speaker);
    List<SpeakerDto> to(List<Speaker> speakers);
}

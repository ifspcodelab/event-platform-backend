package br.edu.ifsp.spo.eventos.eventplatformbackend.activity;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ActivityMapper {
    ActivityDto to(Activity activity);
    List<ActivityDto> to(List<Activity> activities);
}

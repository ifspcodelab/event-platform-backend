package br.edu.ifsp.spo.eventos.eventplatformbackend.activity;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActivityMapper {
    ActivityDto to (Activity activity);
}

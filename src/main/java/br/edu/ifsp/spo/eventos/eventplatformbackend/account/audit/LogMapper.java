package br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LogMapper {
    LogDto to(Log log);
    List<LogDto> to(List<Log> logs);
}

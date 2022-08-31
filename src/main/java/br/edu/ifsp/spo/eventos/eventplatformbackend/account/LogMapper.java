package br.edu.ifsp.spo.eventos.eventplatformbackend.account;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.audit.Log;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.dto.LogDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LogMapper {
    List<LogDto> to(List<Log> logs);
}

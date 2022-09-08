package br.edu.ifsp.spo.eventos.eventplatformbackend.attendance;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {
    AttendanceDto to(Attendance attendance);
}

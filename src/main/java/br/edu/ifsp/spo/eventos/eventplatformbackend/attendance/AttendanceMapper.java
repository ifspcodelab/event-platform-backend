package br.edu.ifsp.spo.eventos.eventplatformbackend.attendance;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {
    AttendanceDto to(Attendance attendance);
    List<AttendanceDto> to(List<Attendance> attendances);
}

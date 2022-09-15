package br.edu.ifsp.spo.eventos.eventplatformbackend.attendance;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {
    @Mapping(source = "sessionSchedule.session.id", target = "sessionId")
    @Mapping(source = "sessionSchedule.id", target = "sessionScheduleId")
    AttendanceDto to(Attendance attendance);
    List<AttendanceDto> to(List<Attendance> attendances);
}

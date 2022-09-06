package br.edu.ifsp.spo.eventos.eventplatformbackend.registration;

import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RegistrationMapper {
    RegistrationDto to(Registration registration);
    List<RegistrationDto> to(List<Registration> registrations);
}

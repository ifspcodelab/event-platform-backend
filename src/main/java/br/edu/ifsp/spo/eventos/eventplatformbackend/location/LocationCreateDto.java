package br.edu.ifsp.spo.eventos.eventplatformbackend.location;

import lombok.Value;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Value
public class LocationCreateDto {
    @NotNull
    @NotBlank
    @Size(min = 4, max = 150)
    String name;

    @NotNull
    @NotBlank
    @Size(min = 20, max = 300)
    String address;

    public String getName() {
        return name.strip();
    }
}

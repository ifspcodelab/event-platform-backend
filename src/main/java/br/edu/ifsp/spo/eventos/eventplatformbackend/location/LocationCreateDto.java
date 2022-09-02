package br.edu.ifsp.spo.eventos.eventplatformbackend.location;

import lombok.Value;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Value
public class LocationCreateDto {
    @NotNull
    @NotBlank
    @Size(min = 1, max = 200)
    String name;

    @NotNull
    @NotBlank
    @Size(min = 10, max = 300)
    String address;

    public String getName() {
        return name.strip();
    }

    public String getAddress() {
        return address.strip();
    }
}

package br.edu.ifsp.spo.eventos.eventplatformbackend.space;

import lombok.Value;

import javax.validation.Valid;
import javax.validation.constraints.*;

@Value
public class SpaceCreateDto {
    @NotNull
    @NotBlank
    @Size (min = 1, max = 100)
    String name;
    @NotNull
    @Min(value = 2)
    @Max(value = 9999)
    Integer capacity;
    @Valid
    @NotNull
    SpaceType type;

    public String getName() {
        return name.strip();
    }
}

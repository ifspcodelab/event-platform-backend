package br.edu.ifsp.spo.eventos.eventplatformbackend.area;

import lombok.Value;

import javax.validation.constraints.*;

@Value
public class AreaCreateDto {
    @NotNull
    @NotBlank
    @Size(min = 4, max = 80)
    String name;

    @Size(min = 4, max = 150)
    String reference;

    public String getName() {
        return name.strip();
    }

    public String getReference() {
        return reference != null ? reference.strip() : null;
    }
}

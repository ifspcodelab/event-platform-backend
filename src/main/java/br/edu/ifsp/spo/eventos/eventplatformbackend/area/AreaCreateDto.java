package br.edu.ifsp.spo.eventos.eventplatformbackend.area;

import lombok.Value;

import javax.validation.constraints.*;

@Value
public class AreaCreateDto {
    @NotNull
    @NotBlank
    @Size(min = 1, max = 200)
    String name;

    @Size(min = 1, max = 200)
    String reference;

    public String getName() {
        return name.strip();
    }

    public String getReference() {
        return reference != null ? reference.strip() : null;
    }
}

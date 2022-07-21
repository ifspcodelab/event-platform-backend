package br.edu.ifsp.spo.eventos.eventplatformbackend.area;

import lombok.Value;

import javax.validation.constraints.*;

@Value
public class AreaCreateDto {

    @NotNull
    @NotBlank
    @Size(min = 4, max = 80)
    String name;

    //TODO: definir regexp que aceita apenas letras e números
    @Size(min = 4, max = 150)
    String reference;
}

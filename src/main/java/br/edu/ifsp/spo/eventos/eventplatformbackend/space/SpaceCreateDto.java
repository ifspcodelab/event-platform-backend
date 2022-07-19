package br.edu.ifsp.spo.eventos.eventplatformbackend.space;

import lombok.Value;

import javax.validation.constraints.*;

@Value
public class SpaceCreateDto {

    @NotNull
    @NotBlank
    @Size (min=4, max=100)
    @Pattern(regexp = "^[0-9A-Za-záàâãéèêíóôõúçñÁÀÂÃÉÈÍÓÔÕÚÇ,.\\s-]*$", message = "Campo nome deve conter apenas letras e números")
    String name;
    @NotNull
    @Min(value = 2)
    @Max(value = 9999)
    Integer capacity;
    //TODO: Validar tipos que não existem
    @NotNull
    SpaceType type;

}

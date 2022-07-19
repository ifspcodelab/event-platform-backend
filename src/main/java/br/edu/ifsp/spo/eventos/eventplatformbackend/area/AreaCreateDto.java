package br.edu.ifsp.spo.eventos.eventplatformbackend.area;

import lombok.Value;

import javax.validation.constraints.*;

@Value
public class AreaCreateDto {
    @NotNull
    @NotBlank
    @Size(min = 4, max = 80)
    @Pattern(regexp="^[0-9A-Za-záàâãéèêíóôõúçñÁÀÂÃÉÈÍÓÔÕÚÇ,:.-[\\s]]*$", message = "Campo nome deve conter apenas letras e números")
    String name;

    //TODO: definir regexp que aceita apenas letras e números
    @Pattern(regexp="^[0-9A-Za-záàâãéèêíóôõúçñÁÀÂÃÉÈÍÓÔÕÚÇ,:.-[\\s]]*$", message = "Campo referência deve conter apenas letras e números")
    @Size(min = 4, max = 150)
    String reference;
}

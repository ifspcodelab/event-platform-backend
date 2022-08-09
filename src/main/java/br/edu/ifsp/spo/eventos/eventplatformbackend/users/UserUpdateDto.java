package br.edu.ifsp.spo.eventos.eventplatformbackend.users;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.Name;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Getter
@Setter
public class UserUpdateDto {

    @NotNull
    @NotBlank
    @Size(min = 5, max = 256)
    private String role;
    private Boolean verified;
}

package br.edu.ifsp.spo.eventos.eventplatformbackend.speaker;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.Name;
import lombok.Value;
import org.hibernate.validator.constraints.URL;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Value
public class SpeakerCreateDto {
    @NotNull
    @NotBlank
    @Name
    @Size(min = 5, max = 256)
    String name;
    @NotNull
    @Email
    @Size(max = 350)
    String email;
    @NotNull
    @CPF
    String cpf;
    @NotNull
    @NotBlank
    @Size(min = 150, max = 300)
    String curriculum;
    @URL
    String lattes;
    @URL
    String linkedin;
    @NotNull
    @NotBlank
    @Size(min = 8)
    String phoneNumber;

    public String getCpf() {
        return cpf.replace(".", "").replace("-", "");
    }
}

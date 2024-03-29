package br.edu.ifsp.spo.eventos.eventplatformbackend.common.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CancellationMessageCreateDto {
    @NotNull
    @NotBlank
    @Size(min = 5)
    String reason;

    public String getReason() {
        return reason.strip();
    }
}

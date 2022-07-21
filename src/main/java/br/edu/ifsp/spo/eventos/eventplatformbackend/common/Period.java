package br.edu.ifsp.spo.eventos.eventplatformbackend.common;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.ValidPeriod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Embeddable;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@ValidPeriod
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Period {
    @NotNull
    @FutureOrPresent
    LocalDate startDate;
    @NotNull
    @FutureOrPresent
    LocalDate endDate;
}

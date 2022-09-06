package br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Embeddable;
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
    LocalDate startDate;
    @NotNull
    LocalDate endDate;

    public boolean started() {
        return startDate.isEqual(LocalDate.now()) || startDate.isBefore(LocalDate.now());
    }

    public boolean ended() {
        return endDate.isBefore(LocalDate.now());
    }

    public boolean todayIsWithinThePeriod() {
        return started() && !ended();
    }

    public boolean todayIsOutOfPeriod() {
        return !started() || ended();
    }
}

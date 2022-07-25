package br.edu.ifsp.spo.eventos.eventplatformbackend.common.validators;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.Period;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.assertj.core.api.Assertions.assertThat;
import javax.validation.*;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class ValidPeriodValidatorTest {
    Validator validator;

    @BeforeEach
    public void before() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @ParameterizedTest()
    @MethodSource
    void periodsValid(String startDate, String endDate) {
        Period period = new Period(LocalDate.parse(startDate), LocalDate.parse(endDate));

        Set<ConstraintViolation<Period>> violations = validator.validate(period);

        assertThat(violations).hasSize(0);
    }

    private static Stream<Arguments> periodsValid() {
        return Stream.of(
            arguments("2022-03-01", "2022-03-01"),
            arguments("2022-03-01", "2022-03-05")
        );
    }

    @Test
    void periodInvalid() {
        Period period = new Period(LocalDate.parse("2022-03-01"), LocalDate.parse("2022-02-28"));

        Set<ConstraintViolation<Period>> violations = validator.validate(period);

        assertThat(violations).hasSize(1);
    }
}

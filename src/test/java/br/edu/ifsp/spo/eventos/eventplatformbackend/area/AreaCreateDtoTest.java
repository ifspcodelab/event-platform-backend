package br.edu.ifsp.spo.eventos.eventplatformbackend.area;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.*;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class AreaCreateDtoTest {
    Validator validator;

    @BeforeEach
    public void before() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    //AAA
    @Test
    public void areaCreateDtoValidReference() {
        // Arrange
        AreaCreateDto areaCreateDto = new AreaCreateDto("Bloco C", "Piso Superior");

        // Act
        Set<ConstraintViolation<AreaCreateDto>> violations = validator.validate(areaCreateDto);

        // Assert
        assertThat(violations).hasSize(0);
    }

    @Test
    public void areaCreateDtoInvalidReferenceOnlySpaces() {
        // Arrange
        AreaCreateDto areaCreateDto = new AreaCreateDto("Bloco C", "   ");

        // Act
        Set<ConstraintViolation<AreaCreateDto>> violations = validator.validate(areaCreateDto);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .map(Path::toString)
                .first()
                .isEqualTo("reference");
    }

    @ParameterizedTest(name = "Valid reference value {0}")
    @ValueSource(strings = { "Piso superior", "Piso superior 5.", "Piso-superior", "Piso: superior", "448, 98" })
    void areaCreateDtoValidReference(String reference) {
        // Arrange
        AreaCreateDto areaCreateDto = new AreaCreateDto("Bloco C", reference);

        // Act
        Set<ConstraintViolation<AreaCreateDto>> violations = validator.validate(areaCreateDto);

        // Assert
        assertThat(violations).hasSize(0);
    }

    @ParameterizedTest(name = "Invalid reference value {0}")
    @ValueSource(strings = { "   ", "$#@ *", "Piso superior?", "Piso superior!" })
    void areaCreateDtoInvalidReference(String reference) {
        // Arrange
        AreaCreateDto areaCreateDto = new AreaCreateDto("Bloco C", reference);

        // Act
        Set<ConstraintViolation<AreaCreateDto>> violations = validator.validate(areaCreateDto);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .map(Path::toString)
                .first()
                .isEqualTo("reference");
    }
}

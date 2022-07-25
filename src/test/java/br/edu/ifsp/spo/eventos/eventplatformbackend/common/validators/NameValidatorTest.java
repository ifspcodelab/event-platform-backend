package br.edu.ifsp.spo.eventos.eventplatformbackend.common.validators;

import br.edu.ifsp.spo.eventos.eventplatformbackend.area.AreaCreateDto;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.Name;
import lombok.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.*;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Value
class TesteNameAnnotation {
    @Name
    String name;
}

public class NameValidatorTest {
    Validator validator;

    @BeforeEach
    public void before() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @ParameterizedTest(name = "Valid name value {0}")
    @ValueSource(strings = { "Maria da Silva", "Shu Li", "Dawn O'Porter", "Claude Lévi-Strauss"})
    void nameValid(String name) {
        // Arrange
        TesteNameAnnotation testeNameAnnotation = new TesteNameAnnotation(name);

        // Act
        Set<ConstraintViolation<TesteNameAnnotation>> violations = validator.validate(testeNameAnnotation);

        // Assert
        assertThat(violations).hasSize(0);
    }

    @ParameterizedTest(name = "Valid name value {0}")
    @ValueSource(strings = { "Maria 123", "Shu Li?", "Dawn O'Porter!", "@ClaudeLévi-Strauss", "", "   "})
    void nameInvalid(String name) {
        // Arrange
        TesteNameAnnotation testeNameAnnotation = new TesteNameAnnotation(name);

        // Act
        Set<ConstraintViolation<TesteNameAnnotation>> violations = validator.validate(testeNameAnnotation);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations)
                .extracting(ConstraintViolation::getPropertyPath)
                .map(Path::toString)
                .first()
                .isEqualTo("name");
    }
}

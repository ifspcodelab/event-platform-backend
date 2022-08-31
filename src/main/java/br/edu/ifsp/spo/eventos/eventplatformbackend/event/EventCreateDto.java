package br.edu.ifsp.spo.eventos.eventplatformbackend.event;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.Period;
import lombok.Value;
import org.hibernate.validator.constraints.URL;
import javax.validation.Valid;
import javax.validation.constraints.*;

@Value
public class EventCreateDto {
    @NotNull
    @NotBlank
    @Size(min = 3, max = 100)
    String title;
    @NotNull
    @NotBlank
    String slug;
    @NotNull
    @NotBlank
    @Size(min = 50, max = 150)
    String summary;
    @NotNull
    @NotBlank
    @Size(min = 100, max = 5000)
    String presentation;
    @NotNull
    @NotBlank
    @Size(min = 50, max = 5000)
    String contact;
    @NotNull
    @Valid
    Period registrationPeriod;
    @NotNull
    @Valid
    Period executionPeriod;
    @URL
    String smallerImage;
    @URL
    String biggerImage;

    public String getTitle() {
        return title.strip();
    }

    public String getSlug() {
        return slug.strip();
    }

    public String getSummary() {
        return summary.strip();
    }

    public String getPresentation() {
        return presentation.strip();
    }

    public String getContact() {
        return contact.strip();
    }
}

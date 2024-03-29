package br.edu.ifsp.spo.eventos.eventplatformbackend.subevent;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.Period;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.Slug;
import lombok.Value;
import org.hibernate.validator.constraints.URL;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Value
public class SubeventCreateDto {
    @NotNull
    @NotBlank
    @Size(min = 3, max = 100)
    String title;
    @NotNull
    @NotBlank
    @Slug
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
    @Valid
    Period executionPeriod;
    @NotNull
    @URL
    String smallerImage;
    @NotNull
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

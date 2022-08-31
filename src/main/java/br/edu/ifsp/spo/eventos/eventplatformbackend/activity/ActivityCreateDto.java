package br.edu.ifsp.spo.eventos.eventplatformbackend.activity;

import lombok.Value;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Value
public class ActivityCreateDto {
    @NotNull
    @NotBlank
    @Size(min = 5, max = 200)
    String title;
    @NotNull
    @NotBlank
    String slug;
    @NotNull
    @NotBlank
    @Size(min = 50, max = 5000)
    String description;
    @Valid
    @NotNull
    ActivityType type;
    @NotNull
    ActivityModality modality;
    @NotNull
    boolean needRegistration;
    @NotNull
    @Min(value = 10)
    Integer duration;
    @NotNull
    @Min(value = 5)
    Integer setupTime;

    public String getTitle() {
        return title.strip();
    }

    public String getSlug() {
        return slug.strip();
    }

    public String getDescription() {
        return description.strip();
    }
}

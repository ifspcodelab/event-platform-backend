package br.edu.ifsp.spo.eventos.eventplatformbackend.space;

import javax.validation.constraints.*;

public class SpaceCreateDto {

    @NotNull
    @NotBlank
    @Size (min=4, max=100)
    private String name;
    @NotNull
    @Min(value = 2)
    @Max(value = 9999)
    private Integer capacity;
    //TODO: Validar tipos que não existem
    @NotNull
    private SpaceType type;

    public SpaceCreateDto(String name, Integer capacity, SpaceType type) {
        this.name = name;
        this.capacity = capacity;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public SpaceType getType() {
        return type;
    }

    public void setType(SpaceType type) {
        this.type = type;
    }
}

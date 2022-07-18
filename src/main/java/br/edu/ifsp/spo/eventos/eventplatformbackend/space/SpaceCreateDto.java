package br.edu.ifsp.spo.eventos.eventplatformbackend.space;

import javax.validation.constraints.*;

public class SpaceCreateDto {

    @NotNull
    @NotBlank
    @Size (min=4, max=100)
    private String name;
    @NotNull
    @Min(2)
    @Max(9999)
    private int capacity;
    @NotNull
    @NotBlank
    private String type;

    public SpaceCreateDto(String name, int capacity, String type) {
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

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

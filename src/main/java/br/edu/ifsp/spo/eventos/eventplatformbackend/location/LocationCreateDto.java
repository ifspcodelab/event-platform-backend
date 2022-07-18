package br.edu.ifsp.spo.eventos.eventplatformbackend.location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class LocationCreateDto {
    @NotNull
    @NotBlank
    @Size(min = 4, max = 150)
    private String name;

    @NotNull
    @NotBlank
    @Size(min = 20, max = 300)
    private String address;

    public LocationCreateDto(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

package br.edu.ifsp.spo.eventos.eventplatformbackend.space;

import java.util.UUID;

public class SpaceDto {

    private UUID id;
    private String name;
    private Integer capacity;
    private String type;

    public SpaceDto(UUID id, String name, Integer capacity, String type) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.type = type;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

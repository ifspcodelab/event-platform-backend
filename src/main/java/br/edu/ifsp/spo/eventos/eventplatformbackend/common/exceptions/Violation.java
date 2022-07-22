package br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions;

public class Violation {
    private String name;
    private String message;

    public Violation(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

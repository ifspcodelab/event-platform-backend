package br.edu.ifsp.spo.eventos.eventplatformbackend.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResourceName {
    AREA("Area"),
    SPACE("Space");

    private String name;
}

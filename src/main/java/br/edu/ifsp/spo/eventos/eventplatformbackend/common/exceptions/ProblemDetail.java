package br.edu.ifsp.spo.eventos.eventplatformbackend.common.exceptions;

import lombok.Value;

import java.util.List;

@Value
public class ProblemDetail {
    String title;
    List<Violation> violations;
}

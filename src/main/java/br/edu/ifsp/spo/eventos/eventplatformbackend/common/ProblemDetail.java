package br.edu.ifsp.spo.eventos.eventplatformbackend.common;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProblemDetail {
    String title;
    List<Violation> violations;
}

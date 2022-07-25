package br.edu.ifsp.spo.eventos.eventplatformbackend.common.security;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/tests")
public class TestController {
    @GetMapping
    public String test(){
        return "test";
    }
}
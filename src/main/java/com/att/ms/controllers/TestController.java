package com.att.ms.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class TestController {


    @GetMapping("/diHola")
    public String diHola(){
        return "{\"messge\":\"Hola\"}";
    }

}

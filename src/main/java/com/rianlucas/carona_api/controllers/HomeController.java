package com.rianlucas.carona_api.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/home")
public class HomeController {
    
    @GetMapping
    public String index() {
        return "Welcome to Carona API!";
    }

}

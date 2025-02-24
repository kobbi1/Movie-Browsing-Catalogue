package com.team18.MBC.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController  // Converts the controller into a REST API (returns JSON)
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public ResponseEntity<?> home() {
        return ResponseEntity.ok(Map.of("message", "Welcome to the Movie Browsing Catalogue API"));
    }
}

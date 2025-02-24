package com.team18.MBC.Controllers;

import com.team18.MBC.core.Actor;
import com.team18.MBC.Services.ActorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController  // Converts the controller into a REST API (returns JSON)
@RequestMapping("/actors")
public class ActorController {
    private final ActorService actorService;

    public ActorController(ActorService actorService) {
        this.actorService = actorService;
    }

    // Get all actors
    @GetMapping
    public ResponseEntity<List<Actor>> getAllActors() {
        return ResponseEntity.ok(actorService.getAllActors());
    }

    // Get an actor by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getActorById(@PathVariable Long id) {
        Actor actor = actorService.getActorsById(id);
        return actor != null ? ResponseEntity.ok(actor) : ResponseEntity.notFound().build();
    }
}

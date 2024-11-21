package com.team18.MBC.Controllers;

import com.team18.MBC.core.Actor;
import com.team18.MBC.Services.ActorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/actors")
public class ActorController {
    private ActorService actorService;



    public ActorController(ActorService actorService) {
        this.actorService = actorService;
    }

    @GetMapping
    public String getAllActors(Model model) {
        List<Actor> actors = actorService.getAllActors();
        model.addAttribute("actors", actors);
        return "actors";
    }

    @GetMapping("/{id}")
    public String getActorsById(@PathVariable Long id , Model model) {
        Actor actor = actorService.getActorsById(id);
        model.addAttribute("actor",actor);
        return "actor";
    }
}
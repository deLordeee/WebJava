package org.example.cosmocats.controller;



import org.example.cosmocats.service.CosmoCatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cosmocats")
@RequiredArgsConstructor
public class CosmoCatController {

    private final CosmoCatService cosmoCatService;

    @GetMapping
    public List<String> getCosmoCats() {
        return cosmoCatService.getCosmoCats();
    }
}

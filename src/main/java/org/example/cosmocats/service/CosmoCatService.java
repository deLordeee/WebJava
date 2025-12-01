package org.example.cosmocats.service;

import org.example.cosmocats.featuretoggle.FeatureToggles;
import org.example.cosmocats.featuretoggle.annotation.FeatureToggle;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CosmoCatService {

    @FeatureToggle(FeatureToggles.COSMO_CATS)
    public List<String> getCosmoCats() {
        return List.of(
                "Nebula Thing 1",
                "Orbit Thing 2",
                "Star Thing 3"
        );
    }
}
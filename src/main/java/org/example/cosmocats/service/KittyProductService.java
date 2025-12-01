package org.example.cosmocats.service;

import org.example.cosmocats.featuretoggle.FeatureToggles;
import org.example.cosmocats.featuretoggle.annotation.FeatureToggle;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KittyProductService {

    @FeatureToggle(FeatureToggles.KITTY_PRODUCTS)
    public List<String> getKittyProducts() {
        return List.of(
                "Kitty Star Thing 1",
                "Kitty Cosmic Thing 2",
                "Kitty Space Thing 3"
        );
    }
}
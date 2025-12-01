package org.example.cosmocats.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Getter
@ConfigurationProperties(prefix = "feature")
public class FeatureToggleProperties {
    private final CosmoCats cosmoCats;
    private final KittyProducts kittyProducts;

    public FeatureToggleProperties(CosmoCats cosmoCats, KittyProducts kittyProducts) {
        this.cosmoCats = cosmoCats;
        this.kittyProducts = kittyProducts;
    }

    @Getter
    public static class CosmoCats {
        private final boolean enabled;

        public CosmoCats(boolean enabled) {
            this.enabled = enabled;
        }
    }

    @Getter
    public static class KittyProducts {
        private final boolean enabled;

        public KittyProducts(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public Map<String, Boolean> getToggles() {
        return Map.of(
            "cosmo-cats", cosmoCats != null && cosmoCats.isEnabled(),
            "kitty-products", kittyProducts != null && kittyProducts.isEnabled()
        );
    }
}

package org.example.cosmocats.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "feature")
public class FeatureToggleProperties {
    private CosmoCats cosmoCats;
    private KittyProducts kittyProducts;

    @Getter
    @Setter
    public static class CosmoCats {
        private boolean enabled;
    }

    @Getter
    @Setter
    public static class KittyProducts {
        private boolean enabled;
    }

    public Map<String, Boolean> getToggles() {
        Map<String, Boolean> toggles = new HashMap<>();
        toggles.put("cosmo-cats", cosmoCats != null && cosmoCats.isEnabled());
        toggles.put("kitty-products", kittyProducts != null && kittyProducts.isEnabled());
        return toggles;
    }
}

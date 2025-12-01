package org.example.cosmocats.web;

import org.example.cosmocats.AbstractIT;
import org.example.cosmocats.featuretoggle.FeatureToggleExtension;
import org.example.cosmocats.featuretoggle.FeatureToggles;
import org.example.cosmocats.featuretoggle.annotation.DisabledFeatureToggle;
import org.example.cosmocats.featuretoggle.annotation.EnabledFeatureToggle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@DisplayName("CosmoCat Controller IT")
@ExtendWith(FeatureToggleExtension.class)
class CosmoCatControllerIT extends AbstractIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisabledFeatureToggle(FeatureToggles.COSMO_CATS)
    void shouldGet404WhenCosmoCatsFeatureDisabled() throws Exception {
        mockMvc.perform(get("/api/v1/cosmocats"))
                .andExpect(status().isNotFound());
    }

    @Test
    @EnabledFeatureToggle(FeatureToggles.COSMO_CATS)
    void shouldGet200AndCosmoCatsListWhenFeatureEnabled() throws Exception {
        mockMvc.perform(get("/api/v1/cosmocats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Nebula Thing 1"))
                .andExpect(jsonPath("$[1]").value("Orbit Thing 2"))
                .andExpect(jsonPath("$[2]").value("Star Thing 3"));
    }
}

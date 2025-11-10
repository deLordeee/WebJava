package org.example.cosmocats.service;

import org.example.cosmocats.featuretoggle.FeatureToggleService;
import org.example.cosmocats.featuretoggle.FeatureToggles;
import org.example.cosmocats.featuretoggle.aspect.FeatureToggleAspect;
import org.example.cosmocats.featuretoggle.exception.FeatureToggleNotEnabledException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CosmoCatService Unit Tests")
class CosmoCatServiceTest {

    private CosmoCatService cosmoCatService;
    private CosmoCatService proxiedService;

    @Mock
    private FeatureToggleService featureToggleService;

    private FeatureToggleAspect featureToggleAspect;

    @BeforeEach
    void setUp() {
        cosmoCatService = new CosmoCatService();
        featureToggleAspect = new FeatureToggleAspect(featureToggleService);

        // Create a proxy that applies the aspect to our service
        AspectJProxyFactory proxyFactory = new AspectJProxyFactory(cosmoCatService);
        proxyFactory.addAspect(featureToggleAspect);
        proxiedService = proxyFactory.getProxy();
    }

    @Test
    @DisplayName("Should return cosmo cats when feature is enabled")
    void getCosmoCats_WhenEnabled_ReturnsData() throws Throwable {

        when(featureToggleService.check("cosmo-cats")).thenReturn(true);


        List<String> cats = proxiedService.getCosmoCats();


        assertThat(cats).isNotNull();
        assertThat(cats).isNotEmpty();
        assertThat(cats).hasSize(3);
        assertThat(cats).contains(
                "Nebula Thing 1",
                "Orbit Thing 2",
                "Star Thing 3"
        );


        verify(featureToggleService).check("cosmo-cats");
    }

    @Test
    @DisplayName("Should throw exception when feature is disabled")
    void getCosmoCats_WhenDisabled_ThrowsException() {

        when(featureToggleService.check("cosmo-cats")).thenReturn(false);


        assertThatThrownBy(() -> proxiedService.getCosmoCats())
                .isInstanceOf(FeatureToggleNotEnabledException.class)
                .hasMessageContaining("Feature toggle 'cosmo-cats' is not enabled");


        verify(featureToggleService).check("cosmo-cats");
    }


    @Test
    @DisplayName("Service method returns expected data (pure unit test)")
    void getCosmoCats_ReturnsExpectedData() {

        List<String> cats = cosmoCatService.getCosmoCats();


        assertThat(cats).containsExactly(
                "Nebula Thing 1",
                "Orbit Thing 2",
                "Star Thing 3"
        );
    }
}
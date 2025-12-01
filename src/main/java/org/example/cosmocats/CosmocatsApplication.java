package org.example.cosmocats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableConfigurationProperties(FeatureToggleProperties.class)
public class CosmocatsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CosmocatsApplication.class, args);
    }

}

package tn.esprit.autoloc.web;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "AutoLoc API",
                version = "1.0",
                description = "API REST de gestion de la location de véhicules : "
                        + "agences, véhicules, clients, réservations, contrats, paiements, "
                        + "employés, équipements et maintenances."
        )
)
public class OpenApiConfig {
}

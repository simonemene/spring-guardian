package com.example.guardian.core.model;

import java.util.List;

/**
 * Frameworks and architectural signals detected during source scanning.
 *
 * @param usesSpringWeb true when Spring Web or MVC is detected
 * @param usesSpringSecurity true when Spring Security is detected
 * @param usesJpa true when JPA or Spring Data JPA is detected
 * @param usesActuator true when Spring Boot Actuator is detected
 * @param usesValidation true when Bean Validation is detected
 * @param usesOpenApi true when OpenAPI or Swagger annotations are detected
 * @param usesLombok true when Lombok is detected
 * @param usesSpringBatch true when Spring Batch is detected
 * @param hasControllerLayer true when controller classes are detected
 * @param hasServiceLayer true when service classes are detected
 * @param hasRepositoryLayer true when repository classes are detected
 * @param hasDomainLayer true when a domain package is detected
 * @param hasApplicationLayer true when an application package is detected
 * @param hasInfrastructureLayer true when an infrastructure package is detected
 * @param detectedArchitecturalStyles inferred architecture style labels
 * @author Simone Meneghetti
 */
public record ProjectCapabilities(
        boolean usesSpringWeb,
        boolean usesSpringSecurity,
        boolean usesJpa,
        boolean usesActuator,
        boolean usesValidation,
        boolean usesOpenApi,
        boolean usesLombok,
        boolean usesSpringBatch,
        boolean hasControllerLayer,
        boolean hasServiceLayer,
        boolean hasRepositoryLayer,
        boolean hasDomainLayer,
        boolean hasApplicationLayer,
        boolean hasInfrastructureLayer,
        List<String> detectedArchitecturalStyles
) {
}

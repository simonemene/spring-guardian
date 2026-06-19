package com.example.guardian.core.rules;

import com.example.guardian.core.config.GuardianSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for the deterministic Spring Guardian rule set.
 *
 * @author Simone Meneghetti
 */
public final class GuardianRules {

    private GuardianRules() {
    }

    /**
     * Creates the default deterministic rules executed by Spring Guardian.
     *
     * @param settings scanner thresholds and configuration
     * @return default rule list
     */
    public static List<SpringRule> defaultRules(GuardianSettings settings) {
        List<SpringRule> baseRules = new ArrayList<>(List.of(
                new HardcodedConfigRule(),
                new FieldInjectionRule(),
                new ControllerInjectsRepositoryRule(),
                new FatControllerRule(settings),
                new MissingServiceLayerRule(),
                new EntityExposedInControllerRule(),
                new SelfInvocationProxyRule(),
                new InvalidTransactionalUsageRule(),
                new ManualConnectionManagementRule(),
                new MissingRestControllerAdviceRule(),
                new GenericTryCatchRule(),
                new MissingTestsRule(),
                new PathVariableWithoutNameRule(),
                new ApiVersioningMissingRule(settings),
                new PomQualityRule(),
                new NamingConventionRule(),
                new TransactionalOnControllerRule(),
                new ReadOnlyTransactionMissingRule(),
                new ServiceReturnsEntityRule(),
                new OptionalGetWithoutGuardRule(),
                new ConsoleLoggingRule(),
                new LoggerStaticFinalRule(),
                new RequestBodyWithoutValidationRule(),
                new RequestDtoWithoutValidationRule(),
                new NullReturnInServiceOrRepositoryRule(),
                new ManualHttpClientCreationRule(),
                new LayerDependencyViolationRule(),
                new PackageStructureInconsistentRule(),
                new TooManyDependenciesRule(settings),
                new GodClassRule(settings),
                new ComplexServiceMethodRule(settings),
                new DuplicatedMavenDependencyRule(),
                new SpringDependencyVersionOverrideRule(),
                new MissingEnvironmentProfilesRule(),
                new PossibleSecretInConfigRule(),
                new UnsafeDdlAutoRule(),
                new ActuatorExposeAllRule(),
                new CsrfDisabledRule(),
                new PermitAllTooBroadRule(),
                new PasswordEncoderMissingRule(),
                new TestWithoutAssertionsRule(),
                new SpringBootTestOverusedRule(),
                new MockitoTestWithoutVerifyOrAssertRule(),
                new CorsAllowAllWithCredentialsRule(),
                new EmptyCatchBlockRule(),
                new JpaEagerFetchingRule(),
                new RepositoryCallInLoopRule(),
                new GetEndpointMutatesStateRule(),
                new ControllerRawResponseRule(),
                new ThreadSleepInTestRule(),
                new JpaEntityConstructorRule(),
                new JpaToOneLazyRelationshipRule(),
                new DddDomainDependsOnSpringRule(),
                new ServiceDependsOnWebLayerRule(),
                new RepositoryDependsOnUpperLayerRule(),
                new SecurityFilterChainMissingRule(),
                new CsrfDisabledWithoutStatelessRule(),
                new EndpointMissingOpenApiOperationRule(),
                new AllArgsConstructorOnSpringComponentRule(),
                new ConstructorDependencyFieldNotFinalRule(),
                new RestControllerWithoutBaseMappingRule(),
                new ApplicationPropertiesExternalizationRule(),
                new HardcodedActiveSpringProfileRule(),
                new MavenDependencyVersionConflictRule(),
                new MavenMixedStackDependencyRule(),
                new MavenDependencyHygieneRule(),
                new PomGovernanceRules()
        ));
        baseRules.addAll(WebBatchRuleCatalog.webRules());
        baseRules.addAll(WebBatchRuleCatalog.batchRules());
        baseRules.addAll(WebBatchRuleCatalog.sharedReadinessRules());
        baseRules.addAll(WebBatchAdvisorCatalog.rules());
        return List.copyOf(baseRules);
    }
}

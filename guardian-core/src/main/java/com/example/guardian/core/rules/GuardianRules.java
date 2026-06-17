package com.example.guardian.core.rules;

import com.example.guardian.core.config.GuardianSettings;

import java.util.List;

public final class GuardianRules {

    private GuardianRules() {
    }

    public static List<SpringRule> defaultRules(GuardianSettings settings) {
        return List.of(
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
                new ThreadSleepInTestRule()
        );
    }
}

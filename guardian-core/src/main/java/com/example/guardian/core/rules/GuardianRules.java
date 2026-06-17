package com.example.guardian.core.rules;

import com.example.guardian.core.config.GuardianSettings;

import java.util.List;
import java.util.Set;

/**
 * Factory for the deterministic Spring Guardian rule set.
 *
 * @author p15518 - Simone Meneghetti
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
                new ObjectCreationAdvisorRule(
                        "SPR064_MANUAL_OBJECT_MAPPER",
                        com.example.guardian.core.model.Severity.MAJOR,
                        Set.of("ObjectMapper", "XmlMapper"),
                        "ObjectMapper created manually",
                        "Manual ObjectMapper instances can bypass Spring Boot Jackson modules, date handling, naming policies and custom serializers.",
                        "Inject the Boot-managed ObjectMapper or expose a single configured ObjectMapper bean when customization is required.",
                        true
                ),
                new ObjectCreationAdvisorRule(
                        "SPR065_MANUAL_THREAD_CREATION",
                        com.example.guardian.core.model.Severity.MAJOR,
                        Set.of("Thread"),
                        "Thread created manually",
                        "Manual threads bypass Spring lifecycle, task execution configuration, observability and graceful shutdown.",
                        "Use TaskExecutor, ThreadPoolTaskExecutor, @Async or TaskScheduler depending on the use case.",
                        false
                ),
                new StaticMethodAdvisorRule(
                        "SPR066_MANUAL_EXECUTOR_CREATION",
                        com.example.guardian.core.model.Severity.MAJOR,
                        "Executors",
                        Set.of("newFixedThreadPool", "newCachedThreadPool", "newSingleThreadExecutor", "newScheduledThreadPool", "newWorkStealingPool"),
                        "Executor created manually",
                        "Manual executor services can escape Spring lifecycle, shutdown hooks, metrics and central tuning.",
                        "Expose a TaskExecutor or ThreadPoolTaskExecutor bean and inject it where asynchronous work is needed.",
                        false
                ),
                new ObjectCreationAdvisorRule(
                        "SPR067_TIMER_SCHEDULING",
                        com.example.guardian.core.model.Severity.MINOR,
                        Set.of("Timer", "TimerTask"),
                        "Timer-based scheduling detected",
                        "Timer and TimerTask are low-level scheduling APIs and are harder to align with Spring profiles, lifecycle and observability.",
                        "Use @Scheduled or TaskScheduler with explicit configuration and tests.",
                        false
                ),
                new ObjectCreationAdvisorRule(
                        "SPR068_MANUAL_JDBC_TEMPLATE",
                        com.example.guardian.core.model.Severity.MINOR,
                        Set.of("JdbcTemplate", "NamedParameterJdbcTemplate"),
                        "JdbcTemplate created manually",
                        "Creating templates manually can duplicate transaction, datasource and exception translation configuration.",
                        "Define JdbcTemplate or NamedParameterJdbcTemplate as beans, or inject the auto-configured template when available.",
                        true
                ),
                new ObjectCreationAdvisorRule(
                        "SPR069_MANUAL_PASSWORD_ENCODER",
                        com.example.guardian.core.model.Severity.MAJOR,
                        Set.of("BCryptPasswordEncoder", "Pbkdf2PasswordEncoder", "SCryptPasswordEncoder", "Argon2PasswordEncoder"),
                        "Password encoder created manually",
                        "Password encoders should be centralized so authentication, tests and migration policies use the same hashing strategy.",
                        "Expose one PasswordEncoder bean and inject it wherever password verification or encoding is required.",
                        true
                ),
                new StaticMethodAdvisorRule(
                        "SPR070_DIRECT_ENVIRONMENT_ACCESS",
                        com.example.guardian.core.model.Severity.MINOR,
                        "System",
                        Set.of("getenv", "getProperty"),
                        "Environment read directly from code",
                        "Reading environment or system properties directly hides configuration from Spring binding, validation and profile management.",
                        "Use @ConfigurationProperties, Environment or validated properties objects for application configuration.",
                        false
                ),
                new ObjectCreationAdvisorRule(
                        "SPR071_MANUAL_FILE_RESOURCE_ACCESS",
                        com.example.guardian.core.model.Severity.MINOR,
                        Set.of("FileInputStream", "FileOutputStream", "FileReader", "FileWriter"),
                        "File resource opened manually",
                        "Direct file access can hardcode infrastructure assumptions and makes classpath, filesystem and externalized resources harder to swap.",
                        "Use Resource, ResourceLoader or configuration properties to make resource access explicit and testable.",
                        false
                ),
                new SpringBeanCreatedWithNewRule(),
                new MockBeanModernizationAdvisorRule(),
                new StructuredLoggingAdvisorRule(),
                new MockMvcTesterAdvisorRule()
        );
    }
}

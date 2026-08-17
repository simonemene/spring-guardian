package com.example.guardian.core.rules;

import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.example.guardian.core.rules.CatalogPatternRule.Definition.line;
import static com.example.guardian.core.rules.CatalogPatternRule.SourceTarget.CONFIG;
import static com.example.guardian.core.rules.CatalogPatternRule.SourceTarget.JAVA_MAIN;
import static com.example.guardian.core.rules.CatalogPatternRule.SourceTarget.POM;

/**
 * Additional high-signal Spring architecture, runtime, modernization and Batch rules.
 * Rules in this catalog deliberately prefer deterministic evidence over broad stylistic heuristics.
 *
 * @author Simone Meneghetti
 */
final class AdvancedSpringRuleCatalog {

    private AdvancedSpringRuleCatalog() {
    }

    static List<SpringRule> rules() {
        List<SpringRule> rules = new ArrayList<>();
        rules.add(new ControllerCallsControllerRule());
        rules.add(new RemoteCallInsideTransactionRule());
        rules.add(new ReadOnlyTransactionWritesRule());
        rules.add(new TransactionalExceptionSwallowedRule());
        rules.add(new InvalidAsyncMethodRule());
        rules.add(new ScheduledMethodSignatureRule());
        rules.add(new GetWithRequestBodyRule());
        rules.add(new FindAllThenFilterRule());
        rules.add(new FlushInsideLoopRule());
        rules.add(new EntityInjectsSpringBeanRule());
        rules.add(new SensitiveDataLoggedRule());
        rules.add(new MdcWithoutCleanupRule());
        rules.add(new NoOpPasswordEncoderRule());
        rules.add(new DefaultPasswordEncoderRule());
        rules.add(new HardcodedJwtSecretRule());
        rules.add(new BCryptLowStrengthRule());
        rules.add(new EntityLombokDataRule());
        rules.add(new ManyToManyCascadeAllRule());
        rules.add(new BatchJobParameterScopeRule());
        rules.add(new BatchRandomizedJobParameterRule());
        rules.add(new BatchRetryWithoutBackoffRule());
        rules.add(new AsyncTransactionalCombinedAlternativeRule());
        rules.add(new ControllerMapResponseAlternativeRule());
        rules.add(new TransactionalEventPublishAlternativeRule());
        rules.addAll(patternRules());
        return List.copyOf(rules);
    }

    private static List<SpringRule> patternRules() {
        List<CatalogPatternRule.Definition> definitions = new ArrayList<>();

        // Runtime/configuration risks.
        definitions.add(line("CFG101_ALLOW_CIRCULAR_REFERENCES", Severity.MAJOR, CONFIG,
                Set.of("spring.main.allow-circular-references=true", "allow-circular-references: true"),
                "Circular bean references explicitly enabled",
                "Allowing circular references can hide dependency cycles and makes initialization/proxy behavior harder to reason about.",
                "Break the dependency cycle by extracting an application boundary, event, port or smaller service; keep circular-reference support disabled."));
        definitions.add(line("CFG102_ALLOW_BEAN_OVERRIDE", Severity.MAJOR, CONFIG,
                Set.of("spring.main.allow-bean-definition-overriding=true", "allow-bean-definition-overriding: true"),
                "Bean definition overriding enabled",
                "Silent bean replacement makes runtime wiring depend on registration order and can hide accidental duplicate configuration.",
                "Keep bean overriding disabled and resolve duplicate bean names explicitly with qualifiers or configuration boundaries."));
        definitions.add(line("CFG103_FLYWAY_CLEAN_ENABLED", Severity.CRITICAL, CONFIG,
                Set.of("spring.flyway.clean-disabled=false", "clean-disabled: false"),
                "Flyway clean operation enabled",
                "Allowing Flyway clean can drop managed database objects/data if the operation is invoked in the wrong environment.",
                "Keep Flyway clean disabled outside controlled disposable environments and protect destructive database operations operationally.").requiringFile("flyway"));
        definitions.add(line("CFG104_LIQUIBASE_DROP_FIRST", Severity.CRITICAL, CONFIG,
                Set.of("spring.liquibase.drop-first=true", "drop-first: true"),
                "Liquibase drop-first enabled",
                "drop-first removes database objects before applying the changelog and is destructive for persistent environments.",
                "Disable drop-first for persistent environments; use isolated disposable databases for rebuild-from-zero workflows.").requiringFile("liquibase"));
        definitions.add(line("CFG105_SQL_INIT_ALWAYS", Severity.MINOR, CONFIG,
                Set.of("spring.sql.init.mode=always", "mode: always"),
                "SQL initialization forced on every environment",
                "Always-on SQL initialization can unexpectedly execute schema/data scripts against non-embedded databases.",
                "Scope SQL initialization by profile/environment or use Flyway/Liquibase for governed schema changes.").requiringFile("spring.sql.init"));
        definitions.add(line("WEB102_ERROR_STACKTRACE_ALWAYS", Severity.MAJOR, CONFIG,
                Set.of("server.error.include-stacktrace=always", "include-stacktrace: always"),
                "HTTP error stack traces always included",
                "Stack traces can disclose package names, implementation details and internal failure paths to API clients.",
                "Do not expose stack traces in API responses; log the exception server-side and return ProblemDetail/a stable error contract."));
        definitions.add(line("WEB103_ERROR_MESSAGE_ALWAYS", Severity.MINOR, CONFIG,
                Set.of("server.error.include-message=always", "include-message: always"),
                "Exception messages always included in HTTP errors",
                "Raw exception messages can contain SQL, paths, identifiers or implementation details not intended for API consumers.",
                "Return controlled client-safe messages from centralized exception handling and keep technical details in logs."));
        definitions.add(line("SEC105_SPRING_SECURITY_DEBUG_ENABLED", Severity.MAJOR, CONFIG,
                Set.of("spring.security.debug=true", "security.debug: true"),
                "Spring Security debug mode enabled",
                "Security debug output is verbose and can reveal request/security processing details while adding runtime overhead.",
                "Disable Spring Security debug mode outside short-lived local diagnostics and use targeted logging levels instead."));
        definitions.add(line("SEC106_CSRF_IGNORES_ALL", Severity.MAJOR, JAVA_MAIN,
                Set.of("ignoringRequestMatchers(\"/**\")", "ignoringAntMatchers(\"/**\")"),
                "CSRF protection ignored for every route",
                "Ignoring CSRF for all routes is effectively a global disable while making the configuration look selectively scoped.",
                "Keep CSRF protection for browser/session flows; for stateless APIs document token authentication and scope exclusions narrowly.").onlyWhen(AdvancedSpringRuleCatalog::usesSecurity));
        definitions.add(line("SEC107_INSECURE_SESSION_COOKIE", Severity.MINOR, CONFIG,
                Set.of("server.servlet.session.cookie.secure=false", "secure: false"),
                "Session cookie explicitly marked non-secure",
                "A non-secure session cookie can be transmitted over plain HTTP if the application is accessed without TLS.",
                "Use secure cookies in TLS environments and keep local-development exceptions profile-specific.").requiringFile("session", "cookie"));
        definitions.add(line("WEB104_CROSS_ORIGIN_WILDCARD", Severity.MINOR, JAVA_MAIN,
                Set.of("@CrossOrigin(\"*\")", "origins = \"*\"", "origins={\"*\"}"),
                "Wildcard @CrossOrigin policy",
                "A wildcard origin broadens browser access to the endpoint and can become unsafe when the API later gains credentials or sensitive data.",
                "Configure an explicit allow-list per environment and keep credential policy centralized.").onlyWhen(AdvancedSpringRuleCatalog::usesWeb));
        definitions.add(line("JPA105_SHOW_SQL_ENABLED", Severity.MINOR, CONFIG,
                Set.of("spring.jpa.show-sql=true", "show-sql: true"),
                "JPA SQL console output enabled",
                "Direct SQL console output is noisy, bypasses normal logger control and can expose query values/structure in production logs.",
                "Disable show-sql outside diagnostics and use controlled Hibernate SQL logger categories when SQL tracing is necessary.").onlyWhen(AdvancedSpringRuleCatalog::usesJpa));
        definitions.add(line("OBS103_ROOT_DEBUG_OR_TRACE", Severity.MINOR, CONFIG,
                Set.of("logging.level.root=debug", "logging.level.root=trace", "root: debug", "root: trace"),
                "Root logging set to DEBUG/TRACE",
                "Root-level verbose logging can produce large log volumes, increase I/O cost and unintentionally capture sensitive operational detail.",
                "Keep root logging at INFO/WARN in normal environments and enable DEBUG/TRACE only for targeted packages and limited diagnostics."));
        definitions.add(line("OBS104_EXCEPTION_MESSAGE_LOGGED_WITHOUT_THROWABLE", Severity.MINOR, JAVA_MAIN,
                Set.of(".error(e.getMessage())", ".error(ex.getMessage())", ".warn(e.getMessage())", ".warn(ex.getMessage())"),
                "Exception message logged without throwable",
                "Logging only getMessage() discards stack trace and exception type, making production diagnosis harder.",
                "Log contextual text and pass the exception object as the final SLF4J argument, e.g. log.error(\"Operation failed\", ex)."));
        definitions.add(line("POM041_INSECURE_HTTP_REPOSITORY", Severity.MAJOR, POM,
                Set.of("<url>http://"),
                "Maven repository uses plain HTTP",
                "Dependencies/plugins downloaded over unencrypted HTTP can be modified in transit and weaken build supply-chain integrity.",
                "Use an HTTPS repository endpoint or an internal repository manager with TLS and governed mirrors.").requiringLine("<url>http://"));
        definitions.add(line("POM042_DYNAMIC_VERSION_RANGE", Severity.MINOR, POM,
                Set.of("<version>[", "<version>("),
                "Dynamic Maven dependency version range",
                "Version ranges make builds less reproducible because the resolved artifact can change without a source change.",
                "Pin dependency/plugin versions through dependency management/BOMs and upgrade them deliberately."));

        // Batch reliability and restartability.
        definitions.add(line("BAT041_SAVE_STATE_FALSE", Severity.MAJOR, JAVA_MAIN,
                Set.of("saveState(false)", "setSaveState(false)"),
                "Batch item state persistence disabled",
                "Disabling saveState prevents the reader/writer from persisting restart state and can make failed jobs restart from the wrong position.",
                "Keep state persistence enabled for restartable jobs, or document why the component is intentionally non-restartable and idempotent.").onlyWhen(AdvancedSpringRuleCatalog::usesBatch));
        definitions.add(line("BAT042_RUN_ID_INCREMENTER_REVIEW", Severity.MINOR, JAVA_MAIN,
                Set.of("new RunIdIncrementer()", "RunIdIncrementer()"),
                "RunIdIncrementer used",
                "A generated run.id creates a new JobInstance rather than restarting an existing failed instance, which can hide restartability problems.",
                "Use RunIdIncrementer only when each launch is intentionally a new business instance; use identifying business JobParameters for restart semantics.").onlyWhen(AdvancedSpringRuleCatalog::usesBatch));
        definitions.add(line("BAT044_MANUAL_JOB_STATUS_MUTATION", Severity.MAJOR, JAVA_MAIN,
                Set.of(".setStatus(BatchStatus", ".setStatus(org.springframework.batch.core.BatchStatus"),
                "Batch execution status mutated manually",
                "Directly changing JobExecution/StepExecution status can conflict with Spring Batch lifecycle and repository state transitions.",
                "Express outcome through exceptions, ExitStatus/listeners and normal Batch lifecycle APIs; avoid manually forcing BatchStatus.").onlyWhen(AdvancedSpringRuleCatalog::usesBatch));
        definitions.add(line("BAT045_BATCH_SCHEMA_INIT_ALWAYS", Severity.MAJOR, CONFIG,
                Set.of("spring.batch.jdbc.initialize-schema=always", "initialize-schema: always"),
                "Spring Batch metadata schema always initialized",
                "Always initializing Batch metadata can attempt DDL on persistent environments where schema changes should be controlled separately.",
                "Use a governed migration for persistent environments and scope automatic Batch schema initialization to disposable/local profiles.").onlyWhen(AdvancedSpringRuleCatalog::usesBatch));
        definitions.add(line("BAT046_CURSOR_READER_WITH_TASK_EXECUTOR", Severity.MAJOR, JAVA_MAIN,
                Set.of("taskExecutor("),
                "Cursor reader combined with multi-threaded step",
                "Cursor-based readers are stateful and require careful synchronization/restart semantics when a step is multi-threaded.",
                "Use a thread-safe/paging/partitioned reader strategy or keep cursor processing single-threaded unless thread safety is explicitly guaranteed.").requiringFile("JdbcCursorItemReader").onlyWhen(AdvancedSpringRuleCatalog::usesBatch));
        definitions.add(line("BAT047_JOB_SCOPE_WITH_PARTITIONING", Severity.MAJOR, JAVA_MAIN,
                Set.of("partitioner(", "gridSize(", "taskExecutor("),
                "@JobScope used with parallel/partitioned Batch configuration",
                "Job-scoped state is not a good fit for worker threads/partitioned steps and can create context propagation/state access problems.",
                "Prefer @StepScope for components used by partitioned or multi-threaded steps and pass partition data through the StepExecutionContext.").requiringFile("@JobScope").onlyWhen(AdvancedSpringRuleCatalog::usesBatch));
        definitions.add(line("BAT048_FLAT_FILE_APPEND_ALLOWED", Severity.MINOR, JAVA_MAIN,
                Set.of("setAppendAllowed(true)", "append(true)"),
                "Flat-file writer appends to existing output",
                "Appending can duplicate output when a job/step is restarted unless file naming and restart semantics are explicitly idempotent.",
                "Use restart-aware output handling and verify idempotency; prefer new execution-specific resources when duplicate append is unsafe.").requiringFile("FlatFileItemWriter").onlyWhen(AdvancedSpringRuleCatalog::usesBatch));
        definitions.add(line("BAT049_JDBC_WRITER_ASSERT_UPDATES_FALSE", Severity.MINOR, JAVA_MAIN,
                Set.of("assertUpdates(false)", "setAssertUpdates(false)"),
                "JdbcBatchItemWriter update assertions disabled",
                "Disabling update assertions can hide statements that affect zero rows when one-row update semantics were expected.",
                "Keep update assertions enabled unless zero-row updates are a documented valid outcome and are monitored separately.").requiringFile("JdbcBatchItemWriter").onlyWhen(AdvancedSpringRuleCatalog::usesBatch));

        // Spring Alternative / modernization advisor additions.
        definitions.add(alt("SPR_ALT024_WEB_SECURITY_CONFIGURER_ADAPTER", JAVA_MAIN,
                Set.of("extends WebSecurityConfigurerAdapter"),
                "Legacy WebSecurityConfigurerAdapter configuration",
                "The adapter inheritance model was replaced by component-based SecurityFilterChain configuration.",
                "Modernize to one or more SecurityFilterChain beans and explicit authentication/authorization beans."));
        definitions.add(alt("SPR_ALT025_ENABLE_GLOBAL_METHOD_SECURITY", JAVA_MAIN,
                Set.of("@EnableGlobalMethodSecurity"),
                "Legacy method-security enablement",
                "Modern Spring Security uses @EnableMethodSecurity and a simplified authorization model.",
                "When upgrading Spring Security, replace @EnableGlobalMethodSecurity with @EnableMethodSecurity and review pre/post authorization behavior."));
        definitions.add(alt("SPR_ALT026_AUTHORIZE_REQUESTS_DSL", JAVA_MAIN,
                Set.of(".authorizeRequests("),
                "Legacy authorizeRequests DSL",
                "The modern servlet authorization DSL is authorizeHttpRequests and uses AuthorizationManager-based infrastructure.",
                "Use authorizeHttpRequests(...) and requestMatchers(...) when modernizing Spring Security."));
        definitions.add(alt("SPR_ALT027_ANT_OR_MVC_MATCHERS_DSL", JAVA_MAIN,
                Set.of(".antMatchers(", ".mvcMatchers("),
                "Legacy Spring Security matcher DSL",
                "antMatchers/mvcMatchers were replaced by requestMatchers in modern Spring Security.",
                "Use requestMatchers with explicit patterns/HTTP methods and review matcher semantics during the upgrade."));
        definitions.add(alt("SPR_ALT028_WEBMVC_CONFIGURER_ADAPTER", JAVA_MAIN,
                Set.of("extends WebMvcConfigurerAdapter"),
                "Legacy WebMvcConfigurerAdapter",
                "WebMvcConfigurer has default methods, so the adapter base class is obsolete.",
                "Implement WebMvcConfigurer directly and override only the required methods."));
        definitions.add(alt("SPR_ALT029_HANDLER_INTERCEPTOR_ADAPTER", JAVA_MAIN,
                Set.of("extends HandlerInterceptorAdapter"),
                "Legacy HandlerInterceptorAdapter",
                "HandlerInterceptor now provides default methods and the adapter inheritance layer is unnecessary.",
                "Implement HandlerInterceptor directly."));
        definitions.add(alt("SPR_ALT030_JOB_BUILDER_FACTORY", JAVA_MAIN,
                Set.of("JobBuilderFactory"),
                "Legacy JobBuilderFactory usage",
                "Modern Spring Batch constructs JobBuilder with an explicit JobRepository, making infrastructure dependencies clearer.",
                "When moving to Spring Batch 5+, inject JobRepository and construct JobBuilder(name, jobRepository)."));
        definitions.add(alt("SPR_ALT031_STEP_BUILDER_FACTORY", JAVA_MAIN,
                Set.of("StepBuilderFactory"),
                "Legacy StepBuilderFactory usage",
                "Modern Spring Batch constructs StepBuilder with explicit JobRepository/transaction manager dependencies.",
                "When moving to Spring Batch 5+, inject JobRepository and build steps with StepBuilder plus the explicit transaction manager."));
        definitions.add(alt("SPR_ALT032_ENABLE_BATCH_PROCESSING_REVIEW", JAVA_MAIN,
                Set.of("@EnableBatchProcessing"),
                "@EnableBatchProcessing requires review on modern Boot",
                "Spring Boot can auto-configure Batch infrastructure; explicitly enabling Batch can change/back off parts of Boot auto-configuration.",
                "On modern Spring Boot, keep @EnableBatchProcessing only when you intentionally own the Batch infrastructure configuration."));
        definitions.add(alt("SPR_ALT033_REQUIRES_NEW_REVIEW", JAVA_MAIN,
                Set.of("Propagation.REQUIRES_NEW"),
                "REQUIRES_NEW transaction boundary",
                "REQUIRES_NEW suspends the current transaction and consumes an independent connection/commit boundary, which can surprise callers and increase pool pressure.",
                "Keep REQUIRES_NEW only for an explicitly independent unit of work; otherwise prefer one clear application transaction boundary."));
        definitions.add(alt("SPR_ALT034_CUSTOM_HEALTH_ENDPOINT", JAVA_MAIN,
                Set.of("@GetMapping(\"/health\")", "@RequestMapping(\"/health\")"),
                "Custom health controller candidate",
                "A custom /health endpoint duplicates operational semantics that Spring Boot Actuator already standardizes and integrates with health contributors.",
                "Use Actuator health groups/HealthIndicator for operational health and keep business readiness checks as dedicated contributors.").onlyWhen(AdvancedSpringRuleCatalog::usesWeb));
        definitions.add(alt("SPR_ALT035_HTTP_SERVLET_RESPONSE_WRITER", JAVA_MAIN,
                Set.of("response.getWriter()", "httpServletResponse.getWriter()"),
                "Direct HttpServletResponse writer usage",
                "Writing the response manually bypasses normal message conversion, typed response contracts and centralized error handling.",
                "Return DTO/ResponseEntity/StreamingResponseBody/Resource as appropriate and let Spring MVC own response serialization.").inPaths("/controller/"));
        definitions.add(alt("SPR_ALT036_REACTIVE_BLOCKING_CALL", JAVA_MAIN,
                Set.of(".block()", ".blockOptional()"),
                "Blocking Reactor call",
                "Calling block() inside reactive application flow can pin event-loop threads and undermine reactive backpressure/scalability.",
                "Compose Mono/Flux operators end-to-end; block only at a deliberate imperative boundary that is not running on a reactive event loop.").requiringFile("reactor."));
        definitions.add(alt("SPR_ALT037_OPTIONAL_AUTOWIRED_DEPENDENCY", JAVA_MAIN,
                Set.of("@Autowired(required = false)", "@Autowired(required=false)"),
                "Optional dependency expressed with nullable field injection",
                "required=false can hide optional wiring as mutable/null state and makes the component contract less explicit.",
                "Prefer constructor injection with Optional/ObjectProvider or conditional bean configuration so optionality is visible in the dependency contract."));
        definitions.add(alt("SPR_ALT038_SPRINGFOX_OPENAPI", POM,
                Set.of("springfox-swagger", "springfox-boot-starter"),
                "Springfox Swagger dependency detected",
                "Springfox targets older Spring generations and is a frequent migration obstacle on modern Spring Boot/Spring Framework.",
                "For modern Boot applications evaluate springdoc-openapi and migrate annotations/configuration incrementally."));
        definitions.add(alt("SPR_ALT039_SPRING_CLOUD_SLEUTH", POM,
                Set.of("spring-cloud-starter-sleuth"),
                "Spring Cloud Sleuth dependency detected",
                "Tracing moved to Micrometer Tracing in modern Spring Boot/Spring Cloud generations.",
                "When modernizing, migrate tracing to Micrometer Tracing and the exporter/bridge appropriate for your backend."));
        definitions.add(alt("SPR_ALT040_HYSTRIX", POM,
                Set.of("spring-cloud-starter-netflix-hystrix", "hystrix-core"),
                "Hystrix dependency detected",
                "Hystrix is a legacy resilience stack and blocks upgrades to current Spring Cloud generations.",
                "Migrate circuit breaker/retry/time limiter behavior to Spring Cloud CircuitBreaker with Resilience4j or another supported implementation."));
        definitions.add(alt("SPR_ALT042_CONSTRUCTOR_BINDING_TYPE_ANNOTATION", JAVA_MAIN,
                Set.of("@ConstructorBinding"),
                "@ConstructorBinding annotation review",
                "On modern Spring Boot, a single parameterized constructor for @ConfigurationProperties is bound automatically; type-level ConstructorBinding is often unnecessary/obsolete.",
                "For Boot 3-era code, keep @ConstructorBinding only where multiple constructors require explicit selection and remove obsolete type-level usage during modernization."));
        definitions.add(alt("SPR_ALT043_BATCH_THROTTLE_LIMIT", JAVA_MAIN,
                Set.of(".throttleLimit("),
                "Legacy Batch throttleLimit configuration",
                "throttleLimit-based multi-threaded step configuration is deprecated in modern Spring Batch and can obscure executor capacity control.",
                "Use a TaskExecutor/repeat-operations strategy with explicit bounded concurrency appropriate to your Spring Batch version.").onlyWhen(AdvancedSpringRuleCatalog::usesBatch));
        definitions.add(alt("SPR_ALT044_SIMPLE_JOB_LAUNCHER", JAVA_MAIN,
                Set.of("SimpleJobLauncher"),
                "Legacy SimpleJobLauncher usage",
                "Modern Spring Batch infrastructure favors TaskExecutorJobLauncher and auto-configured JobLauncher behavior.",
                "When upgrading Batch infrastructure, use the supported JobLauncher implementation/configuration for your target version rather than constructing SimpleJobLauncher directly.").onlyWhen(AdvancedSpringRuleCatalog::usesBatch));
        definitions.add(alt("SPR_ALT045_MAP_JOB_REPOSITORY", JAVA_MAIN,
                Set.of("MapJobRepositoryFactoryBean"),
                "Legacy in-memory Batch JobRepository",
                "MapJobRepositoryFactoryBean belongs to older Batch infrastructure and does not provide durable restart metadata.",
                "Use a JDBC-backed JobRepository for real restartability or the supported resourceless repository only for explicitly non-restartable disposable workloads.").onlyWhen(AdvancedSpringRuleCatalog::usesBatch));

        return definitions.stream().map(CatalogPatternRule::new).map(SpringRule.class::cast).toList();
    }

    private static CatalogPatternRule.Definition alt(String id, CatalogPatternRule.SourceTarget target, Set<String> patterns,
                                                      String title, String why, String fix) {
        return line(id, Severity.INFO, target, patterns, title, why, fix).max(4);
    }

    private static boolean usesWeb(ProjectScanContext context) {
        return context.capabilities().usesSpringWeb();
    }

    private static boolean usesSecurity(ProjectScanContext context) {
        return context.capabilities().usesSpringSecurity();
    }

    private static boolean usesJpa(ProjectScanContext context) {
        return context.capabilities().usesJpa();
    }

    private static boolean usesBatch(ProjectScanContext context) {
        return context.capabilities().usesSpringBatch();
    }
}

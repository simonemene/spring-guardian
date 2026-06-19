package com.example.guardian.core.rules;

import com.example.guardian.core.model.Severity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Unified catalog for Spring Alternative Advisor rules.
 * It is the single entry point for legacy ADV rules, non-duplicated SPR advisor rules
 * and the enterprise Spring Alternatives introduced for architecture modernization.
 *
 * @author Simone Meneghetti
 */
final class SpringAlternativeRulesCatalog {

    private SpringAlternativeRulesCatalog() {
    }

    /**
     * Creates all Spring Alternative Advisor rules using stable canonical identifiers.
     *
     * @return unique Spring Alternative Advisor rules
     */
    static List<SpringRule> rules() {
        List<SpringRule> rules = new ArrayList<>();
        rules.addAll(canonicalLegacyAdvisorRules());
        rules.addAll(nonDuplicatedSpringAdvisorRules());
        rules.addAll(enterpriseSpringAlternativeRules());
        return uniqueById(rules);
    }

    private static List<SpringRule> canonicalLegacyAdvisorRules() {
        List<SpringRule> rules = new ArrayList<>();
        rules.add(new ObjectCreationAdvisorRule("ADV001_MANUAL_OBJECT_MAPPER", Severity.INFO, Set.of("ObjectMapper", "XmlMapper"), "ObjectMapper created manually", "A manually created ObjectMapper can bypass Spring Boot Jackson modules, naming policy and JSON configuration.", "Inject the Spring Boot managed ObjectMapper or expose one configured bean.", true));
        rules.add(new ObjectCreationAdvisorRule("ADV002_MANUAL_GSON", Severity.INFO, Set.of("Gson", "GsonBuilder"), "Gson created manually", "A second JSON engine can produce contracts that differ from Spring Boot JSON configuration.", "Prefer the Boot-managed ObjectMapper or centralize Gson as a bean only when Gson is explicitly required.", true));
        rules.add(new ObjectCreationAdvisorRule("ADV003_MANUAL_REST_TEMPLATE", Severity.INFO, Set.of("RestTemplate"), "RestTemplate created manually", "A RestTemplate created with new can bypass shared timeout, interceptor, error-handler and observability configuration.", "Inject a configured RestTemplate or evaluate RestClient for new synchronous HTTP clients.", true));
        rules.add(new StaticMethodAdvisorRule("ADV004_WEBCLIENT_CREATE_SCATTERED", Severity.INFO, "WebClient", Set.of("create"), "WebClient.create used directly", "WebClient.create can duplicate base URLs, codecs, filters, metrics and TLS/proxy configuration.", "Inject WebClient.Builder or a configured WebClient bean.", false));
        rules.add(new StaticMethodAdvisorRule("ADV005_RESTCLIENT_CREATE_SCATTERED", Severity.INFO, "RestClient", Set.of("create"), "RestClient.create used directly", "RestClient.create can duplicate timeout, interceptor and observation setup across classes.", "Expose a RestClient bean or inject a configured RestClient.Builder.", false));
        rules.add(new ObjectCreationAdvisorRule("ADV006_LOW_LEVEL_HTTP_CLIENT", Severity.INFO, Set.of("HttpURLConnection", "HttpClient", "CloseableHttpClient", "OkHttpClient"), "Low-level HTTP client detected", "Direct HTTP clients make timeout, retry, tracing and tests less consistent in Spring applications.", "Prefer RestClient, WebClient or a centrally configured HTTP client bean.", false));
        rules.add(new ObjectCreationAdvisorRule("ADV008_MANUAL_THREAD_CREATION", Severity.INFO, Set.of("Thread"), "Thread created manually", "Manual threads bypass Spring lifecycle, graceful shutdown and observability.", "Use TaskExecutor, ThreadPoolTaskExecutor, @Async or TaskScheduler.", false));
        rules.add(new StaticMethodAdvisorRule("ADV009_MANUAL_EXECUTOR", Severity.INFO, "Executors", Set.of("newFixedThreadPool", "newCachedThreadPool", "newSingleThreadExecutor", "newScheduledThreadPool", "newWorkStealingPool"), "Executor created manually", "Manual executor services can escape Spring lifecycle, shutdown hooks, metrics and central tuning.", "Expose a TaskExecutor or ThreadPoolTaskExecutor bean and inject it where asynchronous work is needed.", false));
        rules.add(new StaticMethodAdvisorRule("ADV011_THREAD_SLEEP", Severity.INFO, "Thread", Set.of("sleep"), "Thread.sleep in application code", "Thread.sleep makes backoff, retry and asynchronous orchestration fragile.", "Use scheduling, explicit retry/backoff or controlled synchronization primitives.", false));
        rules.add(new StaticMethodAdvisorRule("ADV012_DIRECT_SYSTEM_ENVIRONMENT_ACCESS", Severity.INFO, "System", Set.of("getenv", "getProperty"), "Direct runtime environment access", "Reading environment or system properties directly hides configuration from Spring binding, validation and profile management.", "Use @ConfigurationProperties, Environment in an adapter, or validated properties objects.", false));
        rules.add(new AnnotationUsageAdvisorRule("ADV013_SCATTERED_VALUE", Severity.INFO, Set.of("Value"), "@Value used for application configuration", "Many scattered @Value properties make configuration harder to validate, document and evolve.", "Group properties in validated @ConfigurationProperties and keep @Value only for isolated values.", false));
        rules.add(new ObjectCreationAdvisorRule("ADV014_DIRECT_FILE_STREAM", Severity.INFO, Set.of("FileInputStream", "FileOutputStream", "FileReader", "FileWriter"), "Direct file stream access", "Direct file streams make Resource, classpath, external storage and testing strategies harder to swap.", "Use Resource, ResourceLoader or typed configuration with Resource values.", false));
        rules.add(new StaticMutableCacheAdvisorRule());
        rules.add(new StaticMethodAdvisorRule("ADV021_MANUAL_VALIDATOR_FACTORY", Severity.INFO, "Validation", Set.of("buildDefaultValidatorFactory"), "ValidatorFactory created manually", "Manual validator factories can bypass Spring validation configuration and ConstraintValidator injection.", "Inject Validator or use @Valid/@Validated at web and service boundaries.", false));
        rules.add(new ObjectCreationAdvisorRule("ADV023_SIMPLE_DATE_FORMAT_MANUAL", Severity.INFO, Set.of("SimpleDateFormat"), "Legacy date formatter detected", "SimpleDateFormat is mutable and often creates local formatting rules inconsistent with Jackson and Spring Boot.", "Use java.time.format.DateTimeFormatter and centralize API date formatting policy.", false));
        rules.add(new StaticMethodAdvisorRule("ADV033_DIRECT_LOCAL_DATE_NOW", Severity.INFO, "LocalDate", Set.of("now"), "LocalDate.now used directly", "Direct clock access makes time-dependent logic less deterministic in tests.", "Inject java.time.Clock and use LocalDate.now(clock).", false));
        rules.add(new StaticMethodAdvisorRule("ADV033_DIRECT_LOCAL_DATE_TIME_NOW", Severity.INFO, "LocalDateTime", Set.of("now"), "LocalDateTime.now used directly", "Direct clock access makes time-dependent logic less deterministic in tests.", "Inject java.time.Clock and use LocalDateTime.now(clock).", false));
        rules.add(new StaticMethodAdvisorRule("ADV033_DIRECT_INSTANT_NOW", Severity.INFO, "Instant", Set.of("now"), "Instant.now used directly", "Direct clock access makes time-dependent logic less deterministic in tests.", "Inject java.time.Clock and use Instant.now(clock).", false));
        rules.add(new StaticMethodAdvisorRule("ADV066_COMPLETABLE_FUTURE_DEFAULT_EXECUTOR", Severity.INFO, "CompletableFuture", Set.of("supplyAsync", "runAsync"), "CompletableFuture without explicit executor", "The common pool is not governed by Spring and can create uncontrolled concurrency.", "Use @Async with a configured executor or pass an explicit Executor.", false));
        rules.add(new StaticMethodAdvisorRule("ADV101_SYSTEM_EXIT_IN_APPLICATION_CODE", Severity.INFO, "System", Set.of("exit"), "System.exit in application code", "Terminating the JVM directly bypasses Spring lifecycle, cleanup and managed exit status.", "Propagate an application error or use Spring Boot, Batch or scheduler managed exit status.", false));
        return rules;
    }

    private static List<SpringRule> nonDuplicatedSpringAdvisorRules() {
        List<SpringRule> rules = new ArrayList<>();
        rules.add(new ObjectCreationAdvisorRule("SPR067_TIMER_SCHEDULING", Severity.INFO, Set.of("Timer", "TimerTask"), "Timer-based scheduling detected", "Timer and TimerTask are low-level scheduling APIs and are harder to align with Spring profiles, lifecycle and observability.", "Use @Scheduled or TaskScheduler with explicit configuration and tests.", false));
        rules.add(new ObjectCreationAdvisorRule("SPR068_MANUAL_JDBC_TEMPLATE", Severity.INFO, Set.of("JdbcTemplate", "NamedParameterJdbcTemplate"), "JdbcTemplate created manually", "Creating templates manually can duplicate transaction, datasource and exception translation configuration.", "Inject the auto-configured JdbcTemplate or expose one centrally configured bean.", true));
        rules.add(new ObjectCreationAdvisorRule("SPR069_MANUAL_PASSWORD_ENCODER", Severity.INFO, Set.of("BCryptPasswordEncoder", "Pbkdf2PasswordEncoder", "SCryptPasswordEncoder", "Argon2PasswordEncoder"), "Password encoder created manually", "Password encoders should be centralized so authentication, tests and migration policies share the same hashing strategy.", "Expose one PasswordEncoder bean and inject it wherever password verification or encoding is required.", true));
        rules.add(new SpringBeanCreatedWithNewRule());
        rules.add(new MockBeanModernizationAdvisorRule());
        rules.add(new StructuredLoggingAdvisorRule());
        rules.add(new MockMvcTesterAdvisorRule());
        rules.add(new StaticMethodAdvisorRule("SPR077_WEBCLIENT_BUILDER_CREATED_MANUALLY", Severity.INFO, "WebClient", Set.of("builder"), "WebClient builder created directly", "Direct WebClient builders can skip shared codecs, filters, metrics and TLS/proxy configuration.", "Inject WebClient.Builder or a configured WebClient bean.", false));
        rules.add(new StaticMethodAdvisorRule("SPR078_RESTCLIENT_BUILDER_CREATED_MANUALLY", Severity.INFO, "RestClient", Set.of("builder"), "RestClient builder created directly", "Direct RestClient builders can duplicate timeout, interceptor and observation configuration across classes.", "Expose a configured RestClient.Builder or RestClient bean for application services.", false));
        rules.add(new ObjectCreationAdvisorRule("SPR079_LOW_LEVEL_URL_CLIENT", Severity.INFO, Set.of("URL"), "java.net.URL used as HTTP client primitive", "Using URL directly for calls hides timeout, retry, TLS/proxy and test configuration.", "Prefer RestClient, WebClient or a centrally configured HTTP client bean.", false));
        rules.add(new MissingEnableAnnotationAdvisorRule("SPR083_ASYNC_WITHOUT_ENABLE_ASYNC", Severity.INFO, "Async", "EnableAsync", "@Async used without @EnableAsync", "Async methods are ignored if asynchronous execution is not enabled in the application context.", "Add @EnableAsync in a configuration class and define the TaskExecutor used by asynchronous work."));
        rules.add(new MissingEnableAnnotationAdvisorRule("SPR084_SCHEDULED_WITHOUT_ENABLE_SCHEDULING", Severity.INFO, "Scheduled", "EnableScheduling", "@Scheduled used without @EnableScheduling", "Scheduled methods are ignored if scheduling is not enabled in the application context.", "Add @EnableScheduling in a configuration class and make scheduler settings explicit."));
        rules.add(new ObjectCreationAdvisorRule("SPR090_MANUAL_CACHE_STRUCTURE", Severity.INFO, Set.of("ConcurrentHashMap"), "Manual in-memory cache candidate", "Maps used as caches inside Spring components are often invisible to eviction policy, metrics and operational control.", "Evaluate Spring Cache with a configured CacheManager when the map represents reusable cached data.", false));
        rules.add(new ApplicationContextGetBeanAdvisorRule());
        return rules;
    }

    private static List<SpringRule> enterpriseSpringAlternativeRules() {
        List<SpringRule> rules = new ArrayList<>();
        rules.add(delegate(new SecurityFilterChainMissingRule(), "SPR_ALT001_SECURITY_FILTER_CHAIN_MISSING", "SecurityFilterChain missing", "A Spring Security application without an explicit SecurityFilterChain can rely on defaults that are unclear during audit and modernization.", "Declare an explicit SecurityFilterChain bean with request matchers, authentication policy and a secure default."));
        rules.add(delegate(new CsrfDisabledWithoutStatelessRule(), "SPR_ALT002_CSRF_DISABLED_WITHOUT_STATELESS_API", "CSRF disabled without stateless API evidence", "Disabling CSRF without a clearly stateless API model can expose browser-based clients to cross-site request forgery.", "Keep CSRF enabled for browser flows or document a stateless API with SessionCreationPolicy.STATELESS and token-based authentication."));
        rules.add(delegate(new PermitAllTooBroadRule(), "SPR_ALT003_PERMIT_ALL_TOO_BROAD", "Authorization rule too broad", "Broad permitAll rules can expose endpoints that should require authentication or authorization.", "Restrict public access to explicit endpoints and make the default rule authenticated."));
        rules.add(delegate(new ActuatorExposeAllRule(), "SPR_ALT004_ACTUATOR_EXPOSURE_WILDCARD", "Actuator exposure wildcard", "Exposing all Actuator endpoints can reveal operational details or management operations.", "Expose only health/info/metrics where needed and protect sensitive endpoints with Spring Security and network policy."));
        rules.add(pattern("SPR_ALT005_HEALTH_DETAILS_ALWAYS_PUBLIC", CatalogPatternRule.SourceTarget.CONFIG, Set.of("management.endpoint.health.show-details=always", "show-details: always", "show-details: \"always\"", "show-details: 'always'"), "Health details always exposed", "Always exposing health details can leak dependency names, status and failure information.", "Use management.endpoint.health.show-details=when_authorized or restrict detailed health to protected management users."));
        rules.add(delegate(new EntityExposedInControllerRule(), "SPR_ALT006_ENTITY_RETURNED_BY_CONTROLLER", "JPA entity returned by controller", "Returning entities from REST controllers couples the HTTP contract to persistence and can trigger lazy loading or data exposure.", "Return response DTOs or projections and perform mapping inside the service/application boundary."));
        rules.add(new EntityAcceptedAsRequestBodyAlternativeRule());
        rules.add(delegate(new RequestBodyWithoutValidationRule(), "SPR_ALT008_MISSING_REQUEST_VALIDATION", "Request body without Bean Validation", "Unvalidated input can reach the service layer in an inconsistent state.", "Use @Valid on @RequestBody parameters and Bean Validation constraints on request DTOs."));
        rules.add(delegate(new MissingRestControllerAdviceRule(), "SPR_ALT009_MISSING_CONTROLLER_ADVICE", "REST error handling is not centralized", "Without @RestControllerAdvice, APIs often return inconsistent errors or leak technical messages.", "Introduce @RestControllerAdvice with ProblemDetail or a stable error response model."));
        rules.add(delegate(new OpenEntityManagerInViewEnabledRule(), "SPR_ALT010_OPEN_IN_VIEW_ENABLED", "Open EntityManager in View enabled", "Open EntityManager in View can hide database access in the web layer and cause unexpected lazy-loading queries.", "Disable OSIV and load required data inside transactional service methods using DTOs, projections, fetch joins or entity graphs."));
        rules.add(new RepositoryBusinessLogicAlternativeRule());
        rules.add(new ConcatenatedQueryAlternativeRule());
        rules.add(delegate(new ReadOnlyTransactionMissingRule(), "SPR_ALT013_MISSING_READ_ONLY_TRANSACTION", "Read-only transaction missing", "Read paths without explicit readOnly transactions lose useful intent and persistence optimization hints.", "Use @Transactional(readOnly = true) on service methods that only read data."));
        rules.add(delegate(new JpaEagerFetchingRule(), "SPR_ALT014_ENTITY_RELATION_EAGER_FETCH", "JPA relation uses eager fetch", "EAGER relations can load heavy object graphs and increase N+1 risk.", "Prefer LAZY by default and load required graphs with fetch join, @EntityGraph or projections."));
        rules.add(delegate(new InvalidTransactionalUsageRule(), "SPR_ALT015_TRANSACTIONAL_PRIVATE_METHOD", "@Transactional may be ineffective", "Private, static or final transactional methods may not be intercepted by Spring proxies.", "Put @Transactional on public service methods invoked through the Spring bean proxy, or use TransactionTemplate explicitly."));
        rules.add(delegate(new SelfInvocationProxyRule(), "SPR_ALT016_TRANSACTIONAL_SELF_INVOCATION", "Spring proxy self-invocation", "Self-invocation can bypass @Transactional, @Async, cache and retry advice.", "Move the advised method to another Spring bean or redesign the use-case boundary so calls pass through the proxy."));
        rules.add(delegate(new TransactionalOnControllerRule(), "SPR_ALT017_TRANSACTIONAL_ON_CONTROLLER", "@Transactional on controller", "Transactions in controllers mix HTTP concerns and business unit-of-work boundaries.", "Move the transactional boundary to a service method that represents the application use case."));
        rules.add(new ConfigurationPropertiesValidationAlternativeRule());
        rules.add(delegate(new PossibleSecretInConfigRule(), "SPR_ALT019_SECRET_LIKE_PROPERTY", "Secret-like property committed", "Secrets in repository configuration can leak credentials and make rotation harder.", "Move secrets to environment variables, Vault, secret manager or mounted configuration."));
        rules.add(delegate(new ConsoleLoggingRule(), "SPR_ALT020_SYSTEM_OUT_LOGGING", "Console logging detected", "System.out, System.err and printStackTrace bypass logging levels, appenders, correlation and structured collection.", "Use SLF4J with contextual messages and pass exceptions as logger parameters."));
        return rules;
    }

    private static SpringRule delegate(SpringRule delegate, String id, String title, String whyItMatters, String suggestedFix) {
        return new DelegatingSpringAlternativeRule(delegate, id, Severity.INFO, title, whyItMatters, suggestedFix);
    }

    private static SpringRule pattern(String id, CatalogPatternRule.SourceTarget target, Set<String> anyInLine, String title, String whyItMatters, String suggestedFix) {
        return new CatalogPatternRule(CatalogPatternRule.Definition.line(id, Severity.INFO, target, anyInLine, title, whyItMatters, suggestedFix).max(5));
    }

    private static List<SpringRule> uniqueById(List<SpringRule> rules) {
        Map<String, SpringRule> byId = new LinkedHashMap<>();
        for (SpringRule rule : rules) {
            byId.putIfAbsent(rule.id(), rule);
        }
        return List.copyOf(byId.values());
    }
}

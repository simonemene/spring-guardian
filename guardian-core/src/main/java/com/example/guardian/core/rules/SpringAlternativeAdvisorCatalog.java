package com.example.guardian.core.rules;

import com.example.guardian.core.model.Severity;

import java.util.List;
import java.util.Set;

/**
 * Provides the deterministic Spring Alternative Advisor rule set.
 *
 * @author Simone Meneghetti
 */
final class SpringAlternativeAdvisorCatalog {

    private SpringAlternativeAdvisorCatalog() {
    }

    /**
     * Creates the rules that suggest Spring-native alternatives for manual Java APIs.
     *
     * @return Spring Alternative Advisor rules
     */
    static List<SpringRule> rules() {
        return List.of(
                new ObjectCreationAdvisorRule(
                        "SPR064_MANUAL_OBJECT_MAPPER",
                        Severity.INFO,
                        Set.of("ObjectMapper", "XmlMapper"),
                        "ObjectMapper created manually",
                        "Manual ObjectMapper instances can bypass Spring Boot Jackson modules, date handling, naming policies and custom serializers.",
                        "Inject the Boot-managed ObjectMapper or expose a single configured ObjectMapper bean when customization is required.",
                        true
                ),
                new ObjectCreationAdvisorRule(
                        "SPR065_MANUAL_THREAD_CREATION",
                        Severity.INFO,
                        Set.of("Thread"),
                        "Thread created manually",
                        "Manual threads bypass Spring lifecycle, task execution configuration, observability and graceful shutdown.",
                        "Use TaskExecutor, ThreadPoolTaskExecutor, @Async or TaskScheduler depending on the use case.",
                        false
                ),
                new StaticMethodAdvisorRule(
                        "SPR066_MANUAL_EXECUTOR_CREATION",
                        Severity.INFO,
                        "Executors",
                        Set.of("newFixedThreadPool", "newCachedThreadPool", "newSingleThreadExecutor", "newScheduledThreadPool", "newWorkStealingPool"),
                        "Executor created manually",
                        "Manual executor services can escape Spring lifecycle, shutdown hooks, metrics and central tuning.",
                        "Expose a TaskExecutor or ThreadPoolTaskExecutor bean and inject it where asynchronous work is needed.",
                        false
                ),
                new ObjectCreationAdvisorRule(
                        "SPR067_TIMER_SCHEDULING",
                        Severity.INFO,
                        Set.of("Timer", "TimerTask"),
                        "Timer-based scheduling detected",
                        "Timer and TimerTask are low-level scheduling APIs and are harder to align with Spring profiles, lifecycle and observability.",
                        "Use @Scheduled or TaskScheduler with explicit configuration and tests.",
                        false
                ),
                new ObjectCreationAdvisorRule(
                        "SPR068_MANUAL_JDBC_TEMPLATE",
                        Severity.INFO,
                        Set.of("JdbcTemplate", "NamedParameterJdbcTemplate"),
                        "JdbcTemplate created manually",
                        "Creating templates manually can duplicate transaction, datasource and exception translation configuration.",
                        "Define JdbcTemplate or NamedParameterJdbcTemplate as beans, or inject the auto-configured template when available.",
                        true
                ),
                new ObjectCreationAdvisorRule(
                        "SPR069_MANUAL_PASSWORD_ENCODER",
                        Severity.INFO,
                        Set.of("BCryptPasswordEncoder", "Pbkdf2PasswordEncoder", "SCryptPasswordEncoder", "Argon2PasswordEncoder"),
                        "Password encoder created manually",
                        "Password encoders should be centralized so authentication, tests and migration policies use the same hashing strategy.",
                        "Expose one PasswordEncoder bean and inject it wherever password verification or encoding is required.",
                        true
                ),
                new StaticMethodAdvisorRule(
                        "SPR070_DIRECT_ENVIRONMENT_ACCESS",
                        Severity.INFO,
                        "System",
                        Set.of("getenv", "getProperty"),
                        "Environment read directly from code",
                        "Reading environment or system properties directly hides configuration from Spring binding, validation and profile management.",
                        "Use @ConfigurationProperties, Environment or validated properties objects for application configuration.",
                        false
                ),
                new ObjectCreationAdvisorRule(
                        "SPR071_MANUAL_FILE_RESOURCE_ACCESS",
                        Severity.INFO,
                        Set.of("FileInputStream", "FileOutputStream", "FileReader", "FileWriter"),
                        "File resource opened manually",
                        "Direct file access can hardcode infrastructure assumptions and makes classpath, filesystem and externalized resources harder to swap.",
                        "Use Resource, ResourceLoader or configuration properties to make resource access explicit and testable.",
                        false
                ),
                new SpringBeanCreatedWithNewRule(),
                new MockBeanModernizationAdvisorRule(),
                new StructuredLoggingAdvisorRule(),
                new MockMvcTesterAdvisorRule(),
                new ObjectCreationAdvisorRule(
                        "SPR076_MANUAL_REST_TEMPLATE",
                        Severity.INFO,
                        Set.of("RestTemplate"),
                        "RestTemplate created manually",
                        "A manually created RestTemplate usually bypasses shared timeout, interceptor, error handling and observability configuration.",
                        "Inject a configured RestTemplate bean, or evaluate RestClient for new synchronous HTTP clients.",
                        true
                ),
                new StaticMethodAdvisorRule(
                        "SPR077_WEBCLIENT_BUILDER_CREATED_MANUALLY",
                        Severity.INFO,
                        "WebClient",
                        Set.of("builder", "create"),
                        "WebClient builder created manually",
                        "Manual WebClient creation can skip shared codecs, base URLs, filters, metrics and TLS/proxy configuration.",
                        "Inject WebClient.Builder or a configured WebClient bean and centralize cross-cutting HTTP concerns.",
                        false
                ),
                new StaticMethodAdvisorRule(
                        "SPR078_RESTCLIENT_BUILDER_CREATED_MANUALLY",
                        Severity.INFO,
                        "RestClient",
                        Set.of("builder", "create"),
                        "RestClient builder created directly",
                        "Direct RestClient builders are useful for local cases but can duplicate timeout, interceptor and observation configuration across classes.",
                        "Expose a configured RestClient.Builder or RestClient bean when the client is used by application services.",
                        false
                ),
                new ObjectCreationAdvisorRule(
                        "SPR079_LOW_LEVEL_HTTP_CLIENT",
                        Severity.INFO,
                        Set.of("URL", "HttpURLConnection", "HttpClient", "CloseableHttpClient", "OkHttpClient"),
                        "Low-level HTTP client detected",
                        "Low-level HTTP clients make timeout, retry, proxy, TLS, tracing and testing less consistent in Spring applications.",
                        "Prefer RestClient, WebClient or a centrally configured HTTP client bean.",
                        false
                ),
                new ObjectCreationAdvisorRule(
                        "SPR080_SIMPLE_DATE_FORMAT",
                        Severity.INFO,
                        Set.of("SimpleDateFormat"),
                        "SimpleDateFormat detected",
                        "SimpleDateFormat is mutable and error-prone, especially when reused across threads.",
                        "Use java.time DateTimeFormatter and centralize date formatting policy when it is part of an API contract.",
                        false
                ),
                new ObjectCreationAdvisorRule(
                        "SPR081_GSON_CREATED_MANUALLY",
                        Severity.INFO,
                        Set.of("Gson", "GsonBuilder"),
                        "Gson created manually",
                        "Manual Gson instances can create a second JSON policy that differs from Spring Boot Jackson configuration.",
                        "Prefer the Boot-managed ObjectMapper for application JSON, or centralize Gson as a bean if Gson is explicitly required.",
                        true
                ),
                new AnnotationUsageAdvisorRule(
                        "SPR082_VALUE_INJECTION_FOR_GROUPED_CONFIG",
                        Severity.INFO,
                        Set.of("Value"),
                        "@Value configuration detected",
                        "Many scattered @Value properties make configuration harder to validate, document and evolve.",
                        "Use @ConfigurationProperties with validation for grouped application settings and keep @Value only for isolated values.",
                        false
                ),
                new MissingEnableAnnotationAdvisorRule(
                        "SPR083_ASYNC_WITHOUT_ENABLE_ASYNC",
                        Severity.INFO,
                        "Async",
                        "EnableAsync",
                        "@Async used without @EnableAsync",
                        "Async methods are ignored if asynchronous execution is not enabled in the application context.",
                        "Add @EnableAsync in a configuration class and define the TaskExecutor used by asynchronous work."
                ),
                new MissingEnableAnnotationAdvisorRule(
                        "SPR084_SCHEDULED_WITHOUT_ENABLE_SCHEDULING",
                        Severity.INFO,
                        "Scheduled",
                        "EnableScheduling",
                        "@Scheduled used without @EnableScheduling",
                        "Scheduled methods are ignored if scheduling is not enabled in the application context.",
                        "Add @EnableScheduling in a configuration class and make scheduler settings explicit."
                ),
                new StaticMethodAdvisorRule(
                        "SPR085_DIRECT_LOCAL_DATE_TIME_NOW",
                        Severity.INFO,
                        "LocalDateTime",
                        Set.of("now"),
                        "LocalDateTime.now used directly",
                        "Direct clock access makes time-dependent logic harder to test and reproduce.",
                        "Inject java.time.Clock and use LocalDateTime.now(clock) in business logic.",
                        false
                ),
                new StaticMethodAdvisorRule(
                        "SPR088_THREAD_SLEEP_IN_PRODUCTION_CODE",
                        Severity.INFO,
                        "Thread",
                        Set.of("sleep"),
                        "Thread.sleep in production code",
                        "Sleeping threads hides synchronization problems and wastes server resources.",
                        "Use scheduling, async orchestration, retry/backoff libraries or explicit synchronization primitives.",
                        false
                ),
                new StaticMethodAdvisorRule(
                        "SPR089_MANUAL_VALIDATOR_FACTORY",
                        Severity.INFO,
                        "Validation",
                        Set.of("buildDefaultValidatorFactory"),
                        "ValidatorFactory created manually",
                        "Manual validator factories can bypass Spring validation configuration and constraint validator injection.",
                        "Inject Validator or use @Valid and @Validated at the web/service boundary.",
                        false
                ),
                new ObjectCreationAdvisorRule(
                        "SPR090_MANUAL_CACHE_STRUCTURE",
                        Severity.INFO,
                        Set.of("ConcurrentHashMap"),
                        "Manual in-memory cache candidate",
                        "Maps used as caches inside Spring components are often invisible to eviction policy, metrics and operational control.",
                        "Evaluate Spring Cache abstraction with a configured CacheManager when the map represents reusable cached data.",
                        false
                ),
                new ApplicationContextGetBeanAdvisorRule()
        );
    }
}

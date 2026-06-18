package com.example.guardian.core;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.ReportLanguage;
import com.example.guardian.core.model.RuleGuidance;

/**
 * Builds concise, action-oriented guidance for each report finding.
 * The UI uses this object to show what was detected, the concrete risk,
 * the expected Spring-oriented solution and, when available, official documentation and examples.
 *
 * @author p15518 - Simone Meneghetti
 */
final class RuleGuidanceCatalog {

    private RuleGuidanceCatalog() {
    }

    static RuleGuidance guidance(Finding finding, String localizedTitle, String localizedWhy, String localizedFix, ReportLanguage language) {
        String code = shortCode(finding.ruleId());
        if (code.startsWith("SPR064")) {
            return advisor(language, "new ObjectMapper()", "ObjectMapper gestito da Spring Boot", "https://docs.spring.io/spring-boot/reference/features/json.html", "private final ObjectMapper mapper = new ObjectMapper();", "private final ObjectMapper mapper;\n\npublic MyComponent(ObjectMapper mapper) {\n    this.mapper = mapper;\n}");
        }
        if (code.startsWith("SPR082")) {
            return advisor(language, "@Value usato in più punti", "@ConfigurationProperties validato", "https://docs.spring.io/spring-boot/reference/features/external-config.html", "@Value(\"${external.api.url}\")\nprivate String url;", "@ConfigurationProperties(\"external.api\")\n@Validated\npublic record ApiProperties(@NotBlank String url) {}");
        }
        if (code.startsWith("SPR085")) {
            return advisor(language, "LocalDateTime.now() usato direttamente", "Clock iniettato", "https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/time/Clock.html", "LocalDateTime now = LocalDateTime.now();", "LocalDateTime now = LocalDateTime.now(clock);");
        }
        return switch (code) {
            case "ADV001" -> advisor(language, "new ObjectMapper()", "ObjectMapper gestito da Spring Boot", "https://docs.spring.io/spring-boot/reference/features/json.html", "private final ObjectMapper mapper = new ObjectMapper();", "private final ObjectMapper mapper;\n\npublic MyComponent(ObjectMapper mapper) {\n    this.mapper = mapper;\n}");
            case "ADV002" -> advisor(language, "Gson/GsonBuilder manuale", "Jackson e la configurazione JSON di Spring Boot", "https://docs.spring.io/spring-boot/reference/features/json.html", "Gson gson = new GsonBuilder().create();", "// Usa DTO e ObjectMapper/Jackson configurato da Spring Boot");
            case "ADV003" -> advisor(language, "RestTemplate creato con new", "RestClient o un bean RestTemplate configurato", "https://docs.spring.io/spring-boot/reference/io/rest-client.html", "RestTemplate restTemplate = new RestTemplate();", "@Bean\nRestClient restClient(RestClient.Builder builder) {\n    return builder.baseUrl(baseUrl).build();\n}");
            case "ADV004" -> advisor(language, "WebClient.create() sparso", "WebClient.Builder gestito da Spring", "https://docs.spring.io/spring-boot/reference/io/rest-client.html", "WebClient client = WebClient.create(url);", "@Bean\nWebClient apiClient(WebClient.Builder builder) {\n    return builder.baseUrl(url).build();\n}");
            case "ADV005" -> advisor(language, "RestClient.create()/builder duplicato", "RestClient bean centralizzato", "https://docs.spring.io/spring-boot/reference/io/rest-client.html", "RestClient client = RestClient.create(url);", "@Bean\nRestClient apiClient(RestClient.Builder builder) {\n    return builder.baseUrl(url).build();\n}");
            case "ADV006" -> advisor(language, "HttpURLConnection/URL", "RestClient o WebClient", "https://docs.spring.io/spring-boot/reference/io/rest-client.html", "HttpURLConnection con = (HttpURLConnection) url.openConnection();", "ResponseDto body = restClient.get().uri(\"/resource\").retrieve().body(ResponseDto.class);");
            case "ADV007" -> advisor(language, "OkHttpClient manuale", "client HTTP configurato come bean Spring o RestClient/WebClient", "https://docs.spring.io/spring-boot/reference/io/rest-client.html", "OkHttpClient client = new OkHttpClient();", "@Bean\nRestClient apiClient(RestClient.Builder builder) {\n    return builder.build();\n}");
            case "ADV008" -> advisor(language, "Thread creato manualmente", "TaskExecutor, @Async o TaskScheduler", "https://docs.spring.io/spring-framework/reference/integration/scheduling.html", "new Thread(task).start();", "@Async\npublic void runAsync() {\n    task.run();\n}");
            case "ADV009" -> advisor(language, "ExecutorService manuale", "ThreadPoolTaskExecutor configurato come bean", "https://docs.spring.io/spring-framework/reference/integration/scheduling.html", "ExecutorService executor = Executors.newFixedThreadPool(4);", "@Bean\nThreadPoolTaskExecutor applicationTaskExecutor() {\n    return new ThreadPoolTaskExecutor();\n}");
            case "ADV010" -> advisor(language, "Timer/TimerTask", "@Scheduled o TaskScheduler", "https://docs.spring.io/spring-framework/reference/integration/scheduling.html", "new Timer().schedule(task, delay);", "@Scheduled(fixedDelayString = \"${job.delay}\")\nvoid run() {\n    service.execute();\n}");
            case "ADV011" -> advisor(language, "Thread.sleep", "Scheduler, retry con backoff o attesa controllata", "https://docs.spring.io/spring-framework/reference/integration/scheduling.html", "Thread.sleep(5000);", "@Scheduled(fixedDelayString = \"${polling.delay}\")\nvoid poll() {\n    service.poll();\n}");
            case "ADV012" -> advisor(language, "System.getenv/System.getProperty", "Environment o @ConfigurationProperties", "https://docs.spring.io/spring-boot/reference/features/external-config.html", "String url = System.getenv(\"API_URL\");", "@ConfigurationProperties(\"external.api\")\npublic record ApiProperties(String url) {}");
            case "ADV013" -> advisor(language, "molti @Value sparsi", "@ConfigurationProperties validato", "https://docs.spring.io/spring-boot/reference/features/external-config.html", "@Value(\"${external.api.url}\")\nprivate String url;", "@ConfigurationProperties(\"external.api\")\n@Validated\npublic record ApiProperties(@NotBlank String url) {}");
            case "ADV014" -> advisor(language, "FileInputStream/FileReader diretto", "Resource o ResourceLoader", "https://docs.spring.io/spring-framework/reference/core/resources.html", "new FileInputStream(path);", "Resource resource = resourceLoader.getResource(location);");
            case "ADV015" -> advisor(language, "ClassPathResource ripetuto", "Resource injection o ResourceLoader centralizzato", "https://docs.spring.io/spring-framework/reference/core/resources.html", "new ClassPathResource(\"template.csv\");", "public MyService(@Value(\"classpath:template.csv\") Resource template) { ... }");
            case "ADV016" -> advisor(language, "ConcurrentHashMap usata come cache", "Spring Cache con @Cacheable/@CacheEvict", "https://docs.spring.io/spring-framework/reference/integration/cache.html", "private final Map<String, Value> cache = new ConcurrentHashMap<>();", "@Cacheable(\"values\")\npublic Value find(String id) {\n    return repository.find(id);\n}");
            case "ADV017" -> advisor(language, "Map statica come registry", "bean, factory o strategy Spring", "https://docs.spring.io/spring-framework/reference/core/beans.html", "static Map<String, Handler> handlers = new HashMap<>();", "public Router(List<Handler> handlers) {\n    this.handlers = indexByType(handlers);\n}");
            case "ADV018" -> advisor(language, "ApplicationContext.getBean nel codice business", "constructor injection", "https://docs.spring.io/spring-framework/reference/core/beans/dependencies/factory-collaborators.html", "MyService s = context.getBean(MyService.class);", "public Controller(MyService service) {\n    this.service = service;\n}");
            case "ADV019" -> advisor(language, "new Service/Repository dentro bean Spring", "dependency injection", "https://docs.spring.io/spring-framework/reference/core/beans/dependencies/factory-collaborators.html", "OrderService service = new OrderService();", "public OrderController(OrderService service) {\n    this.service = service;\n}");
            case "ADV020" -> advisor(language, "validazione manuale ripetuta", "Bean Validation", "https://docs.spring.io/spring-framework/reference/core/validation/beanvalidation.html", "if (request.name() == null || request.name().isBlank()) { ... }", "public record Request(@NotBlank String name) {}\n\n@PostMapping\nvoid create(@Valid @RequestBody Request request) { ... }");
            case "ADV021" -> advisor(language, "Validation.buildDefaultValidatorFactory", "Validator gestito da Spring", "https://docs.spring.io/spring-framework/reference/core/validation/beanvalidation.html", "Validator v = Validation.buildDefaultValidatorFactory().getValidator();", "public MyService(Validator validator) {\n    this.validator = validator;\n}");
            case "ADV022" -> advisor(language, "transazioni begin/commit manuali", "@Transactional", "https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative.html", "connection.setAutoCommit(false);\nconnection.commit();", "@Transactional\npublic void executeUseCase() {\n    repository.save(entity);\n}");
            case "ADV023" -> advisor(language, "TransactionTemplate sparso", "confine transazionale nel service con @Transactional", "https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative.html", "transactionTemplate.execute(status -> doWork());", "@Transactional\npublic void doWork() {\n    ...\n}");
            case "ADV024" -> advisor(language, "retry loop manuale", "Spring Retry o Resilience4j", "https://docs.spring.io/spring-retry/docs/current/reference/html/", "for (int i = 0; i < 3; i++) { tryCall(); }", "@Retryable(maxAttemptsExpression = \"${retry.max-attempts}\")\npublic Response call() { ... }");
            case "ADV025" -> advisor(language, "circuit breaker manuale", "Resilience4j", "https://resilience4j.readme.io/docs/circuitbreaker", "if (failures > threshold) { return fallback; }", "@CircuitBreaker(name = \"externalApi\", fallbackMethod = \"fallback\")\npublic Response call() { ... }");
            case "ADV026" -> advisor(language, "controller custom per health check", "Actuator HealthIndicator", "https://docs.spring.io/spring-boot/reference/actuator/endpoints.html", "@GetMapping(\"/health\") String health() { return \"OK\"; }", "@Component\nclass ExternalHealthIndicator implements HealthIndicator {\n    public Health health() { return Health.up().build(); }\n}");
            case "ADV027" -> advisor(language, "metriche manuali nei log o contatori custom", "Micrometer MeterRegistry", "https://docs.spring.io/spring-boot/reference/actuator/metrics.html", "log.info(\"processed={}\", count);", "Counter counter = meterRegistry.counter(\"orders.processed\");\ncounter.increment();");
            case "ADV028" -> advisor(language, "audit manuale sparso", "ApplicationEvent o astrazione audit centralizzata", "https://docs.spring.io/spring-framework/reference/core/beans/context-introduction.html#context-functionality-events", "auditRepository.save(new Audit(...));", "applicationEventPublisher.publishEvent(new OrderChangedEvent(...));");
            case "ADV029" -> advisor(language, "event bus/listener manuale", "ApplicationEventPublisher e @EventListener", "https://docs.spring.io/spring-framework/reference/core/beans/context-introduction.html#context-functionality-events", "customEventBus.publish(event);", "publisher.publishEvent(event);\n\n@EventListener\nvoid handle(Event event) { ... }");
            case "ADV030" -> advisor(language, "inizializzazione pesante nel costruttore", "@PostConstruct, ApplicationRunner o SmartLifecycle", "https://docs.spring.io/spring-framework/reference/core/beans/annotation-config/postconstruct-and-predestroy-annotations.html", "public Service() { initRemoteState(); }", "@PostConstruct\nvoid init() {\n    initRemoteState();\n}");
            case "ADV031" -> advisor(language, "destroy manuale non gestito", "@PreDestroy o DisposableBean", "https://docs.spring.io/spring-framework/reference/core/beans/annotation-config/postconstruct-and-predestroy-annotations.html", "Runtime.getRuntime().addShutdownHook(...);", "@PreDestroy\nvoid close() {\n    resource.close();\n}");
            case "ADV032" -> advisor(language, "SimpleDateFormat", "DateTimeFormatter", "https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/time/format/DateTimeFormatter.html", "new SimpleDateFormat(\"yyyy-MM-dd\");", "private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;");
            case "ADV033" -> advisor(language, "LocalDateTime.now diretto", "Clock iniettato", "https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/time/Clock.html", "LocalDateTime now = LocalDateTime.now();", "LocalDateTime now = LocalDateTime.now(clock);");
            case "ADV034" -> advisor(language, "UUID.randomUUID diretto in logica testabile", "generatore UUID come bean", "https://docs.spring.io/spring-framework/reference/core/beans.html", "String id = UUID.randomUUID().toString();", "public MyService(IdGenerator generator) {\n    this.generator = generator;\n}");
            case "ADV035" -> advisor(language, "Random diretto nella logica business", "generatore iniettato/testabile", "https://docs.spring.io/spring-framework/reference/core/beans.html", "Random random = new Random();", "public MyService(RandomGenerator generator) {\n    this.generator = generator;\n}");
            case "ADV036" -> advisor(language, "PasswordEncoder locale", "PasswordEncoder bean centralizzato", "https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html", "new BCryptPasswordEncoder();", "@Bean\nPasswordEncoder passwordEncoder() {\n    return PasswordEncoderFactories.createDelegatingPasswordEncoder();\n}");
            case "ADV037" -> advisor(language, "JdbcTemplate creato manualmente", "JdbcTemplate auto-configurato come bean", "https://docs.spring.io/spring-boot/reference/data/sql.html", "new JdbcTemplate(dataSource);", "public Repository(JdbcTemplate jdbcTemplate) {\n    this.jdbcTemplate = jdbcTemplate;\n}");
            case "ADV038" -> advisor(language, "DataSource creato manualmente", "DataSource auto-configurato via properties", "https://docs.spring.io/spring-boot/reference/data/sql.html", "DriverManagerDataSource ds = new DriverManagerDataSource();", "spring.datasource.url=${DB_URL}\nspring.datasource.username=${DB_USER}");
            case "ADV039" -> advisor(language, "factory manuale di oggetti", "@Bean o factory component Spring", "https://docs.spring.io/spring-framework/reference/core/beans/java.html", "return new Client(url, timeout);", "@Bean\nClient client(ClientProperties properties) {\n    return new Client(properties.url(), properties.timeout());\n}");
            case "ADV040" -> advisor(language, "mapping manuale ripetitivo", "mapper Spring bean o MapStruct", "https://mapstruct.org/documentation/stable/reference/html/", "dto.setName(entity.getName());", "@Mapper(componentModel = \"spring\")\ninterface CustomerMapper { CustomerDto toDto(Customer entity); }");
            case "ADV041" -> advisor(language, "parsing JSON manuale nel controller", "binding automatico su DTO", "https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/requestbody.html", "JsonNode node = objectMapper.readTree(body);", "public Response create(@Valid @RequestBody CreateRequest request) { ... }");
            case "ADV042" -> advisor(language, "parsing manuale query param", "binding Spring MVC", "https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/requestparam.html", "Integer page = Integer.valueOf(request.getParameter(\"page\"));", "public PageDto list(@RequestParam int page) { ... }");
            case "ADV043" -> advisor(language, "gestione errori manuale", "@RestControllerAdvice", "https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-exceptionhandler.html", "try { ... } catch (Exception e) { return ResponseEntity.status(500).build(); }", "@RestControllerAdvice\nclass ApiExceptionHandler {\n    @ExceptionHandler(MyException.class) ProblemDetail handle(MyException ex) { ... }\n}");
            case "ADV044" -> advisor(language, "error response custom non standardizzata", "ProblemDetail/ErrorResponse", "https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-rest-exceptions.html", "return new ErrorDto(\"ERR\", message);", "ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);\nproblem.setDetail(message);");
            case "ADV045" -> advisor(language, "while(true) per scheduling", "@Scheduled o TaskScheduler", "https://docs.spring.io/spring-framework/reference/integration/scheduling.html", "while (true) { poll(); Thread.sleep(delay); }", "@Scheduled(fixedDelayString = \"${polling.delay}\")\nvoid poll() { ... }");
            case "ADV046" -> advisor(language, "polling senza backoff", "TaskScheduler con policy o retry/backoff", "https://docs.spring.io/spring-framework/reference/integration/scheduling.html", "poll(); poll(); poll();", "@Scheduled(fixedDelayString = \"${polling.delay}\")\nvoid pollWithBackoffPolicy() { ... }");
            case "ADV047" -> advisor(language, "email sender manuale", "JavaMailSender", "https://docs.spring.io/spring-boot/reference/io/email.html", "Transport.send(message);", "public MailService(JavaMailSender mailSender) {\n    this.mailSender = mailSender;\n}");
            case "ADV048" -> advisor(language, "client HTTP senza timeout centralizzato", "RestClient/WebClient builder configurato", "https://docs.spring.io/spring-boot/reference/io/rest-client.html", "RestClient.create(url);", "@Bean\nRestClient client(RestClient.Builder builder) {\n    return builder.requestFactory(timeoutFactory()).build();\n}");
            case "ADV049" -> advisor(language, "properties holder statico", "@ConfigurationProperties", "https://docs.spring.io/spring-boot/reference/features/external-config.html", "public static String API_URL;", "@ConfigurationProperties(\"api\")\npublic record ApiProperties(String url) {}");
            case "ADV050" -> advisor(language, "utility statica con dipendenze", "bean service/component", "https://docs.spring.io/spring-framework/reference/core/beans.html", "StaticUtil.doWork(repository);", "@Service\nclass WorkService {\n    private final Repository repository;\n}");
            case "ADV051" -> advisor(language, "BeanFactory pattern manuale", "@Configuration con @Bean o factory component", "https://docs.spring.io/spring-framework/reference/core/beans/java.html", "if (type.equals(\"A\")) return new A();", "@Bean\nStrategy strategy() { return new AStrategy(); }");
            case "ADV052" -> advisor(language, "reflection per creare componenti", "DI o factory Spring", "https://docs.spring.io/spring-framework/reference/core/beans.html", "clazz.getDeclaredConstructor().newInstance();", "public MyFactory(List<Strategy> strategies) { ... }");
            case "ADV053" -> advisor(language, "singleton manuale", "singleton bean Spring", "https://docs.spring.io/spring-framework/reference/core/beans/factory-scopes.html", "private static final Service INSTANCE = new Service();", "@Service\nclass Service { ... }");
            case "ADV054" -> advisor(language, "gestione locale/message manuale", "MessageSource", "https://docs.spring.io/spring-framework/reference/core/beans/context-introduction.html#context-functionality-messagesource", "messages.get(locale).get(code);", "messageSource.getMessage(code, args, locale);");
            case "ADV055" -> advisor(language, "mappa i18n manuale", "MessageSource", "https://docs.spring.io/spring-framework/reference/core/beans/context-introduction.html#context-functionality-messagesource", "Map<Locale, String> labels = ...;", "messageSource.getMessage(\"label.key\", null, locale);");
            case "ADV056" -> advisor(language, "event bus interno manuale", "ApplicationEventPublisher", "https://docs.spring.io/spring-framework/reference/core/beans/context-introduction.html#context-functionality-events", "eventBus.send(event);", "applicationEventPublisher.publishEvent(event);");
            case "ADV057" -> advisor(language, "lock scheduler manuale", "TaskScheduler con policy o lock dedicato", "https://docs.spring.io/spring-framework/reference/integration/scheduling.html", "if (running.compareAndSet(false, true)) { ... }", "@Scheduled(...)\nvoid run() {\n    schedulerService.executeSafely();\n}");
            case "ADV058" -> advisor(language, "rate limit manuale nel controller", "filter/interceptor/gateway policy", "https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-config/interceptors.html", "if (tooManyRequests(user)) return 429;", "Registra un HandlerInterceptor o applica la policy nel gateway/API layer.");
            case "ADV059" -> advisor(language, "filtro servlet manuale non ordinato", "FilterRegistrationBean o SecurityFilterChain", "https://docs.spring.io/spring-boot/how-to/webserver.html", "new MyFilter();", "@Bean\nFilterRegistrationBean<MyFilter> myFilter() { ... }");
            case "ADV060" -> advisor(language, "interceptor non centralizzato", "WebMvcConfigurer", "https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-config/interceptors.html", "registry locale non governato", "@Configuration\nclass WebConfig implements WebMvcConfigurer {\n    public void addInterceptors(InterceptorRegistry registry) { ... }\n}");
            case "ADV061" -> advisor(language, "CORS manuale per controller", "CorsConfigurationSource o WebMvcConfigurer", "https://docs.spring.io/spring-framework/reference/web/webmvc-cors.html", "response.addHeader(\"Access-Control-Allow-Origin\", \"*\");", "@Bean\nCorsConfigurationSource corsConfigurationSource() { ... }");
            case "ADV062" -> advisor(language, "moduli ObjectMapper configurati a mano", "Jackson2ObjectMapperBuilderCustomizer", "https://docs.spring.io/spring-boot/reference/features/json.html", "objectMapper.registerModule(new JavaTimeModule());", "@Bean\nJackson2ObjectMapperBuilderCustomizer jsonCustomizer() { ... }");
            case "ADV063" -> advisor(language, "serializzazione date locale/manuale", "configurazione Jackson centralizzata", "https://docs.spring.io/spring-boot/reference/features/json.html", "@JsonFormat sparso ovunque", "spring.jackson.date-format=... oppure Jackson customizer centralizzato");
            case "ADV064" -> advisor(language, "parsing enum manuale", "Converter o Formatter Spring", "https://docs.spring.io/spring-framework/reference/core/validation/convert.html", "Status.valueOf(value.toUpperCase());", "@Component\nclass StatusConverter implements Converter<String, Status> { ... }");
            case "ADV065" -> advisor(language, "request logging filter manuale", "filtro centralizzato o osservabilità", "https://docs.spring.io/spring-boot/reference/actuator/observability.html", "log.info(\"request {}\", request.getRequestURI());", "Usa osservabilità/MDC/filtro centralizzato configurato come bean.");
            case "ADV066" -> advisor(language, "CompletableFuture.supplyAsync senza executor", "@Async con executor configurato", "https://docs.spring.io/spring-framework/reference/integration/scheduling.html", "CompletableFuture.supplyAsync(() -> work());", "@Async(\"applicationTaskExecutor\")\npublic CompletableFuture<Result> work() { ... }");
            case "ADV067" -> advisor(language, "ScheduledExecutorService manuale", "TaskScheduler", "https://docs.spring.io/spring-framework/reference/integration/scheduling.html", "Executors.newScheduledThreadPool(1);", "@Bean\nTaskScheduler taskScheduler() {\n    return new ThreadPoolTaskScheduler();\n}");
            case "ADV068" -> advisor(language, "synchronized sul service", "lock esplicito, transazione o componente di coordinamento", "https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative.html", "public synchronized void process() { ... }", "Usa transazioni, lock applicativi espliciti o un componente dedicato alla concorrenza.");
            case "ADV069" -> advisor(language, "evict cache manuale", "@CacheEvict", "https://docs.spring.io/spring-framework/reference/integration/cache.html", "cache.remove(id);", "@CacheEvict(cacheNames = \"customers\", key = \"#id\")\npublic void update(String id) { ... }");
            case "ADV070" -> advisor(language, "lookup cache manuale", "@Cacheable", "https://docs.spring.io/spring-framework/reference/integration/cache.html", "if (cache.containsKey(id)) return cache.get(id);", "@Cacheable(cacheNames = \"customers\", key = \"#id\")\npublic Customer find(String id) { ... }");
            case "ADV071" -> advisor(language, "retry counter manuale in Batch", "Spring Batch retry/skip policy", "https://docs.spring.io/spring-batch/reference/step/chunk-oriented-processing/retry-logic.html", "executionContext.putInt(\"retry\", retry + 1);", "faultTolerant().retry(RemoteException.class).retryLimit(3)");
            case "ADV072" -> advisor(language, "audit batch scritto ovunque", "JobExecutionListener/StepExecutionListener", "https://docs.spring.io/spring-batch/reference/step/chunk-oriented-processing/intercepting-execution.html", "auditRepository.save(...) in ogni writer", "class AuditJobListener implements JobExecutionListener { ... }");
            case "ADV073" -> advisor(language, "paging SQL fragile", "Pageable o JdbcPagingItemReader", "https://docs.spring.io/spring-batch/reference/readers-and-writers/database.html", "SELECT * FROM table LIMIT 100 OFFSET ?", "JdbcPagingItemReaderBuilder<T>().sortKeys(Map.of(\"ID\", ASCENDING))");
            case "ADV074" -> advisor(language, "repository semplice implementato a mano", "Spring Data repository", "https://docs.spring.io/spring-data/jpa/reference/repositories.html", "class CustomerRepository { ... JDBC manuale ... }", "interface CustomerRepository extends JpaRepository<Customer, Long> {}");
            case "ADV075" -> advisor(language, "query read-only senza readOnly", "@Transactional(readOnly = true)", "https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html", "@Transactional\npublic List<Customer> findAll() { ... }", "@Transactional(readOnly = true)\npublic List<Customer> findAll() { ... }");
            case "ADV076" -> advisor(language, "validazione DTO manuale nel service", "Bean Validation groups", "https://docs.spring.io/spring-framework/reference/core/validation/beanvalidation.html", "validateCreate(request);", "public record CreateRequest(@NotBlank(groups = Create.class) String name) {}");
            case "ADV077" -> advisor(language, "documentazione REST manuale/commenti", "OpenAPI annotations", "https://springdoc.org/", "// Creates a customer\n@PostMapping", "@Operation(summary = \"Create customer\")\n@PostMapping");
            case "ADV078" -> advisor(language, "mappatura status manuale", "ResponseEntity/ProblemDetail policy centralizzata", "https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-rest-exceptions.html", "if (error) return ResponseEntity.status(400).body(map);", "throw new BusinessException(...); // gestita da @RestControllerAdvice");
            case "ADV079" -> advisor(language, "if su profilo/ambiente", "@Profile o @ConditionalOnProperty", "https://docs.spring.io/spring-boot/reference/features/profiles.html", "if (\"prod\".equals(env)) { ... }", "@Profile(\"prod\")\n@Bean\nProdClient prodClient() { ... }");
            case "ADV080" -> advisor(language, "feature switch manuale", "@ConditionalOnProperty e properties tipizzate", "https://docs.spring.io/spring-boot/reference/features/developing-auto-configuration.html#features.developing-auto-configuration.condition-annotations", "if (enabled) { register(); }", "@ConditionalOnProperty(prefix=\"feature.x\", name=\"enabled\", havingValue=\"true\")");
            case "ADV081" -> advisor(language, "CSV reader manuale", "Spring Batch FlatFileItemReader", "https://docs.spring.io/spring-batch/reference/readers-and-writers/flat-files/file-item-reader.html", "Files.lines(path).map(this::parse);", "FlatFileItemReaderBuilder<Row>().resource(resource).delimited().names(...).build();");
            case "ADV082" -> advisor(language, "CSV writer manuale", "Spring Batch FlatFileItemWriter", "https://docs.spring.io/spring-batch/reference/readers-and-writers/flat-files/file-item-writer.html", "Files.writeString(path, csv);", "FlatFileItemWriterBuilder<Row>().resource(resource).delimited().names(...).build();");
            case "ADV083" -> advisor(language, "XML parsing manuale ripetuto", "Jackson XML/JAXB configurato come bean", "https://docs.spring.io/spring-boot/reference/features/json.html", "DocumentBuilderFactory.newInstance();", "Usa un mapper XML configurato come bean o binding dedicato.");
            case "ADV084" -> advisor(language, "retry HTTP manuale", "RetryTemplate o Resilience4j", "https://docs.spring.io/spring-retry/docs/current/reference/html/", "for (int attempt=0; attempt<3; attempt++) call();", "@Retryable\npublic Response callRemote() { ... }");
            case "ADV085" -> advisor(language, "timeout gestito con thread", "timeout del client HTTP", "https://docs.spring.io/spring-boot/reference/io/rest-client.html", "future.get(5, TimeUnit.SECONDS);", "Configura connect/read timeout nel RestClient/WebClient builder.");
            case "ADV086" -> advisor(language, "polling scheduler custom", "Spring Batch, Quartz o TaskScheduler", "https://docs.spring.io/spring-framework/reference/integration/scheduling.html", "while (true) { checkTable(); }", "@Scheduled(...) void checkTable() { ... } oppure Job Spring Batch pianificato.");
            case "ADV087" -> advisor(language, "mappa idempotenza manuale", "repository/cache/idempotency service", "https://docs.spring.io/spring-framework/reference/integration/cache.html", "processedIds.put(id, true);", "@Cacheable oppure tabella idempotency con vincolo unico e service dedicato.");
            case "ADV088" -> advisor(language, "contesto request statico", "interceptor/filter controllato", "https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-config/interceptors.html", "RequestContext.set(user);", "HandlerInterceptor imposta e pulisce il contesto in afterCompletion.");
            case "ADV089" -> advisor(language, "parsing manuale Authorization header", "Spring Security filter/converter", "https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html", "String token = header.substring(7);", "Configura oauth2ResourceServer().jwt() o un AuthenticationConverter dedicato.");
            case "ADV090" -> advisor(language, "controllo ruoli manuale", "AuthorizationManager o method security", "https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html", "if (user.getRoles().contains(\"ADMIN\"))", "@PreAuthorize(\"hasRole('ADMIN')\")");
            case "ADV091" -> advisor(language, "parametri paginazione manuali", "Pageable", "https://docs.spring.io/spring-data/commons/reference/repositories/core-extensions.html#core.web", "@RequestParam int page, @RequestParam int size", "public PageDto list(Pageable pageable) { ... }");
            case "ADV092" -> advisor(language, "sorting manuale non controllato", "Sort/Pageable con whitelist", "https://docs.spring.io/spring-data/commons/reference/repositories/core-extensions.html#core.web", "String sort = request.getParameter(\"sort\");", "Accetta Pageable e valida/limita i campi ordinabili.");
            case "ADV093" -> advisor(language, "flag lifecycle manuali", "SmartLifecycle", "https://docs.spring.io/spring-framework/reference/core/beans/factory-nature.html", "boolean started; void start(){ started=true; }", "class Worker implements SmartLifecycle { ... }");
            case "ADV094" -> advisor(language, "ApplicationRunner con logica pesante", "runner leggero e startup controllato", "https://docs.spring.io/spring-boot/reference/features/spring-application.html#features.spring-application.command-line-runner", "run() { importAllData(); }", "Sposta il job in Batch/Scheduler e lascia al runner solo bootstrap controllato.");
            case "ADV095" -> advisor(language, "migrazione DB manuale allo startup", "Flyway o Liquibase", "https://docs.spring.io/spring-boot/reference/howto/data-initialization.html", "jdbcTemplate.execute(\"ALTER TABLE ...\");", "Usa db/migration/V1__init.sql con Flyway o changelog Liquibase.");
            case "ADV096" -> advisor(language, "script SQL manuali all’avvio", "Flyway o Liquibase", "https://docs.spring.io/spring-boot/reference/howto/data-initialization.html", "runSql(\"schema.sql\");", "Gestisci evoluzioni schema con Flyway/Liquibase.");
            case "ADV097" -> advisor(language, "reload config custom", "Spring Cloud Config o strategia refresh governata", "https://docs.spring.io/spring-cloud-config/docs/current/reference/html/", "reloadPropertiesFromFile();", "Usa configurazione esternalizzata e refresh strategy documentata.");
            case "ADV098" -> advisor(language, "parsing file segreti manuale", "externalized config/secret manager", "https://docs.spring.io/spring-boot/reference/features/external-config.html", "Files.readString(Path.of(\"secret.txt\"));", "Inietta il segreto via environment, secret manager o configurazione montata.");
            case "ADV099" -> advisor(language, "object pooling custom", "pool/client bean gestito", "https://docs.spring.io/spring-framework/reference/core/beans/factory-scopes.html", "Queue<Client> pool = new ArrayDeque<>();", "Configura il client/pool come bean e gestisci lifecycle/timeout centralmente.");
            case "ADV100" -> advisor(language, "thread naming manuale", "ThreadPoolTaskExecutor configuration", "https://docs.spring.io/spring-framework/reference/integration/scheduling.html", "new Thread(r, \"worker-\" + id);", "executor.setThreadNamePrefix(\"app-worker-\");");
            default -> byArea(finding, localizedTitle, localizedWhy, localizedFix, language);
        };
    }

    private static RuleGuidance byArea(Finding finding, String title, String why, String fix, ReportLanguage language) {
        String code = shortCode(finding.ruleId());
        if (language == ReportLanguage.ENGLISH) {
            return englishAreaGuidance(code, title, why, fix);
        }
        return italianAreaGuidance(code, title, why, fix);
    }

    private static RuleGuidance italianAreaGuidance(String code, String title, String why, String fix) {
        if (code.startsWith("SEC")) {
            return new RuleGuidance(
                    "Ho rilevato una configurazione Spring Security rischiosa: " + title + ".",
                    why,
                    fix + " Mantieni le regole di sicurezza esplicite, ordinate e verificabili; evita matcher troppo ampi e profili di produzione permissivi.",
                    "SecurityFilterChain, AuthorizationManager, @EnableMethodSecurity, configurazione OAuth2 Resource Server/JWT dove applicabile",
                    "https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html",
                    "http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());",
                    "http.authorizeHttpRequests(auth -> auth\n    .requestMatchers(\"/actuator/health\").permitAll()\n    .requestMatchers(HttpMethod.GET, \"/api/v1/public/**\").permitAll()\n    .anyRequest().authenticated());"
            );
        }
        if (code.startsWith("WEB")) {
            return new RuleGuidance(
                    "Ho rilevato un problema nel layer Web/API: " + title + ".",
                    why,
                    fix + " Il controller dovrebbe restare sottile: validazione DTO, delega al service, gestione errori centralizzata e contratto HTTP documentato.",
                    "@RestController, DTO con Bean Validation, @RestControllerAdvice, ProblemDetail, OpenAPI @Operation",
                    "https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller.html",
                    "@PostMapping(\"/customers\")\npublic Object create(@RequestBody Customer entity) { ... }",
                    "@Operation(summary = ...)\n@PostMapping(/api/v1/customers)\npublic ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) { ... }"
            );
        }
        if (code.startsWith("BAT")) {
            return new RuleGuidance(
                    "Ho rilevato un rischio operativo Spring Batch: " + title + ".",
                    why,
                    fix + " Un batch production-ready deve dichiarare chiaramente parametri, restartability, chunk, retry/skip, listener, metriche e idempotenza.",
                    "JobParametersValidator, JobExecutionListener, StepExecutionListener, faultTolerant(), JdbcPagingItemReader con sort key stabile",
                    "https://docs.spring.io/spring-batch/reference/",
                    "return new StepBuilder(...).tasklet(tasklet, tx).build();",
                    "StepBuilder(...).chunk(properties.chunkSize(), transactionManager).reader(reader()).processor(processor()).writer(writer()).faultTolerant().retryLimit(3).listener(stepListener).build();"
            );
        }
        if (code.startsWith("ARCH")) {
            return new RuleGuidance(
                    "Ho rilevato un confine architetturale poco chiaro: " + title + ".",
                    why,
                    fix + " Definisci la direzione delle dipendenze e separa controller, casi d'uso, dominio e infrastruttura. Il package common/util non deve diventare un contenitore di logica applicativa.",
                    "Struttura per layer o feature, package domain/application/infrastructure, porte e adapter nei profili DDD/esagonali",
                    "https://docs.spring.io/spring-modulith/reference/verification.html",
                    "utils/GvLog contiene costanti, logica applicativa o dipendenze di più layer.",
                    "Sposta la logica nel layer corretto: logging tecnico in infrastructure/logging, policy di business in domain/application, mapping in mapper dedicati. Mantieni utils solo per funzioni pure e realmente trasversali."
            );
        }
        if (code.startsWith("POM")) {
            return new RuleGuidance(
                    "Ho rilevato un problema di governance Maven/Spring Boot: " + title + ".",
                    why,
                    fix + " Lascia al parent/BOM Spring Boot la gestione delle versioni quando possibile e mantieni scope, plugin e dependencyManagement coerenti tra i moduli.",
                    "spring-boot-starter-parent, spring-boot-dependencies BOM, dependencyManagement, pluginManagement",
                    "https://docs.spring.io/spring-boot/maven-plugin/using.html",
                    "<dependency>\n  <groupId>org.springframework.boot</groupId>\n  <artifactId>spring-boot-starter-web</artifactId>\n  <version>...</version>\n</dependency>",
                    "<parent>\n  <groupId>org.springframework.boot</groupId>\n  <artifactId>spring-boot-starter-parent</artifactId>\n  <version>${spring-boot.version}</version>\n</parent>"
            );
        }
        if (code.startsWith("CLD")) {
            return new RuleGuidance(
                    "Ho rilevato un problema di configurazione cloud/12-factor: " + title + ".",
                    why,
                    fix + " L'artefatto dovrebbe restare neutro rispetto all'ambiente: valori runtime, endpoint, segreti, path e cron devono arrivare dalla piattaforma di deploy.",
                    "Externalized Configuration, Environment, @ConfigurationProperties, ConfigMap/Secret/Vault o secret manager",
                    "https://docs.spring.io/spring-boot/reference/features/external-config.html",
                    "external.api.url=https://server-prod/api",
                    "external.api.url=${EXTERNAL_API_URL}\n\n@ConfigurationProperties(\"external.api\")\npublic record ExternalApiProperties(String url) {}"
            );
        }
        if (code.startsWith("OBS")) {
            return new RuleGuidance(
                    "Ho rilevato una carenza di osservabilità: " + title + ".",
                    why,
                    fix + " In produzione servono log strutturati, health check, metriche, correlation id e indicatori dedicati per dipendenze critiche.",
                    "Spring Boot Actuator, Micrometer, ObservationRegistry, HealthIndicator, logging strutturato",
                    "https://docs.spring.io/spring-boot/reference/actuator/observability.html",
                    "System.out.println(\"processed=\" + count);",
                    "Counter processed = meterRegistry.counter(...);\nprocessed.increment(count);"
            );
        }
        return new RuleGuidance(
                "Ho rilevato questo problema: " + title + ".",
                why,
                fix,
                "Pattern Spring coerente con il tipo di componente rilevato",
                "https://docs.spring.io/spring-framework/reference/",
                "// Codice rilevato mostrato nella sezione evidenza",
                "// Applica la correzione indicata mantenendo responsabilità e layer separati"
        );
    }

    private static RuleGuidance englishAreaGuidance(String code, String title, String why, String fix) {
        if (code.startsWith("SEC")) {
            return new RuleGuidance("Detected risky Spring Security configuration: " + title + ".", why, fix + " Keep security rules explicit, ordered and verifiable.", "SecurityFilterChain, AuthorizationManager, @EnableMethodSecurity, OAuth2 Resource Server/JWT when applicable", "https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html", "http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());", "http.authorizeHttpRequests(auth -> auth\n    .requestMatchers(healthEndpoint).permitAll()\n    .anyRequest().authenticated());");
        }
        if (code.startsWith("WEB")) {
            return new RuleGuidance("Detected Web/API layer issue: " + title + ".", why, fix + " Keep controllers thin: DTO validation, service delegation, centralized error handling and documented HTTP contracts.", "@RestController, validated DTOs, @RestControllerAdvice, ProblemDetail, OpenAPI @Operation", "https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller.html", "@PostMapping(\"/customers\")\npublic Object create(@RequestBody Customer entity) { ... }", "@Operation(summary = \"Create customer\")\n@PostMapping(\"/api/v1/customers\")\npublic ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) { ... }");
        }
        if (code.startsWith("BAT")) {
            return new RuleGuidance("Detected Spring Batch operational risk: " + title + ".", why, fix + " Production batch jobs should make parameters, restartability, chunking, retry/skip, listeners, metrics and idempotency explicit.", "JobParametersValidator, JobExecutionListener, StepExecutionListener, faultTolerant(), stable JdbcPagingItemReader", "https://docs.spring.io/spring-batch/reference/", "tasklet(tasklet, tx).build();", "chunk(properties.chunkSize(), tx).reader(reader()).processor(processor()).writer(writer()).faultTolerant().retryLimit(3).build();");
        }
        if (code.startsWith("ARCH")) {
            return new RuleGuidance("Detected unclear architecture boundary: " + title + ".", why, fix + " Define dependency direction and separate controllers, use cases, domain and infrastructure.", "Layered or feature-based structure, domain/application/infrastructure, ports and adapters", "https://docs.spring.io/spring-modulith/reference/verification.html", "utils package contains business or framework-coupled logic.", "Move technical logging to infrastructure, business policies to domain/application and mapping to dedicated mappers.");
        }
        if (code.startsWith("POM")) {
            return new RuleGuidance("Detected Maven/Spring Boot governance issue: " + title + ".", why, fix + " Let the Spring Boot parent/BOM manage versions when possible and keep scopes/plugins consistent.", "spring-boot-starter-parent, spring-boot-dependencies BOM, dependencyManagement, pluginManagement", "https://docs.spring.io/spring-boot/maven-plugin/using.html", "<dependency>...<version>...</version></dependency>", "Use Spring Boot parent/BOM and central dependencyManagement.");
        }
        if (code.startsWith("CLD")) {
            return new RuleGuidance("Detected cloud/12-factor configuration issue: " + title + ".", why, fix + " Keep the artifact environment-neutral and inject runtime values from the deployment platform.", "Externalized Configuration, Environment, @ConfigurationProperties, ConfigMap/Secret/Vault", "https://docs.spring.io/spring-boot/reference/features/external-config.html", "external.api.url=https://server-prod/api", "external.api.url=${EXTERNAL_API_URL}");
        }
        if (code.startsWith("OBS")) {
            return new RuleGuidance("Detected observability gap: " + title + ".", why, fix + " Production systems need structured logs, health, metrics, correlation IDs and dependency indicators.", "Spring Boot Actuator, Micrometer, ObservationRegistry, HealthIndicator", "https://docs.spring.io/spring-boot/reference/actuator/observability.html", "System.out.println(\"processed=\" + count);", "meterRegistry.counter(\"items.processed\").increment(count);");
        }
        return new RuleGuidance("Detected issue: " + title + ".", why, fix, "Spring pattern matching the detected component", "https://docs.spring.io/spring-framework/reference/", "// Detected code shown in evidence", "// Apply the recommended Spring-oriented fix");
    }

    private static RuleGuidance advisor(ReportLanguage language, String detected, String alternative, String documentationUrl, String beforeExample, String afterExample) {
        if (language == ReportLanguage.ENGLISH) {
            return new RuleGuidance(
                    "Detected: " + detected + ".",
                    "The code uses a manual implementation or a low-level API. It may work, but it usually bypasses Spring configuration, lifecycle management, testing support or observability.",
                    "Use " + alternative + ". Centralize the configuration as a bean or typed configuration object and keep application code focused on business behavior.",
                    alternative,
                    documentationUrl,
                    beforeExample,
                    afterExample
            );
        }
        return new RuleGuidance(
                "Ho rilevato: " + detected + ".",
                "Il codice usa un'implementazione manuale o un'API di basso livello. Può funzionare, ma spesso aggira configurazione, lifecycle, testabilità o osservabilità offerte dall'ecosistema Spring.",
                "Usa " + alternative + ". Centralizza la configurazione come bean o proprietà tipizzata e lascia al codice applicativo solo la logica di business.",
                alternative,
                documentationUrl,
                beforeExample,
                afterExample
        );
    }

    private static String shortCode(String ruleId) {
        if (ruleId == null) {
            return "";
        }
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("^(SPR|SEC|WEB|BAT|CLD|OBS|POM|ADV|ARCH)\\d+").matcher(ruleId);
        return matcher.find() ? matcher.group() : ruleId;
    }
}

package com.example.guardian.core;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.ReportLanguage;
import com.example.guardian.core.model.RuleGuidance;

/**
 * Builds concise, action-oriented guidance for each report finding.
 * The UI uses this object to show what was detected, the concrete risk,
 * the expected Spring-oriented solution and, when available, official documentation and examples.
 *
 * @author Simone Meneghetti
 */
final class RuleGuidanceCatalog {

    private RuleGuidanceCatalog() {
    }

    static RuleGuidance guidance(Finding finding, String localizedTitle, String localizedWhy, String localizedFix, ReportLanguage language) {
        String code = shortCode(finding.ruleId());
        if (code.startsWith("CAP001")) {
            return advisor(language, "Spring Web without Bean Validation", "spring-boot-starter-validation + @Valid", "https://docs.spring.io/spring-framework/reference/core/validation/beanvalidation.html", "public ResponseEntity<?> create(@RequestBody Request request)", "public ResponseEntity<?> create(@Valid @RequestBody Request request)");
        }
        if (code.startsWith("CAP002")) {
            return advisor(language, "Spring Web without OpenAPI metadata", "springdoc-openapi with @Operation and @ApiResponse", "https://springdoc.org/", "@GetMapping(\"/orders\")", "@Operation(summary = \"List orders\")\n@ApiResponse(responseCode = \"200\")\n@GetMapping(\"/orders\")");
        }
        if (code.startsWith("CAP003")) {
            return advisor(language, "Spring application without Actuator", "spring-boot-starter-actuator", "https://docs.spring.io/spring-boot/reference/actuator/index.html", "No actuator dependency detected", "Add spring-boot-starter-actuator and expose health/info/metrics intentionally");
        }
        if (code.startsWith("CAP004")) {
            return advisor(language, "Spring Batch without operational observability", "Actuator, Micrometer and Batch metrics", "https://docs.spring.io/spring-boot/reference/actuator/metrics.html", "Batch project without Actuator/Micrometer detected", "Expose job, step, read, write, skip and failure metrics");
        }
        if (code.startsWith("SPR064")) {
            return advisor(language, "new ObjectMapper()", "ObjectMapper gestito da Spring Boot", "https://docs.spring.io/spring-boot/reference/features/json.html", "private final ObjectMapper mapper = new ObjectMapper();", "private final ObjectMapper mapper;\n\npublic MyComponent(ObjectMapper mapper) {\n    this.mapper = mapper;\n}");
        }
        if (code.startsWith("SPR096")) {
            return advisor(language, "spring.jpa.open-in-view=true", "Service-level @Transactional boundary with DTO/projection fetch plan", "https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative.html", "spring.jpa.open-in-view=true", "spring.jpa.open-in-view=false\n// Load required data inside @Transactional service methods");
        }
        if (code.startsWith("SPR082")) {
            return advisor(language, "@Value usato in più punti", "@ConfigurationProperties validato", "https://docs.spring.io/spring-boot/reference/features/external-config.html", "@Value(\"${external.api.url}\")\nprivate String url;", "@ConfigurationProperties(\"external.api\")\n@Validated\npublic record ApiProperties(@NotBlank String url) {}");
        }
        if (code.startsWith("SPR085")) {
            return advisor(language, "LocalDateTime.now() usato direttamente", "Clock iniettato", "https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/time/Clock.html", "LocalDateTime now = LocalDateTime.now();", "LocalDateTime now = LocalDateTime.now(clock);");
        }
        if (code.startsWith("SPR083")) {
            return advisor(language, "@Async senza @EnableAsync", "@EnableAsync con TaskExecutor esplicito", "https://docs.spring.io/spring-framework/reference/integration/scheduling.html", "@Async\nvoid runAsync() { ... }", "@EnableAsync\n@Configuration\nclass AsyncConfig { ... }");
        }
        if (code.startsWith("SPR084")) {
            return advisor(language, "@Scheduled senza @EnableScheduling", "@EnableScheduling con scheduler configurato", "https://docs.spring.io/spring-framework/reference/integration/scheduling.html", "@Scheduled(fixedDelay = 1000)\nvoid run() { ... }", "@EnableScheduling\n@Configuration\nclass SchedulingConfig { ... }");
        }
        return switch (code) {
            case "SPR_ALT001" -> advisor(language, "SecurityFilterChain mancante", "SecurityFilterChain esplicito", "https://docs.spring.io/spring-security/reference/servlet/configuration/java.html", "spring-boot-starter-security senza SecurityFilterChain", "@Bean\nSecurityFilterChain security(HttpSecurity http) throws Exception { return http.authorizeHttpRequests(a -> a.anyRequest().authenticated()).build(); }");
            case "SPR_ALT002" -> advisor(language, "CSRF disabilitato senza API stateless evidente", "CSRF abilitato o SessionCreationPolicy.STATELESS documentato", "https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html", "http.csrf(csrf -> csrf.disable());", "http.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));");
            case "SPR_ALT003" -> advisor(language, "permitAll troppo ampio", "Authorization DSL granulare", "https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html", ".anyRequest().permitAll()", ".requestMatchers(\"/actuator/health\").permitAll()\n.anyRequest().authenticated()");
            case "SPR_ALT004" -> advisor(language, "Actuator esposto con wildcard", "Actuator exposure minimale e protetto", "https://docs.spring.io/spring-boot/reference/actuator/endpoints.html", "management.endpoints.web.exposure.include=*", "management.endpoints.web.exposure.include=health,info,metrics");
            case "SPR_ALT005" -> advisor(language, "health details sempre pubblici", "management.endpoint.health.show-details=when_authorized", "https://docs.spring.io/spring-boot/reference/actuator/endpoints.html", "management.endpoint.health.show-details=always", "management.endpoint.health.show-details=when_authorized");
            case "SPR_ALT006" -> advisor(language, "Entity JPA restituita dal controller", "DTO/projection REST", "https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller.html", "public Customer get() { return entity; }", "public CustomerResponse get() { return mapper.toResponse(entity); }");
            case "SPR_ALT007" -> advisor(language, "Entity JPA accettata come @RequestBody", "Request DTO validato", "https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/requestbody.html", "create(@RequestBody Customer entity)", "create(@Valid @RequestBody CustomerRequest request)");
            case "SPR_ALT008" -> advisor(language, "@RequestBody senza @Valid", "Bean Validation", "https://docs.spring.io/spring-framework/reference/core/validation/beanvalidation.html", "create(@RequestBody Request request)", "create(@Valid @RequestBody Request request)");
            case "SPR_ALT009" -> advisor(language, "error handling REST non centralizzato", "@RestControllerAdvice + ProblemDetail", "https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-rest-exceptions.html", "catch (Exception ex) { return badRequest(ex.getMessage()); }", "@RestControllerAdvice\nclass ApiErrors { @ExceptionHandler ProblemDetail handle(Exception ex) { ... } }");
            case "SPR_ALT010" -> advisor(language, "Open EntityManager in View attivo", "Service transaction boundary + DTO/projection", "https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative.html", "spring.jpa.open-in-view=true", "spring.jpa.open-in-view=false");
            case "SPR_ALT011" -> advisor(language, "logica business nel repository", "@Service per orchestrazione e Repository per persistenza", "https://docs.spring.io/spring-data/jpa/reference/repositories.html", "class OrderRepository { if (...) publish(); }", "@Service\nclass OrderService { ... }\ninterface OrderRepository extends JpaRepository<Order, Long> {}");
            case "SPR_ALT012" -> advisor(language, "query costruita concatenando stringhe", "bind parameters / Specification / Querydsl", "https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html", "createQuery(\"from Order where name='\" + name + \"'\")", "@Query(\"from Order o where o.name = :name\")");
            case "SPR_ALT013" -> advisor(language, "read path senza readOnly", "@Transactional(readOnly = true)", "https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html", "public List<Order> findAll() { return repository.findAll(); }", "@Transactional(readOnly = true)\npublic List<Order> findAll() { return repository.findAll(); }");
            case "SPR_ALT014" -> advisor(language, "relazione JPA EAGER", "LAZY + fetch join / @EntityGraph / projection", "https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html", "@ManyToOne(fetch = FetchType.EAGER)", "@ManyToOne(fetch = FetchType.LAZY)");
            case "SPR_ALT015" -> advisor(language, "@Transactional su metodo non intercettabile", "metodo service pubblico o TransactionTemplate", "https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html", "@Transactional private void save() {}", "@Transactional public void save() {}");
            case "SPR_ALT016" -> advisor(language, "self-invocation di metodo proxato", "boundary in altro bean Spring", "https://docs.spring.io/spring-framework/reference/core/aop/proxying.html", "this.saveTransactional();", "otherService.saveTransactional();");
            case "SPR_ALT017" -> advisor(language, "@Transactional nel controller", "transaction boundary nel service layer", "https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative.html", "@RestController @Transactional class OrderController {}", "@Service class OrderService { @Transactional void create(...) {} }");
            case "SPR_ALT018" -> advisor(language, "@ConfigurationProperties senza validazione", "@ConfigurationProperties + @Validated + Bean Validation", "https://docs.spring.io/spring-boot/reference/features/external-config.html", "@ConfigurationProperties(\"api\") record ApiProps(String url) {}", "@ConfigurationProperties(\"api\") @Validated record ApiProps(@NotBlank String url) {}");
            case "SPR_ALT019" -> advisor(language, "segreto in configurazione versionabile", "externalized config / secret manager", "https://docs.spring.io/spring-boot/reference/features/external-config.html", "api.password=mySecret", "api.password=${API_PASSWORD}");
            case "SPR_ALT020" -> advisor(language, "System.out o printStackTrace", "SLF4J logger / structured logging", "https://docs.spring.io/spring-boot/reference/features/logging.html", "System.out.println(value);", "log.info(\"value={}\", value);");
            case "ADV001" -> advisor(language, "new ObjectMapper()", "ObjectMapper gestito da Spring Boot", "https://docs.spring.io/spring-boot/reference/features/json.html", "private final ObjectMapper mapper = new ObjectMapper();", "private final ObjectMapper mapper;\n\npublic MyComponent(ObjectMapper mapper) {\n    this.mapper = mapper;\n}");
            case "ADV002" -> advisor(language, "Gson/GsonBuilder manuale", "Jackson e la configurazione JSON di Spring Boot", "https://docs.spring.io/spring-boot/reference/features/json.html", "Gson gson = new GsonBuilder().create();", "ObjectMapper objectMapper gestito da Spring Boot");
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
            case "ADV084" -> advisor(language, "retry HTTP manuale", "RetryTemplate o Resilience4j", "https://docs.spring.io/spring-retry/docs/current/reference/html/", "", "");
            case "ADV085" -> advisor(language, "timeout gestito con thread", "timeout del client HTTP", "https://docs.spring.io/spring-boot/reference/io/rest-client.html", "future.get(5, TimeUnit.SECONDS);", "Configura connect/read timeout nel RestClient/WebClient builder.");
            case "ADV086" -> advisor(language, "polling scheduler custom", "Spring Batch, Quartz o TaskScheduler", "https://docs.spring.io/spring-framework/reference/integration/scheduling.html", "while (true) { checkTable(); }", "@Scheduled(...) void checkTable() { ... } oppure Job Spring Batch pianificato.");
            case "ADV087" -> advisor(language, "mappa idempotenza manuale", "servizio di idempotenza persistente o cache gestita", "https://docs.spring.io/spring-framework/reference/integration/cache.html", "", "");
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
        String evidence = compactEvidence(finding.evidence(), language);
        RuleGuidance specific = language == ReportLanguage.ENGLISH
                ? specificEnglishGuidance(code, title, evidence)
                : specificItalianGuidance(code, title, evidence);
        if (specific != null) {
            return specific;
        }
        return language == ReportLanguage.ENGLISH
                ? englishAreaGuidance(code, title, why, fix, evidence)
                : italianAreaGuidance(code, title, why, fix, evidence);
    }

    private static RuleGuidance specificItalianGuidance(String code, String title, String evidence) {
        return switch (code) {
            case "WEB004" -> problemIt("Request DTO senza Bean Validation" + evidence, "Il controller accetta input senza vincoli dichiarati. Dati incompleti o non validi possono raggiungere il service layer e generare errori più avanti nel flusso.", "Aggiungi annotazioni Bean Validation sul DTO e usa @Valid nel controller.", "Bean Validation + @Valid", "https://docs.spring.io/spring-framework/reference/core/validation/beanvalidation.html", "public ResponseEntity<?> create(@RequestBody CustomerRequest request) { ... }", "public record CustomerRequest(@NotBlank String name) {}\n\n@PostMapping\npublic ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) { ... }");
            case "WEB006", "SPR006" -> problemIt("Entity JPA usata come contratto REST" + evidence, "Il contratto HTTP viene legato al modello di persistenza. Cambiando tabella, relazioni o fetch plan rischi di rompere l'API o esporre campi interni.", "Usa DTO separati per request/response e mapper espliciti tra API e dominio/persistenza.", "DTO REST + mapper dedicato", "https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller.html", "public ResponseEntity<Customer> create(@RequestBody Customer entity) { ... }", "public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {\n    CustomerResponse response = service.create(request);\n    return ResponseEntity.status(HttpStatus.CREATED).body(response);\n}");
            case "WEB010" -> problemIt("Endpoint GET che sembra modificare stato" + evidence, "GET dovrebbe essere sicuro e idempotente. Usarlo per avviare modifiche può creare problemi con cache, proxy, retry automatici e client HTTP.", "Usa POST, PUT o PATCH per operazioni che modificano stato e documenta l'effetto nell'OpenAPI.", "Semantica HTTP corretta", "https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller.html", "@GetMapping(\"/orders/save\")\npublic void save() { service.save(); }", "@PostMapping(\"/orders\")\npublic ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest request) { ... }");
            case "WEB013", "SPR010" -> problemIt("Gestione errori REST non centralizzata" + evidence, "Senza un punto unico di gestione, gli endpoint possono restituire formati diversi, messaggi tecnici o status code incoerenti.", "Introduci @RestControllerAdvice e usa ProblemDetail o un modello errore standard.", "@RestControllerAdvice + ProblemDetail", "https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-rest-exceptions.html", "catch (Exception ex) { return ResponseEntity.badRequest().body(ex.getMessage()); }", "@RestControllerAdvice\nclass ApiExceptionHandler {\n  @ExceptionHandler(BusinessException.class)\n  ProblemDetail handle(BusinessException ex) { ... }\n}");
            case "WEB016" -> problemIt("Try/catch tecnico dentro controller" + evidence, "Il controller mescola protocollo HTTP e gestione tecnica degli errori. Il risultato è un contratto API meno uniforme e più difficile da testare.", "Lascia propagare eccezioni applicative controllate e mappale in @RestControllerAdvice.", "@RestControllerAdvice", "https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-rest-exceptions.html", "try { return ResponseEntity.ok(service.run()); } catch (Exception ex) { ... }", "@PostMapping\nResponseEntity<ResponseDto> run(@Valid @RequestBody RequestDto request) {\n  return ResponseEntity.ok(service.run(request));\n}");
            case "WEB029" -> problemIt("Messaggio tecnico dell'eccezione esposto al client" + evidence, "exception.getMessage() può rivelare dettagli interni, SQL, path, host, stack tecnici o dati non adatti al client.", "Restituisci un errore applicativo standard con codice funzionale, messaggio controllato e status HTTP coerente.", "ProblemDetail / ErrorResponse", "https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-rest-exceptions.html", "return ResponseEntity.badRequest().body(ex.getMessage());", "ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);\ndetail.setTitle(\"Richiesta non valida\");\ndetail.setDetail(\"Controlla i dati inviati.\");");
            case "WEB036" -> problemIt("Page<Entity> esposto direttamente" + evidence, "La paginazione espone il modello JPA e dettagli di serializzazione non sempre stabili. Il contratto API diventa dipendente dal database.", "Mappa Page<Entity> in Page<ResponseDto> o in un wrapper API esplicito.", "DTO paginato", "https://docs.spring.io/spring-data/commons/reference/repositories/core-extensions.html#core.web", "public Page<Customer> list(Pageable pageable) { ... }", "public Page<CustomerResponse> list(Pageable pageable) {\n  return service.list(pageable).map(mapper::toResponse);\n}");
            case "ARCH016" -> problemIt("Package common/util con possibile logica applicativa" + evidence, "Un package utilitario condiviso può diventare un contenitore indistinto di logging, costanti, helper e regole business. Questo aumenta accoppiamento e rende poco chiaro il proprietario della logica.", "Lascia in utils solo funzioni pure e trasversali. Sposta logging tecnico in infrastructure, regole applicative nei service/use case e regole di dominio nel domain layer.", "Layering esplicito / Spring Modulith boundaries", "https://docs.spring.io/spring-modulith/reference/verification.html", "utils/GvLog o CommonUtils contiene logica usata da più layer.", "infrastructure/logging/GvLogger\napplication/<feature>/<UseCaseService>\ndomain/<feature>/<BusinessPolicy>");
            case "ARCH018" -> problemIt("Possibile assenza di mapper tra API e modello interno" + evidence, "Se il controller restituisce direttamente entity o modelli interni, il contratto REST si accoppia alla struttura interna dell'applicazione.", "Verifica il tipo restituito dal controller. Se è un'entity o un modello interno, introduci DTO e mapper dedicati; se è già un DTO, puoi ignorare o sopprimere la regola.", "Mapper dedicato / MapStruct / componente Spring", "https://mapstruct.org/documentation/stable/reference/html/", "return ResponseEntity.ok(customerEntity);", "return ResponseEntity.ok(customerMapper.toResponse(customerEntity));");
            case "CLD001", "CLD020" -> problemIt("Valore di ambiente incluso nell'artefatto" + evidence, "URL, host o endpoint dentro application.properties/yml legano il pacchetto a un ambiente specifico e rendono meno ripetibile il rilascio.", "Sostituisci il valore con placeholder e inietta il valore tramite variabile ambiente, ConfigMap, Secret, Vault o piattaforma di deploy.", "Externalized Configuration", "https://docs.spring.io/spring-boot/reference/features/external-config.html", "external.api.url=https://server-prod/api", "external.api.url=${EXTERNAL_API_URL}");
            case "CLD003", "SPR037" -> problemIt("Segreto presente in configurazione o repository" + evidence, "Password, token o chiavi nel repository possono essere copiati, loggati o riusati; inoltre la rotazione diventa più difficile.", "Rimuovi il segreto dal repository, ruotalo e usa secret manager, Vault, variabili ambiente o configurazione montata.", "Secret management / externalized config", "https://docs.spring.io/spring-boot/reference/features/external-config.html", "api.password=mySecret", "api.password=${API_PASSWORD}");
            case "CLD013", "SPR082" -> problemIt("Configurazione non modellata con proprietà tipizzate" + evidence, "Valori sparsi rendono più difficile validare, documentare e testare la configurazione. Gli errori emergono tardi, spesso a runtime.", "Raggruppa le proprietà in @ConfigurationProperties e aggiungi validazione con @Validated.", "@ConfigurationProperties validato", "https://docs.spring.io/spring-boot/reference/features/external-config.html", "@Value(\"${api.url}\") String url;", "@ConfigurationProperties(\"api\")\n@Validated\npublic record ApiProperties(@NotBlank String url) {}");
            case "CLD014" -> problemIt("@ConfigurationProperties senza validazione" + evidence, "La configurazione viene caricata, ma valori mancanti o malformati possono emergere solo quando il codice li usa.", "Aggiungi @Validated e vincoli Bean Validation sui campi obbligatori.", "@Validated + Bean Validation", "https://docs.spring.io/spring-boot/reference/features/external-config.html", "@ConfigurationProperties(\"api\")\npublic record ApiProperties(String url) {}", "@ConfigurationProperties(\"api\")\n@Validated\npublic record ApiProperties(@NotBlank String url) {}");
            case "OBS006", "SPR021" -> problemIt("Uso di System.out o printStackTrace" + evidence, "I log non passano da livelli, formati, MDC/correlation id e raccolta centralizzata; in produzione la diagnosi diventa più difficile.", "Usa SLF4J Logger, includi contesto utile e passa il throwable quando logghi errori.", "SLF4J Logger / structured logging", "https://docs.spring.io/spring-boot/reference/features/logging.html", "System.out.println(\"Errore\");\nex.printStackTrace();", "private static final Logger log = LoggerFactory.getLogger(MyClass.class);\nlog.error(\"Errore durante l'elaborazione id={}\", id, ex);");
            case "OBS009" -> problemIt("Blocco catch vuoto" + evidence, "L'errore viene perso: non resta traccia nei log, non viene generata metrica e il chiamante può ricevere un risultato apparentemente valido.", "Gestisci l'eccezione in modo esplicito: log con throwable, rilancio di eccezione applicativa o compensazione controllata.", "Logging strutturato + gestione errori esplicita", "https://docs.spring.io/spring-boot/reference/features/logging.html", "catch (Exception ex) { }", "catch (ExternalServiceException ex) {\n  log.warn(\"Servizio esterno non disponibile\", ex);\n  throw new IntegrationUnavailableException();\n}");
            case "OBS011" -> problemIt("Errore loggato senza throwable" + evidence, "Senza passare l'eccezione al logger perdi stack trace e causa radice. Il troubleshooting diventa molto più lento.", "Passa sempre l'eccezione come ultimo argomento del log di errore.", "SLF4J Logger", "https://docs.spring.io/spring-boot/reference/features/logging.html", "log.error(\"Errore: {}\", ex.getMessage());", "log.error(\"Errore durante l'elaborazione dell'ordine {}\", orderId, ex);");
            case "OBS014" -> problemIt("Possibile dato sensibile nei log" + evidence, "Loggare password, token, autorizzazioni o dati personali espone informazioni che spesso finiscono in sistemi centralizzati e retention lunga.", "Maschera i valori sensibili e logga solo identificativi tecnici sicuri.", "Logging sicuro / redaction", "https://docs.spring.io/spring-boot/reference/features/logging.html", "log.info(\"password={}\", password);", "log.info(\"Credenziali ricevute per userId={}\", userId);");
            case "POM017" -> problemIt("Versione Java non governata nel POM" + evidence, "Senza una versione Java esplicita, build locale, CI e runtime possono usare livelli diversi del linguaggio o del bytecode.", "Definisci java.version nel parent e usa maven-compiler-plugin/release coerente con la versione Spring Boot adottata.", "java.version + maven-compiler-plugin", "https://docs.spring.io/spring-boot/maven-plugin/using.html", "<!-- java.version assente -->", "<properties>\n  <java.version>17</java.version>\n</properties>");
            case "SEC021" -> problemIt("Più SecurityFilterChain senza ordine esplicito" + evidence, "Con più catene di sicurezza, l'ordine determina quale configurazione intercetta la richiesta. Senza @Order puoi ottenere comportamenti inattesi.", "Aggiungi @Order e matcher specifici per ogni chain, dalla più specifica alla più generale.", "SecurityFilterChain + @Order", "https://docs.spring.io/spring-security/reference/servlet/configuration/java.html", "@Bean SecurityFilterChain api(HttpSecurity http) { ... }\n@Bean SecurityFilterChain actuator(HttpSecurity http) { ... }", "@Bean @Order(1) SecurityFilterChain actuator(HttpSecurity http) { ... }\n@Bean @Order(2) SecurityFilterChain api(HttpSecurity http) { ... }");
            case "SEC028", "SEC038" -> problemIt("Autorizzazione troppo ampia o poco granulare" + evidence, "Un path pubblico o una regola solo autenticata può esporre operazioni di modifica a utenti non autorizzati al caso d'uso.", "Definisci matcher precisi e ruoli/scope per endpoint mutanti o amministrativi.", "AuthorizationManager / requestMatchers / method security", "https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html", ".requestMatchers(\"/**\").permitAll()", ".requestMatchers(HttpMethod.POST, \"/api/v1/admin/**\").hasRole(\"ADMIN\")\n.anyRequest().authenticated()");
            case "SPR011" -> problemIt("Catch troppo generico" + evidence, "Catturare Exception o Throwable nasconde casi diversi dietro lo stesso comportamento e può impedire rollback, diagnosi e gestione corretta dell'errore.", "Cattura solo eccezioni che sai gestire; per le altre lascia propagare e mappa la risposta con @RestControllerAdvice.", "Eccezioni specifiche + @RestControllerAdvice", "https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-rest-exceptions.html", "catch (Exception ex) { return ResponseEntity.badRequest().body(ex.getMessage()); }", "catch (ExternalServiceException ex) { throw new IntegrationException(ex); }");
            default -> null;
        };
    }

    private static RuleGuidance specificEnglishGuidance(String code, String title, String evidence) {
        return switch (code) {
            case "SPR041" -> problemEn(
                    "Spring Security authorization rule is too broad" + evidence,
                    "A broad permitAll rule can expose endpoints that should require authentication or authorization.",
                    "Restrict permitAll to explicit public endpoints and make the default rule authenticated.",
                    "SecurityFilterChain with explicit request matchers",
                    "https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html",
                    "http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());",
                    "http.authorizeHttpRequests(auth -> auth\n    .requestMatchers(\"/actuator/health\").permitAll()\n    .anyRequest().authenticated());"
            );
            case "WEB004" -> problemEn(
                    "Request DTO without Bean Validation" + evidence,
                    "The controller accepts input without declared constraints, so incomplete or invalid data can reach the service layer.",
                    "Add Bean Validation annotations on the request DTO and use @Valid in the controller.",
                    "Bean Validation + @Valid",
                    "https://docs.spring.io/spring-framework/reference/core/validation/beanvalidation.html",
                    "public ResponseEntity<?> create(@RequestBody CustomerRequest request) { ... }",
                    "public record CustomerRequest(@NotBlank String name) {}"
            );
            case "OBS025" -> problemEn(
                    "Health details are always exposed" + evidence,
                    "Always exposing health details may reveal operational information about dependencies or internal errors.",
                    "Expose details only to authorized users or disable them in production.",
                    "Actuator health endpoint",
                    "https://docs.spring.io/spring-boot/reference/actuator/endpoints.html",
                    "management.endpoint.health.show-details=always",
                    "management.endpoint.health.show-details=when-authorized"
            );
            case "CLD003" -> problemEn(
                    "Possible secret in packaged configuration" + evidence,
                    "Passwords, tokens or keys committed with the artifact can be copied, logged or reused and are harder to rotate.",
                    "Remove the value from the repository, rotate it if real and use environment variables, Vault, a secret manager or mounted configuration.",
                    "Externalized configuration and secret management",
                    "https://docs.spring.io/spring-boot/reference/features/external-config.html",
                    "api.password=mySecret",
                    "api.password=${API_PASSWORD}"
            );
            default -> null;
        };
    }

    private static RuleGuidance italianAreaGuidance(String code, String title, String why, String fix, String evidence) {
        if (code.startsWith("SEC")) {
            return problemIt("Configurazione Spring Security da rivedere: " + title + evidence, why, fix, "SecurityFilterChain / AuthorizationManager / Method Security", "https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html", "http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());", "http.authorizeHttpRequests(auth -> auth\n    .requestMatchers(\"/actuator/health\").permitAll()\n    .anyRequest().authenticated());");
        }
        if (code.startsWith("WEB")) {
            return problemIt("Contratto Web/API da rivedere: " + title + evidence, why, fix, "DTO validati, controller sottili, @RestControllerAdvice, OpenAPI", "https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller.html", "@PostMapping\npublic Object create(@RequestBody Entity entity) { ... }", "@PostMapping(\"/api/v1/resources\")\npublic ResponseEntity<ResponseDto> create(@Valid @RequestBody RequestDto request) {\n    return ResponseEntity.ok(service.create(request));\n}");
        }
        if (code.startsWith("BAT")) {
            return problemIt("Rischio operativo Spring Batch: " + title + evidence, why, fix, "JobParametersValidator, listener, chunk, retry/skip, metriche", "https://docs.spring.io/spring-batch/reference/", "new StepBuilder(\"step\", jobRepository).tasklet(tasklet, tx).build();", "new StepBuilder(\"importCustomers\", jobRepository)\n  .<Input, Output>chunk(properties.chunkSize(), tx)\n  .reader(reader())\n  .processor(processor())\n  .writer(writer())\n  .faultTolerant()\n  .retryLimit(3)\n  .listener(stepListener)\n  .build();");
        }
        if (code.startsWith("ARCH")) {
            return problemIt("Confine architetturale da chiarire: " + title + evidence, why, fix, "Layering esplicito, DDD/Hexagonal boundaries, Spring Modulith verification", "https://docs.spring.io/spring-modulith/reference/verification.html", "", "");
        }
        if (code.startsWith("POM")) {
            return problemIt("Governance Maven/Spring Boot da rivedere: " + title + evidence, why, fix, "Spring Boot BOM, dependencyManagement, pluginManagement", "https://docs.spring.io/spring-boot/maven-plugin/using.html", "<dependency>...<version>gestita a mano</version></dependency>", "Usa spring-boot-starter-parent o spring-boot-dependencies BOM e centralizza versioni/plugin nel parent.");
        }
        if (code.equals("CLD003")) {
            return problemIt("Possibile segreto presente nella configurazione" + evidence, "Password, token o chiavi nel repository possono essere copiati, loggati o riutilizzati; inoltre la rotazione diventa più difficile.", "Rimuovi il valore dal repository, ruotalo se reale e usa variabili ambiente, Vault, secret manager o configurazione montata.", "Configurazione esterna e gestione segreti", "https://docs.spring.io/spring-boot/reference/features/external-config.html", "api.password=mySecret", "api.password=${API_PASSWORD}");
        }
        if (code.equals("CLD001") || code.equals("CLD020") || code.equals("CLD028")) {
            return problemIt("Valore runtime o di ambiente incluso nel pacchetto" + evidence, "URL, host, datasource o valori runtime dentro application.properties/yml legano l'artefatto a un ambiente specifico e rendono meno ripetibile il rilascio.", "Sostituisci il valore con un placeholder e inietta il valore dalla piattaforma di deploy.", "Configurazione esterna 12-factor", "https://docs.spring.io/spring-boot/reference/features/external-config.html", "external.api.url=https://server-prod/api", "external.api.url=${EXTERNAL_API_URL}");
        }
        if (code.equals("CLD005")) {
            return problemIt("Cron operativo incluso nel pacchetto" + evidence, "Una schedulazione operativa committata nel pacchetto rende il comportamento diverso da modificare tra ambienti e può richiedere una nuova build per cambiare finestra di esecuzione.", "Usa una proprietà esterna per la cron expression e valorizzala per ambiente dalla piattaforma di deploy.", "Configurazione esterna della schedulazione", "https://docs.spring.io/spring-framework/reference/integration/scheduling.html", "batch.import.cron=0 0 2 * * *", "batch.import.cron=${BATCH_IMPORT_CRON}");
        }
        if (code.equals("CLD015")) {
            return problemIt("Configurazione health da rivedere" + evidence, "Mostrare sempre i dettagli health può esporre informazioni operative su componenti, dipendenze o errori interni.", "In produzione mostra i dettagli solo agli utenti autorizzati o disabilitali; separa readiness e liveness se usi orchestratori/container.", "Actuator health endpoint", "https://docs.spring.io/spring-boot/reference/actuator/endpoints.html", "management.endpoint.health.show-details=always", "management.endpoint.health.show-details=when-authorized");
        }
        if (code.equals("CLD024")) {
            return problemIt("Endpoint esterno non modellato come proprietà tipizzata" + evidence, "Endpoint esterni sparsi o non tipizzati rendono più difficile validare configurazione, timeout, ambienti e test.", "Raggruppa gli endpoint in @ConfigurationProperties validato e inietta il bean nei client applicativi.", "@ConfigurationProperties", "https://docs.spring.io/spring-boot/reference/features/external-config.html", "@Value(\"${external.api.url}\") String url;", "@ConfigurationProperties(\"external.api\")\npublic record ExternalApiProperties(String url) {}");
        }
        if (code.equals("CLD026")) {
            return problemIt("Impostazioni del connection pool incluse nel pacchetto" + evidence, "Pool troppo piccoli o troppo grandi per un ambiente possono causare saturazione, code o consumo eccessivo di connessioni.", "Mantieni default sicuri e sovrascrivi dimensione pool/timeout per ambiente tramite configurazione esterna.", "Configurazione datasource Spring Boot", "https://docs.spring.io/spring-boot/reference/data/sql.html", "spring.datasource.hikari.maximum-pool-size=30", "spring.datasource.hikari.maximum-pool-size=${DB_POOL_MAX_SIZE:10}");
        }
        if (code.startsWith("CLD")) {
            return problemIt("Aspetto 12-factor/cloud readiness da rivedere: " + title + evidence, why, fix, "Configurazione esterna, @ConfigurationProperties e gestione segreti", "https://docs.spring.io/spring-boot/reference/features/external-config.html", "api.url=https://server-prod/api", "api.url=${API_URL}");
        }
        if (code.startsWith("OBS")) {
            return problemIt("Osservabilità da migliorare: " + title + evidence, why, fix, "Actuator, Micrometer, HealthIndicator, logging strutturato", "https://docs.spring.io/spring-boot/reference/actuator/observability.html", "System.out.println(value);", "log.info(\"Operazione completata id={}\", id);\nmeterRegistry.counter(\"operation.completed\").increment();");
        }
        return problemIt("Problema rilevato: " + title + evidence, why, fix, "Pattern Spring coerente con il componente rilevato", "https://docs.spring.io/spring-framework/reference/", "Guarda la sezione evidenza per la riga esatta rilevata", "Applica il pattern consigliato nella classe coinvolta");
    }

    private static RuleGuidance englishAreaGuidance(String code, String title, String why, String fix, String evidence) {
        if (code.startsWith("SEC")) {
            return problemEn("Spring Security configuration to review: " + title + evidence, why, fix, "SecurityFilterChain / AuthorizationManager / Method Security", "https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html", "http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());", "http.authorizeHttpRequests(auth -> auth\n    .requestMatchers(\"/actuator/health\").permitAll()\n    .anyRequest().authenticated());");
        }
        if (code.startsWith("WEB")) {
            return problemEn("Web/API contract to review: " + title + evidence, why, fix, "Validated DTOs, thin controllers, @RestControllerAdvice, OpenAPI", "https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller.html", "@PostMapping\npublic Object create(@RequestBody Entity entity) { ... }", "@PostMapping(\"/api/v1/resources\")\npublic ResponseEntity<ResponseDto> create(@Valid @RequestBody RequestDto request) {\n    return ResponseEntity.ok(service.create(request));\n}");
        }
        if (code.startsWith("BAT")) {
            return problemEn("Spring Batch operational risk: " + title + evidence, why, fix, "JobParametersValidator, listeners, chunk, retry/skip, metrics", "https://docs.spring.io/spring-batch/reference/", "new StepBuilder(\"step\", jobRepository).tasklet(tasklet, tx).build();", "new StepBuilder(\"importCustomers\", jobRepository)\n  .<Input, Output>chunk(properties.chunkSize(), tx)\n  .reader(reader())\n  .processor(processor())\n  .writer(writer())\n  .faultTolerant()\n  .retryLimit(3)\n  .listener(stepListener)\n  .build();");
        }
        if (code.startsWith("ARCH")) {
            return problemEn("Architecture boundary to clarify: " + title + evidence, why, fix, "Explicit layering, DDD/Hexagonal boundaries, Spring Modulith verification", "https://docs.spring.io/spring-modulith/reference/verification.html", "", "");
        }
        if (code.startsWith("POM")) {
            return problemEn("Maven/Spring Boot governance issue: " + title + evidence, why, fix, "Spring Boot BOM, dependencyManagement, pluginManagement", "https://docs.spring.io/spring-boot/maven-plugin/using.html", "<dependency>...<version>manual</version></dependency>", "Use spring-boot-starter-parent or spring-boot-dependencies BOM and centralize versions/plugins in the parent.");
        }
        if (code.startsWith("CLD")) {
            return problemEn("12-factor/cloud-readiness issue: " + title + evidence, why, fix, "Externalized configuration, @ConfigurationProperties and secret management", "https://docs.spring.io/spring-boot/reference/features/external-config.html", "api.url=https://server-prod/api", "api.url=${API_URL}");
        }
        if (code.startsWith("OBS")) {
            return problemEn("Observability issue: " + title + evidence, why, fix, "Actuator, Micrometer, HealthIndicator, structured logging", "https://docs.spring.io/spring-boot/reference/actuator/observability.html", "System.out.println(value);", "log.info(\"Operation completed id={}\", id);\nmeterRegistry.counter(\"operation.completed\").increment();");
        }
        return problemEn("Detected issue: " + title + evidence, why, fix, "Spring pattern matching the detected component", "https://docs.spring.io/spring-framework/reference/", "See the evidence section for the exact code line", "Apply the recommended pattern using the affected class and responsibility");
    }

    private static RuleGuidance problemIt(String detected, String impact, String solution, String alternative, String docs, String before, String after) {
        return new RuleGuidance(toItalianText(detected), toItalianText(impact), toItalianText(solution), toItalianText(alternative), docs, "", "");
    }

    private static RuleGuidance problemEn(String detected, String impact, String solution, String alternative, String docs, String before, String after) {
        return new RuleGuidance(toEnglishText(detected), toEnglishText(impact), toEnglishText(solution), toEnglishText(alternative), docs, "", "");
    }

    private static String compactEvidence(String evidence, ReportLanguage language) {
        if (evidence == null || evidence.isBlank()) {
            return "";
        }
        String clean = evidence.strip().replaceAll("\\s+", " ");
        if (clean.length() > 140) {
            clean = clean.substring(0, 137) + "...";
        }
        return language == ReportLanguage.ENGLISH ? " Evidence: `" + clean + "`." : " Evidenza: `" + clean + "`.";
    }

    private static RuleGuidance advisor(ReportLanguage language, String detected, String alternative, String documentationUrl, String beforeExample, String afterExample) {
        if (language == ReportLanguage.ENGLISH) {
            String englishDetected = toEnglishText(detected);
            String englishAlternative = toEnglishText(alternative);
            return new RuleGuidance(
                    "Detected: " + englishDetected + ".",
                    "The code uses a manual implementation or a low-level API. It may work, but it usually bypasses Spring configuration, lifecycle management, testing support or observability.",
                    "Use " + englishAlternative + ". Centralize the configuration as a bean or typed configuration object and keep application code focused on business behavior.",
                    englishAlternative,
                    documentationUrl,
                    "",
                    ""
            );
        }
        String italianDetected = toItalianText(detected);
        String italianAlternative = toItalianText(alternative);
        return new RuleGuidance(
                "Ho rilevato: " + italianDetected + ".",
                "Il codice usa un'implementazione manuale o un'API di basso livello. Può funzionare, ma spesso aggira configurazione, ciclo di vita, testabilità o osservabilità offerte dall'ecosistema Spring.",
                "Usa " + italianAlternative + ". Centralizza la configurazione come bean o proprietà tipizzata e lascia al codice applicativo solo la logica di business.",
                italianAlternative,
                documentationUrl,
                "",
                ""
        );
    }

    private static String toItalianText(String value) {
        if (value == null) {
            return null;
        }
        return value
                .replace("Externalized Configuration", "Configurazione esterna")
                .replace("Secret management / externalized config", "Gestione dei segreti e configurazione esterna")
                .replace("SecurityFilterChain + @Order", "SecurityFilterChain con @Order")
                .replace("Layering esplicito, DDD/Hexagonal boundaries, Spring Modulith verification", "Layer espliciti, confini DDD o esagonali e verifica con Spring Modulith")
                .replace("Explicit layering, DDD/Hexagonal boundaries, Spring Modulith verification", "Layer espliciti, confini DDD o esagonali e verifica con Spring Modulith")
                .replace("Spring pattern matching the detected component", "Pattern Spring coerente con il componente rilevato")
                .replace("externalized config/secret manager", "configurazione esterna o secret manager")
                .replace("request logging filter manuale", "filtro manuale per il log delle richieste")
                .replace("reload config custom", "ricaricamento configurazione custom")
                .replace("parsing file segreti manuale", "lettura manuale di file con segreti")
                .replace("HTTP retry", "retry HTTP")
                .replace("manuale", "manuale")
                .replace("Manuale", "Manuale")
                .replace("custom", "personalizzato")
                .replace("Custom", "Personalizzato")
                .replace("lifecycle", "ciclo di vita")
                .replace("Lifecycle", "Ciclo di vita")
                .replace("structured logging", "log strutturato")
                .replace("Structured logging", "Log strutturato")
                .replace("Feature flag", "Feature flag")
                .replace("business", "applicativa")
                .replace("Business", "Applicativa")
                .replace("detected issue", "problema rilevato")
                .replace("Detected issue", "Problema rilevato")
                .replace("Boot-managed", "gestito da Spring Boot")
                .replace("managed", "gestito")
                .replace("Low-level", "Basso livello")
                .replace("low-level", "basso livello")
                .replace("HTTP client", "client HTTP")
                .replace("builder", "builder")
                .replace("scattered", "sparso")
                .replace("Scattered", "Sparso")
                .replace("repeated", "ripetuto")
                .replace("Repeated", "Ripetuto")
                .replace("manual ObjectMapper", "ObjectMapper manuale")
                .replace("Manual ObjectMapper", "ObjectMapper manuale")
                .replace("mappa idempotenza manuale", "mappa di idempotenza manuale")
                .replace("repository/cache/idempotency service", "servizio di idempotenza persistente o cache gestita")
                .replace("service", "servizio")
                .replace("Service", "Servizio")
                .replace("cache", "cache")
                .replace("Cache", "Cache")
                .replace("idempotency", "idempotenza")
                .replace("Idempotency", "Idempotenza")
                .replace("Detected:", "Rilevato:")
                .replace("Detected", "Rilevato")
                .replace("Possible impact", "Impatto possibile")
                .replace("Recommended solution", "Soluzione consigliata")
                .replace("Technical code", "Codice regola")
                .replace("should use", "dovrebbe usare")
                .replace("should evaluate", "dovrebbe valutare")
                .replace("should be replaced by", "dovrebbe essere sostituito da")
                .replace("should become", "dovrebbe diventare")
                .replace("should", "dovrebbe")
                .replace("Should", "Dovrebbe")
                .replace("without", "senza")
                .replace("Without", "Senza")
                .replace("with", "con")
                .replace("With", "Con")
                .replace("requires", "richiede")
                .replace("Requires", "Richiede")
                .replace("may", "può")
                .replace("May", "Può")
                .replace("Use ", "Usa ");
    }

    private static String toEnglishText(String value) {
        if (value == null) {
            return null;
        }
        return value
                .replace("new ObjectMapper()", "new ObjectMapper()")
                .replace("Gson/GsonBuilder manuale", "manual Gson/GsonBuilder")
                .replace("RestTemplate creato con new", "RestTemplate created with new")
                .replace("WebClient.create() sparso", "scattered WebClient.create()")
                .replace("RestClient.create()/builder duplicato", "duplicated RestClient.create()/builder")
                .replace("HttpURLConnection/URL", "HttpURLConnection/URL")
                .replace("OkHttpClient manuale", "manual OkHttpClient")
                .replace("Thread creato manualmente", "manually created thread")
                .replace("ExecutorService manuale", "manual ExecutorService")
                .replace("Timer/TimerTask", "Timer/TimerTask")
                .replace("Thread.sleep", "Thread.sleep")
                .replace("System.getenv/System.getProperty", "System.getenv/System.getProperty")
                .replace("molti @Value sparsi", "many scattered @Value fields")
                .replace("FileInputStream/FileReader diretto", "direct FileInputStream/FileReader usage")
                .replace("ClassPathResource ripetuto", "repeated ClassPathResource usage")
                .replace("ConcurrentHashMap usata come cache", "ConcurrentHashMap used as a cache")
                .replace("Map statica come registry", "static Map used as a registry")
                .replace("ApplicationContext.getBean nel codice business", "ApplicationContext.getBean in business code")
                .replace("new Service/Repository dentro bean Spring", "new Service/Repository inside a Spring bean")
                .replace("validazione manuale ripetuta", "repeated manual validation")
                .replace("transazioni begin/commit manuali", "manual begin/commit transactions")
                .replace("retry loop manuale", "manual retry loop")
                .replace("controller custom per health check", "custom health-check controller")
                .replace("logica applicativa dentro listener/event", "business logic inside listener/event")
                .replace("factory manuale di oggetti", "manual object factory")
                .replace("mapping manuale ripetitivo", "repetitive manual mapping")
                .replace("gestione errori manuale", "manual error handling")
                .replace("utility statica con dipendenze", "static utility with dependencies")
                .replace("gestione locale/message manuale", "manual locale/message handling")
                .replace("serializzazione date locale/manuale", "local/manual date serialization")
                .replace("request logging filter manuale", "manual request logging filter")
                .replace("reload config custom", "custom configuration reload")
                .replace("parsing file segreti manuale", "manual secret-file parsing")
                .replace("configurazione", "configuration")
                .replace("proprietà", "properties")
                .replace("validato", "validated")
                .replace("Validazione", "Validation")
                .replace("manuale", "manual")
                .replace("Manuale", "Manual")
                .replace("gestito da Spring Boot", "managed by Spring Boot")
                .replace("gestito da Spring", "managed by Spring")
                .replace("centralizzato", "centralized")
                .replace("ciclo di vita", "lifecycle")
                .replace("osservabilità", "observability")
                .replace("sicurezza", "security")
                .replace("filtro", "filter")
                .replace("filtri", "filters")
                .replace("lettura", "reading")
                .replace("segreti", "secrets")
                .replace("personalizzato", "custom")
                .replace("applicativa", "business")
                .replace("Applicativa", "Business");
    }

    private static String shortCode(String ruleId) {
        if (ruleId == null) {
            return "";
        }
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("^(SPR_ALT|SPR|SEC|WEB|BAT|CLD|OBS|POM|ADV|ARCH|CAP)\\d+").matcher(ruleId);
        return matcher.find() ? matcher.group() : ruleId;
    }
}

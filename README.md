# Spring Guardian

<img width="1666" height="944" alt="Spring Guardian dashboard" src="https://github.com/user-attachments/assets/b355b536-4b9c-4d9e-bda3-38fa36e9aceb" />

## English

Spring Guardian is a deterministic **Spring Architecture Auditor** and **Release Readiness Scanner** for Spring Boot projects.

It analyzes Java source code, Maven POM files and Spring configuration files without executing the target project, without calling AI services, without opening database connections and without storing scan state. The goal is not to print hundreds of unreadable static-analysis lines, but to answer practical architecture questions:

- Is this Spring project ready for release?
- Which areas are weak: security, web/API, JPA, batch, Maven, prontezza cloud, observability or modernization?
- Which findings block production?
- Which files and classes are involved?
- Why does each issue matter?
- What is the recommended Spring-oriented fix?

Spring Guardian is complementary to generic tools such as SonarQube. It focuses on Spring-specific architecture, framework usage, release readiness, modernization opportunities and production-readiness patterns.

## Modules

```text
spring-guardian
├── guardian-core      # deterministic scanner and rules
├── guardian-cli       # command-line scan entry point
├── guardian-server    # Spring Boot REST API and OpenAPI documentation
└── guardian-ui        # Angular dashboard
```

## Core principles

```text
Deterministic first
No AI calls
No database calls
No hidden state
No persistence
CI/CD friendly
Spring-specific findings
Readable release decision
```

Every scan is stateless. The selected profile calibrates only the current request and never hides findings. Security findings remain blocking even when legacy baseline mode is active.

## Start the backend

Recommended on Windows / PowerShell:

```powershell
.\scripts\run-backend.ps1
```

Or with CMD:

```cmd
scripts\run-backend.cmd
```

Manual equivalent from the project root:

```bash
mvn -pl guardian-server -am spring-boot:run
```

Health endpoint:

```text
http://localhost:8080/api/v1/health
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

## Start the Angular UI

Recommended:

```powershell
.\scripts\run-ui.ps1
```

Or manually:

```bash
cd guardian-ui
npm install
npm start
```

Open:

```text
http://localhost:4200
```

The Angular app uses `proxy.conf.json` and forwards `/api` to `http://localhost:8080`.

## Docker

```bash
docker compose up --build
```

Open:

```text
http://localhost:3000
```

Docker services:

```text
spring-guardian-server -> port 8080
spring-guardian-ui    -> port 3000
```

You can mount projects under `./sample-projects` and scan them inside the container from `/scan`.

## Scan modes

The UI supports three scan modes:

1. **Upload ZIP**  
   Upload a compressed Spring project.

2. **Upload folder**  
   Select the project root folder directly from the browser. The folder should contain the main `pom.xml` or Maven modules.

3. **Backend path**  
   Scan a path readable by the backend, for example:

   ```text
   C:\projects\my-project
   /scan/my-project
   ```

## Stateless scan profile

Supported parameters:

```text
projectType: WEB_API | BATCH | LIBRARY
architectureStyle: AUTO_DETECTED | LAYERED | DOMAIN_DRIVEN_DESIGN | HEXAGONAL | LEGACY_LAYERED
releaseTarget: PRODUCTION | INTERNAL | LEGACY_BASELINE
knownIssuesAccepted: true | false
```

The UI keeps the profile simple:

- project type;
- release target: production or test/QA;
- legacy baseline flag.

The backend still accepts `architectureStyle` for CLI and CI usage, but the UI sends `AUTO_DETECTED`. Architecture style is detected from packages, annotations, dependencies and Spring capabilities.

## Release readiness

The report contains an explicit release decision:

```text
READY
READY_WITH_WARNINGS
NOT_READY
```

The decision is produced by deterministic quality gates:

```text
GATE_SECURITY
GATE_WEB_LAYER
GATE_JPA
GATE_BATCH
GATE_DEPENDENCY_GOVERNANCE
GATE_CLOUD_READINESS
GATE_OBSERVABILITY
GATE_DEPENDENCY_INJECTION
GATE_ARCHITECTURE_BOUNDARIES
GATE_TESTS
GATE_BUILD_CONFIG
GATE_SPRING_ALTERNATIVE_ADVISOR
GATE_PROFILE_ALIGNMENT
```

## Detected capabilities

Spring Guardian detects project context from Java source code and Maven POMs. It does not execute the target project.

Detected capabilities include:

```text
Spring Web
Spring Security
JPA / Spring Data JPA
Actuator
Bean Validation
OpenAPI / Swagger
Lombok
Spring Batch
Controller layer
Service layer
Repository layer
Domain layer
Application layer
Infrastructure layer
DDD / Hexagonal / Layered style signals
```

Capabilities drive conditional rules. For example, JPA rules are evaluated when JPA is detected, Batch rules become relevant for Batch projects, and DDD/hexagonal boundary rules become stricter for projects using domain/application/infrastructure separation.

## Finding types

Findings are grouped by deterministic rule and classified by technical type:

```text
Java code
Maven POM
Dependencies
Configuration
Tests
Security
JPA and persistence
Web/API layer
Dependency injection
Runtime code
Architecture and boundaries
Spring Batch
Cloud readiness
Observability
Spring Alternative Advisor
```

This keeps reports readable even when the same rule affects many classes.

## Rule families

### Existing Spring foundation

Spring Guardian already covers the core Spring review families:

```text
field injection
fat controllers
repository injected into controller
missing service layer
entity exposed through REST API
self-invocation and Spring proxy risks
@Transactional in the wrong layer
manual JDBC connection handling
missing @RestControllerAdvice
generic catch blocks
missing tests
path variable without explicit name
missing API versioning
POM quality
naming conventions
read-only transaction hints
Optional.get without guard
console logging and printStackTrace
DTO validation gaps
null returns in service/repository
manual HTTP client creation
package/layer inconsistencies
large classes and complex services
duplicated Maven dependencies
unsafe ddl-auto
actuator exposure
CSRF/CORS/security smells
JPA eager fetching
repository calls inside loops
GET endpoints mutating state
raw controller responses
thread sleeps in tests
JPA constructor and lazy relationship checks
DDD/hexagonal domain dependency checks
service/repository layer dependency violations
SecurityFilterChain checks
OpenAPI operation checks
@AllArgsConstructor on Spring components
constructor dependency fields not final
REST controller without base mapping
configuration externalization
hardcoded active profiles
Maven conflict and hygiene checks
```

### New architecture and boundary rules

The extended catalog adds architecture checks for DDD, hexagonal and modular Spring projects:

```text
ARCH001 - Domain layer depends on web layer
ARCH002 - Domain layer depends on persistence repository
ARCH003 - Application layer returns ResponseEntity
ARCH004 - Application layer depends on servlet API
ARCH005 - Controller calls infrastructure adapter directly
ARCH007 - Entity placed in shared/common module
ARCH008 - API DTO appears in domain layer
ARCH009 - Domain event declared in infrastructure
ARCH010 - Domain uses Spring ApplicationEventPublisher directly
ARCH011 - Spring configuration placed in domain package
ARCH012 - Common/util package may become a dumping ground
ARCH013 - Port interface annotated as Spring component
ARCH014 - Adapter owns transactional business boundary
ARCH015 - Application lacks visible architectural package structure
```

### New Spring Security rules

```text
SEC001 - anyRequest().permitAll
SEC002 - /** permitAll or broad wildcard security matcher
SEC003 - CSRF disabled without evident stateless policy
SEC004 - CORS wildcard with credentials
SEC005 - all actuator endpoints exposed
SEC006 - formLogin enabled in REST API
SEC007 - httpBasic enabled in production
SEC008 - rememberMe enabled in REST API
SEC009 - frame options disabled
SEC010 - H2 console enabled outside dev/test
SEC011 - Swagger explicitly enabled in production
SEC012 - JWT/security secret hardcoded or locally created
SEC013 - NoOpPasswordEncoder
SEC015 - repository reads SecurityContextHolder
SEC016 - hardcoded role/authority strings
SEC017 - permitAll may protect mutating endpoints
SEC018 - controller-level CORS policy
SEC019 - custom filter without explicit order/policy
SEC020 - manual security exception handling
```

### New Web/API rules

```text
WEB001 - service returns ResponseEntity
WEB002 - service uses servlet API
WEB003 - controller handles technical exceptions locally
WEB004 - API may expose exception.getMessage()
WEB005 - Page<Entity> exposed directly
WEB006 - Pageable accepted without visible limit policy
WEB007 - Sort accepted directly from API
WEB008 - generic Object response
WEB009 - Map response used as API contract
WEB010 - bulk endpoint without visible limit policy
WEB011 - GET endpoint appears to mutate state
WEB012 - mutating endpoint should validate request body
WEB013 - OpenAPI missing on public controller
WEB014 - admin endpoint mixed with public API
WEB015 - DTO placed near entity package
WEB016 - manual validation in controller
WEB017 - POST creation endpoint may lack explicit status
WEB018 - DELETE endpoint should use clear status semantics
WEB019 - multipart endpoint without visible media/limit contract
WEB020 - controller depends on repository
```

### New Spring Batch rules

```text
BAT001 - job should declare parameter validation
BAT002 - chunk size appears hardcoded
BAT003 - JdbcPagingItemReader requires stable sort key
BAT004 - JdbcCursorItemReader detected on batch workload
BAT005 - reader uses SELECT *
BAT006 - ItemProcessor should be stateless
BAT007 - retry on generic Exception
BAT008 - skip on generic Exception
BAT009 - fault tolerance without visible listener
BAT010 - ExecutionContext usage requires serialization discipline
BAT011 - RunIdIncrementer detected
BAT012 - allowStartIfComplete requires idempotency
BAT013 - parallel step requires thread-safety
BAT014 - grid size appears hardcoded
BAT015 - System.exit inside batch
BAT016 - Thread.sleep inside batch
BAT017 - writer may contain business logic
BAT018 - processor may call remote services per item
BAT019 - generic job or step names
BAT020 - custom ExitStatus should be mapped operationally
```

### New 12-factor and cloud-readiness rules

```text
CLD001 - environment URL packaged in config
CLD002 - active Spring profile committed
CLD003 - secret-like value in packaged configuration
CLD004 - absolute runtime path in configuration
CLD005 - application logs written to local file
CLD006 - local HTTP session state detected
CLD007 - local in-memory state detected
CLD008 - host or endpoint hardcoded in Java
CLD009 - feature flag appears hardcoded
CLD010 - @ConfigurationProperties should be validated
CLD011 - health endpoint customization should support probes
CLD012 - graceful shutdown should be intentional
CLD013 - HTTP client should have timeout policy
CLD014 - batch runtime path not externalized
CLD015 - production profile configuration committed
```

### New observability rules

```text
OBS001 - console print used instead of logger
OBS002 - printStackTrace used
OBS004 - MDC usage requires cleanup
OBS005 - sensitive data may be logged
OBS006 - manual metric counter detected
OBS007 - custom health endpoint may duplicate Actuator
OBS008 - batch listeners should expose counts and duration
OBS009 - correlation header should be propagated consistently
OBS010 - health details exposed in production
```

### New Maven / POM governance rules

```text
POM001 - Spring Boot without parent or BOM
POM002 - Boot starter version override
POM003 - unstable version in production
POM004 - system-scoped dependency
POM005 - Lombok scope not explicit
POM006 - test dependency without test scope
POM007 - H2 compile scope in production
POM008 - DevTools scope not restricted
POM009 - MVC and WebFlux mixed
POM010 - javax and jakarta mixed
POM011 - logging stack mixed
POM012 - custom repositories in project POM
POM013 - custom plugin repositories
POM016 - validation used without starter
POM017 - Java version not governed
POM018 - WAR with embedded Tomcat not provided
POM019 - parent without dependencyManagement
POM020 - parent without pluginManagement
```

### Spring Alternative Advisor

The Spring Alternative Advisor is intentionally large because it is the most distinctive part of Spring Guardian. It detects code that can work, but does not use the Spring ecosystem in a production-ready and idiomatic way.

Examples:

```text
manual ObjectMapper -> Boot-managed ObjectMapper
manual RestTemplate/WebClient/RestClient creation -> configured builders/beans
low-level HttpURLConnection/URL/OkHttpClient -> Spring HTTP clients
manual Thread/ExecutorService/Timer -> TaskExecutor/@Async/TaskScheduler
System.getenv/System.getProperty -> Environment/@ConfigurationProperties
@Value scattered across services -> validated @ConfigurationProperties
FileInputStream/FileReader -> Resource/ResourceLoader
ConcurrentHashMap cache -> Spring Cache
ApplicationContext.getBean -> constructor injection or factory
manual validation -> Bean Validation
manual transaction begin/commit -> @Transactional
manual retry loop -> Spring Retry / Resilience4j
manual health controller -> Actuator HealthIndicator
manual metrics/timing -> Micrometer / Observation
manual audit scattered across services -> events/listeners/audit abstraction
manual localization -> MessageSource
manual CORS annotation -> centralized CORS configuration
manual Jackson module registration -> Jackson2ObjectMapperBuilderCustomizer
manual enum parsing -> Converter/Formatter
CompletableFuture default executor -> @Async or injected TaskExecutor
manual ScheduledExecutorService -> TaskScheduler
manual cache eviction -> @CacheEvict
manual DB migrations -> Flyway/Liquibase
manual profile branching -> @Profile/@ConditionalOnProperty
```

## Source evidence in findings

Each affected component can include the exact source excerpt that triggered the rule. The UI renders this excerpt as a code block under the technical evidence, so the reviewer can immediately see the detected line without opening the file manually.

Example affected component payload:

```json
{
  "type": "JAVA_CLASS",
  "name": "OrderController",
  "filePath": "src/main/java/com/example/OrderController.java",
  "line": 24,
  "evidence": "@Transactional",
  "codeSnippet": "  23 | @RestController\n  24 | @Transactional\n  25 | public class OrderController {"
}
```

## API

Health:

```http
GET /api/v1/health
```

Upload ZIP:

```http
POST /api/v1/scans/upload?language=en&projectType=WEB_API&architectureStyle=AUTO_DETECTED&releaseTarget=PRODUCTION&knownIssuesAccepted=false
Content-Type: multipart/form-data
field: file
```

Upload folder from browser:

```http
POST /api/v1/scans/upload-folder?language=en&projectType=WEB_API&architectureStyle=AUTO_DETECTED&releaseTarget=PRODUCTION&knownIssuesAccepted=false
Content-Type: multipart/form-data
field: files
```

Server-side local path:

```http
POST /api/v1/scans/local?language=en
Content-Type: application/json

{
  "path": "/scan/my-project",
  "projectType": "WEB_API",
  "architectureStyle": "AUTO_DETECTED",
  "releaseTarget": "PRODUCTION",
  "knownIssuesAccepted": false
}
```

## CLI

Build:

```bash
mvn -pl guardian-cli -am package
```

Run:

```bash
java -jar guardian-cli/target/spring-guardian-cli.jar scan /path/to/project --format json --language en --project-type WEB_API --architecture-style AUTO_DETECTED --release-target PRODUCTION
```

Italian report:

```bash
java -jar guardian-cli/target/spring-guardian-cli.jar scan /path/to/project --format json --language it --project-type WEB_API --architecture-style AUTO_DETECTED --release-target INTERNAL --known-issues
```

## Tests

The backend includes unit, slice, integration and end-to-end tests:

```text
guardian-core/src/test/java/com/example/guardian/core/ProjectScanServiceLanguageTest.java
guardian-core/src/test/java/com/example/guardian/core/ProjectScanServiceTest.java
guardian-core/src/test/java/com/example/guardian/core/rules/AdvancedArchitectureRulesTest.java
guardian-core/src/test/java/com/example/guardian/core/rules/SpringAlternativeAdvisorRulesTest.java
guardian-core/src/test/java/com/example/guardian/core/rules/ConfigurationAndDependencyGovernanceRulesTest.java
guardian-core/src/test/java/com/example/guardian/core/rules/ExtendedSpringArchitectureRulesTest.java
guardian-server/src/test/java/com/example/guardian/server/controller/ScanControllerWebMvcTest.java
guardian-server/src/test/java/com/example/guardian/server/ScanControllerIT.java
guardian-server/src/test/java/com/example/guardian/server/e2e/ScanControllerE2EIT.java
```

Commands:

```bash
mvn test
mvn verify
```

## Swagger and JavaDoc

The backend exposes OpenAPI documentation through Springdoc. REST APIs are documented with `@Operation`, `@ApiResponse`, `@Parameter` and `@Schema` where applicable.

The main Java code exposes JavaDoc on models, rules, services, controllers, configurations and CLI commands.

## Git ignore

The project ignores generated and local artifacts such as:

```text
.idea/
target/
node_modules/
dist/
.angular/
*.class
*.jar
*.war
```

---

## Italiano

Spring Guardian è uno **scanner architetturale Spring** e un **scanner di prontezza al rilascio** deterministico per progetti Spring Boot.

Analizza codice Java, POM Maven e file di configurazione Spring senza eseguire il progetto analizzato, senza chiamate AI, senza connessioni database e senza salvare stato. L'obiettivo non è stampare centinaia di righe tecniche incomprensibili, ma rispondere a domande pratiche di architettura:

- questo progetto Spring è pronto al rilascio?
- quali aree sono deboli: sicurezza, web/API, JPA, batch, Maven, prontezza cloud, osservabilità o modernizzazione?
- quali problemi bloccano la produzione?
- quali file e classi sono coinvolti?
- perché ogni problema conta?
- qual è la correzione consigliata secondo le pratiche Spring?

Spring Guardian è complementare a strumenti generici come SonarQube. Non prova a sostituire un SAST generalista: si concentra su architettura Spring, uso corretto del framework, prontezza al rilascio, opportunità di modernizzazione e pattern adatti alla produzione.

## Moduli

```text
spring-guardian
├── guardian-core      # scanner deterministico e regole
├── guardian-cli       # comando CLI
├── guardian-server    # API REST Spring Boot e OpenAPI
└── guardian-ui        # dashboard Angular
```

## Principi

```text
Approccio deterministico come base
Nessuna chiamata AI
Nessuna chiamata database
Nessuno stato nascosto
Nessuna persistenza
Adatto a CI/CD
Problemi specifici dell'ecosistema Spring
Decisione di rilascio leggibile
```

Ogni scansione è stateless. Il profilo selezionato calibra solo la richiesta corrente e non nasconde i problemi. I problemi di sicurezza restano bloccanti anche con baseline legacy attiva.

## Avvio backend

Metodo consigliato su Windows con PowerShell:

```powershell
.\scripts\run-backend.ps1
```

Oppure con CMD:

```cmd
scripts\run-backend.cmd
```

Comando manuale equivalente dalla root del progetto:

```bash
mvn -pl guardian-server -am spring-boot:run
```

Health endpoint:

```text
http://localhost:8080/api/v1/health
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

## Avvio UI Angular

Metodo consigliato:

```powershell
.\scripts\run-ui.ps1
```

In alternativa, manualmente:

```bash
cd guardian-ui
npm install
npm start
```

Apri:

```text
http://localhost:4200
```

La UI utilizza `proxy.conf.json` e inoltra `/api` al backend su `http://localhost:8080`.

## Docker

```bash
docker compose up --build
```

Poi apri:

```text
http://localhost:3000
```

Servizi:

```text
spring-guardian-server -> porta 8080
spring-guardian-ui    -> porta 3000
```

Con Docker puoi montare progetti sotto `./sample-projects` e leggerli nel container da `/scan`.

## Modalità di scansione

La UI supporta tre modalità:

1. **Carica ZIP**  
   Carica un progetto compresso in formato ZIP.

2. **Carica cartella**  
   Seleziona dal browser la cartella root del progetto. La cartella dovrebbe contenere il `pom.xml` principale o i moduli Maven.

3. **Percorso backend**  
   Scansiona un percorso leggibile dal backend, ad esempio:

   ```text
   C:\progetti\mio-progetto
   /scan/mio-progetto
   ```

## Profilo di scansione stateless

Parametri supportati:

```text
projectType: WEB_API | BATCH | LIBRARY
architectureStyle: AUTO_DETECTED | LAYERED | DOMAIN_DRIVEN_DESIGN | HEXAGONAL | LEGACY_LAYERED
releaseTarget: PRODUCTION | INTERNAL | LEGACY_BASELINE
knownIssuesAccepted: true | false
```

La UI mantiene il profilo semplice:

- tipo di progetto;
- obiettivo di rilascio: produzione oppure test/QA;
- flag per baseline legacy.

Il backend accetta ancora `architectureStyle` per CLI e CI, ma il frontend invia `AUTO_DETECTED`. Lo stile architetturale viene rilevato tramite package, annotazioni, dipendenze e capability Spring.

## Prontezza al rilascio

Il report contiene una decisione esplicita:

```text
READY
READY_WITH_WARNINGS
NOT_READY
```

La decisione viene costruita con controlli qualità deterministici:

```text
GATE_SECURITY
GATE_WEB_LAYER
GATE_JPA
GATE_BATCH
GATE_DEPENDENCY_GOVERNANCE
GATE_CLOUD_READINESS
GATE_OBSERVABILITY
GATE_DEPENDENCY_INJECTION
GATE_ARCHITECTURE_BOUNDARIES
GATE_TESTS
GATE_BUILD_CONFIG
GATE_SPRING_ALTERNATIVE_ADVISOR
GATE_PROFILE_ALIGNMENT
```

## Capability rilevate

Spring Guardian rileva il contesto tecnico del progetto da codice Java e POM Maven. Non esegue il progetto analizzato.

Capability rilevate:

```text
Spring Web
Spring Security
JPA / Spring Data JPA
Actuator
Bean Validation
OpenAPI / Swagger
Lombok
Spring Batch
Controller layer
Service layer
Repository layer
Domain layer
Application layer
Infrastructure layer
segnali DDD / Hexagonal / Layered
```

Le capability alimentano regole condizionali. Per esempio, le regole JPA vengono valutate quando è presente JPA, le regole Batch diventano rilevanti per progetti batch, e le regole DDD/esagonali diventano più severe quando esistono domain/application/infrastructure.

## Tipi di finding

I finding sono raggruppati per regola deterministica e classificati per tipo tecnico:

```text
Codice Java
POM Maven
Dipendenze
Configurazione
Test
Sicurezza
JPA e persistenza
Layer web/API
Dependency injection
Codice runtime
Architettura e confini
Spring Batch
Cloud readiness
Osservabilità
Spring Alternative Advisor
```

Questo mantiene leggibili i report anche quando una stessa regola coinvolge molte classi.

## Famiglie di regole

### Base Spring già presente

Spring Guardian copre già le principali famiglie di review Spring:

```text
field injection
controller troppo grandi
repository iniettato nel controller
service layer mancante
entity esposta da API REST
rischi di self-invocation con proxy Spring
@Transactional nel layer sbagliato
gestione manuale connessioni JDBC
@RestControllerAdvice mancante
catch generici
test mancanti
PathVariable senza nome esplicito
versionamento API mancante
qualità POM
naming convention
transazioni read-only
Optional.get senza guard
System.out e printStackTrace
gap di validazione DTO
null return in service/repository
client HTTP manuali
incoerenza package/layer
classi grandi e service complessi
dipendenze Maven duplicate
ddl-auto rischioso
actuator esposto
CSRF/CORS/security smell
fetch JPA eager
repository call dentro loop
GET che modifica stato
response controller raw
Thread.sleep nei test
costruttori entity JPA e relazioni lazy
regole DDD/esagonali
violazioni service/repository layer
SecurityFilterChain
OpenAPI @Operation
@AllArgsConstructor su componenti Spring
campi dependency non final
controller REST senza base mapping
configurazione esternalizzata
profilo Spring attivo hardcoded
conflitti e qualità Maven
```

### Regole aggiunte architetturali

```text
ARCH001 - Domain layer dipende dal web layer
ARCH002 - Domain layer dipende da repository di persistenza
ARCH003 - Application layer ritorna ResponseEntity
ARCH004 - Application layer dipende da servlet API
ARCH005 - Controller chiama direttamente adapter infrastructure
ARCH007 - Entity collocata in modulo shared/common
ARCH008 - DTO API usato nel domain layer
ARCH009 - Domain event dichiarato in infrastructure
ARCH010 - Domain usa direttamente ApplicationEventPublisher
ARCH011 - Configurazione Spring nel package domain
ARCH012 - Common/util rischia di diventare dumping ground
ARCH013 - Interfaccia port annotata come componente Spring
ARCH014 - Adapter possiede il confine transazionale business
ARCH015 - Struttura package non rende visibile l'architettura
```

### Regole aggiunte Spring Security

```text
SEC001 - anyRequest().permitAll
SEC002 - /** permitAll o matcher troppo ampio
SEC003 - CSRF disabilitato senza stateless evidente
SEC004 - CORS wildcard con credentials
SEC005 - tutti gli endpoint actuator esposti
SEC006 - formLogin attivo in API REST
SEC007 - httpBasic attivo in produzione
SEC008 - rememberMe in API REST
SEC009 - frame options disabilitato
SEC010 - H2 console abilitata fuori dev/test
SEC011 - Swagger abilitato esplicitamente in produzione
SEC012 - secret JWT/security hardcoded o creato localmente
SEC013 - NoOpPasswordEncoder
SEC015 - repository legge SecurityContextHolder
SEC016 - ruoli/authority hardcoded
SEC017 - permitAll potenzialmente su endpoint mutating
SEC018 - CORS configurato sul controller
SEC019 - filtro custom senza ordine/policy esplicita
SEC020 - gestione manuale eccezioni security
```

### Regole aggiunte Web/API

```text
WEB001 - service ritorna ResponseEntity
WEB002 - service usa servlet API
WEB003 - controller gestisce eccezioni tecniche localmente
WEB004 - API espone exception.getMessage()
WEB005 - Page<Entity> esposto direttamente
WEB006 - Pageable accettato senza policy di limite
WEB007 - Sort accettato direttamente dall'API
WEB008 - response Object generica
WEB009 - response Map usata come contratto API
WEB010 - endpoint bulk senza limite visibile
WEB011 - GET sembra modificare stato
WEB012 - endpoint mutating dovrebbe validare input
WEB013 - OpenAPI mancante su controller pubblico
WEB014 - endpoint admin mischiato ad API pubblica
WEB015 - DTO vicino a package entity
WEB016 - validazione manuale nel controller
WEB017 - POST di creazione senza status esplicito
WEB018 - DELETE con semantica status poco chiara
WEB019 - multipart senza contratto media/limiti visibile
WEB020 - controller dipende da repository
```

### Regole aggiunte Spring Batch

```text
BAT001 - job dovrebbe validare JobParameters
BAT002 - chunk size hardcoded
BAT003 - JdbcPagingItemReader richiede sort key stabile
BAT004 - JdbcCursorItemReader rilevato su workload batch
BAT005 - reader usa SELECT *
BAT006 - ItemProcessor dovrebbe essere stateless
BAT007 - retry su Exception generico
BAT008 - skip su Exception generico
BAT009 - fault tolerance senza listener visibile
BAT010 - ExecutionContext richiede disciplina di serializzazione
BAT011 - RunIdIncrementer rilevato
BAT012 - allowStartIfComplete richiede idempotenza
BAT013 - step parallelo richiede thread-safety
BAT014 - grid size hardcoded
BAT015 - System.exit dentro batch
BAT016 - Thread.sleep dentro batch
BAT017 - writer con possibile business logic
BAT018 - processor con possibile chiamata remota per item
BAT019 - nomi job/step generici
BAT020 - ExitStatus custom da mappare operativamente
```

### Regole aggiunte 12-factor / prontezza cloud

```text
CLD001 - URL ambiente dentro config pacchettizzata
CLD002 - profilo Spring attivo committato
CLD003 - valore simile a secret in configurazione
CLD004 - path runtime assoluto in configurazione
CLD005 - log applicativi scritti su file locale
CLD006 - stato HttpSession locale
CLD007 - stato/cache locale in memoria
CLD008 - host o endpoint hardcoded in Java
CLD009 - feature flag hardcoded
CLD010 - @ConfigurationProperties da validare
CLD011 - health endpoint da allineare a probe
CLD012 - graceful shutdown da rendere intenzionale
CLD013 - client HTTP senza policy timeout visibile
CLD014 - path batch runtime non esternalizzato
CLD015 - configurazione profilo prod committata
```

### Regole aggiunte Observability

```text
OBS001 - console print invece di logger
OBS002 - printStackTrace
OBS004 - MDC richiede cleanup
OBS005 - dati sensibili potenzialmente loggati
OBS006 - contatore manuale invece di metrica
OBS007 - health endpoint custom che duplica Actuator
OBS008 - listener batch da usare per contatori e durata
OBS009 - correlation header da propagare coerentemente
OBS010 - health details esposti in produzione
```

### Regole aggiunte Maven / POM governance

```text
POM001 - Spring Boot senza parent o BOM
POM002 - versione esplicita su Boot starter
POM003 - versione instabile in produzione
POM004 - dependency system-scoped
POM005 - scope Lombok non esplicito
POM006 - dependency di test senza scope test
POM007 - H2 compile scope in produzione
POM008 - DevTools non ristretto
POM009 - MVC e WebFlux mescolati
POM010 - javax e jakarta mescolati
POM011 - logging stack mescolato
POM012 - repository custom nel POM progetto
POM013 - plugin repository custom
POM016 - validation usata senza starter
POM017 - versione Java non governata
POM018 - WAR con Tomcat embedded non provided
POM019 - parent senza dependencyManagement
POM020 - parent senza pluginManagement
```

### Spring Alternative Advisor

Lo Spring Alternative Advisor è volutamente grande perché è una delle parti più distintive di Spring Guardian. Rileva codice che può funzionare, ma non usa bene l'ecosistema Spring in modo idiomatico e production-ready.

Esempi:

```text
ObjectMapper manuale -> ObjectMapper gestito da Spring Boot
RestTemplate/WebClient/RestClient creati manualmente -> builder/bean configurati
HttpURLConnection/URL/OkHttpClient -> client HTTP Spring
Thread/ExecutorService/Timer manuali -> TaskExecutor/@Async/TaskScheduler
System.getenv/System.getProperty -> Environment/@ConfigurationProperties
@Value sparsi -> @ConfigurationProperties validato
FileInputStream/FileReader -> Resource/ResourceLoader
ConcurrentHashMap cache -> Spring Cache
ApplicationContext.getBean -> constructor injection o factory
validazione manuale -> Bean Validation
gestione manuale begin/commit delle transazioni -> @Transactional
ciclo di retry manuale -> Spring Retry / Resilience4j
controller health manuale -> Actuator HealthIndicator
metriche o misurazioni manuali -> Micrometer / Observation
audit distribuito in più punti -> eventi/listener/servizio audit
localizzazione manuale -> MessageSource
@CrossOrigin distribuito sui controller -> configurazione CORS centralizzata
moduli Jackson manuali -> Jackson2ObjectMapperBuilderCustomizer
parsing enum manuale -> Converter/Formatter
CompletableFuture sul common pool -> @Async o TaskExecutor iniettato
ScheduledExecutorService manuale -> TaskScheduler
eviction manuale della cache -> @CacheEvict
migrazioni DB manuali -> Flyway/Liquibase
branching manuale sui profili -> @Profile/@ConditionalOnProperty
```

## Evidenza del codice nei problemi

Ogni componente coinvolto può includere il frammento di codice che ha fatto scattare la regola. La UI mostra questo frammento come blocco di codice sotto l’evidenza tecnica, così chi revisiona il report vede subito la riga rilevata senza dover aprire manualmente il file.

Esempio di componente coinvolto nel JSON:

```json
{
  "type": "JAVA_CLASS",
  "name": "OrderController",
  "filePath": "src/main/java/com/example/OrderController.java",
  "line": 24,
  "evidence": "@Transactional",
  "codeSnippet": "  23 | @RestController\n  24 | @Transactional\n  25 | public class OrderController {"
}
```

## API

Health:

```http
GET /api/v1/health
```

Scan ZIP:

```http
POST /api/v1/scans/upload?language=it&projectType=WEB_API&architectureStyle=AUTO_DETECTED&releaseTarget=PRODUCTION&knownIssuesAccepted=false
Content-Type: multipart/form-data
field: file
```

Scan cartella dal browser:

```http
POST /api/v1/scans/upload-folder?language=it&projectType=WEB_API&architectureStyle=AUTO_DETECTED&releaseTarget=PRODUCTION&knownIssuesAccepted=false
Content-Type: multipart/form-data
field: files
```

Scan path locale server-side:

```http
POST /api/v1/scans/local?language=it
Content-Type: application/json

{
  "path": "/scan/my-project",
  "projectType": "WEB_API",
  "architectureStyle": "AUTO_DETECTED",
  "releaseTarget": "PRODUCTION",
  "knownIssuesAccepted": false
}
```

## CLI

Build:

```bash
mvn -pl guardian-cli -am package
```

Esecuzione:

```bash
java -jar guardian-cli/target/spring-guardian-cli.jar scan /path/to/project --format json --language it --project-type WEB_API --architecture-style AUTO_DETECTED --release-target PRODUCTION
```

Report inglese:

```bash
java -jar guardian-cli/target/spring-guardian-cli.jar scan /path/to/project --format json --language en --project-type WEB_API --architecture-style AUTO_DETECTED --release-target INTERNAL --known-issues
```

## Test

Il backend include test unitari, slice, di integrazione ed end-to-end:

```text
guardian-core/src/test/java/com/example/guardian/core/ProjectScanServiceLanguageTest.java
guardian-core/src/test/java/com/example/guardian/core/ProjectScanServiceTest.java
guardian-core/src/test/java/com/example/guardian/core/rules/AdvancedArchitectureRulesTest.java
guardian-core/src/test/java/com/example/guardian/core/rules/SpringAlternativeAdvisorRulesTest.java
guardian-core/src/test/java/com/example/guardian/core/rules/ConfigurationAndDependencyGovernanceRulesTest.java
guardian-core/src/test/java/com/example/guardian/core/rules/ExtendedSpringArchitectureRulesTest.java
guardian-server/src/test/java/com/example/guardian/server/controller/ScanControllerWebMvcTest.java
guardian-server/src/test/java/com/example/guardian/server/ScanControllerIT.java
guardian-server/src/test/java/com/example/guardian/server/e2e/ScanControllerE2EIT.java
```

Comandi:

```bash
mvn test
mvn verify
```

## Swagger e JavaDoc

Il backend espone documentazione OpenAPI tramite Springdoc. Le API REST sono documentate con `@Operation`, `@ApiResponse`, `@Parameter` e `@Schema` dove applicabile.

Il codice Java principale espone JavaDoc su modelli, regole, servizi, controller, configurazioni e comandi CLI.

## Git ignore

Il progetto ignora artifact generati o locali come:

```text
.idea/
target/
node_modules/
dist/
.angular/
*.class
*.jar
*.war
```

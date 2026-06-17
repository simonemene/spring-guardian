# Spring Guardian

<img width="1666" height="944" alt="ChatGPT Image 17 giu 2026, 12_50_38" src="https://github.com/user-attachments/assets/b355b536-4b9c-4d9e-bda3-38fa36e9aceb" />


Spring Guardian è uno scanner architetturale per progetti Spring Boot. Analizza codice Java e POM Maven, individua problemi tipici di manutenzione, sicurezza, layering, testabilità e qualità Spring, e produce un report leggibile raggruppato per regola.

L'obiettivo non è stampare centinaia di righe tecniche incomprensibili, ma rispondere a queste domande:

- qual è il rischio complessivo del progetto;
- quali aree sono più deboli;
- quali problemi vanno corretti prima;
- quali classi o file sono coinvolti;
- perché il problema conta;
- qual è il fix consigliato.

## Moduli

```text
spring-guardian
├── guardian-core
├── guardian-cli
├── guardian-server
└── guardian-ui
```

## Avvio backend

Metodo consigliato su Windows/PowerShell:

```powershell
.\scripts\run-backend.ps1
```

Oppure con CMD:

```cmd
scripts\run-backend.cmd
```

Comandi manuali equivalenti dalla root del progetto:

```bash
mvn -pl guardian-server -am spring-boot:run
```

Il parent Maven configura `spring-boot-maven-plugin` con `skip=true`, mentre `guardian-server` lo riabilita con `skip=false` e main class esplicita. In questo modo il comando include `guardian-core` nel reactor runtime senza provare ad avviare il POM aggregator.

Endpoint di test:

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

## Avvio frontend Angular

Metodo consigliato:

```powershell
.\scripts\run-ui.ps1
```

Oppure manualmente:

```bash
cd guardian-ui
npm install
npm start
```

Apri:

```text
http://localhost:4200
```

Il frontend usa `proxy.conf.json` e inoltra `/api` al backend su `http://localhost:8080`.

Non sono inclusi `.npmrc` o `package-lock.json`: `npm install` userà il registry configurato sulla macchina, evitando riferimenti a registry temporanei o non raggiungibili. Il `package-lock.json` è ignorato dal Git del progetto per evitare di committare lock generati con registry aziendali o locali.

## Lingua report

La UI permette di scegliere la lingua del report:

- Italiano;
- Inglese.

La lingua viene inviata al backend tramite query parameter:

```text
language=it
language=en
```

I testi parlanti del report sono localizzati lato backend: titolo problema, categoria, spiegazione, fix consigliato, riepilogo esecutivo, spiegazione del punteggio e azioni consigliate.

## Modalità di scansione dalla UI

La UI Angular supporta tre modalità:

1. **Carica ZIP**  
   Carichi un progetto compresso `.zip`.

2. **Carica cartella**  
   Selezioni direttamente la cartella root del progetto dal browser. È la modalità più comoda quando lavori in locale. La cartella dovrebbe contenere il `pom.xml` principale o i moduli Maven.

3. **Percorso backend**  
   Inserisci un path leggibile dal backend, ad esempio:

   ```text
   C:\progetti\mio-progetto
   /scan/mio-progetto
   ```

   Questa modalità è utile quando backend e progetto sono sulla stessa macchina oppure quando in Docker monti una cartella sotto `/scan`.

## Esperienza utente e lettura problemi

La UI non mostra solo severità come Critico, Alto, Medio e Info. Ogni problema viene classificato anche per tipo tecnico:

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
Spring Alternative Advisor
```

Questo permette di capire subito se un report è dominato da codice applicativo, POM/dipendenze, configurazione, test o suggerimenti Spring. La sezione **Alternative Spring** è separata per non mischiare suggerimenti di modernizzazione con problemi bloccanti di rilascio.

## Avvio completo con Docker

```bash
docker compose up --build
```

Poi apri:

```text
http://localhost:3000
```

Servizi Docker:

```text
spring-guardian-server -> porta 8080
spring-guardian-ui    -> porta 3000
```

Con Docker puoi montare progetti sotto `./sample-projects` e leggerli nel container da `/scan`.


## Profilo di scansione stateless

Ogni scansione può essere calibrata senza salvare nulla e senza usare AI, database o stato applicativo. Il profilo serve solo per la richiesta corrente.

Parametri supportati:

```text
projectType: WEB_API | BATCH | LIBRARY
architectureStyle: AUTO_DETECTED | LAYERED | DOMAIN_DRIVEN_DESIGN | HEXAGONAL | LEGACY_LAYERED
releaseTarget: PRODUCTION | INTERNAL | LEGACY_BASELINE
knownIssuesAccepted: true | false
```

La UI mantiene il profilo semplice e mostra solo:

- tipo applicazione;
- obiettivo scansione, produzione oppure test/QA;
- flag legacy con problemi noti.

Lo stile architetturale viene rilevato automaticamente dal backend tramite package, annotazioni, dipendenze e capability Spring. L'API mantiene `architectureStyle` per uso CLI o pipeline CI, ma il frontend invia `AUTO_DETECTED`.

Questa calibrazione non nasconde i problemi: cambia solo la severità decisionale dei quality gate. I problemi di sicurezza restano bloccanti anche quando è attiva la baseline legacy.

## Prontezza al rilascio e quality gate

Il report ora contiene una decisione esplicita:

```text
READY
READY_WITH_WARNINGS
NOT_READY
```

La decisione è costruita con gate deterministici:

```text
GATE_SECURITY
GATE_WEB_LAYER
GATE_JPA
GATE_DEPENDENCY_INJECTION
GATE_ARCHITECTURE_BOUNDARIES
GATE_TESTS
GATE_BUILD_CONFIG
GATE_SPRING_ALTERNATIVE_ADVISOR
GATE_PROFILE_ALIGNMENT
```

In italiano la UI mostra sezioni parlanti come:

- Spring Security;
- layer web e contratti API;
- JPA e persistenza;
- dependency injection;
- confini architetturali;
- test;
- build e configurazione;
- Spring Alternative Advisor.

In inglese le stesse sezioni sono restituite come:

- Spring Security;
- Web layer and API contracts;
- JPA and persistence;
- Dependency injection;
- Architecture boundaries;
- Tests;
- Build and configuration;
- Spring Alternative Advisor.

## Rilevamento stack e pattern architetturali

Spring Guardian rileva il contesto tecnico del progetto analizzando sorgenti Java e POM Maven. Non esegue il progetto e non apre connessioni esterne.

Esempi di capability rilevate:

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
DDD / Hexagonal / Layered
```

Queste capability alimentano le sezioni UI, i quality gate e le regole condizionali. Ad esempio, le regole JPA vengono valutate quando il progetto usa JPA, mentre le regole DDD diventano più severe quando selezioni Domain Driven Design o architettura esagonale.

## Nuovi controlli architetturali

La scansione include nuovi controlli mirati per rendere il report più vicino a una review architetturale reale:

```text
SPR053 - Entity JPA senza costruttore no-args pubblico o protected
SPR054 - Relazione JPA ToOne senza fetch LAZY esplicito
SPR055 - Domain layer che dipende da Spring in profilo DDD/esagonale
SPR056 - Service layer che dipende dal web layer
SPR057 - Repository layer che dipende da layer superiori
SPR058 - Spring Security rilevato ma SecurityFilterChain mancante
SPR059 - CSRF disabilitato senza session management stateless
SPR060 - Endpoint REST senza @Operation OpenAPI
SPR061 - @AllArgsConstructor su componente Spring
SPR062 - Dipendenza assegnata da costruttore ma campo non final
SPR063 - @RestController senza base @RequestMapping
```

Le descrizioni e i fix di queste regole sono bilingue e vengono localizzati dal backend.

## Spring Alternative Advisor

Spring Guardian include una sezione dedicata e visibile in UI alle alternative Spring o Spring Boot quando il codice usa API Java manuali o pattern che funzionano, ma non sfruttano bene il framework. La tab **Alternative Spring** mostra solo questi suggerimenti, mentre la tab **Problemi** permette di filtrare anche per tipo tecnico: codice Java, POM Maven, dipendenze, configurazione, test, sicurezza, JPA, web layer e Spring Alternative Advisor.

Esempi di controlli:

```text
SPR064 - ObjectMapper creato manualmente invece di usare quello gestito da Spring Boot
SPR065 - Thread creato manualmente invece di TaskExecutor, @Async o TaskScheduler
SPR066 - ExecutorService creato manualmente invece di ThreadPoolTaskExecutor
SPR067 - Timer o TimerTask invece di @Scheduled o TaskScheduler
SPR068 - JdbcTemplate creato manualmente invece di bean auto-configurato
SPR069 - PasswordEncoder creato manualmente invece di bean centralizzato
SPR070 - System.getenv o System.getProperty invece di @ConfigurationProperties o Environment
SPR071 - FileInputStream/FileReader diretti invece di Resource o ResourceLoader
SPR072 - Service, repository, client o adapter creati con new dentro componenti Spring
SPR073 - @MockBean/@SpyBean da modernizzare verso @MockitoBean/@MockitoSpyBean
SPR074 - Structured logging Spring Boot da valutare sui progetti moderni
SPR075 - MockMvcTester da valutare nei nuovi test MVC su Spring Boot recenti
SPR076 - RestTemplate creato manualmente da sostituire con bean configurato o RestClient
SPR077 - WebClient.builder/create usato direttamente invece di builder/bean configurato
SPR078 - RestClient.builder/create duplicato in classi applicative
SPR079 - URL/HttpClient/OkHttpClient/HttpURLConnection usati come client HTTP di basso livello
SPR080 - SimpleDateFormat invece di DateTimeFormatter
SPR081 - Gson/GsonBuilder manuale accanto alla policy JSON Spring Boot/Jackson
SPR082 - @Value sparsi dove sarebbe meglio @ConfigurationProperties validato
SPR083 - @Async senza @EnableAsync
SPR084 - @Scheduled senza @EnableScheduling
SPR085 - LocalDateTime.now diretto invece di Clock iniettato nella logica applicativa
SPR086 - ApplicationContext.getBean invece di constructor injection o factory dedicata
SPR088 - Thread.sleep nel codice applicativo
SPR089 - Validation.buildDefaultValidatorFactory invece di Validator gestito da Spring
SPR090 - ConcurrentHashMap usato come possibile cache manuale invece di Spring Cache
```

Questa sezione non chiama servizi esterni e non scarica informazioni online durante la scansione. È un catalogo deterministico locale di alternative note e consigli architetturali.

In inglese la stessa area viene esposta come **Spring Alternative Advisor** e mantiene titoli, spiegazioni e fix localizzati dal backend.

## API principali

### Health

```http
GET /api/v1/health
```

### Scan ZIP

```http
POST /api/v1/scans/upload?language=it&projectType=WEB_API&architectureStyle=AUTO_DETECTED&releaseTarget=PRODUCTION&knownIssuesAccepted=false
Content-Type: multipart/form-data
field: file
```

### Scan cartella caricata dal browser

```http
POST /api/v1/scans/upload-folder?language=it&projectType=WEB_API&architectureStyle=AUTO_DETECTED&releaseTarget=PRODUCTION&knownIssuesAccepted=false
Content-Type: multipart/form-data
field: files
```

Il frontend invia tutti i file della cartella preservando i path relativi.

### Scan path locale server-side

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

## Report JSON

Il report è raggruppato per regola. In questo modo, se una stessa regola trova 50 classi coinvolte, la UI mostra una sola scheda con l'elenco dei file interessati.

Esempio semplificato:

```json
{
  "projectName": "my-project",
  "profile": {
    "projectType": "WEB_API",
    "architectureStyle": "AUTO_DETECTED",
    "releaseTarget": "PRODUCTION",
    "knownIssuesAccepted": false
  },
  "capabilities": {
    "usesSpringWeb": true,
    "usesSpringSecurity": true,
    "usesJpa": true,
    "hasControllerLayer": true,
    "hasServiceLayer": true,
    "hasRepositoryLayer": true,
    "detectedArchitecturalStyles": ["LAYERED"]
  },
  "releaseReadiness": {
    "status": "NOT_READY",
    "label": "Non pronto al rilascio",
    "releasable": false,
    "blockers": [
      "Spring Security: 2 problemi richiedono revisione in quest'area."
    ],
    "warnings": []
  },
  "architectureScore": 72,
  "riskLevel": "HIGH",
  "qualityGates": [
    {
      "code": "GATE_SECURITY",
      "name": "Spring Security",
      "status": "FAIL",
      "failingFindings": 2
    },
    {
      "code": "GATE_SPRING_ALTERNATIVE_ADVISOR",
      "name": "Spring Alternative Advisor",
      "status": "WARNING",
      "required": false
    }
  ],
  "findingsByType": [
    {
      "category": "Spring Alternative Advisor",
      "findings": 8,
      "explanation": "API Java manuali e alternative Spring moderne rilevate dallo Spring Alternative Advisor."
    },
    {
      "category": "Dipendenze",
      "findings": 2,
      "explanation": "Problemi su dipendenze e versioni Maven che possono influire su build o compatibilità Spring."
    }
  ],
  "architectureAreas": [
    {
      "code": "JPA_PERSISTENCE",
      "name": "JPA e persistenza",
      "findings": 3,
      "readinessStatus": "ATTENTION"
    }
  ],
  "findings": [
    {
      "ruleId": "SPR053_JPA_ENTITY_ACCESSIBLE_NO_ARGS_CONSTRUCTOR",
      "severity": "MAJOR",
      "category": "JPA, persistenza e integrazioni",
      "findingType": "JPA",
      "findingTypeLabel": "JPA e persistenza",
      "title": "Entity JPA senza costruttore no-args accessibile",
      "occurrences": 1,
      "whyItMatters": "Le entity JPA devono poter essere istanziate dal provider tramite un costruttore no-args pubblico o protected.",
      "suggestedFix": "Aggiungi un costruttore no-args protected e mantieni i costruttori di dominio espliciti per l'uso applicativo.",
      "affectedComponents": [
        {
          "type": "JAVA_CLASS",
          "name": "Customer",
          "filePath": "src/main/java/.../Customer.java",
          "line": 12,
          "evidence": "Entity Customer defines constructors but no public or protected no-args constructor."
        }
      ]
    }
  ]
}
```

Nella UI i codici tecnici come `SPR030_GOD_CLASS` vengono mostrati come codice breve, ad esempio `SPR030`, mentre titolo, spiegazione e fix sono parlanti.

## Swagger e JavaDoc

Il backend espone documentazione OpenAPI tramite Springdoc.

Il codice Java principale espone JavaDoc su modelli, regole, controller, DTO, configurazioni, servizi backend e comandi CLI. Le API REST principali sono annotate con `@Operation`, `@ApiResponse`, `@Parameter` e `@Schema`.

## Test backend

Il backend include una struttura di test divisa per livello:

```text
guardian-core/src/test/java/com/example/guardian/core/ProjectScanServiceLanguageTest.java
guardian-core/src/test/java/com/example/guardian/core/rules/AdvancedArchitectureRulesTest.java
guardian-core/src/test/java/com/example/guardian/core/rules/SpringAlternativeAdvisorRulesTest.java
guardian-server/src/test/java/com/example/guardian/server/service/ZipWorkspaceServiceTest.java
guardian-server/src/test/java/com/example/guardian/server/controller/ScanControllerWebMvcTest.java
guardian-server/src/test/java/com/example/guardian/server/ScanControllerIT.java
guardian-server/src/test/java/com/example/guardian/server/e2e/ScanControllerE2EIT.java
```

Copertura prevista:

- unit test per parser lingua, servizio di scansione e workspace ZIP/folder;
- slice test `@WebMvcTest` per controller REST;
- integration test `@SpringBootTest` con porta random;
- end-to-end test HTTP multipart ZIP con backend reale.

Comandi:

```bash
mvn test
mvn verify
```

## Policy sui test nei progetti analizzati

Le regole architetturali production-oriented, come `SPR030_GOD_CLASS`, ignorano i test:

```text
src/test/**
*Test.java
*Tests.java
*IT.java
```

Questo evita falsi positivi come classi di test grandi, che spesso sono accettabili se organizzano molti scenari dello stesso componente.

## Casi coperti

Esempi di regole incluse:

- controller troppo grandi;
- service complessi;
- repository iniettati nei controller;
- entity JPA esposte direttamente dalle API;
- DTO senza validazione;
- `@Transactional` su controller;
- transazioni read-only mancanti;
- `Optional.get()` senza controllo;
- `System.out.println` o `printStackTrace`;
- logger non `static final`;
- profili ambiente mancanti;
- CSRF disabilitato;
- `permitAll` troppo ampio;
- actuator esposto completamente;
- CORS wildcard con credentials;
- catch vuoti;
- fetch JPA `EAGER`;
- chiamate repository dentro loop;
- endpoint GET che mutano stato;
- response non tipizzate nei controller;
- `Thread.sleep` nei test;
- entity JPA con costruttori non conformi;
- relazioni JPA ToOne senza `LAZY`;
- domain layer che dipende da Spring in profili DDD/esagonali;
- service layer che dipende da `ResponseEntity`, servlet o web layer;
- repository che dipende da layer superiori;
- Spring Security senza `SecurityFilterChain`;
- CSRF disabilitato senza sessioni stateless;
- endpoint REST senza `@Operation`;
- `@AllArgsConstructor` su componenti Spring;
- campi dependency non `final` con constructor injection;
- controller REST senza base mapping;
- ObjectMapper creato manualmente;
- RestTemplate, WebClient, RestClient e client HTTP di basso livello creati manualmente;
- Thread, ExecutorService, Timer, TimerTask e Thread.sleep nel codice applicativo;
- JdbcTemplate e PasswordEncoder creati fuori da bean centralizzati;
- System.getenv/System.getProperty nel codice applicativo;
- FileInputStream/FileReader diretti;
- SimpleDateFormat, Gson, ValidatorFactory e accesso diretto al clock;
- collaboratori Spring creati con new;
- @Value sparsi invece di @ConfigurationProperties;
- @Async/@Scheduled senza relativa abilitazione;
- ApplicationContext.getBean fuori da factory dedicate;
- possibili cache manuali con ConcurrentHashMap;
- @MockBean/@SpyBean da modernizzare;
- structured logging e MockMvcTester come opportunità Spring Boot moderne.

## CLI

Build:

```bash
mvn -pl guardian-cli -am package
```

Esecuzione:

```bash
java -jar guardian-cli/target/spring-guardian-cli.jar scan /path/to/project --format json --language it --project-type WEB_API --architecture-style AUTO_DETECTED --release-target PRODUCTION
```

Lingua inglese:

```bash
java -jar guardian-cli/target/spring-guardian-cli.jar scan /path/to/project --format json --language en --project-type WEB_API --architecture-style AUTO_DETECTED --release-target INTERNAL --known-issues
```

## Git ignore

Il progetto include `.gitignore` root per evitare commit accidentali di:

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

## v10 - Configuration and dependency governance

Questa versione rafforza due aree fondamentali per usare Spring Guardian come gate di rilascio aziendale.

### Configurazione esternalizzata

Spring Guardian ora segnala quando `application.properties`, `application.yml` o `application.yaml` contengono valori runtime specifici dell'ambiente dentro `src/main/resources`.

Nuove regole:

```text
SPR091_APPLICATION_PROPERTIES_SHOULD_BE_EXTERNALIZED
SPR092_HARDCODED_ACTIVE_SPRING_PROFILE
```

Esempi rilevati:

```properties
spring.datasource.url=jdbc:postgresql://server:5432/app
server.port=8081
spring.profiles.active=prod
external.payment.endpoint=https://payments.example.local
```

Il principio è: l'artefatto deve essere il più possibile neutro rispetto all'ambiente. Nel repository devono restare placeholder, default sicuri o configurazioni realmente comuni; valori di ambiente, segreti, host, porte, endpoint, cron e path operativi devono arrivare da variabili ambiente, configurazione montata, ConfigMap/Secret, Vault, secret manager o piattaforma di deploy.

English: Spring Guardian now detects packaged runtime configuration inside `application.properties` or `application.yml`. The artifact should stay environment-neutral; environment-specific values should be injected by the deployment platform.

### Conflitti e igiene dipendenze Maven

Spring Guardian ora controlla anche conflitti e combinazioni sospette nel POM.

Nuove regole:

```text
SPR093_MAVEN_DEPENDENCY_VERSION_CONFLICT
SPR094_MAVEN_MIXED_STACK_DEPENDENCIES
SPR095_MAVEN_DEPENDENCY_HYGIENE
```

Esempi rilevati:

```text
Stessa dipendenza dichiarata con versioni diverse in moduli diversi
spring-boot-starter-web insieme a spring-boot-starter-webflux
javax.persistence insieme a jakarta.persistence
Gson insieme a Jackson senza decisione esplicita
Log4j insieme allo starter logging di default
Versioni SNAPSHOT, LATEST, RELEASE o range Maven
Dipendenze system-scoped
Lombok senza scope esplicito
```

English: Maven governance now includes direct version conflict detection, suspicious mixed stacks and release hygiene checks such as SNAPSHOT/ranged versions, system-scoped dependencies and missing Lombok scope.

### UI aggiornata

La dashboard mostra ora card dedicate anche a:

```text
Configurazione
Dipendenze
Spring Alternative Advisor
```

Questo evita di leggere solo Critico/Alto/Medio e permette di capire subito se il progetto è debole per codice Java, POM, dipendenze, configurazione, test, sicurezza, JPA, layer web/API o alternative Spring.

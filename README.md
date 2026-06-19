# Spring Guardian

<img width="1666" height="944" alt="Spring Guardian dashboard" src="https://github.com/user-attachments/assets/b355b536-4b9c-4d9e-bda3-38fa36e9aceb" />

## English

Spring Guardian is a deterministic **Spring Architecture Auditor** and **Release Readiness Scanner** for Spring Boot projects.

It analyzes Java source code, Maven POM files and Spring configuration files without executing the target project, without calling AI services, without opening database connections and without storing scan state.

Its goal is not to produce a noisy list of generic static-analysis warnings. Its goal is to answer practical engineering questions:

- Is this Spring project ready for release?
- Which findings block production?
- Which findings should be fixed before release?
- Which findings are technical debt?
- Which findings are only Spring modernization suggestions?
- Which files and code lines caused the finding?
- Why does the finding matter?
- What is the recommended Spring-oriented fix?

Spring Guardian is complementary to tools such as SonarQube. It focuses on Spring architecture, framework usage, modernization opportunities, cloud readiness, maintainability and release-readiness patterns.

## Current supported scope

This hardened version intentionally focuses on two project profiles only:

```text
WEB_API  -> Spring MVC / REST APIs, validation, controller boundaries, security-related web risks and release readiness.
BATCH    -> Spring Batch jobs, chunk/retry/skip behavior, restartability, operational paths and batch observability.
```

Camel-specific route analysis is intentionally disabled for now. Camel classes are scanned only as normal Java/Spring code when they belong to a Web/API project; they are not interpreted as Spring Batch processors or Camel-specific architecture findings.

The rule engine favors high-confidence evidence. Import statements, comments and generic Java constructs are not used as the main proof for architectural or Advisor findings.


## Brand identity

The application now includes a simple Spring Guardian brand system:

```text
minimal shield + code mark
```

The official UI assets are stored under:

```text
guardian-ui/src/assets/spring-guardian-logo.svg
guardian-ui/src/assets/spring-guardian-wordmark.svg
guardian-ui/src/assets/favicon.svg
```

The visual direction is intentionally minimal and professional: the shield communicates protection, the code mark communicates static analysis and modernization, and the compact shape remains readable in the header, favicon and GitHub contexts.

## What Spring Guardian is not

Spring Guardian is intentionally scoped:

```text
It is not a replacement for SonarQube.
It is not a complete security audit.
It is not a runtime profiler.
It does not execute the target application.
It does not prove the application is bug-free.
It does not call AI models.
It does not connect to databases.
```

It focuses on deterministic Spring-specific review signals: architecture, layering, Spring Security configuration, Web/API contracts, Spring Batch operational readiness, Maven governance, 12-factor/cloud readiness, observability and Spring-native alternatives.

## Decision-first report UX

Large rule catalogs can become noisy if the report does not guide the user. Spring Guardian separates findings by decision impact:

```text
CRITICAL -> production blocker
MAJOR    -> fix before release
MINOR    -> relevant technical debt
INFO     -> advisory, modernization or best-practice suggestion
```

The UI highlights five lanes:

```text
1. Production blockers
2. Fix before release
3. Relevant technical debt
4. Spring suggestions
5. Informational notes
```

This makes the tool usable even with a large catalog: the reviewer immediately sees what must be fixed now and what can be planned later.


## Action-oriented finding cards

Every finding card is now intentionally short and decision-oriented. The report does not only say that a rule matched. It separates the information needed to act:

```text
What was detected
Possible impact
Recommended solution
Detected class/file/line
Source evidence
Official documentation link when useful
Optional generic example only outside the main evidence block
```

The **Findings** tab is grouped by technical area so the report does not feel mixed or noisy:

```text
Spring Security
Web/API contracts
Architecture and boundaries
JPA and persistence
Spring Batch
Cloud / 12-factor readiness
Observability
POM / dependency governance
Tests
Runtime correctness
```

Spring Batch findings are profile-aware: they are shown only when the scan profile is `BATCH` or when Spring Batch is actually detected in the target project. A Web/API project should not receive Batch findings only because it contains classes named Processor or loops over collections.

Spring Alternative Advisor cards have an additional structure:

```text
Detected manual/low-level implementation
Spring alternative to use
Official documentation link
Affected source code
```

Example:

```text
Detected: new ObjectMapper().
Use: the ObjectMapper managed by Spring Boot.
Documentation: https://docs.spring.io/spring-boot/reference/features/json.html
```

Before:

```java
private final ObjectMapper mapper = new ObjectMapper();
```

After:

```java
private final ObjectMapper mapper;

public MyProcessor(ObjectMapper mapper) {
    this.mapper = mapper;
}
```

This keeps the Advisor useful: it must say exactly what was detected, which Spring abstraction should replace it and why that alternative is safer for configuration, lifecycle, testing and observability.

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
Source evidence for each finding
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

Endpoints:

```text
Health       http://localhost:8080/api/v1/health
Swagger UI   http://localhost:8080/swagger-ui/index.html
OpenAPI JSON http://localhost:8080/v3/api-docs
```

## Start the Angular UI

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

1. **Upload ZIP**: upload a compressed Spring project.
2. **Upload folder**: select the project root folder directly from the browser.
3. **Backend path**: scan a path readable by the backend, for example `/scan/my-project`.

## Stateless scan profile

Supported parameters:

```text
projectType: WEB_API | BATCH
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

The report contains an explicit decision:

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

Advisor findings do not block production by default. They are separated from blocking issues so the report remains actionable.

## Rule catalog policy

The rule catalog keeps stable rule identifiers, but the runtime catalog is intentionally conservative in this version. It enables high-confidence checks for:

```text
WEB*   Web/API contracts, validation and controller boundaries
BAT*   Spring Batch restartability and operational readiness
CLD*   selected 12-factor/cloud-readiness configuration checks
OBS*   selected observability and logging checks
POM*   selected Maven dependency-governance checks
ADV*   high-confidence Spring Alternative Advisor detections
SPR*   original Spring-specific rules that have concrete source evidence
```

Rules that only relied on imports, comments, isolated keywords or weak textual hints are not part of the active runtime catalog. This keeps the report credible: every finding must be supported by code that demonstrates the reported issue.

## Source evidence in findings

Each affected component can include the exact source excerpt that triggered the rule. The UI renders this excerpt as a code block under the technical evidence, so reviewers can understand the finding without opening the file manually.

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

## Concrete examples

### 1. Self-invocation and `@Transactional`

Before:

```java
@Service
class OrderService {
    public void createOrder() {
        saveOrder();
    }

    @Transactional
    void saveOrder() {
        // database write
    }
}
```

Scan result:

```text
SPR007 - Spring proxy self-invocation risk
Impact: the transactional method may be called without crossing the Spring proxy.
```

After:

```java
@Service
class OrderApplicationService {
    private final OrderWriter orderWriter;

    public void createOrder() {
        orderWriter.saveOrder();
    }
}

@Service
class OrderWriter {
    @Transactional
    void saveOrder() {
        // database write
    }
}
```

### 2. Repository injected into controller

Before:

```java
@RestController
class CustomerController {
    private final CustomerRepository repository;

    @GetMapping("/customers/{id}")
    Customer find(@PathVariable Long id) {
        return repository.findById(id).orElseThrow();
    }
}
```

Scan result:

```text
SPR003 / WEB017 - Controller depends on repository
Impact: the controller bypasses the application/service layer.
```

After:

```java
@RestController
class CustomerController {
    private final CustomerService customerService;

    @GetMapping("/customers/{id}")
    CustomerResponse find(@PathVariable Long id) {
        return customerService.findCustomer(id);
    }
}
```

### 3. Entity exposed through API

Before:

```java
@GetMapping("/customers/{id}")
CustomerEntity find(@PathVariable Long id) {
    return service.findEntity(id);
}
```

Scan result:

```text
SPR006 / WEB005 - Entity exposed in REST response
Impact: the API contract is coupled to the persistence model.
```

After:

```java
@GetMapping("/customers/{id}")
CustomerResponse find(@PathVariable Long id) {
    return service.findCustomer(id);
}
```

### 4. Broad `permitAll`

Before:

```java
http.authorizeHttpRequests(auth -> auth
        .requestMatchers("/**").permitAll()
        .anyRequest().authenticated());
```

Scan result:

```text
SEC003 - Wildcard security matcher detected
Impact: the whole application can become publicly accessible.
```

After:

```java
http.authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/v1/public/**", "/actuator/health").permitAll()
        .anyRequest().authenticated());
```

### 5. Environment-specific configuration packaged in the artifact

Before:

```properties
spring.profiles.active=prod
external.payment.endpoint=https://payments.example.local
spring.datasource.password=secret
```

Scan result:

```text
CLD001 / CLD002 / CLD003 - Runtime configuration packaged in application config
Impact: the artifact is no longer environment-neutral.
```

After:

```properties
external.payment.endpoint=${PAYMENT_ENDPOINT}
spring.datasource.password=${DB_PASSWORD}
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

## Tests

The project includes backend tests by level:

```text
guardian-core/src/test/java/com/example/guardian/core/ProjectScanServiceLanguageTest.java
guardian-core/src/test/java/com/example/guardian/core/rules/AdvancedArchitectureRulesTest.java
guardian-core/src/test/java/com/example/guardian/core/rules/SpringAlternativeAdvisorRulesTest.java
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

---

## Italiano

Spring Guardian è uno **scanner architetturale deterministico** per progetti Spring Boot. Analizza codice Java, POM Maven e file di configurazione Spring senza eseguire il progetto, senza chiamare servizi AI, senza aprire connessioni al database e senza salvare stato applicativo.

L'obiettivo non è produrre una lista rumorosa di segnalazioni generiche, ma rispondere a domande concrete di review tecnica:

- il progetto è pronto al rilascio?
- quali problemi bloccano la produzione?
- quali problemi vanno corretti prima del rilascio?
- quali elementi sono debito tecnico?
- quali elementi sono solo suggerimenti di modernizzazione Spring?
- quali file e righe di codice hanno generato la segnalazione?
- perché il problema è rilevante?
- qual è la correzione consigliata in ottica Spring?

Spring Guardian non sostituisce strumenti generici come SonarQube. È complementare: si concentra su architettura Spring, uso corretto del framework, modernizzazione, prontezza cloud, manutenibilità e readiness di rilascio.

## Perimetro supportato attuale

Questa versione stabilizzata si concentra intenzionalmente su due profili di progetto:

```text
WEB_API  -> API Spring MVC / REST, validazione, confini controller, rischi web/security e readiness di rilascio.
BATCH    -> job Spring Batch, chunk/retry/skip, riavviabilità, path operativi e osservabilità batch.
```

L'analisi specifica di Camel è volutamente disattivata per ora. Le classi Camel vengono lette solo come normale codice Java/Spring quando appartengono a un progetto Web/API; non vengono interpretate come processor Spring Batch o come finding architetturali specifici di Camel.

Il motore regole privilegia evidenze ad alta confidenza. Import, commenti e costrutti Java generici non vengono usati come prova principale per problemi architetturali o finding dello Spring Advisor.


## Identità visiva

L'applicazione include ora un piccolo sistema di brand per Spring Guardian:

```text
marchio guardiana + scudo + codice legacy respinto
```

Gli asset ufficiali della UI sono disponibili in:

```text
guardian-ui/src/assets/spring-guardian-logo.svg
guardian-ui/src/assets/spring-guardian-wordmark.svg
guardian-ui/src/assets/favicon.svg
```

La direzione visiva è volutamente semplice e professionale: lo scudo comunica protezione, la marchio rende il prodotto riconoscibile e i frammenti di codice rappresentano legacy, anti-pattern e configurazioni rischiose che vengono intercettate. Il logo compatto viene usato come favicon e marchio nell'header; il wordmark viene usato come illustrazione principale.

## Cosa non è Spring Guardian

Spring Guardian ha un perimetro preciso:

```text
Non sostituisce SonarQube.
Non è un audit completo di sicurezza.
Non è un profiler runtime.
Non esegue l'applicazione analizzata.
Non garantisce che l'applicazione sia priva di bug.
Non chiama modelli AI.
Non si collega al database.
```

Il suo valore è nella review deterministica e Spring-specifica: architettura, layering, configurazione Spring Security, contratti Web/API, prontezza operativa Spring Batch, governo Maven, 12-factor/cloud readiness, osservabilità e alternative Spring idiomatiche.

## Report orientato alla decisione

Un catalogo ampio di regole può diventare rumoroso se il report non guida l'utente. Per questo Spring Guardian separa le segnalazioni per impatto decisionale:

```text
CRITICAL -> blocca potenzialmente la produzione
MAJOR    -> da correggere prima del rilascio
MINOR    -> debito tecnico rilevante
INFO     -> advisor, modernizzazione o best practice a basso impatto
```

La UI evidenzia cinque gruppi:

```text
1. Blocchi produzione
2. Da correggere prima del rilascio
3. Debito tecnico rilevante
4. Suggerimenti Spring
5. Note informative
```

In questo modo il report resta leggibile anche con molte regole: il reviewer capisce subito cosa correggere immediatamente e cosa pianificare in seguito.


## Schede problema orientate all'azione

Ogni scheda del report è stata resa più sintetica e operativa. Il report non si limita a dire che una regola è scattata: separa chiaramente le informazioni necessarie per intervenire:

```text
Cosa ho rilevato
Cosa può comportare
Soluzione consigliata
Classe/file/riga coinvolti
Evidenza del codice
```

Le schede dello Spring Alternative Advisor hanno una struttura ancora più esplicita:

```text
Implementazione manuale o API di basso livello rilevata
Alternativa Spring da usare
Link alla documentazione ufficiale
Codice sorgente coinvolto
```

Esempio:

```text
Ho rilevato: new ObjectMapper().
Usa: ObjectMapper gestito da Spring Boot.
Documentazione: https://docs.spring.io/spring-boot/reference/features/json.html
```

Prima:

```java
private final ObjectMapper mapper = new ObjectMapper();
```

Dopo:

```java
private final ObjectMapper mapper;

public MyProcessor(ObjectMapper mapper) {
    this.mapper = mapper;
}
```

In questo modo l'Advisor non resta generico: deve dire esattamente cosa ha rilevato, quale astrazione Spring usare e perché quella soluzione è più governabile per configurazione, lifecycle, testabilità e osservabilità.

## Moduli

```text
spring-guardian
├── guardian-core      # scanner deterministico e regole
├── guardian-cli       # esecuzione da riga di comando
├── guardian-server    # API REST Spring Boot e documentazione OpenAPI
└── guardian-ui        # dashboard Angular
```

## Principi fondamentali

```text
Prima le regole deterministiche
Nessuna chiamata AI
Nessuna connessione al database
Nessuno stato nascosto
Nessuna persistenza
Adatto a CI/CD
Segnalazioni specifiche per Spring
Decisione di rilascio leggibile
Evidenza del codice per ogni finding
```

Ogni scansione è stateless. Il profilo selezionato calibra solo la richiesta corrente e non nasconde mai i problemi. Le segnalazioni di sicurezza restano bloccanti anche quando è attiva la baseline legacy.

## Avvio backend

Windows / PowerShell:

```powershell
.\scripts\run-backend.ps1
```

CMD:

```cmd
scripts\run-backend.cmd
```

Comando manuale dalla root:

```bash
mvn -pl guardian-server -am spring-boot:run
```

Endpoint:

```text
Health       http://localhost:8080/api/v1/health
Swagger UI   http://localhost:8080/swagger-ui/index.html
OpenAPI JSON http://localhost:8080/v3/api-docs
```

## Avvio frontend Angular

```powershell
.\scripts\run-ui.ps1
```

Oppure:

```bash
cd guardian-ui
npm install
npm start
```

Apri:

```text
http://localhost:4200
```

## Politica del catalogo regole

La versione attuale mantiene identificativi regola stabili, ma abilita nel runtime solo controlli ad alta confidenza per Web/API e Spring Batch:

```text
WEB*   Contratti Web/API, validazione e confini controller
BAT*   Spring Batch, riavviabilità e prontezza operativa
CLD*   controlli selezionati di configurazione 12-factor/cloud readiness
OBS*   controlli selezionati di osservabilità e logging
POM*   controlli selezionati di governo Maven
ADV*   rilevazioni ad alta confidenza dello Spring Alternative Advisor
SPR*   regole Spring-specifiche originali con evidenza concreta nel codice
```

Le regole basate solo su import, commenti, keyword isolate o segnali testuali deboli non fanno parte del catalogo runtime attivo.

## Evidenza del codice rilevato

Ogni componente coinvolto può includere il frammento di codice che ha attivato la regola. La UI lo mostra come blocco codice sotto l'evidenza tecnica, così il reviewer può capire subito il motivo della segnalazione senza aprire manualmente il file.

Esempio:

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

## Esempi concreti

### 1. Self-invocation con `@Transactional`

Prima:

```java
@Service
class OrderService {
    public void createOrder() {
        saveOrder();
    }

    @Transactional
    void saveOrder() {
        // scrittura su database
    }
}
```

Risultato scansione:

```text
SPR007 - Rischio self-invocation del proxy Spring
Impatto: il metodo transazionale può essere chiamato senza attraversare il proxy Spring.
```

Dopo:

```java
@Service
class OrderApplicationService {
    private final OrderWriter orderWriter;

    public void createOrder() {
        orderWriter.saveOrder();
    }
}

@Service
class OrderWriter {
    @Transactional
    void saveOrder() {
        // scrittura su database
    }
}
```

### 2. Repository nel controller

Prima:

```java
@RestController
class CustomerController {
    private final CustomerRepository repository;

    @GetMapping("/customers/{id}")
    Customer find(@PathVariable Long id) {
        return repository.findById(id).orElseThrow();
    }
}
```

Risultato scansione:

```text
SPR003 / WEB017 - Controller dipende dal repository
Impatto: il controller salta il layer applicativo/service.
```

Dopo:

```java
@RestController
class CustomerController {
    private final CustomerService customerService;

    @GetMapping("/customers/{id}")
    CustomerResponse find(@PathVariable Long id) {
        return customerService.findCustomer(id);
    }
}
```

### 3. Entity esposta nell'API

Prima:

```java
@GetMapping("/customers/{id}")
CustomerEntity find(@PathVariable Long id) {
    return service.findEntity(id);
}
```

Risultato scansione:

```text
SPR006 / WEB005 - Entity esposta nella response REST
Impatto: il contratto API è accoppiato al modello di persistenza.
```

Dopo:

```java
@GetMapping("/customers/{id}")
CustomerResponse find(@PathVariable Long id) {
    return service.findCustomer(id);
}
```

### 4. `permitAll` troppo ampio

Prima:

```java
http.authorizeHttpRequests(auth -> auth
        .requestMatchers("/**").permitAll()
        .anyRequest().authenticated());
```

Risultato scansione:

```text
SEC003 - Matcher di sicurezza wildcard rilevato
Impatto: l'intera applicazione può diventare pubblicamente accessibile.
```

Dopo:

```java
http.authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/v1/public/**", "/actuator/health").permitAll()
        .anyRequest().authenticated());
```

### 5. Configurazione d'ambiente dentro l'artefatto

Prima:

```properties
spring.profiles.active=prod
external.payment.endpoint=https://payments.example.local
spring.datasource.password=secret
```

Risultato scansione:

```text
CLD001 / CLD002 / CLD003 - Configurazione runtime dentro application config
Impatto: l'artefatto non è più neutro rispetto all'ambiente.
```

Dopo:

```properties
external.payment.endpoint=${PAYMENT_ENDPOINT}
spring.datasource.password=${DB_PASSWORD}
```

## API principali

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

## Scan source transparency

Every report now exposes the root path actually scanned by the backend. When the backend-path mode is used, the UI clears the previous report before starting a new scan and shows both the requested source and the resolved scanned root. This prevents users from mistaking a previous report for the result of a new scan.

## Trasparenza sulla sorgente analizzata

Ogni report espone il percorso root realmente analizzato dal backend. Quando si usa la modalità percorso backend, l'interfaccia rimuove il report precedente prima di avviare una nuova scansione e mostra sia la sorgente richiesta sia il percorso risolto dal backend. In questo modo è chiaro quale progetto è stato analizzato.

package com.example.guardian.core;

import com.example.guardian.core.model.ReportLanguage;

/**
 * Provides localized texts for Spring Guardian rules.
 *
 * @author p15518 - Simone Meneghetti
 */
final class RuleTextCatalog {

    private RuleTextCatalog() {
    }

    static String title(String ruleId, String fallback, ReportLanguage language) {
        RuleText text = textFor(ruleId, language);
        return text == null ? fallback : text.title();
    }

    static String why(String ruleId, String fallback, ReportLanguage language) {
        RuleText text = textFor(ruleId, language);
        return text == null ? fallback : text.why();
    }

    static String fix(String ruleId, String fallback, ReportLanguage language) {
        RuleText text = textFor(ruleId, language);
        return text == null ? fallback : text.fix();
    }

    private static RuleText textFor(String ruleId, ReportLanguage language) {
        return language == ReportLanguage.ENGLISH ? englishTextFor(ruleId) : italianTextFor(ruleId);
    }

    private static RuleText italianTextFor(String ruleId) {
        return switch (ruleId) {
            case "SPR001_HARDCODED_CONFIG" -> new RuleText(
                    "Configurazione scritta direttamenete nel codice",
                    "Timeout, durate, URL, soglie e parametri tecnici scritti nel codice rendono difficile cambiare comportamento tra ambienti e aumentano il rischio di modifiche massive e non controllate",
                    "Sposta il valore in configurazione applicativa e leggilo tramite @ConfigurationProperties(se sono più proprietà di un gruppo altrimenti @Value con singola proprietà), usando tipi espliciti come Duration quando possibile."
            );
            case "SPR002_FIELD_INJECTION" -> new RuleText(
                    "Dipendenza iniettata su campo",
                    "L'iniezione su campo rende le dipendenze meno visibili, complica i test unitari e permette oggetti in stato parziale [Possibili NullPointerException].",
                    "Usa constructor injection con campi final, preferibilmente con un solo costruttore oppure usa Lombok con @RequiredArgsConstructor."
            );
            case "SPR003_CONTROLLER_INJECTS_REPOSITORY" -> new RuleText(
                    "Controller collegato direttamente al repository",
                    "Il controller dovrebbe orchestrare la richiesta HTTP, non contenere logica. Saltare il service layer aumenta accoppiamento e duplicazione.",
                    "Introduci un service applicativo e sposta le elaborazioni business, transazioni e accesso ai repository."
            );
            case "SPR004_FAT_CONTROLLER" -> new RuleText(
                    "Controller troppo grande",
                    "Controller grandi tendono a mescolare validazione, mapping, logica applicativa e gestione errori, rendendo fragile l'API.",
                    "Riduci il controller a endpoint sottili e sposta logica in service, mapper e validator dedicati. Di solito il controller prende la request e restituisce la response, massimo un riga di codice"
            );
            case "SPR005_MISSING_SERVICE_LAYER" -> new RuleText(
                    "Service layer mancante o poco evidente",
                    "Senza un livello service chiaro, la logica applicativa finisce spesso in controller, repository o utility non governate.",
                    "Crea service espliciti per i casi d'uso principali e lascia ai repository solo l'accesso ai dati, implementa il service anche se solo fa da passa carte"
            );
            case "SPR006_ENTITY_EXPOSED_IN_CONTROLLER" -> new RuleText(
                    "Entity JPA esposta dall'API",
                    "Esporre entity nei controller lega il contratto REST al modello database e può rivelare campi interni o relazioni indesiderate.",
                    "Usa DTO per request/response e mapper espliciti tra API e dominio/persistenza."
            );
            case "SPR007_SELF_INVOCATION_PROXY" -> new RuleText(
                    "Chiamata interna che può bypassare il proxy Spring",
                    "Quando un metodo della stessa classe chiama un metodo annotato con @Transactional, @Async o simili, il proxy Spring non interviene. Leggi questo articolo https://medium.com/me/stats/post/6aa8bfe43239",
                    "Sposta il metodo proxato in un altro bean oppure riorganizza il caso d'uso per attraversare il proxy Spring. "
            );
            case "SPR008_INVALID_TRANSACTIONAL_USAGE" -> new RuleText(
                    "Uso sospetto di @Transactional",
                    "Transazioni applicate nel punto sbagliato possono non proteggere davvero l'operazione o creare confini transazionali confusi. I repository jpa sono di natura transactional",
                    "Metti @Transactional sul service che rappresenta il caso d'uso e verifica propagazione, readOnly e gestione eccezioni. "
            );
            case "SPR009_MANUAL_CONNECTION_MANAGEMENT" -> new RuleText(
                    "Gestione manuale di connessioni JDBC",
                    "Aprire o chiudere connessioni manualmente aggira parte della gestione Spring e può causare leak o transazioni incoerenti.",
                    "Usa JdbcTemplate, repository Spring o transaction manager Spring, evitando DriverManager e close manuali non necessari."
            );
            case "SPR010_MISSING_REST_CONTROLLER_ADVICE" -> new RuleText(
                    "Gestione errori REST centralizzata mancante",
                    "Senza @RestControllerAdvice gli errori rischiano risposte incoerenti, stack trace o messaggi poco chiari per il client.",
                    "Aggiungi un handler globale con @RestControllerAdvice e response standard per validazione, business error e errori tecnici."
            );
            case "SPR011_GENERIC_TRY_CATCH" -> new RuleText(
                    "Catch troppo generico",
                    "Catturare Exception o Throwable nasconde errori diversi sotto lo stesso comportamento e può impedire rollback o gestione corretta. Spring usa quasi sempre unchecked, non controllare mai le eccezioni a meno di voler lanciare eccezioni business specifiche." +
                            "Le RunTimeException non devono essere controllate perchè sono inattese, l'applicativo deve rompersi",
                    "Cattura eccezioni specifiche, lascia propagare quelle non gestibili e centralizza la mappatura errori con @RestControllerAdvice."
            );
            case "SPR012_MISSING_TESTS" -> new RuleText(
                    "Copertura test non rilevata",
                    "L'assenza di test rende rischiosi refactor e modifiche su controller, service e regole di business.",
                    "Aggiungi unit test per la logica, slice test per controller/repository e integration test per flussi critici."
            );
            case "SPR013_API_VERSIONING_MISSING" -> new RuleText(
                    "Versionamento API mancante",
                    "API senza versione esplicita sono più difficili da evolvere senza rompere client esistenti.",
                    "Usa un prefisso stabile come /api/v1 o una strategia documentata di versionamento."
            );
            case "SPR014_PATH_VARIABLE_WITHOUT_NAME" -> new RuleText(
                    "PathVariable senza nome esplicito",
                    "Affidarsi al nome parametro compilato può essere fragile se la build non conserva i parameter names.",
                    "Specifica sempre il nome nella @PathVariable."
            );
            case "SPR015_POM_QUALITY" -> new RuleText(
                    "Qualità Maven migliorabile",
                    "Un POM poco governato rende più difficile standardizzare build, versioni e plugin nel tempo.",
                    "Centralizza versioni, plugin e convenzioni nel parent o nel dependencyManagement."
            );
            case "SPR016_NAMING_CONVENTION" -> new RuleText(
                    "Naming poco coerente",
                    "Nomi incoerenti rendono più difficile capire ruolo e responsabilità dei componenti.",
                    "Rinomina classi e bean seguendo convenzioni Spring chiare: Controller, Service, Repository, Configuration, Properties."
            );
            case "SPR017_READ_ONLY_TRANSACTION_MISSING" -> new RuleText(
                    "Transazione readOnly mancante",
                    "Query e casi d'uso di sola lettura senza readOnly perdono un'informazione utile per ottimizzazione e chiarezza.",
                    "Usa @Transactional(readOnly = true) sui service di sola lettura."
            );
            case "SPR018_SERVICE_RETURNS_ENTITY" -> new RuleText(
                    "Service restituisce entity direttamente",
                    "Far uscire entity dai service può propagare dettagli di persistenza fuori dal confine applicativo.",
                    "Restituisci DTO, projection o modelli di dominio separati quando il dato attraversa layer esterni."
            );
            case "SPR019_CONTROLLER_TRANSACTIONAL" -> new RuleText(
                    "Transazione aperta nel controller",
                    "Il controller non dovrebbe definire il confine transazionale del caso d'uso.",
                    "Sposta @Transactional nel service applicativo."
            );
            case "SPR020_OPTIONAL_GET_WITHOUT_GUARD" -> new RuleText(
                    "Optional.get senza controllo",
                    "Chiamare get senza verificare la presenza può generare NoSuchElementException in runtime.",
                    "Usa orElseThrow con eccezione significativa, map/orElse oppure controlla isPresent prima di accedere."
            );
            case "SPR021_CONSOLE_LOGGING" -> new RuleText(
                    "Log scritto con System.out o printStackTrace",
                    "La console diretta non rispetta livelli, formati, correlazione e raccolta centralizzata dei log.",
                    "Usa SLF4J Logger con livello adeguato e messaggi contestualizzati."
            );
            case "SPR022_LOGGER_SHOULD_BE_STATIC_FINAL" -> new RuleText(
                    "Logger non static final",
                    "Logger non standardizzati creano rumore e incoerenza nel codice.",
                    "Dichiara il logger come private static final Logger oppure usa @Slf4j se Lombok è accettato."
            );
            case "SPR023_REQUEST_BODY_WITHOUT_VALIDATION" -> new RuleText(
                    "Request body senza validazione",
                    "Dati in ingresso non validati possono arrivare nei service in stato non coerente.",
                    "Aggiungi @Valid sul parametro @RequestBody e vincoli Bean Validation sul DTO."
            );
            case "SPR024_REQUEST_DTO_WITHOUT_VALIDATION" -> new RuleText(
                    "DTO di input senza vincoli",
                    "Un DTO senza annotazioni di validazione non documenta né protegge le regole minime dell'input.",
                    "Aggiungi vincoli come @NotNull, @NotBlank, @Size, @Positive o validatori custom."
            );
            case "SPR025_NULL_RETURN_IN_REPOSITORY_OR_SERVICE" -> new RuleText(
                    "Service o repository ritorna null",
                    "Ritornare null obbliga i chiamanti a controlli difensivi e aumenta il rischio di NullPointerException.",
                    "Usa Optional per assenza singola, liste vuote per collezioni e oggetti risultato espliciti."
            );
            case "SPR026_HTTP_CLIENT_CREATED_MANUALLY" -> new RuleText(
                    "Client HTTP creato manualmente",
                    "Creare client HTTP direttamente rende più difficile configurare timeout, retry, TLS, proxy e osservabilità.",
                    "Centralizza RestClient/WebClient/RestTemplate come bean configurato e riusabile."
            );
            case "SPR027_LAYER_DEPENDENCY_VIOLATION" -> new RuleText(
                    "Dipendenza tra layer non corretta",
                    "Quando i layer dipendono nella direzione sbagliata, il progetto diventa più accoppiato e meno modificabile.",
                    "Mantieni dipendenze coerenti: controller -> service -> repository, evitando ritorni o scorciatoie tra layer."
            );
            case "SPR028_PACKAGE_STRUCTURE_INCONSISTENT" -> new RuleText(
                    "Struttura package poco coerente",
                    "Package disordinati rendono difficile navigare il progetto e capire i confini funzionali o tecnici.",
                    "Riorganizza per feature o layer in modo coerente e documenta la convenzione scelta."
            );
            case "SPR029_TOO_MANY_DEPENDENCIES" -> new RuleText(
                    "Classe con troppe dipendenze",
                    "Molte dipendenze nel costruttore sono spesso sintomo di responsabilità eccessive.",
                    "Dividi il componente per casi d'uso o estrai collaboratori più piccoli e coesi."
            );
            case "SPR030_GOD_CLASS" -> new RuleText(
                    "Classe troppo grande",
                    "Classi molto grandi raccolgono responsabilità diverse e diventano difficili da leggere, testare e modificare in sicurezza.",
                    "Dividi la classe per responsabilità e sposta operazioni coese in service, componenti o mapper dedicati."
            );
            case "SPR031_COMPLEX_SERVICE_METHOD" -> new RuleText(
                    "Metodo service troppo complesso",
                    "Metodi con molte condizioni o rami sono più difficili da testare e da modificare senza regressioni.",
                    "Estrai regole in metodi privati chiari, policy object, validator o piccoli service di dominio."
            );
            case "SPR032_DUPLICATED_MAVEN_DEPENDENCY" -> new RuleText(
                    "Dipendenza Maven duplicata",
                    "Dipendenze duplicate o ripetute rendono la build più rumorosa e possono causare conflitti di versione.",
                    "Rimuovi duplicati e centralizza versioni nel dependencyManagement."
            );
            case "SPR033_SPRING_DEPENDENCY_VERSION_OVERRIDDEN" -> new RuleText(
                    "Versione Spring sovrascritta manualmente",
                    "Forzare versioni Spring fuori dal BOM può creare incompatibilità difficili da diagnosticare.",
                    "Lascia gestire le versioni Spring Boot al BOM, salvo eccezioni motivate e documentate."
            );
            case "SPR036_MISSING_ENVIRONMENT_PROFILES" -> new RuleText(
                    "Profili ambiente mancanti",
                    "Senza profili o configurazioni per ambiente, aumenta il rischio di usare valori sbagliati in test, collaudo o produzione.",
                    "Definisci profili/configurazioni separate per local, test, staging e produzione."
            );
            case "SPR037_POSSIBLE_SECRET_IN_CONFIG" -> new RuleText(
                    "Possibile segreto in configurazione",
                    "Password, token o chiavi nel repository sono un rischio di sicurezza e rotazione credenziali.",
                    "Sposta i segreti in vault, variabili ambiente o secret manager e lascia nel repo solo placeholder."
            );
            case "SPR038_DDL_AUTO_UNSAFE" -> new RuleText(
                    "ddl-auto potenzialmente pericoloso",
                    "Impostazioni come create, create-drop o update possono modificare lo schema database in modo non controllato.",
                    "In produzione usa validate/none e gestisci lo schema con migration tool come Flyway o Liquibase."
            );
            case "SPR039_ACTUATOR_EXPOSE_ALL" -> new RuleText(
                    "Actuator esposto troppo ampiamente",
                    "Esporre tutti gli endpoint actuator può rivelare informazioni sensibili o abilitare operazioni non desiderate.",
                    "Esponi solo endpoint necessari e proteggili con Spring Security, rete o ruoli dedicati."
            );
            case "SPR040_CSRF_DISABLED" -> new RuleText(
                    "CSRF disabilitato",
                    "Disabilitare CSRF senza contesto può esporre applicazioni browser-based a richieste indesiderate.",
                    "Disabilita CSRF solo per API stateless reali o endpoint specifici, documentando la motivazione."
            );
            case "SPR041_PERMIT_ALL_TOO_BROAD" -> new RuleText(
                    "Regola permitAll troppo ampia",
                    "Permettere accesso pubblico a pattern larghi può esporre endpoint non previsti.",
                    "Limita permitAll a endpoint specifici e verifica l'ordine delle regole di sicurezza."
            );
            case "SPR042_PASSWORD_ENCODER_MISSING" -> new RuleText(
                    "PasswordEncoder mancante",
                    "Gestire password senza encoder esplicito è rischioso e può portare a storage non sicuro.",
                    "Definisci un PasswordEncoder robusto, ad esempio BCryptPasswordEncoder o delegating encoder."
            );
            case "SPR043_TEST_WITHOUT_ASSERTIONS" -> new RuleText(
                    "Test senza assert",
                    "Un test senza assert o verify può passare anche se il comportamento reale è sbagliato.",
                    "Aggiungi assert sul risultato o verify sulle interazioni rilevanti."
            );
            case "SPR044_SPRING_BOOT_TEST_OVERUSED" -> new RuleText(
                    "Uso eccessivo di @SpringBootTest",
                    "Caricare tutto il contesto Spring per test semplici rallenta la suite e rende i test più fragili.",
                    "Preferisci unit test, slice test (@WebMvcTest, @DataJpaTest) o test di integrazione mirati."
            );
            case "SPR045_MOCKITO_TEST_WITHOUT_VERIFY_OR_ASSERT" -> new RuleText(
                    "Test Mockito senza verifica utile",
                    "Mock configurati senza assert o verify non dimostrano il comportamento atteso.",
                    "Aggiungi assert sul risultato o verify sulle chiamate che rappresentano l'effetto del caso d'uso."
            );
            case "SPR046_CORS_ALLOW_ALL_WITH_CREDENTIALS" -> new RuleText(
                    "CORS aperto con credenziali abilitate",
                    "Consentire tutte le origini insieme alle credenziali è una configurazione rischiosa per cookie e sessioni.",
                    "Specifica origini consentite esplicite e usa credentials solo quando necessario."
            );
            case "SPR047_EMPTY_CATCH_BLOCK" -> new RuleText(
                    "Catch vuoto",
                    "Un catch vuoto nasconde errori reali e rende difficile capire perché un'operazione fallisce.",
                    "Gestisci l'errore, loggalo con contesto oppure rilancialo con un'eccezione significativa."
            );
            case "SPR048_JPA_EAGER_FETCHING" -> new RuleText(
                    "Fetch JPA EAGER",
                    "EAGER può caricare relazioni non necessarie e creare query pesanti o effetti N+1.",
                    "Preferisci LAZY e usa fetch join, entity graph o query dedicate quando serve caricare relazioni."
            );
            case "SPR049_REPOSITORY_CALL_INSIDE_LOOP" -> new RuleText(
                    "Repository chiamato dentro un ciclo",
                    "Chiamate database ripetute dentro loop possono causare molti round-trip e degradare le performance.",
                    "Sostituisci con query bulk, findAllById, join o caricamento anticipato dei dati necessari."
            );
            case "SPR050_GET_ENDPOINT_MUTATES_STATE" -> new RuleText(
                    "Endpoint GET che modifica dati",
                    "GET dovrebbe essere sicuro e idempotente dal punto di vista HTTP. Usarlo per modifiche crea problemi con cache, crawler e client.",
                    "Usa POST, PUT, PATCH o DELETE per operazioni che cambiano stato."
            );
            case "SPR051_CONTROLLER_RAW_RESPONSE" -> new RuleText(
                    "Risposta controller non tipizzata",
                    "Risposte raw o Object rendono meno chiaro il contratto API e peggiorano documentazione e test.",
                    "Usa DTO di risposta o ResponseEntity<T> con tipo concreto."
            );
            case "SPR052_THREAD_SLEEP_IN_TEST" -> new RuleText(
                    "Thread.sleep nei test",
                    "Sleep temporali rendono i test lenti e instabili, soprattutto in CI.",
                    "Usa Awaitility, clock controllabile, latch o sincronizzazione esplicita."
            );
            default -> null;
        };
    }

    private static RuleText englishTextFor(String ruleId) {
        return switch (ruleId) {
            case "SPR001_HARDCODED_CONFIG" -> new RuleText("Hardcoded technical value", "Timeouts, durations, URLs, thresholds and technical parameters hardcoded in source code are harder to change across environments and increase invasive change risk.", "Move the value to application configuration and bind it with @ConfigurationProperties, using explicit types such as Duration when possible.");
            case "SPR002_FIELD_INJECTION" -> new RuleText("Field injection", "Field injection hides dependencies, complicates unit testing and allows partially initialized objects.", "Use constructor injection with final fields, preferably with a single constructor.");
            case "SPR003_CONTROLLER_INJECTS_REPOSITORY" -> new RuleText("Controller directly depends on a repository", "A controller should orchestrate HTTP requests, not data access. Skipping the service layer increases coupling and duplication.", "Introduce an application service and move rules, transactions and repository access there.");
            case "SPR004_FAT_CONTROLLER" -> new RuleText("Controller is too large", "Large controllers tend to mix validation, mapping, application logic and error handling, making the API fragile.", "Keep controllers thin and move logic to services, mappers and dedicated validators.");
            case "SPR005_MISSING_SERVICE_LAYER" -> new RuleText("Service layer is missing or unclear", "Without a clear service layer, application logic often ends up in controllers, repositories or unmanaged utilities.", "Create explicit services for main use cases and keep repositories focused on data access.");
            case "SPR006_ENTITY_EXPOSED_IN_CONTROLLER" -> new RuleText("JPA entity exposed by the API", "Exposing entities from controllers couples the REST contract to the database model and may leak internal fields or relationships.", "Use request/response DTOs and explicit mappers between API and domain or persistence models.");
            case "SPR007_SELF_INVOCATION_PROXY" -> new RuleText("Internal call may bypass the Spring proxy", "When a method calls another method of the same class annotated with @Transactional, @Async or similar, the Spring proxy may not be applied.", "Move the proxied method to another bean or reorganize the use case so the call crosses the Spring proxy.");
            case "SPR008_INVALID_TRANSACTIONAL_USAGE" -> new RuleText("Suspicious @Transactional usage", "Transactions placed at the wrong level may not protect the operation or may create unclear transactional boundaries.", "Place @Transactional on the service representing the use case and verify propagation, readOnly and exception handling.");
            case "SPR009_MANUAL_CONNECTION_MANAGEMENT" -> new RuleText("Manual JDBC connection management", "Opening or closing connections manually bypasses part of Spring management and may cause leaks or inconsistent transactions.", "Use JdbcTemplate, Spring repositories or the Spring transaction manager, avoiding DriverManager and unnecessary manual closes.");
            case "SPR010_MISSING_REST_CONTROLLER_ADVICE" -> new RuleText("Central REST error handling is missing", "Without @RestControllerAdvice, errors may produce inconsistent responses, stack traces or unclear client messages.", "Add a global @RestControllerAdvice with standard responses for validation, business errors and technical failures.");
            case "SPR011_GENERIC_TRY_CATCH" -> new RuleText("Overly generic catch block", "Catching Exception or Throwable hides different failure modes behind the same behavior and may prevent rollback or correct handling.", "Catch specific exceptions, let unmanaged exceptions propagate, and centralize error mapping with @RestControllerAdvice.");
            case "SPR012_MISSING_TESTS" -> new RuleText("No test coverage detected", "Missing tests make refactoring and changes on controllers, services and business rules risky.", "Add unit tests for logic, slice tests for controllers/repositories and integration tests for critical flows.");
            case "SPR013_API_VERSIONING_MISSING" -> new RuleText("API versioning is missing", "APIs without explicit versioning are harder to evolve without breaking existing clients.", "Use a stable prefix such as /api/v1 or a documented versioning strategy.");
            case "SPR014_PATH_VARIABLE_WITHOUT_NAME" -> new RuleText("PathVariable has no explicit name", "Relying on compiled parameter names can be fragile when the build does not retain parameter metadata.", "Always specify the name in @PathVariable.");
            case "SPR015_POM_QUALITY" -> new RuleText("Maven quality can be improved", "An unmanaged POM makes it harder to standardize builds, versions and plugins over time.", "Centralize versions, plugins and conventions in the parent or dependencyManagement.");
            case "SPR016_NAMING_CONVENTION" -> new RuleText("Naming is not consistent", "Inconsistent names make component roles and responsibilities harder to understand.", "Rename classes and beans using clear Spring conventions: Controller, Service, Repository, Configuration, Properties.");
            case "SPR017_READ_ONLY_TRANSACTION_MISSING" -> new RuleText("Read-only transaction is missing", "Read-only use cases without readOnly lose useful information for optimization and clarity.", "Use @Transactional(readOnly = true) on read-only service methods.");
            case "SPR018_SERVICE_RETURNS_ENTITY" -> new RuleText("Service returns an entity directly", "Letting entities escape services may propagate persistence details outside the application boundary.", "Return DTOs, projections or dedicated domain models when data crosses external layers.");
            case "SPR019_CONTROLLER_TRANSACTIONAL" -> new RuleText("Transaction opened in controller", "The controller should not define the transactional boundary of the use case.", "Move @Transactional to the application service.");
            case "SPR020_OPTIONAL_GET_WITHOUT_GUARD" -> new RuleText("Optional.get without guard", "Calling get without checking presence may produce NoSuchElementException at runtime.", "Use orElseThrow with a meaningful exception, map/orElse, or check isPresent before access.");
            case "SPR021_CONSOLE_LOGGING" -> new RuleText("Logging with System.out or printStackTrace", "Direct console logging does not respect log levels, formats, correlation or centralized log collection.", "Use an SLF4J Logger with the proper level and contextual messages.");
            case "SPR022_LOGGER_SHOULD_BE_STATIC_FINAL" -> new RuleText("Logger is not static final", "Non-standard loggers create noise and inconsistency in the codebase.", "Declare the logger as private static final Logger or use @Slf4j if Lombok is accepted.");
            case "SPR023_REQUEST_BODY_WITHOUT_VALIDATION" -> new RuleText("Request body without validation", "Unvalidated input may reach services in an inconsistent state.", "Add @Valid on @RequestBody parameters and Bean Validation constraints on DTOs.");
            case "SPR024_REQUEST_DTO_WITHOUT_VALIDATION" -> new RuleText("Input DTO has no constraints", "A DTO without validation annotations does not document or enforce minimum input rules.", "Add constraints such as @NotNull, @NotBlank, @Size, @Positive or custom validators.");
            case "SPR025_NULL_RETURN_IN_REPOSITORY_OR_SERVICE" -> new RuleText("Service or repository returns null", "Returning null forces defensive checks on callers and increases NullPointerException risk.", "Use Optional for single absence, empty lists for collections and explicit result objects.");
            case "SPR026_HTTP_CLIENT_CREATED_MANUALLY" -> new RuleText("HTTP client created manually", "Creating HTTP clients directly makes timeout, retry, TLS, proxy and observability configuration harder.", "Centralize RestClient/WebClient/RestTemplate as a configured reusable bean.");
            case "SPR027_LAYER_DEPENDENCY_VIOLATION" -> new RuleText("Invalid dependency between layers", "When layers depend in the wrong direction, the project becomes more coupled and less changeable.", "Keep dependencies consistent: controller -> service -> repository, avoiding shortcuts between layers.");
            case "SPR028_PACKAGE_STRUCTURE_INCONSISTENT" -> new RuleText("Package structure is not consistent", "Disorganized packages make project navigation and functional or technical boundaries harder to understand.", "Reorganize by feature or layer consistently and document the chosen convention.");
            case "SPR029_TOO_MANY_DEPENDENCIES" -> new RuleText("Class has too many dependencies", "Many constructor dependencies often indicate excessive responsibilities.", "Split the component by use case or extract smaller cohesive collaborators.");
            case "SPR030_GOD_CLASS" -> new RuleText("Class is too large", "Very large classes collect different responsibilities and become hard to read, test and change safely.", "Split the class by responsibility and move cohesive operations into dedicated services, components or mappers.");
            case "SPR031_COMPLEX_SERVICE_METHOD" -> new RuleText("Service method is too complex", "Methods with many branches are harder to test and modify without regressions.", "Extract rules into clear private methods, policy objects, validators or small domain services.");
            case "SPR032_DUPLICATED_MAVEN_DEPENDENCY" -> new RuleText("Duplicated Maven dependency", "Duplicated dependencies make the build noisier and may cause version conflicts.", "Remove duplicates and centralize versions in dependencyManagement.");
            case "SPR033_SPRING_DEPENDENCY_VERSION_OVERRIDDEN" -> new RuleText("Spring dependency version overridden manually", "Forcing Spring versions outside the BOM may create compatibility issues that are difficult to diagnose.", "Let the Spring Boot BOM manage Spring versions unless an exception is justified and documented.");
            case "SPR036_MISSING_ENVIRONMENT_PROFILES" -> new RuleText("Environment profiles are missing", "Without per-environment configuration, the risk of using wrong values in test, staging or production increases.", "Define separate profiles or configuration files for local, test, staging and production.");
            case "SPR037_POSSIBLE_SECRET_IN_CONFIG" -> new RuleText("Possible secret in configuration", "Passwords, tokens or keys in the repository are a security and credential rotation risk.", "Move secrets to a vault, environment variables or a secret manager, leaving only placeholders in the repository.");
            case "SPR038_DDL_AUTO_UNSAFE" -> new RuleText("Potentially dangerous ddl-auto", "Settings such as create, create-drop or update can modify database schemas in an uncontrolled way.", "Use validate/none in production and manage schema changes with tools such as Flyway or Liquibase.");
            case "SPR039_ACTUATOR_EXPOSE_ALL" -> new RuleText("Actuator is exposed too broadly", "Exposing all actuator endpoints may reveal sensitive information or enable undesired operations.", "Expose only required endpoints and protect them with Spring Security, network controls or dedicated roles.");
            case "SPR040_CSRF_DISABLED" -> new RuleText("CSRF is disabled", "Disabling CSRF without context may expose browser-based applications to unwanted requests.", "Disable CSRF only for truly stateless APIs or specific endpoints, documenting the reason.");
            case "SPR041_PERMIT_ALL_TOO_BROAD" -> new RuleText("permitAll rule is too broad", "Allowing public access to broad URL patterns may expose endpoints unintentionally.", "Restrict permitAll to specific endpoints and verify the order of security rules.");
            case "SPR042_PASSWORD_ENCODER_MISSING" -> new RuleText("PasswordEncoder is missing", "Handling passwords without an explicit encoder is risky and may lead to insecure storage.", "Define a strong PasswordEncoder such as BCryptPasswordEncoder or a delegating encoder.");
            case "SPR043_TEST_WITHOUT_ASSERTIONS" -> new RuleText("Test has no assertions", "A test without assertions or verify calls may pass even when behavior is wrong.", "Add assertions on results or verify relevant interactions.");
            case "SPR044_SPRING_BOOT_TEST_OVERUSED" -> new RuleText("@SpringBootTest is overused", "Loading the full Spring context for simple tests slows the suite and makes tests more fragile.", "Prefer unit tests, slice tests such as @WebMvcTest or @DataJpaTest, or focused integration tests.");
            case "SPR045_MOCKITO_TEST_WITHOUT_VERIFY_OR_ASSERT" -> new RuleText("Mockito test has no useful verification", "Mocks configured without assertions or verify calls do not prove the expected behavior.", "Add assertions on results or verify calls that represent the use case effect.");
            case "SPR046_CORS_ALLOW_ALL_WITH_CREDENTIALS" -> new RuleText("CORS is open with credentials enabled", "Allowing every origin together with credentials is risky for cookies and sessions.", "Specify explicit allowed origins and enable credentials only when required.");
            case "SPR047_EMPTY_CATCH_BLOCK" -> new RuleText("Empty catch block", "An empty catch block hides real failures and makes operations hard to diagnose.", "Handle the error, log it with context or rethrow it with a meaningful exception.");
            case "SPR048_JPA_EAGER_FETCHING" -> new RuleText("JPA EAGER fetching", "EAGER may load unnecessary relationships and create heavy queries or N+1 effects.", "Prefer LAZY and use fetch joins, entity graphs or dedicated queries when relationships must be loaded.");
            case "SPR049_REPOSITORY_CALL_INSIDE_LOOP" -> new RuleText("Repository called inside a loop", "Repeated database calls inside loops may create many round trips and degrade performance.", "Replace with bulk queries, findAllById, joins or preloading of the required data.");
            case "SPR050_GET_ENDPOINT_MUTATES_STATE" -> new RuleText("GET endpoint changes state", "GET should be safe and idempotent at HTTP level. Using it for changes creates cache, crawler and client issues.", "Use POST, PUT, PATCH or DELETE for operations that change state.");
            case "SPR051_CONTROLLER_RAW_RESPONSE" -> new RuleText("Controller response is not typed", "Raw or Object responses make API contracts less clear and reduce documentation and test quality.", "Use response DTOs or ResponseEntity<T> with a concrete type.");
            case "SPR052_THREAD_SLEEP_IN_TEST" -> new RuleText("Thread.sleep in tests", "Time-based sleeps make tests slow and unstable, especially in CI.", "Use Awaitility, a controllable clock, latches or explicit synchronization.");
            default -> null;
        };
    }

    private record RuleText(String title, String why, String fix) {
    }
}

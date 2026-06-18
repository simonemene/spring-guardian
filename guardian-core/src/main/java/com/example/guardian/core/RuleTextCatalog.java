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
        String value = text == null ? fallbackText(ruleId, fallback, language, TextPart.TITLE) : text.title();
        return normalizeItalian(value, language);
    }

    static String why(String ruleId, String fallback, ReportLanguage language) {
        RuleText text = textFor(ruleId, language);
        String value = text == null ? fallbackText(ruleId, fallback, language, TextPart.WHY) : text.why();
        return normalizeItalian(value, language);
    }

    static String fix(String ruleId, String fallback, ReportLanguage language) {
        RuleText text = textFor(ruleId, language);
        String value = text == null ? fallbackText(ruleId, fallback, language, TextPart.FIX) : text.fix();
        return normalizeItalian(value, language);
    }

    private static RuleText textFor(String ruleId, ReportLanguage language) {
        return language == ReportLanguage.ENGLISH ? englishTextFor(ruleId) : italianTextFor(ruleId);
    }

    private static RuleText italianTextFor(String ruleId) {
        return switch (ruleId) {
            case "SPR001_HARDCODED_CONFIG" -> new RuleText(
                    "Configurazione scritta direttamente nel codice",
                    "Valori tecnici come timeout, URL, soglie e parametri applicativi scritti nel codice rendono difficile adattare il comportamento tra ambienti e aumentano il rischio di modifiche estese e non controllate.",
                    "Sposta il valore nella configurazione applicativa e leggilo tramite @ConfigurationProperties quando appartiene a un gruppo di proprietà; usa @Value solo per valori isolati. Quando possibile, preferisci tipi espliciti come Duration."
            );
            case "SPR002_FIELD_INJECTION" -> new RuleText(
                    "Dipendenza iniettata su campo",
                    "L'iniezione su campo rende le dipendenze meno visibili, complica i test unitari e può lasciare oggetti in stato parziale.",
                    "Usa constructor injection con campi final, preferibilmente con un solo costruttore oppure usa Lombok con @RequiredArgsConstructor."
            );
            case "SPR003_CONTROLLER_INJECTS_REPOSITORY" -> new RuleText(
                    "Controller collegato direttamente al repository",
                    "Il controller dovrebbe gestire il contratto HTTP e delegare i casi d'uso. Saltare il service layer aumenta accoppiamento, duplicazione e difficoltà di test.",
                    "Introduci un service applicativo e sposta le elaborazioni business, transazioni e accesso ai repository."
            );
            case "SPR004_FAT_CONTROLLER" -> new RuleText(
                    "Controller troppo grande",
                    "Controller troppo grandi tendono a mescolare validazione, mapping, logica applicativa e gestione degli errori, rendendo l'API fragile e difficile da mantenere.",
                    "Riduci il controller a endpoint sottili e sposta la logica in service, mapper e validator dedicati. Il controller dovrebbe ricevere la richiesta, delegare il caso d'uso e costruire la risposta."
            );
            case "SPR005_MISSING_SERVICE_LAYER" -> new RuleText(
                    "Service layer mancante o poco evidente",
                    "Senza un livello service chiaro, la logica applicativa finisce spesso in controller, repository o utility non governate.",
                    "Crea service espliciti per i casi d'uso principali e lascia ai repository solo l'accesso ai dati. Anche un service inizialmente semplice rende il confine applicativo più chiaro e testabile."
            );
            case "SPR006_ENTITY_EXPOSED_IN_CONTROLLER" -> new RuleText(
                    "Entity JPA esposta dall'API",
                    "Esporre entity nei controller lega il contratto REST al modello database e può rivelare campi interni o relazioni indesiderate.",
                    "Usa DTO per request/response e mapper espliciti tra API e dominio/persistenza."
            );
            case "SPR007_SELF_INVOCATION_PROXY" -> new RuleText(
                    "Chiamata interna che può bypassare il proxy Spring",
                    "Quando un metodo della stessa classe chiama un altro metodo annotato con @Transactional, @Async o annotazioni simili, la chiamata non attraversa il proxy Spring e il comportamento atteso può non essere applicato.",
                    "Sposta il metodo che richiede proxy in un altro bean oppure riorganizza il caso d'uso in modo che la chiamata attraversi il proxy Spring."
            );
            case "SPR008_INVALID_TRANSACTIONAL_USAGE" -> new RuleText(
                    "Uso sospetto di @Transactional",
                    "Transazioni applicate nel punto sbagliato possono non proteggere davvero l'operazione o creare confini transazionali confusi. I repository JPA sono già transazionali per le operazioni principali.",
                    "Applica @Transactional sul service che rappresenta il caso d'uso e verifica propagazione, readOnly e gestione delle eccezioni."
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
                    "Cattura eccezioni specifiche, lascia propagare quelle non gestibili e centralizza la mappatura degli errori con @RestControllerAdvice."
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
            case "SPR053_JPA_ENTITY_ACCESSIBLE_NO_ARGS_CONSTRUCTOR" -> new RuleText(
                    "Costruttore JPA senza argomenti mancante",
                    "Le entity JPA devono avere un costruttore senza argomenti pubblico o protected per permettere al provider di persistenza di istanziarle correttamente.",
                    "Aggiungi un costruttore protected senza argomenti e usa costruttori pubblici o factory method per creare entity valide."
            );
            case "SPR054_JPA_TO_ONE_RELATIONSHIP_SHOULD_BE_LAZY" -> new RuleText(
                    "Relazione JPA to-one non dichiarata LAZY",
                    "ManyToOne e OneToOne sono eager di default e possono caricare grafi non richiesti, peggiorando performance e serializzazione.",
                    "Imposta fetch = FetchType.LAZY e carica i dati necessari con query esplicite, projection o entity graph."
            );
            case "SPR055_DOMAIN_LAYER_DEPENDS_ON_SPRING" -> new RuleText(
                    "Dominio dipendente da Spring",
                    "In DDD o architettura esagonale il dominio dovrebbe restare indipendente dal framework per non legare regole di business a dettagli infrastrutturali.",
                    "Sposta annotazioni Spring, adapter e configurazioni nei layer application/infrastructure e mantieni il dominio pulito."
            );
            case "SPR056_SERVICE_DEPENDS_ON_WEB_LAYER" -> new RuleText(
                    "Service dipendente dal layer web",
                    "Un service che conosce HTTP, servlet o ResponseEntity mescola caso d'uso applicativo e trasporto web.",
                    "Lascia HTTP nel controller e passa al service DTO applicativi o comandi di dominio."
            );
            case "SPR057_REPOSITORY_DEPENDS_ON_UPPER_LAYER" -> new RuleText(
                    "Repository dipendente da layer superiori",
                    "Il repository dovrebbe conoscere solo persistenza e query, non controller, service o concetti HTTP.",
                    "Sposta orchestrazione e logica applicativa nei service e lascia al repository solo operazioni di accesso dati."
            );
            case "SPR058_SECURITY_FILTER_CHAIN_MISSING" -> new RuleText(
                    "Spring Security senza SecurityFilterChain esplicita",
                    "Se Spring Security è presente ma manca una SecurityFilterChain, la policy di sicurezza non è leggibile e può dipendere da default non desiderati.",
                    "Definisci SecurityFilterChain con autorizzazioni endpoint, CSRF, CORS, session policy e meccanismo di autenticazione."
            );
            case "SPR059_CSRF_DISABLED_WITHOUT_STATELESS" -> new RuleText(
                    "CSRF disabilitato senza sessione stateless",
                    "Disabilitare CSRF è sensato per API realmente stateless, ma rischioso per applicazioni browser-based con cookie o sessione.",
                    "Dichiara SessionCreationPolicy.STATELESS oppure limita la disabilitazione CSRF agli endpoint dove è motivata."
            );
            case "SPR060_ENDPOINT_WITHOUT_OPENAPI_OPERATION" -> new RuleText(
                    "Endpoint REST senza descrizione OpenAPI",
                    "Endpoint senza @Operation sono meno leggibili in Swagger e rendono più debole la documentazione del contratto API.",
                    "Aggiungi @Operation con summary e description e documenta le risposte principali con @ApiResponse."
            );
            case "SPR061_ALL_ARGS_CONSTRUCTOR_ON_SPRING_COMPONENT" -> new RuleText(
                    "@AllArgsConstructor su componente Spring",
                    "@AllArgsConstructor può iniettare tutti i campi della classe, anche quelli che non rappresentano dipendenze obbligatorie.",
                    "Preferisci costruttore esplicito oppure @RequiredArgsConstructor con dipendenze final."
            );
            case "SPR062_CONSTRUCTOR_DEPENDENCY_FIELD_NOT_FINAL" -> new RuleText(
                    "Dipendenza da costruttore non final",
                    "Le dipendenze obbligatorie iniettate da costruttore sono più sicure quando i campi sono immutabili.",
                    "Rendi final i campi assegnati nel costruttore oppure usa @RequiredArgsConstructor."
            );
            case "SPR063_REST_CONTROLLER_WITHOUT_BASE_MAPPING" -> new RuleText(
                    "Controller REST senza mapping base",
                    "Senza @RequestMapping a livello classe, struttura endpoint e versionamento API sono meno evidenti.",
                    "Aggiungi un mapping base come /api/v1/nome-risorsa e lascia ai metodi le singole operazioni."
            );
            case "SPR064_MANUAL_OBJECT_MAPPER" -> new RuleText(
                    "ObjectMapper creato manualmente",
                    "Creare ObjectMapper a mano può saltare configurazioni globali Spring Boot, moduli JavaTime, serializer, naming strategy e impostazioni comuni.",
                    "Inietta l'ObjectMapper gestito da Spring Boot oppure centralizza la customizzazione in un bean dedicato."
            );
            case "SPR065_MANUAL_THREAD_CREATION" -> new RuleText(
                    "Thread creato manualmente",
                    "I thread creati a mano non seguono lifecycle Spring, configurazione centralizzata, osservabilità e shutdown ordinato.",
                    "Usa TaskExecutor, ThreadPoolTaskExecutor, @Async o TaskScheduler in base al caso d'uso."
            );
            case "SPR066_MANUAL_EXECUTOR_CREATION" -> new RuleText(
                    "Executor creato manualmente",
                    "ExecutorService creati direttamente possono uscire dal lifecycle Spring e rendere difficile tuning, shutdown e metriche.",
                    "Esponi un TaskExecutor o ThreadPoolTaskExecutor come bean e iniettalo dove serve lavoro asincrono."
            );
            case "SPR067_TIMER_SCHEDULING" -> new RuleText(
                    "Scheduling con Timer",
                    "Timer e TimerTask sono API di basso livello e si integrano male con profili, lifecycle e osservabilità Spring.",
                    "Usa @Scheduled o TaskScheduler con configurazione esplicita e test dedicati."
            );
            case "SPR068_MANUAL_JDBC_TEMPLATE" -> new RuleText(
                    "JdbcTemplate creato manualmente",
                    "Creare JdbcTemplate direttamente può duplicare configurazione di datasource, transazioni ed exception translation.",
                    "Inietta il template auto-configurato o definisci un bean centralizzato."
            );
            case "SPR069_MANUAL_PASSWORD_ENCODER" -> new RuleText(
                    "PasswordEncoder creato manualmente",
                    "L'encoder password deve essere governato centralmente per evitare strategie diverse tra login, test e migrazioni.",
                    "Definisci un bean PasswordEncoder e iniettalo nei componenti che codificano o verificano password."
            );
            case "SPR070_DIRECT_ENVIRONMENT_ACCESS" -> new RuleText(
                    "Configurazione letta direttamente dall'ambiente",
                    "System.getenv o System.getProperty nel codice nascondono configurazione, validazione e gestione profili a Spring.",
                    "Usa @ConfigurationProperties, Environment o classi properties validate."
            );
            case "SPR071_MANUAL_FILE_RESOURCE_ACCESS" -> new RuleText(
                    "Risorsa file aperta manualmente",
                    "Accesso diretto a file rende più rigido il passaggio tra classpath, filesystem e risorse esterne.",
                    "Usa Resource, ResourceLoader o proprietà configurabili per rendere l'accesso esplicito e testabile."
            );
            case "SPR072_SPRING_BEAN_CREATED_WITH_NEW" -> new RuleText(
                    "Collaboratore Spring creato con new",
                    "Creare service, repository, client o adapter con new bypassa dependency injection, proxy, transazioni, validazione e test replacement.",
                    "Registra il collaboratore come bean e iniettalo via costruttore."
            );
            case "SPR073_MOCKBEAN_MODERNIZATION_ADVISOR" -> new RuleText(
                    "Annotazione Mockito Spring Boot da modernizzare",
                    "@MockBean e annotazioni correlate sono deprecate nelle versioni Spring Boot recenti in favore del supporto Spring Framework.",
                    "Valuta @MockitoBean e @MockitoSpyBean per nuovi test o modernizzazioni progressive."
            );
            case "SPR074_STRUCTURED_LOGGING_ADVISOR" -> new RuleText(
                    "Structured logging non configurato",
                    "I log strutturati aiutano diagnostica, correlazione e aggregazione in ambienti moderni senza codice custom.",
                    "Valuta logging strutturato Spring Boot con formato ECS, GELF o Logstash nei profili di produzione."
            );
            case "SPR075_MOCKMVC_TESTER_ADVISOR" -> new RuleText(
                    "Opportunità MockMvcTester",
                    "MockMvcTester offre asserzioni MVC più leggibili e moderne nei progetti Spring Boot recenti.",
                    "Valuta MockMvcTester sui nuovi slice test dei controller mantenendo stabili i test MockMvc esistenti."
            );
            case "SPR076_MANUAL_REST_TEMPLATE" -> new RuleText(
                    "RestTemplate creato manualmente",
                    "Un RestTemplate creato a mano può saltare timeout, interceptor, error handling e osservabilità condivisa.",
                    "Inietta un bean RestTemplate configurato oppure valuta RestClient per nuovi client HTTP sincroni."
            );
            case "SPR077_WEBCLIENT_BUILDER_CREATED_MANUALLY" -> new RuleText(
                    "WebClient creato manualmente",
                    "Creare WebClient direttamente può duplicare codec, base URL, filtri, metriche e configurazione TLS/proxy.",
                    "Inietta WebClient.Builder o un bean WebClient configurato centralmente."
            );
            case "SPR078_RESTCLIENT_BUILDER_CREATED_MANUALLY" -> new RuleText(
                    "RestClient creato direttamente",
                    "RestClient è moderno, ma builder locali ripetuti possono duplicare timeout, interceptor e osservabilità.",
                    "Esponi un RestClient.Builder o un bean RestClient configurato quando il client è usato dai service applicativi."
            );
            case "SPR079_LOW_LEVEL_HTTP_CLIENT" -> new RuleText(
                    "Client HTTP di basso livello",
                    "Client HTTP di basso livello rendono meno coerenti timeout, retry, proxy, TLS, tracing e test.",
                    "Preferisci RestClient, WebClient o un client HTTP configurato come bean."
            );
            case "SPR080_SIMPLE_DATE_FORMAT" -> new RuleText(
                    "SimpleDateFormat rilevato",
                    "SimpleDateFormat è mutabile e fragile, soprattutto se riusato tra thread diversi.",
                    "Usa java.time DateTimeFormatter e centralizza il formato quando fa parte di un contratto API."
            );
            case "SPR081_GSON_CREATED_MANUALLY" -> new RuleText(
                    "Gson creato manualmente",
                    "Gson manuale può creare una seconda policy JSON diversa dall'ObjectMapper configurato da Spring Boot.",
                    "Preferisci l'ObjectMapper gestito da Spring Boot oppure centralizza Gson come bean se è davvero richiesto."
            );
            case "SPR082_VALUE_INJECTION_FOR_GROUPED_CONFIG" -> new RuleText(
                    "@Value usato per configurazione applicativa",
                    "Molti @Value sparsi rendono più difficile validare, documentare ed evolvere la configurazione.",
                    "Usa @ConfigurationProperties con validazione per gruppi di proprietà e lascia @Value solo per valori isolati."
            );
            case "SPR083_ASYNC_WITHOUT_ENABLE_ASYNC" -> new RuleText(
                    "@Async senza @EnableAsync",
                    "I metodi @Async vengono ignorati se l'esecuzione asincrona non è abilitata nel contesto Spring.",
                    "Aggiungi @EnableAsync in una configuration class e definisci il TaskExecutor usato dal lavoro asincrono."
            );
            case "SPR084_SCHEDULED_WITHOUT_ENABLE_SCHEDULING" -> new RuleText(
                    "@Scheduled senza @EnableScheduling",
                    "I metodi schedulati vengono ignorati se lo scheduling non è abilitato nel contesto Spring.",
                    "Aggiungi @EnableScheduling in una configuration class e rendi esplicite le impostazioni dello scheduler."
            );
            case "SPR085_DIRECT_LOCAL_DATE_TIME_NOW" -> new RuleText(
                    "Ora di sistema letta direttamente",
                    "Leggere direttamente LocalDateTime.now rende più difficili test, simulazioni e riproduzione dei casi temporali.",
                    "Inietta java.time.Clock e usa LocalDateTime.now(clock) nella logica applicativa."
            );
            case "SPR086_APPLICATION_CONTEXT_GET_BEAN" -> new RuleText(
                    "Bean risolto programmaticamente",
                    "ApplicationContext.getBean nasconde dipendenze e rende meno leggibile il cablaggio rispetto alla constructor injection.",
                    "Inietta il collaboratore via costruttore oppure isola la risoluzione dinamica in una factory dedicata."
            );
            case "SPR088_THREAD_SLEEP_IN_PRODUCTION_CODE" -> new RuleText(
                    "Thread.sleep nel codice applicativo",
                    "Bloccare thread applicativi maschera problemi di coordinamento e spreca risorse server.",
                    "Usa scheduling, asincronia, retry/backoff o primitive di sincronizzazione esplicite."
            );
            case "SPR089_MANUAL_VALIDATOR_FACTORY" -> new RuleText(
                    "ValidatorFactory creato manualmente",
                    "ValidatorFactory manuali possono saltare la configurazione Spring della validazione e l'iniezione nei constraint validator.",
                    "Inietta Validator oppure usa @Valid e @Validated ai confini web/service."
            );
            case "SPR090_MANUAL_CACHE_STRUCTURE" -> new RuleText(
                    "Possibile cache manuale in memoria",
                    "Map usate come cache dentro componenti Spring restano invisibili a eviction, metriche e controllo operativo.",
                    "Valuta Spring Cache con un CacheManager configurato quando la mappa rappresenta dati riusabili in cache."
            );

            case "SPR091_APPLICATION_PROPERTIES_SHOULD_BE_EXTERNALIZED" -> new RuleText(
                    "Configurazione applicativa da esternalizzare",
                    "Valori runtime dentro application.properties o application.yml rendono l'artefatto legato a un ambiente e aumentano il rischio di rilasci non ripetibili.",
                    "Lascia nel repository solo placeholder o default sicuri e passa i valori tramite variabili ambiente, configurazione montata, ConfigMap/Secret, Vault o piattaforma di deploy."
            );
            case "SPR092_HARDCODED_ACTIVE_SPRING_PROFILE" -> new RuleText(
                    "Profilo Spring attivo scritto nel pacchetto",
                    "spring.profiles.active dentro la configurazione confezionata forza il comportamento dell'applicazione e può ignorare quanto deciso da ambiente o pipeline.",
                    "Rimuovi spring.profiles.active dai file packaged e imposta il profilo da variabile ambiente, argomento di avvio o piattaforma di deploy."
            );
            case "SPR093_MAVEN_DEPENDENCY_VERSION_CONFLICT" -> new RuleText(
                    "Conflitto di versioni Maven",
                    "La stessa dipendenza dichiarata con versioni diverse può generare classpath instabili, build non ripetibili e problemi runtime difficili da diagnosticare.",
                    "Mantieni una sola versione per dipendenza e governala tramite BOM Spring Boot, parent POM o dependencyManagement."
            );
            case "SPR094_MAVEN_MIXED_STACK_DEPENDENCIES" -> new RuleText(
                    "Stack di dipendenze potenzialmente mischiati",
                    "Combinare stack sovrapposti può essere corretto solo se intenzionale; altrimenti aumenta il rischio di auto-configurazioni duplicate e comportamento ambiguo.",
                    "Mantieni solo lo stack necessario oppure documenta la scelta e isola la configurazione del caso speciale."
            );
            case "SPR095_MAVEN_DEPENDENCY_HYGIENE" -> new RuleText(
                    "Igiene dipendenze Maven migliorabile",
                    "Versioni SNAPSHOT, range, RELEASE/LATEST, systemPath o scope mancanti rendono meno governabile il rilascio e più fragile la pipeline.",
                    "Usa versioni rilasciate e fisse, dependencyManagement, repository Maven governati e scope espliciti per strumenti come Lombok."
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
            case "SPR053_JPA_ENTITY_ACCESSIBLE_NO_ARGS_CONSTRUCTOR" -> new RuleText("JPA no-argument constructor is missing", "JPA entities must have a public or protected no-argument constructor so the persistence provider can instantiate them correctly.", "Add a protected no-argument constructor and use public constructors or factory methods for valid entity creation.");
            case "SPR054_JPA_TO_ONE_RELATIONSHIP_SHOULD_BE_LAZY" -> new RuleText("JPA to-one relationship is not explicitly LAZY", "ManyToOne and OneToOne are eager by default and may load unwanted object graphs, hurting performance and serialization.", "Set fetch = FetchType.LAZY and load required data with explicit queries, projections or entity graphs.");
            case "SPR055_DOMAIN_LAYER_DEPENDS_ON_SPRING" -> new RuleText("Domain layer depends on Spring", "In DDD or hexagonal architecture the domain should stay independent from the framework so business rules do not depend on infrastructure details.", "Move Spring annotations, adapters and configuration to application or infrastructure layers and keep the domain clean.");
            case "SPR056_SERVICE_DEPENDS_ON_WEB_LAYER" -> new RuleText("Service depends on the web layer", "A service that knows HTTP, servlet APIs or ResponseEntity mixes application use cases with web transport details.", "Keep HTTP concerns in controllers and pass application DTOs or domain commands to services.");
            case "SPR057_REPOSITORY_DEPENDS_ON_UPPER_LAYER" -> new RuleText("Repository depends on upper layers", "A repository should know persistence and queries only, not controllers, services or HTTP concepts.", "Move orchestration and application logic to services and keep repositories limited to data access operations.");
            case "SPR058_SECURITY_FILTER_CHAIN_MISSING" -> new RuleText("Spring Security without explicit SecurityFilterChain", "When Spring Security is present but no SecurityFilterChain is defined, the security policy is hard to review and may rely on unwanted defaults.", "Define a SecurityFilterChain with endpoint authorization, CSRF, CORS, session policy and authentication mechanism.");
            case "SPR059_CSRF_DISABLED_WITHOUT_STATELESS" -> new RuleText("CSRF disabled without stateless session policy", "Disabling CSRF is reasonable for truly stateless APIs, but risky for browser-based applications using cookies or sessions.", "Declare SessionCreationPolicy.STATELESS or limit CSRF disablement to endpoints where it is justified.");
            case "SPR060_ENDPOINT_WITHOUT_OPENAPI_OPERATION" -> new RuleText("REST endpoint has no OpenAPI description", "Endpoints without @Operation are less readable in Swagger and weaken API contract documentation.", "Add @Operation with a summary and description, then document main responses with @ApiResponse.");
            case "SPR061_ALL_ARGS_CONSTRUCTOR_ON_SPRING_COMPONENT" -> new RuleText("@AllArgsConstructor on Spring component", "@AllArgsConstructor may inject every field in the class, including fields that are not mandatory dependencies.", "Prefer an explicit constructor or @RequiredArgsConstructor with final dependencies.");
            case "SPR062_CONSTRUCTOR_DEPENDENCY_FIELD_NOT_FINAL" -> new RuleText("Constructor dependency field is not final", "Mandatory dependencies injected by constructor are safer when their fields are immutable.", "Make constructor-assigned dependency fields final or use @RequiredArgsConstructor.");
            case "SPR063_REST_CONTROLLER_WITHOUT_BASE_MAPPING" -> new RuleText("REST controller has no base mapping", "Without class-level @RequestMapping, endpoint structure and API versioning are less visible.", "Add a base mapping such as /api/v1/resource-name and keep method mappings focused on operations.");
            case "SPR064_MANUAL_OBJECT_MAPPER" -> new RuleText("ObjectMapper created manually", "Manual ObjectMapper instances may bypass Spring Boot global configuration, JavaTime modules, serializers, naming strategy and common settings.", "Inject the Boot-managed ObjectMapper or centralize customization in a dedicated bean.");
            case "SPR065_MANUAL_THREAD_CREATION" -> new RuleText("Thread created manually", "Manual threads do not follow Spring lifecycle, centralized configuration, observability or graceful shutdown.", "Use TaskExecutor, ThreadPoolTaskExecutor, @Async or TaskScheduler depending on the use case.");
            case "SPR066_MANUAL_EXECUTOR_CREATION" -> new RuleText("Executor created manually", "Direct ExecutorService creation can escape Spring lifecycle and make tuning, shutdown and metrics harder.", "Expose a TaskExecutor or ThreadPoolTaskExecutor bean and inject it where asynchronous work is required.");
            case "SPR067_TIMER_SCHEDULING" -> new RuleText("Timer-based scheduling", "Timer and TimerTask are low-level APIs that integrate poorly with Spring profiles, lifecycle and observability.", "Use @Scheduled or TaskScheduler with explicit configuration and dedicated tests.");
            case "SPR068_MANUAL_JDBC_TEMPLATE" -> new RuleText("JdbcTemplate created manually", "Creating JdbcTemplate directly may duplicate datasource, transaction and exception translation configuration.", "Inject the auto-configured template or define a centralized bean.");
            case "SPR069_MANUAL_PASSWORD_ENCODER" -> new RuleText("PasswordEncoder created manually", "Password encoding should be centrally governed to avoid different strategies across login, tests and migrations.", "Define one PasswordEncoder bean and inject it where passwords are encoded or verified.");
            case "SPR070_DIRECT_ENVIRONMENT_ACCESS" -> new RuleText("Environment read directly from code", "System.getenv or System.getProperty hides configuration, validation and profile handling from Spring.", "Use @ConfigurationProperties, Environment or validated properties classes.");
            case "SPR071_MANUAL_FILE_RESOURCE_ACCESS" -> new RuleText("File resource opened manually", "Direct file access makes it harder to switch between classpath, filesystem and external resources.", "Use Resource, ResourceLoader or configurable properties to make resource access explicit and testable.");
            case "SPR072_SPRING_BEAN_CREATED_WITH_NEW" -> new RuleText("Spring collaborator created with new", "Creating services, repositories, clients or adapters with new bypasses dependency injection, proxies, transactions, validation and test replacement.", "Register the collaborator as a bean and inject it through constructor injection.");
            case "SPR073_MOCKBEAN_MODERNIZATION_ADVISOR" -> new RuleText("Spring Boot Mockito annotation should be modernized", "@MockBean and related annotations are deprecated in recent Spring Boot versions in favor of Spring Framework support.", "Evaluate @MockitoBean and @MockitoSpyBean for new tests or progressive modernization.");
            case "SPR074_STRUCTURED_LOGGING_ADVISOR" -> new RuleText("Structured logging is not configured", "Structured logs improve diagnostics, correlation and aggregation in modern environments without custom logging code.", "Evaluate Spring Boot structured logging with ECS, GELF or Logstash formats for production profiles.");
            case "SPR075_MOCKMVC_TESTER_ADVISOR" -> new RuleText("MockMvcTester opportunity", "MockMvcTester provides more fluent MVC assertions in recent Spring Boot projects.", "Evaluate MockMvcTester for new controller slice tests while keeping existing MockMvc tests stable.");
            case "SPR076_MANUAL_REST_TEMPLATE" -> new RuleText("RestTemplate created manually", "A manually created RestTemplate can bypass shared timeout, interceptor, error handling and observability configuration.", "Inject a configured RestTemplate bean, or evaluate RestClient for new synchronous HTTP clients.");
            case "SPR077_WEBCLIENT_BUILDER_CREATED_MANUALLY" -> new RuleText("WebClient created manually", "Direct WebClient creation can duplicate codecs, base URLs, filters, metrics and TLS/proxy configuration.", "Inject WebClient.Builder or a centrally configured WebClient bean.");
            case "SPR078_RESTCLIENT_BUILDER_CREATED_MANUALLY" -> new RuleText("RestClient created directly", "RestClient is modern, but repeated local builders can duplicate timeouts, interceptors and observability configuration.", "Expose a RestClient.Builder or RestClient bean when the client is used by application services.");
            case "SPR079_LOW_LEVEL_HTTP_CLIENT" -> new RuleText("Low-level HTTP client", "Low-level HTTP clients make timeout, retry, proxy, TLS, tracing and tests less consistent.", "Prefer RestClient, WebClient or a centrally configured HTTP client bean.");
            case "SPR080_SIMPLE_DATE_FORMAT" -> new RuleText("SimpleDateFormat detected", "SimpleDateFormat is mutable and fragile, especially when reused across threads.", "Use java.time DateTimeFormatter and centralize formatting when it is part of an API contract.");
            case "SPR081_GSON_CREATED_MANUALLY" -> new RuleText("Gson created manually", "Manual Gson can create a second JSON policy that differs from Spring Boot configured ObjectMapper.", "Prefer the Boot-managed ObjectMapper or centralize Gson as a bean if Gson is explicitly required.");
            case "SPR082_VALUE_INJECTION_FOR_GROUPED_CONFIG" -> new RuleText("@Value used for application configuration", "Many scattered @Value properties make configuration harder to validate, document and evolve.", "Use @ConfigurationProperties with validation for grouped settings and keep @Value only for isolated values.");
            case "SPR083_ASYNC_WITHOUT_ENABLE_ASYNC" -> new RuleText("@Async without @EnableAsync", "@Async methods are ignored if asynchronous execution is not enabled in the Spring context.", "Add @EnableAsync in a configuration class and define the TaskExecutor used by asynchronous work.");
            case "SPR084_SCHEDULED_WITHOUT_ENABLE_SCHEDULING" -> new RuleText("@Scheduled without @EnableScheduling", "Scheduled methods are ignored if scheduling is not enabled in the Spring context.", "Add @EnableScheduling in a configuration class and make scheduler settings explicit.");
            case "SPR085_DIRECT_LOCAL_DATE_TIME_NOW" -> new RuleText("System time read directly", "Direct LocalDateTime.now usage makes tests, simulations and temporal case reproduction harder.", "Inject java.time.Clock and use LocalDateTime.now(clock) in application logic.");
            case "SPR086_APPLICATION_CONTEXT_GET_BEAN" -> new RuleText("Bean resolved programmatically", "ApplicationContext.getBean hides dependencies and makes wiring less readable than constructor injection.", "Inject the collaborator through constructor injection or isolate dynamic resolution behind a dedicated factory.");
            case "SPR088_THREAD_SLEEP_IN_PRODUCTION_CODE" -> new RuleText("Thread.sleep in application code", "Blocking application threads hides coordination problems and wastes server resources.", "Use scheduling, async orchestration, retry/backoff or explicit synchronization primitives.");
            case "SPR089_MANUAL_VALIDATOR_FACTORY" -> new RuleText("ValidatorFactory created manually", "Manual ValidatorFactory instances can bypass Spring validation configuration and constraint validator injection.", "Inject Validator or use @Valid and @Validated at web/service boundaries.");
            case "SPR090_MANUAL_CACHE_STRUCTURE" -> new RuleText("Possible manual in-memory cache", "Maps used as caches inside Spring components are invisible to eviction policy, metrics and operational control.", "Evaluate Spring Cache with a configured CacheManager when the map represents reusable cached data.");

            case "SPR091_APPLICATION_PROPERTIES_SHOULD_BE_EXTERNALIZED" -> new RuleText("Application configuration should be externalized", "Runtime values inside application.properties or application.yml make the artifact environment-specific and increase non-repeatable release risk.", "Keep only safe placeholders or defaults in the repository and pass values through environment variables, mounted config, ConfigMap/Secret, Vault or the deployment platform.");
            case "SPR092_HARDCODED_ACTIVE_SPRING_PROFILE" -> new RuleText("Active Spring profile is packaged", "spring.profiles.active inside packaged configuration forces application behavior and may override the environment or pipeline decision.", "Remove spring.profiles.active from packaged files and set the profile through environment variables, startup arguments or the deployment platform.");
            case "SPR093_MAVEN_DEPENDENCY_VERSION_CONFLICT" -> new RuleText("Maven dependency version conflict", "The same dependency declared with different versions can create unstable classpaths, non-repeatable builds and runtime issues that are hard to diagnose.", "Keep one version per dependency and govern it through the Spring Boot BOM, a parent POM or dependencyManagement.");
            case "SPR094_MAVEN_MIXED_STACK_DEPENDENCIES" -> new RuleText("Potentially mixed dependency stacks", "Combining overlapping stacks can be valid only when intentional; otherwise it increases duplicate auto-configuration and ambiguous behavior risk.", "Keep only the required stack or document the decision and isolate the special-case configuration.");
            case "SPR095_MAVEN_DEPENDENCY_HYGIENE" -> new RuleText("Maven dependency hygiene can be improved", "SNAPSHOT versions, ranges, RELEASE/LATEST, systemPath or missing scopes make releases less governed and pipelines more fragile.", "Use fixed released versions, dependencyManagement, governed Maven repositories and explicit scopes for tools such as Lombok.");
            default -> null;
        };
    }


    private static String fallbackText(String ruleId, String fallback, ReportLanguage language, TextPart part) {
        if (language == ReportLanguage.ENGLISH) {
            return fallback;
        }
        String id = ruleId == null ? "" : ruleId;
        String title = italianTitleFromFallback(fallback == null ? id : fallback);
        if (part == TextPart.TITLE) {
            return title;
        }
        if (id.startsWith("ARCH")) {
            return part == TextPart.WHY
                    ? "La regola evidenzia un possibile accoppiamento tra layer, moduli o bounded context. Questo riduce la chiarezza dei confini architetturali e rende più rischiosi refactoring, test e manutenzione."
                    : "Rivedi le dipendenze tra package e layer. Mantieni dominio e casi d'uso separati da web, infrastruttura e dettagli di persistenza; introduci porte, adapter, mapper o servizi applicativi dove necessario.";
        }
        if (id.startsWith("SEC")) {
            return part == TextPart.WHY
                    ? "La configurazione di sicurezza rilevata può rendere l'applicazione troppo permissiva, incoerente con il profilo di rilascio o difficile da governare in produzione."
                    : "Rendi esplicite autenticazione, autorizzazione, gestione sessione, CSRF, CORS e protezione degli endpoint sensibili. Limita le eccezioni ai soli casi documentati e coprile con test di sicurezza.";
        }
        if (id.startsWith("WEB")) {
            return part == TextPart.WHY
                    ? "Il contratto web/API risulta meno chiaro o meno robusto. Controller troppo accoppiati, DTO non validati o risposte non standardizzate aumentano il rischio di regressioni e comportamenti incoerenti per i client."
                    : "Mantieni i controller sottili, usa DTO validati, centralizza la gestione degli errori, documenta gli endpoint pubblici e separa il contratto REST dal modello di persistenza.";
        }
        if (id.startsWith("BAT")) {
            return part == TextPart.WHY
                    ? "La configurazione Batch rilevata può compromettere riavvio, idempotenza, osservabilità o gestione degli errori. In produzione questi aspetti sono essenziali per esecuzioni ripetibili e governabili."
                    : "Rendi espliciti parametri, chunk size, reader, writer, retry, skip, listener, metriche e semantica di restart. Evita stato mutabile non controllato e operazioni non idempotenti.";
        }
        if (id.startsWith("CLD")) {
            return part == TextPart.WHY
                    ? "Il progetto contiene elementi che riducono la neutralità dell'artefatto rispetto all'ambiente. Questo ostacola deploy ripetibili, configurazione esterna e scalabilità operativa."
                    : "Sposta valori runtime e dipendenze ambientali fuori dall'artefatto. Usa proprietà tipizzate, variabili ambiente, configurazione montata, secret manager e impostazioni esplicite per readiness, logging e shutdown.";
        }
        if (id.startsWith("OBS")) {
            return part == TextPart.WHY
                    ? "L'osservabilità rilevata è insufficiente o non centralizzata. Senza log, metriche, health check e correlazione affidabili, diagnosi e gestione operativa diventano più lente."
                    : "Usa Actuator, Micrometer, logging strutturato, correlation ID e HealthIndicator dedicati. Evita log manuali o non governati e centralizza la raccolta delle informazioni operative.";
        }
        if (id.startsWith("POM")) {
            return part == TextPart.WHY
                    ? "Il POM Maven mostra una configurazione che può rendere build, versioni, plugin o dipendenze meno governabili. Nei progetti Spring Boot il dependency management è parte dell'architettura applicativa."
                    : "Centralizza versioni e plugin nel parent o nel dependencyManagement, usa il BOM di Spring Boot, evita scope impropri e mantieni coerenti starter, driver, librerie di test e packaging.";
        }
        if (id.startsWith("ADV")) {
            return part == TextPart.WHY
                    ? "Il codice usa una soluzione Java manuale o un'API di basso livello che può funzionare, ma non sfrutta pienamente configurazione, lifecycle, testabilità e osservabilità offerte da Spring."
                    : "Valuta l'alternativa Spring indicata dalla regola. Centralizza la configurazione come bean, usa astrazioni Spring idiomatiche e mantieni il codice applicativo più semplice e governabile.";
        }
        return fallback;
    }

    private static String italianTitleFromFallback(String fallback) {
        String value = fallback == null ? "Problema rilevato" : fallback;
        return value
                .replace("Domain layer", "Layer di dominio")
                .replace("Application layer", "Layer applicativo")
                .replace("Infrastructure adapter", "Adapter infrastrutturale")
                .replace("Controller", "Controller")
                .replace("Service", "Service")
                .replace("Repository", "Repository")
                .replace("depends on", "dipende da")
                .replace("must not depend on", "non deve dipendere da")
                .replace("missing", "mancante")
                .replace("Missing", "Mancante")
                .replace("without", "senza")
                .replace("Without", "Senza")
                .replace("detected", "rilevato")
                .replace("Detected", "Rilevato")
                .replace("created manually", "creato manualmente")
                .replace("Created manually", "Creato manualmente")
                .replace("manual", "manuale")
                .replace("Manual", "Manuale")
                .replace("hardcoded", "hardcoded")
                .replace("Hardcoded", "Hardcoded")
                .replace("exposed", "esposto")
                .replace("Exposed", "Esposto")
                .replace("configuration", "configurazione")
                .replace("Configuration", "Configurazione")
                .replace("health endpoint", "endpoint health")
                .replace("endpoint", "endpoint")
                .replace("Actuator", "Actuator")
                .replace("Security", "Security")
                .replace("Spring", "Spring")
                .replace("Maven", "Maven")
                .replace("Batch", "Batch")
                .replace("API", "API")
                .replace("REST", "REST")
                .replace("Web", "Web")
                .replace("code", "codice")
                .replace("Code", "Codice");
    }

    private static String normalizeItalian(String value, ReportLanguage language) {
        if (language == ReportLanguage.ENGLISH || value == null) {
            return value;
        }
        return value
                .replace("direttamenete", "direttamente")
                .replace("perchè", "perché")
                .replace("RunTimeException", "RuntimeException")
                .replace("massive", "estese")
                .replace("fix", "correzione")
                .replace("passa carte", "semplice delega")
                .replace("massimo un riga", "al massimo una riga")
                .replace("database", "al database")
                .replace("JPA sono di natura transactional", "JPA sono già transazionali per le operazioni principali")
                .replace("non controllare mai le eccezioni", "non intercettare eccezioni generiche")
                .replace("deve rompersi", "deve fallire in modo esplicito")
                .replace("Leggi questo articolo https://medium.com/me/stats/post/6aa8bfe43239", "")
                .replace("baseline legacy", "baseline legacy")
                .replace("Cloud readiness", "prontezza cloud")
                .replace("Dependency injection", "iniezione delle dipendenze")
                .replace("production-ready", "pronto per la produzione");
    }

    private enum TextPart {
        TITLE,
        WHY,
        FIX
    }

    private record RuleText(String title, String why, String fix) {
    }
}

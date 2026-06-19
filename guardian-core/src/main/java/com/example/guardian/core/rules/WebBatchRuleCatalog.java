package com.example.guardian.core.rules;

import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.ProjectType;
import com.example.guardian.core.model.Severity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.example.guardian.core.rules.CatalogPatternRule.Definition.line;
import static com.example.guardian.core.rules.CatalogPatternRule.SourceTarget.CONFIG;
import static com.example.guardian.core.rules.CatalogPatternRule.SourceTarget.JAVA_MAIN;
import static com.example.guardian.core.rules.CatalogPatternRule.SourceTarget.POM;

/**
 * High-confidence catalog for the currently supported Web/API and Spring Batch profiles.
 *
 * @author Simone Meneghetti
 */
final class WebBatchRuleCatalog {

    private WebBatchRuleCatalog() {
    }

    /**
     * Creates high-confidence Web/API catalog rules.
     *
     * @return Web/API rules
     */
    static List<SpringRule> webRules() {
        List<CatalogPatternRule.Definition> definitions = new ArrayList<>();
        definitions.add(line("WEB010_GET_ENDPOINT_MUTATES_STATE", Severity.MAJOR, JAVA_MAIN, Set.of("save(", "delete(", "update(", "remove("), "Endpoint GET che sembra modificare stato", "GET dovrebbe essere sicuro e idempotente. Usarlo per modifiche crea problemi con cache, proxy, retry e client HTTP.", "Usa POST, PUT o PATCH per operazioni che modificano stato.").requiringFile("@GetMapping").inPaths("/controller/"));
        definitions.add(line("WEB016_TECHNICAL_TRY_CATCH_IN_CONTROLLER", Severity.MINOR, JAVA_MAIN, Set.of("catch (Exception", "catch(Exception"), "Try/catch tecnico dentro controller", "La gestione tecnica degli errori nel controller produce risposte non uniformi e meno testabili.", "Sposta la mappatura degli errori in @RestControllerAdvice.").inPaths("/controller/"));
        definitions.add(line("WEB024_MULTIPART_WITHOUT_LIMIT_POLICY", Severity.MAJOR, JAVA_MAIN, Set.of("MultipartFile"), "Endpoint multipart senza policy dimensione/tipo", "Upload senza policy esplicita può consumare troppe risorse o accettare contenuti inattesi.", "Configura limiti multipart e valida tipo, dimensione e nome file.").inPaths("/controller/"));
        definitions.add(line("WEB029_EXCEPTION_MESSAGE_EXPOSED", Severity.MAJOR, JAVA_MAIN, Set.of("getMessage()"), "Messaggio tecnico dell'eccezione esposto al client", "Esporre exception.getMessage() può rivelare dettagli interni o dati tecnici al client.", "Usa ProblemDetail o un errore applicativo standard gestito da @RestControllerAdvice.").inPaths("/controller/"));
        definitions.add(line("WEB037_PAGEABLE_WITHOUT_LIMIT", Severity.MAJOR, JAVA_MAIN, Set.of("Pageable"), "Pageable senza limite esplicito", "Un endpoint paginato senza limite massimo può generare query costose e payload troppo grandi.", "Configura una dimensione massima pagina e valida i parametri di paginazione.").inPaths("/controller/"));
        definitions.add(line("WEB039_MANUAL_AUTHORIZATION_IN_CONTROLLER", Severity.MAJOR, JAVA_MAIN, Set.of("hasRole(", "hasAuthority(", "SecurityContextHolder"), "Autorizzazione manuale nel controller", "Controlli autorizzativi sparsi nei controller sono più difficili da verificare e testare.", "Usa method security, AuthorizationManager o policy centralizzate.").inPaths("/controller/"));
        return definitions.stream().map(definition -> new CatalogPatternRule(definition.onlyWhen(WebBatchRuleCatalog::isWebRelevant))).map(SpringRule.class::cast).toList();
    }

    /**
     * Creates high-confidence Spring Batch catalog rules.
     *
     * @return Batch rules
     */
    static List<SpringRule> batchRules() {
        List<CatalogPatternRule.Definition> definitions = new ArrayList<>();
        definitions.add(line("BAT004_CHUNK_SIZE_HARDCODED", Severity.MINOR, JAVA_MAIN, Set.of(".chunk("), "Dimensione chunk da rendere configurabile", "Una dimensione chunk scritta direttamente nel codice rende più difficile calibrare prestazioni e memoria tra ambienti.", "Sposta la dimensione del chunk in una proprietà tipizzata e validata."));
        definitions.add(line("BAT008_MANUAL_DATASOURCE_IN_READER_WRITER", Severity.MAJOR, JAVA_MAIN, Set.of("new DriverManagerDataSource", "DriverManager.getConnection"), "DataSource creato manualmente nel batch", "Creare connessioni o DataSource a mano può bypassare transazioni, pooling e configurazione centralizzata.", "Inietta il DataSource gestito da Spring e costruisci reader/writer tramite bean configurati."));
        definitions.add(line("BAT012_RETRY_ON_GENERIC_EXCEPTION", Severity.MAJOR, JAVA_MAIN, Set.of("retry(Exception.class)", "retry(Throwable.class)"), "Retry su eccezione troppo generica", "Ritentare qualunque eccezione può nascondere bug non recuperabili e ripetere operazioni non idempotenti.", "Configura retry solo per eccezioni recuperabili e documenta backoff e limite tentativi."));
        definitions.add(line("BAT013_SKIP_ON_GENERIC_EXCEPTION", Severity.MAJOR, JAVA_MAIN, Set.of("skip(Exception.class)", "skip(Throwable.class)"), "Skip su eccezione troppo generica", "Saltare qualunque eccezione può nascondere errori funzionali e produrre dati incompleti senza controllo.", "Configura skip solo per eccezioni attese e aggiungi SkipListener o audit."));
        definitions.add(line("BAT014_SKIP_LIMIT_TOO_HIGH", Severity.MAJOR, JAVA_MAIN, Set.of("skipLimit(1000", "skipLimit(Integer.MAX_VALUE)"), "Skip limit troppo alto", "Uno skip limit molto alto può far terminare il job con troppi record scartati.", "Imposta uno skip limit coerente con la qualità attesa dei dati e monitora gli scarti."));
        definitions.add(line("BAT015_FAULT_TOLERANCE_WITHOUT_SKIP_LISTENER", Severity.MINOR, JAVA_MAIN, Set.of("faultTolerant()"), "Fault tolerance da rendere osservabile", "Retry e skip senza listener rendono più difficile capire quali record sono stati scartati o ritentati.", "Aggiungi SkipListener, RetryListener o metriche dedicate per tracciare gli errori recuperati."));
        definitions.add(line("BAT019_ALLOW_START_IF_COMPLETE_ON_STEP", Severity.MAJOR, JAVA_MAIN, Set.of("allowStartIfComplete(true)"), "Step rieseguibile anche se già completato", "allowStartIfComplete(true) può rieseguire uno step già completato e richiede idempotenza esplicita.", "Usalo solo quando lo step è davvero idempotente e documenta la strategia di riavvio."));
        definitions.add(line("BAT020_PREVENT_RESTART_REVIEW", Severity.MINOR, JAVA_MAIN, Set.of("preventRestart()"), "Restart disabilitato", "Disabilitare il restart può rendere più costosi i recuperi operativi dopo un errore.", "Usa preventRestart solo se la riesecuzione è pericolosa o non supportata e documenta la procedura di recupero."));
        definitions.add(line("BAT021_TASK_EXECUTOR_WITHOUT_THREAD_SAFETY", Severity.MAJOR, JAVA_MAIN, Set.of("taskExecutor("), "Step parallelo da verificare per thread-safety", "Un taskExecutor nel batch richiede reader, processor e writer compatibili con esecuzione concorrente.", "Verifica thread-safety, stato condiviso, ordering e transazioni prima di parallelizzare."));
        definitions.add(line("BAT022_PARTITION_GRID_SIZE_HARDCODED", Severity.MINOR, JAVA_MAIN, Set.of("gridSize("), "Grid size di partizionamento da configurare", "Un grid size fisso può non adattarsi ai diversi ambienti o carichi.", "Sposta il grid size in configurazione e calibra in base alle risorse disponibili."));
        definitions.add(line("BAT023_MANUAL_BATCH_THREAD_POOL", Severity.MINOR, JAVA_MAIN, Set.of("Executors.newFixedThreadPool", "ThreadPoolExecutor"), "Thread pool batch creato manualmente", "Un thread pool manuale può non essere governato da Spring, metriche e shutdown applicativo.", "Definisci un TaskExecutor bean configurato e osservabile."));
        definitions.add(line("BAT024_SYSTEM_EXIT_IN_BATCH", Severity.CRITICAL, JAVA_MAIN, Set.of("System.exit"), "System.exit dentro un batch", "Terminare la JVM da un job batch può interrompere transazioni, altri job e cleanup applicativo.", "Propaga un errore controllato e lascia a Spring Batch o scheduler la gestione dell'exit status."));
        definitions.add(line("BAT025_THREAD_SLEEP_IN_BATCH", Severity.MAJOR, JAVA_MAIN, Set.of("Thread.sleep"), "Thread.sleep dentro un batch", "Thread.sleep blocca thread applicativi e rende fragile la gestione di retry e backoff.", "Usa retry/backoff espliciti o una schedulazione controllata."));
        definitions.add(line("BAT028_READER_SELECT_STAR", Severity.MINOR, JAVA_MAIN, Set.of("SELECT *", "select *"), "Reader con SELECT *", "SELECT * rende il reader dipendente dalla struttura fisica della tabella e può caricare colonne inutili.", "Seleziona esplicitamente le colonne necessarie e mantieni un ordinamento stabile quando serve."));
        definitions.add(line("BAT040_BATCH_INPUT_OUTPUT_PATH_HARDCODED", Severity.MAJOR, JAVA_MAIN, Set.of("/tmp/", "C:\\", "FileInputStream"), "Percorso input/output batch hardcoded", "Path locali o assoluti rendono il job dipendente dall'ambiente e complicano deploy e test.", "Sposta i path in configurazione esterna o usa Resource/Storage gestiti dalla piattaforma."));
        return definitions.stream().map(definition -> new CatalogPatternRule(definition.onlyWhen(WebBatchRuleCatalog::isBatchRelevant))).map(SpringRule.class::cast).toList();
    }

    /**
     * Creates high-confidence Maven/configuration rules shared by Web/API and Batch scans.
     *
     * @return shared readiness rules
     */
    static List<SpringRule> sharedReadinessRules() {
        List<CatalogPatternRule.Definition> definitions = new ArrayList<>();
        definitions.add(line("CLD003_SECRET_IN_PACKAGED_CONFIG", Severity.CRITICAL, CONFIG, Set.of("password=", "password:", "secret=", "secret:", "token=", "token:", "api-key=", "api-key:"), "Possibile segreto nella configurazione", "Password, token o chiavi nel repository possono essere copiati, loggati o riutilizzati.", "Rimuovi il valore dal repository e usa variabili ambiente, Vault, secret manager o configurazione montata."));
        definitions.add(line("CLD005_OPERATIONAL_CRON_IN_ARTIFACT", Severity.MINOR, CONFIG, Set.of("cron=", "cron-expression", "scheduler.cron", "schedule.cron"), "Cron operativo incluso nel pacchetto", "Una schedulazione operativa committata rende il comportamento diverso da modificare tra ambienti.", "Usa una proprietà esterna per la cron expression e valorizzala per ambiente dalla piattaforma di deploy."));
        definitions.add(line("CLD015_ACTUATOR_HEALTH_DETAILS_ALWAYS_EXPOSED", Severity.MAJOR, CONFIG, Set.of("management.endpoint.health.show-details=always", "show-details: always"), "Dettagli health sempre esposti", "Mostrare sempre i dettagli health può esporre informazioni operative su componenti e dipendenze.", "In produzione usa when-authorized o disabilita i dettagli non necessari."));
        definitions.add(line("OBS025_HEALTH_DETAILS_ALWAYS_EXPOSED", Severity.MAJOR, CONFIG, Set.of("management.endpoint.health.show-details=always", "show-details: always"), "Dettagli health sempre esposti", "Mostrare sempre i dettagli health può esporre informazioni operative su componenti e dipendenze.", "In produzione usa when-authorized o disabilita i dettagli non necessari."));
        definitions.add(line("OBS006_CONSOLE_PRINT", Severity.MINOR, JAVA_MAIN, Set.of("System.out.println"), "Log scritto con System.out", "System.out non passa da livelli, formato, MDC e raccolta centralizzata.", "Usa SLF4J Logger con contesto utile."));
        definitions.add(line("OBS007_PRINT_STACK_TRACE", Severity.MINOR, JAVA_MAIN, Set.of("printStackTrace()"), "printStackTrace usato", "printStackTrace non passa dal logging applicativo e rende meno governabile la diagnosi.", "Logga l'eccezione con SLF4J passando il throwable come ultimo argomento."));
        definitions.add(line("POM001_BOOT_WITHOUT_PARENT_OR_BOM", Severity.MAJOR, POM, Set.of("spring-boot-starter"), "Spring Boot senza parent o BOM evidente", "Senza parent o BOM Spring Boot la gestione delle versioni diventa meno governabile.", "Usa spring-boot-starter-parent o spring-boot-dependencies BOM."));
        return definitions.stream().map(CatalogPatternRule::new).map(SpringRule.class::cast).toList();
    }

    private static boolean isWebRelevant(ProjectScanContext context) {
        return context.profile().projectType() == ProjectType.WEB_API || context.capabilities().usesSpringWeb();
    }

    private static boolean isBatchRelevant(ProjectScanContext context) {
        return context.profile().projectType() == ProjectType.BATCH || context.capabilities().usesSpringBatch();
    }
}

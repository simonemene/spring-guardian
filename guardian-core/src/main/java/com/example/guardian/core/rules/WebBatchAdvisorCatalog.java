package com.example.guardian.core.rules;

import com.example.guardian.core.model.Severity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * High-confidence Spring Advisor catalog for Web/API and Spring Batch scans.
 *
 * @author Simone Meneghetti
 */
final class WebBatchAdvisorCatalog {

    private WebBatchAdvisorCatalog() {
    }

    /**
     * Creates advisor rules that require concrete AST evidence and avoid import-only matches.
     *
     * @return advisor rules
     */
    static List<SpringRule> rules() {
        List<SpringRule> rules = new ArrayList<>();
        rules.add(new ObjectCreationAdvisorRule("ADV001_MANUAL_OBJECT_MAPPER", Severity.INFO, Set.of("ObjectMapper", "XmlMapper"), "ObjectMapper creato manualmente", "Un ObjectMapper creato con new può bypassare moduli Jackson, naming policy e configurazioni Spring Boot.", "Inietta l'ObjectMapper gestito da Spring Boot o esponi un singolo bean configurato.", true));
        rules.add(new ObjectCreationAdvisorRule("ADV002_MANUAL_GSON", Severity.INFO, Set.of("Gson", "GsonBuilder"), "Gson creato manualmente", "Un secondo motore JSON può produrre contratti diversi da quelli configurati da Spring Boot.", "Preferisci l'ObjectMapper Boot-managed oppure centralizza Gson come bean se è davvero richiesto.", true));
        rules.add(new ObjectCreationAdvisorRule("ADV003_MANUAL_REST_TEMPLATE", Severity.INFO, Set.of("RestTemplate"), "RestTemplate creato manualmente", "Un RestTemplate creato con new può saltare timeout, interceptor, error handler e osservabilità condivisa.", "Inietta un RestTemplate configurato o valuta RestClient per nuovi client sincroni.", true));
        rules.add(new StaticMethodAdvisorRule("ADV004_WEBCLIENT_CREATE_SCATTERED", Severity.INFO, "WebClient", Set.of("create"), "WebClient.create usato direttamente", "WebClient.create può duplicare base URL, codec, filtri, metriche e TLS/proxy configuration.", "Inietta WebClient.Builder o un WebClient bean configurato.", false));
        rules.add(new StaticMethodAdvisorRule("ADV005_RESTCLIENT_CREATE_SCATTERED", Severity.INFO, "RestClient", Set.of("create"), "RestClient.create usato direttamente", "RestClient.create può duplicare timeout, interceptor e osservabilità tra classi.", "Esponi un RestClient bean o inietta RestClient.Builder configurato.", false));
        rules.add(new ObjectCreationAdvisorRule("ADV006_LOW_LEVEL_HTTP_CLIENT", Severity.INFO, Set.of("HttpURLConnection", "HttpClient", "CloseableHttpClient", "OkHttpClient"), "Client HTTP di basso livello", "Client HTTP creati direttamente rendono meno coerenti timeout, retry, tracing e test.", "Preferisci RestClient, WebClient o un client HTTP configurato come bean.", false));
        rules.add(new StaticMethodAdvisorRule("ADV012_DIRECT_SYSTEM_ENVIRONMENT_ACCESS", Severity.INFO, "System", Set.of("getenv", "getProperty"), "Accesso diretto all'ambiente runtime", "Leggere ambiente o system properties direttamente rende più difficile validare, documentare e testare la configurazione applicativa.", "Usa Environment solo in adapter dedicati o preferisci @ConfigurationProperties validato.", false));
        rules.add(new ObjectCreationAdvisorRule("ADV014_DIRECT_FILE_STREAM", Severity.INFO, Set.of("FileInputStream", "FileOutputStream", "FileReader", "FileWriter"), "Accesso file diretto", "Stream file creati direttamente rendono più difficile gestire Resource, classpath, storage esterno e test.", "Usa Resource, ResourceLoader o una proprietà tipizzata con una Resource configurata da Spring.", false));
        rules.add(new ObjectCreationAdvisorRule("ADV008_MANUAL_THREAD_CREATION", Severity.INFO, Set.of("Thread"), "Thread creato manualmente", "Thread manuali bypassano ciclo di vita Spring, shutdown ordinato e osservabilità.", "Usa TaskExecutor, ThreadPoolTaskExecutor, @Async o TaskScheduler.", false));
        rules.add(new StaticMethodAdvisorRule("ADV009_MANUAL_EXECUTOR", Severity.INFO, "Executors", Set.of("newFixedThreadPool", "newCachedThreadPool", "newSingleThreadExecutor", "newScheduledThreadPool"), "Executor creato manualmente", "Executor manuali possono sfuggire a shutdown, metriche e tuning centralizzato.", "Esponi un TaskExecutor o ThreadPoolTaskExecutor bean.", false));
        rules.add(new StaticMethodAdvisorRule("ADV011_THREAD_SLEEP", Severity.INFO, "Thread", Set.of("sleep"), "Thread.sleep nel codice applicativo", "Thread.sleep rende fragile backoff, retry e orchestrazione asincrona.", "Usa scheduler, retry/backoff esplicito o meccanismi di sincronizzazione controllati.", false));
        rules.add(new AnnotationUsageAdvisorRule("ADV013_SCATTERED_VALUE", Severity.INFO, Set.of("Value"), "@Value usato per configurazione applicativa", "Molti @Value sparsi rendono configurazione meno validabile e meno documentabile.", "Raggruppa le proprietà in @ConfigurationProperties validato.", false));
        rules.add(new StaticMethodAdvisorRule("ADV021_MANUAL_VALIDATOR_FACTORY", Severity.INFO, "Validation", Set.of("buildDefaultValidatorFactory"), "ValidatorFactory creato manualmente", "Una factory manuale può bypassare la configurazione Spring e l'iniezione nei ConstraintValidator.", "Inietta Validator o usa @Valid/@Validated ai confini web/service.", false));
        rules.add(new StaticMethodAdvisorRule("ADV033_DIRECT_LOCAL_DATE_NOW", Severity.INFO, "LocalDate", Set.of("now"), "Data corrente letta direttamente", "L'accesso diretto all'orologio rende i test meno deterministici.", "Inietta java.time.Clock e usa LocalDate.now(clock).", false));
        rules.add(new StaticMethodAdvisorRule("ADV033_DIRECT_LOCAL_DATE_TIME_NOW", Severity.INFO, "LocalDateTime", Set.of("now"), "Data e ora lette direttamente", "L'accesso diretto all'orologio rende i test meno deterministici.", "Inietta java.time.Clock e usa LocalDateTime.now(clock).", false));
        rules.add(new StaticMethodAdvisorRule("ADV033_DIRECT_INSTANT_NOW", Severity.INFO, "Instant", Set.of("now"), "Instant letto direttamente", "L'accesso diretto all'orologio rende i test meno deterministici.", "Inietta java.time.Clock e usa Instant.now(clock).", false));
        rules.add(new ObjectCreationAdvisorRule("ADV023_SIMPLE_DATE_FORMAT_MANUAL", Severity.INFO, Set.of("SimpleDateFormat"), "Formattazione date manuale legacy", "SimpleDateFormat è mutabile e spesso produce configurazioni locali incoerenti rispetto a Jackson e Spring Boot.", "Usa java.time.format.DateTimeFormatter, configura Jackson centralmente o inietta un formatter come bean quando serve.", false));
        rules.add(new StaticMethodAdvisorRule("ADV101_SYSTEM_EXIT_IN_APPLICATION_CODE", Severity.INFO, "System", Set.of("exit"), "System.exit nel codice applicativo", "Terminare la JVM direttamente bypassa lifecycle Spring, shutdown ordinato, cleanup e gestione exit status.", "Propaga un errore applicativo o usa exit status gestiti da Spring Boot, Batch o dallo scheduler.", false));
        rules.add(new StaticMethodAdvisorRule("ADV066_COMPLETABLE_FUTURE_DEFAULT_EXECUTOR", Severity.INFO, "CompletableFuture", Set.of("supplyAsync", "runAsync"), "CompletableFuture senza executor esplicito", "Il common pool non è governato da Spring e può creare concorrenza non controllata.", "Usa @Async con executor configurato o passa un Executor esplicito.", false));
        rules.add(new MissingEnableAnnotationAdvisorRule("SPR083_ASYNC_WITHOUT_ENABLE_ASYNC", Severity.INFO, "Async", "EnableAsync", "@Async senza @EnableAsync", "I metodi @Async non vengono eseguiti asincronamente se l'abilitazione manca.", "Aggiungi @EnableAsync e un TaskExecutor esplicito."));
        rules.add(new MissingEnableAnnotationAdvisorRule("SPR084_SCHEDULED_WITHOUT_ENABLE_SCHEDULING", Severity.INFO, "Scheduled", "EnableScheduling", "@Scheduled senza @EnableScheduling", "I metodi @Scheduled non vengono schedulati se l'abilitazione manca.", "Aggiungi @EnableScheduling e rendi esplicite le impostazioni scheduler."));
        rules.add(new StaticMutableCacheAdvisorRule());
        rules.add(new SpringBeanCreatedWithNewRule());
        rules.add(new ApplicationContextGetBeanAdvisorRule());
        return List.copyOf(rules);
    }
}

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
mvn -pl guardian-server -am package -DskipTests
mvn -pl guardian-server spring-boot:run
```

Non usare questo comando:

```bash
mvn -pl guardian-server -am spring-boot:run
```

Con i multi-modulo Maven quel comando può provare ad avviare anche il parent `spring-guardian`, che è un POM aggregator e non ha una main class Spring Boot.

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

## API principali

### Health

```http
GET /api/v1/health
```

### Scan ZIP

```http
POST /api/v1/scans/upload?language=it
Content-Type: multipart/form-data
field: file
```

### Scan cartella caricata dal browser

```http
POST /api/v1/scans/upload-folder?language=it
Content-Type: multipart/form-data
field: files
```

Il frontend invia tutti i file della cartella preservando i path relativi.

### Scan path locale server-side

```http
POST /api/v1/scans/local?language=it
Content-Type: application/json

{
  "path": "/scan/my-project"
}
```

## Report JSON

Il report è raggruppato per regola. In questo modo, se una stessa regola trova 50 classi coinvolte, la UI mostra una sola scheda con l'elenco dei file interessati.

Esempio semplificato:

```json
{
  "projectName": "my-project",
  "architectureScore": 72,
  "riskLevel": "HIGH",
  "summary": {
    "status": "ACTION_REQUIRED",
    "executiveSummary": "Il progetto contiene problemi architetturali, di sicurezza o correttezza da gestire prima di considerarlo pronto per produzione o pipeline CI."
  },
  "findingsByCategory": [
    {
      "category": "Architettura",
      "findings": 3,
      "explanation": "Layering, direzione delle dipendenze, dimensione delle classi, controller troppo ricchi e confini tra componenti Spring."
    }
  ],
  "findings": [
    {
      "ruleId": "SPR030_GOD_CLASS",
      "severity": "MAJOR",
      "category": "Architettura",
      "title": "Classe troppo grande",
      "occurrences": 3,
      "whyItMatters": "Classi molto grandi raccolgono responsabilità diverse e diventano difficili da leggere, testare e modificare in sicurezza.",
      "suggestedFix": "Dividi la classe per responsabilità e sposta operazioni coese in service, componenti o mapper dedicati.",
      "affectedComponents": [
        {
          "type": "JAVA_CLASS",
          "name": "SchedulerService",
          "filePath": "src/main/java/.../SchedulerService.java",
          "line": 20,
          "evidence": "Class SchedulerService has 480 lines and 32 methods."
        }
      ]
    }
  ]
}
```

Nella UI i codici tecnici come `SPR030_GOD_CLASS` vengono mostrati come codice breve, ad esempio `SPR030`, mentre titolo, spiegazione e fix sono parlanti.

## Swagger e JavaDoc

Il backend espone documentazione OpenAPI tramite Springdoc.

Tutti i controller, DTO, configurazioni e servizi backend aggiunti espongono JavaDoc. Le API REST principali sono annotate con `@Operation`, `@ApiResponse`, `@Parameter` e `@Schema`.

## Test backend

Il backend include una struttura di test divisa per livello:

```text
guardian-core/src/test/java/com/example/guardian/core/ProjectScanServiceLanguageTest.java
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
- `Thread.sleep` nei test.

## CLI

Build:

```bash
mvn -pl guardian-cli -am package
```

Esecuzione:

```bash
java -jar guardian-cli/target/spring-guardian-cli.jar scan /path/to/project --format json --language it
```

Lingua inglese:

```bash
java -jar guardian-cli/target/spring-guardian-cli.jar scan /path/to/project --format json --language en
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

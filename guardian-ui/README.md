# Spring Guardian UI

Frontend Angular standalone per Spring Guardian.

## Avvio locale

```bash
cd guardian-ui
npm ci
npm start
```

L'app parte su:

```text
http://localhost:4200
```

Il proxy Angular inoltra `/api` a `http://localhost:8080` tramite `proxy.conf.json`.

## Lingua

La UI permette di scegliere:

- Italiano;
- Inglese.

La scelta viene inviata al backend con `language=it` o `language=en`. Il backend restituisce report localizzati in base alla lingua selezionata.

## Modalità disponibili

- **Carica ZIP**: carica un progetto `.zip`.
- **Carica cartella**: seleziona la cartella root del progetto direttamente dal browser.
- **Percorso backend**: indica una cartella già visibile dal backend, ad esempio `/scan/mio-progetto` o un path Windows locale quando il backend gira sulla stessa macchina.

## Backend richiesto

Avvia il backend dalla root del progetto:

```powershell
.\scripts\run-backend.ps1
```

Oppure manualmente:

```bash
mvn -pl guardian-server -am package -DskipTests
mvn -pl guardian-server spring-boot:run
```

Swagger backend:

```text
http://localhost:8080/swagger-ui/index.html
```

## Docker

Dalla root:

```bash
docker compose up --build
```

UI:

```text
http://localhost:3000
```

Backend:

```text
http://localhost:8080/api/v1/health
```

## Nota registry npm

Non è incluso `.npmrc`: usa il registry configurato sulla macchina. Il `package-lock.json` è incluso per rendere `npm ci` ripetibile in CI e nelle review frontend.

Se sei in azienda puoi usare il registry Nexus aziendale; se sei fuori azienda puoi usare `https://registry.npmjs.org/`.

## Brand assets

The UI includes three brand assets:

```text
src/assets/spring-guardian-logo.svg
src/assets/spring-guardian-wordmark.svg
src/assets/favicon.svg
```

The mark uses a friendly guardian mascot with a shield that deflects broken legacy-code fragments. The compact logo is used in the header and favicon; the wordmark is used in the hero section.

## Automatic scan UX

The scan form is intentionally simple: users choose ZIP, folder upload or backend path, then start the scan. Project type and release target are inferred by the backend from Spring dependencies, code and configuration.

The dashboard uses Spring-centric sections: Spring Modules, Spring Architecture, Spring Alternatives, Production Rules, Suggestions to Verify, Actions and Technical JSON.



## Navigazione enterprise low-noise

La UI ora segue un flusso più semplice:

- **Start**: carica ZIP, cartella o path backend.
- **Review**: leggi Spring Overview, Spring Findings, Spring Alternatives e Spring Quality Gates.
- **Action plan**: apri Spring Review Plan, chiudi la checklist ed esporta gli artefatti.
- **Project**: missione e contatti, separati dal workflow operativo.

La regola UX è: primo impatto breve, dettagli solo su richiesta.

## Ruolo delle pagine

### Spring Overview

È la pagina da leggere per prima. Deve dire in pochi secondi:

- che tipo di progetto Spring è stato analizzato;
- quali aree Spring sono deboli;
- quali finding ad alta priorità guardare;
- dove andare dopo.

### Spring Findings

Mostra i problemi reali nel codice. Non è un catalogo di alternative.

Ogni finding deve comunicare:

- evidenza: file, classe, riga o pattern trovato;
- impatto Spring;
- pattern o best practice Spring da seguire;
- link alla documentazione quando disponibile;
- collegamento alla checklist quando esiste.

### Spring Alternatives

È la pagina “wow”: non replica i finding. Mostra gli **oggetti Spring da preferire**:

- `SecurityFilterChain + @PreAuthorize`;
- DTO + Bean Validation;
- `@RestControllerAdvice + ProblemDetail`;
- `@ConfigurationProperties` validato;
- `RestClient` / `WebClient`;
- service boundary con `@Transactional`;
- Actuator + Micrometer;
- Spring Modulith boundaries.

Per ogni alternativa mostra quando usarla, quando evitarla, perché migliora il progetto e quali finding sono collegati.

### Spring Quality Gates

Mostra solo gate Spring-centric:

- Spring MVC/API boundary;
- Spring Security;
- Spring Data/JPA;
- Spring Boot Production;
- Spring Testing.

Non sostituisce i quality gate generici di SonarQube: serve a capire se l’applicazione usa bene lo stack Spring.

### Spring Review Plan

Unisce il vecchio Architect Mode e la Risk Inbox. È volutamente in fondo alla review.

Contiene solo:

- rischi nevralgici Spring;
- Spring Maturity Score;
- Spring Layer Map;
- Production Readiness;
- roadmap minima;
- Spring Boot Upgrade Path.

Questa pagina è la sintesi finale, non una schermata piena di testo.

### Spring Modernization Checklist

È l’ultimo artefatto operativo. Per ogni item puoi salvare:

- stato: da fare, in corso, completato, ignorato;
- decisione: fix, rischio accettato o ignora;
- owner;
- due date;
- nota di review.

Lo stato resta in `localStorage` con una chiave basata sul fingerprint progetto/report, quindi non si mescola tra progetti diversi.

## Export

La pagina **Spring Exports** raccoglie:

- report JSON completo;
- executive summary Markdown;
- checklist JSON/Markdown con stato e note;
- Mermaid module map;
- OpenRewrite suggestions YAML.

## Principio Spring-centric

Spring Guardian non deve diventare un altro analyzer generico. Ogni pagina deve rispondere a una domanda Spring concreta:

- Sto usando bene Spring MVC/API?
- La security è espressa con Spring Security o con controlli manuali fragili?
- JPA e transazioni sono confinati nel service layer?
- La configurazione Boot è tipizzata e validata?
- L’app è osservabile con Actuator/Micrometer?
- Esiste un piano di modernizzazione Spring chiaro?


## UX low-noise Spring Review

La UI separa tre ruoli:

- **Spring Findings**: problemi reali nel codice con evidenza, impatto e pattern Spring da applicare.
- **Spring Alternatives**: catalogo degli oggetti Spring consigliati, non una copia dei finding.
- **Spring Review Plan**: vista finale sintetica con i rischi nevralgici, layer map, readiness e upgrade path.

La Spring Layer Map mostra prima il flusso atteso:

```text
@RestController -> @Service -> @Repository -> @Entity
```

Nel dialog grafico le frecce rosse indicano refactoring Spring da fare. Se il backend non fornisce abbastanza evidenza classe-classe, la UI lo dichiara e rimanda ai finding dettagliati.

Spring Findings ora include filtri robusti, ricerca su evidenza/file/classi e pulsante per ripristinare tutti i finding quando un filtro nasconde i risultati.

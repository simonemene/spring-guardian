# Spring Guardian UI

Frontend Angular standalone per Spring Guardian.

## Avvio locale

```bash
cd guardian-ui
npm install
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

Non sono inclusi `.npmrc` o `package-lock.json`: `npm install` userà il registry configurato sulla macchina. Il `package-lock.json` è ignorato dal Git del progetto per evitare di committare lock generati con registry aziendali o locali.

Se sei in azienda puoi usare il registry Nexus aziendale; se sei fuori azienda puoi usare `https://registry.npmjs.org/`.

## Brand assets

The UI includes three brand assets:

```text
src/assets/spring-guardian-logo.svg
src/assets/spring-guardian-wordmark.svg
src/assets/favicon.svg
```

The mark uses a friendly guardian mascot with a shield that deflects broken legacy-code fragments. The compact logo is used in the header and favicon; the wordmark is used in the hero section.

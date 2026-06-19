import { CommonModule } from '@angular/common';
import { Component, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SpringGuardianApiService } from './spring-guardian-api.service';
import { ArchitectureReviewReport, AffectedComponent, ArchitectureStyle, FindingGroup, ProjectType, ReleaseTarget, ReportLanguage, Severity } from './spring-guardian.model';

type Tab = 'overview' | 'modules' | 'gates' | 'findings' | 'advisor' | 'production' | 'suggestions' | 'actions' | 'json';
type DecisionLane = 'BLOCKERS' | 'IMPORTANT' | 'IMPROVEMENTS' | 'ADVISOR' | 'INFORMATION';
type UploadMode = 'zip' | 'folder' | 'local';
type TranslationKey = keyof typeof TRANSLATIONS.it;

const TRANSLATIONS = {
  it: {
    eyebrow: 'Scanner architetturale Spring',
    heroTitle: 'Proteggi la tua architettura Spring',
    brandSubtitle: 'Architettura · Prontezza · Modernizzazione',
    heroText: 'Analizza progetti Spring Boot, raggruppa i problemi per area tecnica e mostra in modo chiaro cosa correggere, perché conta e quali classi o file sono coinvolti.',
    precisionMode: 'Modalità precisa Web/Batch',
    precisionModeText: 'Regole più selettive, evidenza reale e nessun finding basato solo su import o esempi generici.',
    scopeOnly: 'Perimetro attivo',
    scopeOnlyText: 'Web/API e Spring Batch. Camel e altri profili restano fuori finché non avranno regole dedicate.',
    evidenceFirst: 'Evidenza prima di tutto',
    evidenceFirstText: 'Ogni card parte da file, riga e snippet realmente trovati nel progetto analizzato.',
    impactVisualTitle: 'Mappa impatto',
    impactVisualText: 'Distribuzione immediata delle severità per capire dove intervenire per primo.',
    projectIdentity: 'Identità scansione',
    highConfidence: 'Alta confidenza',
    language: 'Lingua report',
    italian: 'Italiano',
    english: 'Inglese',
    uploadZip: 'Carica ZIP',
    uploadFolder: 'Carica cartella',
    backendPath: 'Percorso backend',
    zipProjectFile: 'File ZIP del progetto',
    selected: 'Selezionato',
    selectedFemale: 'Selezionata',
    zipHelp: 'Usa questa modalità quando vuoi caricare un progetto compresso.',
    projectRootFolder: 'Cartella root del progetto',
    folderHelp: 'Seleziona la cartella principale del progetto, ad esempio quella che contiene il pom.xml.',
    backendFolderPath: 'Percorso cartella sul backend',
    backendPathHelp: 'Serve quando backend e progetto sono sulla stessa macchina o quando Docker monta una cartella sotto /scan.',
    scanSettings: 'Analisi automatica',
    projectType: 'Tipo rilevato',
    releaseTarget: 'Prontezza rilascio',
    knownIssues: 'Baseline legacy rilevata o dichiarata',
    profileHelp: 'Spring Guardian rileva automaticamente i moduli Spring dal POM e dal codice. Non devi scegliere il tipo progetto: carica il progetto e avvia la review.',
    automaticProfileTitle: 'Analisi automatica Spring',
    automaticProfileText: 'Carica il progetto: Spring Guardian rileva Web, Security, Batch, JPA, JDBC, Actuator, Validation e OpenAPI prima di applicare le regole.',
    analysisStepsTitle: 'Cosa succede dopo il click',
    analysisStepModules: '1. Rilevo i moduli Spring realmente presenti.',
    analysisStepRules: '2. Applico solo le regole compatibili con quei moduli.',
    analysisStepAlternatives: '3. Evidenzio alternative Spring concrete al codice manuale.',
    analysisStepProduction: '4. Controllo prontezza produzione, azioni e JSON tecnico.',
    noManualProfile: 'Nessun profilo manuale: Web, Batch e Production readiness vengono riconosciuti dalla scansione.',
    webApi: 'API Web / REST',
    batch: 'Batch',
    library: 'Libreria / starter',
    autoDetected: 'Rilevata automaticamente',
    production: 'Produzione',
    internal: 'Test / QA',
    legacyBaseline: 'Baseline legacy',
    scanRunning: 'Scansione in corso…',
    startScan: 'Avvia scansione',
    selectZipError: 'Seleziona un file ZIP da analizzare.',
    selectFolderError: 'Seleziona la cartella root del progetto da analizzare.',
    selectPathError: 'Inserisci il percorso di una cartella visibile dal backend.',
    scanError: 'Errore durante la scansione.',
    architectureScore: 'Punteggio architetturale',
    findings: 'problemi',
    rulesExecuted: 'Regole eseguite',
    javaFiles: 'file Java',
    pomFiles: 'POM',
    overview: 'Riepilogo',
    springModules: 'Moduli Spring',
    springModulesSubtitle: 'Capability rilevate dal POM e dal codice: il report applica le regole solo dove il modulo ha senso.',
    productionRules: 'Regole produzione',
    productionRulesSubtitle: 'Rischi di rilascio: configurazione, segreti, Actuator, Maven, profili e prontezza operativa.',
    suggestionsToVerify: 'Consigli da verificare',
    suggestionsSubtitle: 'Note a bassa confidenza o a basso impatto: utili per revisione, ma non trattarle come blocchi senza conferma.',
    capabilityDetected: 'Rilevato',
    capabilityMissing: 'Non rilevato',
    capabilityRecommended: 'Consigliato',
    confidence: 'Confidenza',
    highConfidenceLabel: 'Alta',
    mediumConfidenceLabel: 'Media',
    verifyConfidenceLabel: 'Da verificare',
    springArchitecture: 'Architettura Spring',
    gates: 'Controlli qualità',
    problems: 'Problemi',
    springAdvisor: 'Alternative Spring',
    advisorSubtitle: 'Uso manuale di API Java, componenti di basso livello e alternative Spring più integrate da valutare.',
    advisorOpportunities: 'suggerimenti',
    advisorEmptyTitle: 'Nessuna alternativa Spring rilevata',
    advisorEmptyText: 'Questa scansione non ha rilevato API Java manuali o componenti sostituibili con alternative Spring più integrate.',
    actions: 'Azioni',
    technicalJson: 'JSON tecnico',
    executiveSummary: 'Riepilogo esecutivo',
    releaseReadiness: 'Prontezza al rilascio',
    blockers: 'Blocchi',
    warnings: 'Avvisi',
    noBlockers: 'Nessun blocco rilevato per il profilo selezionato.',
    noWarnings: 'Nessuna avvertenza rilevata.',
    project: 'Progetto',
    scan: 'Scansione',
    scannedRootPath: 'Percorso analizzato',
    requestedSource: 'Sorgente richiesta',
    detectedStack: 'Stack rilevato',
    detectedStyles: 'Stile rilevato',
    selectedProfile: 'Rilevamento automatico',
    impactedAreas: 'Aree architetturali',
    howToRead: 'Come leggere il report',
    gateStatus: 'Stato gate',
    failingFindings: 'problemi bloccanti o rilevanti',
    searchPlaceholder: 'Cerca per codice, classe, file o testo...',
    allSeverities: 'Tutte le severità',
    allAreas: 'Tutte le aree',
    allTypes: 'Tutti i tipi',
    clearFilters: 'Rimuovi filtri',
    problemAreasTitle: 'Aree dei problemi',
    focusedLane: 'Filtro decisionale attivo',
    showingFirstComponents: 'Mostro le prime occorrenze rilevanti. Il JSON tecnico contiene l’elenco completo.',
    moreComponents: 'altre occorrenze nel JSON tecnico',
    currentFinding: 'Esempio generico non rilevato nel progetto',
    expectedImplementation: 'Esempio generico di soluzione',
    findingType: 'Tipo problema',
    typeCode: 'Codice Java',
    typePom: 'POM Maven',
    typeDependencies: 'Dipendenze',
    typeConfiguration: 'Configurazione',
    typeTest: 'Test',
    typeSecurity: 'Sicurezza',
    typeJpa: 'JPA e persistenza',
    typeWebLayer: 'Layer web/API',
    typeDependencyInjection: 'Iniezione delle dipendenze',
    typeRuntimeCode: 'Codice runtime',
    typeSpringAlternative: 'Spring Alternative Advisor',
    typeSpringBatch: 'Spring Batch',
    typeArchitecture: 'Architettura e confini',
    typeCloudReadiness: 'Prontezza cloud',
    typeObservability: 'Osservabilità',
    technicalCode: 'Codice regola',
    whyItMatters: 'Perché conta',
    recommendedFix: 'Soluzione consigliata',
    detectedProblem: 'Cosa ho rilevato',
    riskImpact: 'Cosa può comportare',
    springAlternativeToUse: 'Alternativa Spring da usare',
    officialDocs: 'Documentazione ufficiale',
    beforeExample: 'Esempio generico prima',
    afterExample: 'Esempio generico dopo',
    currentCode: 'Codice realmente rilevato',
    realEvidence: 'Evidenza reale nel progetto',
    examplesHidden: 'Gli esempi generici non vengono mostrati nella card principale: usa file, riga e snippet reali per verificare il finding.',
    solutionPattern: 'Come dovrebbe essere fatto',
    advisorArea: 'Area advisor',
    involvedComponents: 'Classi e file coinvolti',
    technicalEvidence: 'Evidenza rilevata',
    recommendedActions: 'Azioni consigliate',
    priorities: 'priorità',
    exportJson: 'Esporta JSON',
    jsonNote: 'Questa sezione è pensata per sviluppatori e pipeline CI. Per una lettura funzionale usa Riepilogo, Controlli qualità, Problemi e Azioni.',
    emptyTitle: 'Nessuna scansione eseguita',
    emptyText: 'Carica uno ZIP, seleziona la cartella root del progetto oppure indica un percorso leggibile dal backend.',
    critical: 'Critico',
    major: 'Alto',
    minor: 'Medio',
    info: 'Info',
    riskHigh: 'Rischio alto',
    riskMedium: 'Rischio medio',
    riskLow: 'Rischio basso',
    riskHealthy: 'Buono',
    actionRequired: 'Intervento richiesto',
    improvementRequired: 'Miglioramento richiesto',
    noFindings: 'Nessun problema rilevato',
    reviewRecommended: 'Revisione consigliata',
    projectComponent: 'Progetto',
    javaClass: 'Classe Java',
    testClass: 'Classe di test',
    mavenPom: 'POM Maven',
    configFile: 'File configurazione',
    file: 'File',
    occurrence: 'occorrenza',
    occurrences: 'occorrenze',
    selectedFolderFallback: 'cartella selezionata',
    yes: 'Sì',
    no: 'No',
    pass: 'Ok',
    fail: 'Fallito',
    warning: 'Attenzione',
    ok: 'Ok',
    review: 'Revisione',
    blocked: 'Bloccato',
    attention: 'Attenzione',
    decisionBoard: 'Priorità di intervento',
    decisionBoardText: 'Il report è ordinato per decisione: prima i blocchi di rilascio, poi i problemi importanti, il debito tecnico, gli advisor Spring e infine le note informative.',
    blockersLane: 'Blocchi produzione',
    blockersLaneText: 'Critici: correggili prima di considerare il rilascio.',
    importantLane: 'Da correggere prima del rilascio',
    importantLaneText: 'Alti: rischi concreti per sicurezza, architettura o manutenzione.',
    improvementsLane: 'Debito tecnico rilevante',
    improvementsLaneText: 'Medi: miglioramenti da pianificare nel backlog tecnico.',
    advisorLane: 'Suggerimenti Spring',
    advisorLaneText: 'Advisor: alternative Spring e modernizzazione, non blocchi immediati.',
    informationLane: 'Note informative',
    informationLaneText: 'Info: opportunità e best practice a basso impatto.',
    openLane: 'Apri',
    logoAlt: 'Logo Spring Guardian'
  },
  en: {
    eyebrow: 'Spring architecture scanner',
    heroTitle: 'Protect your Spring architecture',
    brandSubtitle: 'Architecture · Readiness · Modernization',
    heroText: 'Analyze Spring Boot projects, group findings by technical area and clearly show what should be fixed, why it matters and which classes or files are involved.',
    precisionMode: 'Precise Web/Batch mode',
    precisionModeText: 'More selective rules, real evidence and no findings based only on imports or generic examples.',
    scopeOnly: 'Active scope',
    scopeOnlyText: 'Web/API and Spring Batch. Camel and other profiles stay out until dedicated rules are available.',
    evidenceFirst: 'Evidence first',
    evidenceFirstText: 'Every card starts from file, line and snippet actually found in the analyzed project.',
    impactVisualTitle: 'Impact map',
    impactVisualText: 'Immediate severity distribution to understand where to act first.',
    projectIdentity: 'Scan identity',
    highConfidence: 'High confidence',
    language: 'Report language',
    italian: 'Italian',
    english: 'English',
    uploadZip: 'Upload ZIP',
    uploadFolder: 'Upload folder',
    backendPath: 'Backend path',
    zipProjectFile: 'Project ZIP file',
    selected: 'Selected',
    selectedFemale: 'Selected',
    zipHelp: 'Use this mode when you want to upload a compressed project.',
    projectRootFolder: 'Project root folder',
    folderHelp: 'Select the project root folder, for example the one containing pom.xml.',
    backendFolderPath: 'Backend folder path',
    backendPathHelp: 'Use this when the backend and the project are on the same machine or when Docker mounts a folder under /scan.',
    scanSettings: 'Automatic analysis',
    projectType: 'Detected type',
    releaseTarget: 'Release readiness',
    knownIssues: 'This is a legacy project with known issues',
    profileHelp: 'Spring Guardian detects Spring modules automatically from the POM and source code. You do not need to choose a project type: load the project and start the review.',
    automaticProfileTitle: 'Automatic Spring analysis',
    automaticProfileText: 'Load the project: Spring Guardian detects Web, Security, Batch, JPA, JDBC, Actuator, Validation and OpenAPI before applying rules.',
    analysisStepsTitle: 'What happens after scan',
    analysisStepModules: '1. Detect the Spring modules actually present.',
    analysisStepRules: '2. Apply only rules that match those modules.',
    analysisStepAlternatives: '3. Highlight concrete Spring alternatives to manual code.',
    analysisStepProduction: '4. Check production readiness, actions and technical JSON.',
    noManualProfile: 'No manual profile: Web, Batch and production readiness are inferred from the scan.',
    webApi: 'Web / REST API',
    batch: 'Batch',
    library: 'Library / starter',
    autoDetected: 'Automatically detected',
    production: 'Production',
    internal: 'Test / QA',
    legacyBaseline: 'Legacy baseline',
    scanRunning: 'Scan running...',
    startScan: 'Start scan',
    selectZipError: 'Select a ZIP file to analyze.',
    selectFolderError: 'Select the project root folder to analyze.',
    selectPathError: 'Enter a folder path visible from the backend.',
    scanError: 'Error during scan.',
    architectureScore: 'Architecture score',
    findings: 'findings',
    rulesExecuted: 'Executed rules',
    javaFiles: 'Java files',
    pomFiles: 'POM files',
    overview: 'Overview',
    springModules: 'Spring Modules',
    springModulesSubtitle: 'Capabilities detected from the POM and code: the report applies rules only where the module makes sense.',
    productionRules: 'Production Rules',
    productionRulesSubtitle: 'Release risks: configuration, secrets, Actuator, Maven, profiles and operational readiness.',
    suggestionsToVerify: 'Suggestions to Verify',
    suggestionsSubtitle: 'Low-confidence or low-impact notes: useful for review, but do not treat them as blockers without confirmation.',
    capabilityDetected: 'Detected',
    capabilityMissing: 'Not detected',
    capabilityRecommended: 'Recommended',
    confidence: 'Confidence',
    highConfidenceLabel: 'High',
    mediumConfidenceLabel: 'Medium',
    verifyConfidenceLabel: 'To verify',
    springArchitecture: 'Spring Architecture',
    gates: 'Quality gates',
    problems: 'Findings',
    springAdvisor: 'Spring Alternatives',
    advisorSubtitle: 'Manual Java objects, low-level APIs and modern Spring alternatives worth evaluating.',
    advisorOpportunities: 'suggestions',
    advisorEmptyTitle: 'No Spring alternative detected',
    advisorEmptyText: 'This scan did not find manual Java objects or APIs that should be replaced by more integrated Spring alternatives.',
    actions: 'Actions',
    technicalJson: 'Technical JSON',
    executiveSummary: 'Executive summary',
    releaseReadiness: 'Release readiness',
    blockers: 'Blockers',
    warnings: 'Warnings',
    noBlockers: 'No blocker detected for the selected profile.',
    noWarnings: 'No warning detected.',
    project: 'Project',
    scan: 'Scan',
    scannedRootPath: 'Scanned root path',
    requestedSource: 'Requested source',
    detectedStack: 'Detected stack',
    detectedStyles: 'Detected style',
    selectedProfile: 'Automatic detection',
    impactedAreas: 'Architecture areas',
    howToRead: 'How to read the report',
    gateStatus: 'Gate status',
    failingFindings: 'blocking or relevant findings',
    searchPlaceholder: 'Search by code, class, file or text...',
    allSeverities: 'All severities',
    allAreas: 'All areas',
    allTypes: 'All types',
    clearFilters: 'Clear filters',
    problemAreasTitle: 'Finding areas',
    focusedLane: 'Active decision filter',
    showingFirstComponents: 'Showing the first relevant occurrences. The technical JSON contains the full list.',
    moreComponents: 'more occurrences in the technical JSON',
    currentFinding: 'Generic example not detected in the project',
    expectedImplementation: 'Generic recommended solution example',
    findingType: 'Finding type',
    typeCode: 'Java code',
    typePom: 'Maven POM',
    typeDependencies: 'Dependencies',
    typeConfiguration: 'Configuration',
    typeTest: 'Tests',
    typeSecurity: 'Security',
    typeJpa: 'JPA and persistence',
    typeWebLayer: 'Web/API layer',
    typeDependencyInjection: 'Dependency injection',
    typeRuntimeCode: 'Runtime code',
    typeSpringAlternative: 'Spring Alternative Advisor',
    typeSpringBatch: 'Spring Batch',
    typeArchitecture: 'Architecture and boundaries',
    typeCloudReadiness: 'Cloud readiness',
    typeObservability: 'Observability',
    technicalCode: 'Technical code',
    whyItMatters: 'Why it matters',
    recommendedFix: 'Recommended solution',
    detectedProblem: 'What was detected',
    riskImpact: 'Possible impact',
    springAlternativeToUse: 'Spring alternative to use',
    officialDocs: 'Official documentation',
    beforeExample: 'Generic before example',
    afterExample: 'Generic after example',
    currentCode: 'Actually detected code',
    realEvidence: 'Real evidence in the project',
    examplesHidden: 'Generic examples are not shown in the main card: use file, line and real snippets to verify the finding.',
    solutionPattern: 'Expected implementation',
    advisorArea: 'Advisor area',
    involvedComponents: 'Involved classes and files',
    technicalEvidence: 'Technical evidence',
    recommendedActions: 'Recommended actions',
    priorities: 'priorities',
    exportJson: 'Export JSON',
    jsonNote: 'This section is meant for developers and CI pipelines. Functional reading is available in Overview, Quality gates, Findings and Actions.',
    emptyTitle: 'No scan executed',
    emptyText: 'Upload a ZIP, select the project root folder or enter a backend-readable path.',
    critical: 'Critical',
    major: 'High',
    minor: 'Medium',
    info: 'Info',
    riskHigh: 'High risk',
    riskMedium: 'Medium risk',
    riskLow: 'Low risk',
    riskHealthy: 'Healthy',
    actionRequired: 'Action required',
    improvementRequired: 'Improvement required',
    noFindings: 'No findings detected',
    reviewRecommended: 'Review recommended',
    projectComponent: 'Project',
    javaClass: 'Java class',
    testClass: 'Test class',
    mavenPom: 'Maven POM',
    configFile: 'Configuration file',
    file: 'File',
    occurrence: 'occurrence',
    occurrences: 'occurrences',
    selectedFolderFallback: 'selected folder',
    yes: 'Yes',
    no: 'No',
    pass: 'Pass',
    fail: 'Fail',
    warning: 'Warning',
    ok: 'Ok',
    review: 'Review',
    blocked: 'Blocked',
    attention: 'Attention',
    decisionBoard: 'Fix priority board',
    decisionBoardText: 'The report is organized by decision impact: release blockers first, then important issues, technical debt, Spring advisors and informational notes.',
    blockersLane: 'Production blockers',
    blockersLaneText: 'Critical findings: fix these before considering the release.',
    importantLane: 'Fix before release',
    importantLaneText: 'High findings: concrete security, architecture or maintenance risks.',
    improvementsLane: 'Relevant technical debt',
    improvementsLaneText: 'Medium findings: plan these in the technical backlog.',
    advisorLane: 'Spring suggestions',
    advisorLaneText: 'Advisor findings: Spring alternatives and modernization, not immediate blockers.',
    informationLane: 'Informational notes',
    informationLaneText: 'Info findings: low-impact opportunities and best practices.',
    openLane: 'Open',
    logoAlt: 'Spring Guardian logo'
  }
} as const;

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  readonly severityOrder: Severity[] = ['CRITICAL', 'MAJOR', 'MINOR', 'INFO'];

  readonly report = signal<ArchitectureReviewReport | null>(null);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  readonly selectedFile = signal<File | null>(null);
  readonly selectedFolderFiles = signal<File[]>([]);
  readonly selectedFolderName = signal<string | null>(null);
  readonly uploadMode = signal<UploadMode>('zip');
  readonly activeTab = signal<Tab>('overview');
  readonly activeDecisionLane = signal<DecisionLane | null>(null);
  readonly selectedLanguage = signal<ReportLanguage>('it');
  readonly filterVersion = signal(0);
  readonly currentScanSource = signal<string | null>(null);

  private scanSequence = 0;

  localPath = '';
  search = '';
  severityFilter: Severity | 'ALL' = 'ALL';
  categoryFilter = 'ALL';
  typeFilter = 'ALL';

  readonly categories = computed(() => {
    const current = this.report();
    if (!current) {
      return [];
    }
    return [...new Set(current.findings.map((finding) => finding.category))].sort();
  });

  readonly findingTypes = computed(() => {
    const current = this.report();
    if (!current) {
      return [];
    }
    return [...new Set(current.findings.map((finding) => this.findingType(finding)))].sort((left, right) => this.findingTypeLabel(left).localeCompare(this.findingTypeLabel(right)));
  });

  readonly springAdvisorFindings = computed(() => {
    const current = this.report();
    if (!current) {
      return [];
    }
    return current.findings.filter((finding) => this.isSpringAdvisorFinding(finding));
  });

  readonly advisorGroups = computed(() => {
    const groups = new Map<string, FindingGroup[]>();
    for (const finding of this.springAdvisorFindings()) {
      const area = this.advisorArea(finding);
      groups.set(area, [...(groups.get(area) ?? []), finding]);
    }
    return Array.from(groups.entries())
      .map(([area, findings]) => ({ area, findings, occurrences: findings.reduce((total, finding) => total + finding.occurrences, 0) }))
      .sort((left, right) => left.area.localeCompare(right.area));
  });

  readonly moduleCards = computed(() => {
    const current = this.report();
    if (!current) {
      return [];
    }
    const c = current.capabilities;
    const it = this.selectedLanguage() === 'it';
    return [
      this.moduleCard('Spring Web/API', c.usesSpringWeb, c.usesSpringWeb ? 'spring-boot-starter-web / @RestController' : it ? 'Nessun segnale Web MVC/WebFlux' : 'No Web MVC/WebFlux signal', c.usesSpringWeb ? it ? 'Regole Web/API attive: controller, DTO, validazione, error handling e OpenAPI.' : 'Web/API rules active: controllers, DTOs, validation, error handling and OpenAPI.' : it ? 'Le regole Web restano inattive se il modulo non è presente.' : 'Web rules stay inactive when the module is not present.'),
      this.moduleCard('Spring Security', c.usesSpringSecurity, c.usesSpringSecurity ? 'spring-boot-starter-security / SecurityFilterChain' : it ? 'Nessun segnale Spring Security' : 'No Spring Security signal', c.usesSpringSecurity ? it ? 'Regole Security attive: matcher, chain, CSRF, header e configurazione.' : 'Security rules active: matchers, chains, CSRF, headers and configuration.' : it ? 'Le regole Security non vengono applicate senza segnali reali.' : 'Security rules are not applied without real signals.'),
      this.moduleCard('Spring Batch', c.usesSpringBatch, c.usesSpringBatch ? 'spring-batch-core / @EnableBatchProcessing' : it ? 'Nessun segnale Spring Batch' : 'No Spring Batch signal', c.usesSpringBatch ? it ? 'Regole Batch attive: job, step, reader, writer, retry, skip e restartability.' : 'Batch rules active: jobs, steps, readers, writers, retry, skip and restartability.' : it ? 'Le regole Batch restano inattive se il progetto non è Batch.' : 'Batch rules stay inactive when the project is not Batch.'),
      this.moduleCard('Spring Data JPA', c.usesJpa, c.usesJpa ? 'spring-boot-starter-data-jpa / @Entity' : it ? 'Nessun segnale JPA' : 'No JPA signal', c.usesJpa ? it ? 'Regole persistenza abilitate dove applicabili.' : 'Persistence rules enabled where applicable.' : it ? 'I controlli JPA restano inattivi.' : 'JPA checks stay inactive.'),
      this.moduleCard('Actuator', c.usesActuator, c.usesActuator ? 'spring-boot-starter-actuator' : it ? 'Actuator non rilevato' : 'Actuator not detected', c.usesActuator ? it ? 'Health, info, metriche e readiness possono essere valutate.' : 'Health, info, metrics and readiness can be reviewed.' : it ? 'Consigliato per prontezza produzione e osservabilità.' : 'Recommended for production readiness and observability.'),
      this.moduleCard('Bean Validation', c.usesValidation, c.usesValidation ? 'spring-boot-starter-validation / @Valid' : it ? 'Validation non rilevata' : 'Validation not detected', c.usesValidation ? it ? 'La validazione degli input può essere valutata.' : 'Input validation can be reviewed.' : it ? 'Consigliata per DTO e request body Web/API.' : 'Recommended for Web/API DTOs and request bodies.'),
      this.moduleCard('OpenAPI', c.usesOpenApi, c.usesOpenApi ? 'springdoc-openapi / @Operation' : it ? 'OpenAPI non rilevato' : 'OpenAPI not detected', c.usesOpenApi ? it ? 'La documentazione API può essere valutata.' : 'API documentation can be reviewed.' : it ? 'Consigliato per API pubbliche o interne governate.' : 'Recommended for public or governed internal APIs.')
    ];
  });

  readonly productionFindings = computed(() => {
    const current = this.report();
    if (!current) {
      return [];
    }
    return current.findings.filter((finding) => this.isProductionFinding(finding));
  });

  readonly suggestionFindings = computed(() => {
    const current = this.report();
    if (!current) {
      return [];
    }
    return current.findings.filter((finding) => this.isSuggestionFinding(finding));
  });

  readonly problemTypeSummaries = computed(() => {
    const current = this.report();
    if (!current) {
      return [];
    }
    const groups = new Map<string, FindingGroup[]>();
    for (const finding of current.findings.filter((item) => !this.isSpringAdvisorFinding(item))) {
      const type = this.findingType(finding);
      groups.set(type, [...(groups.get(type) ?? []), finding]);
    }
    return Array.from(groups.entries())
      .map(([type, findings]) => ({
        type,
        label: this.findingTypeLabel(type),
        findings: findings.length,
        occurrences: findings.reduce((total, finding) => total + finding.occurrences, 0)
      }))
      .sort((left, right) => right.occurrences - left.occurrences);
  });

  readonly problemGroups = computed(() => {
    const groups = new Map<string, FindingGroup[]>();
    for (const finding of this.filteredFindings()) {
      if (this.isSpringAdvisorFinding(finding)) {
        continue;
      }
      const type = this.findingType(finding);
      groups.set(type, [...(groups.get(type) ?? []), finding]);
    }
    return Array.from(groups.entries())
      .map(([type, findings]) => ({
        type,
        label: this.findingTypeLabel(type),
        occurrences: findings.reduce((total, finding) => total + finding.occurrences, 0),
        findings
      }))
      .sort((left, right) => this.problemTypeRank(left.type) - this.problemTypeRank(right.type) || left.label.localeCompare(right.label));
  });


  readonly decisionLanes = computed(() => {
    const current = this.report();
    if (!current) {
      return [];
    }
    return [
      { lane: 'BLOCKERS' as DecisionLane, title: this.t('blockersLane'), text: this.t('blockersLaneText'), count: this.decisionCount(current, 'BLOCKERS') },
      { lane: 'IMPORTANT' as DecisionLane, title: this.t('importantLane'), text: this.t('importantLaneText'), count: this.decisionCount(current, 'IMPORTANT') },
      { lane: 'IMPROVEMENTS' as DecisionLane, title: this.t('improvementsLane'), text: this.t('improvementsLaneText'), count: this.decisionCount(current, 'IMPROVEMENTS') },
      { lane: 'ADVISOR' as DecisionLane, title: this.t('advisorLane'), text: this.t('advisorLaneText'), count: this.decisionCount(current, 'ADVISOR') },
      { lane: 'INFORMATION' as DecisionLane, title: this.t('informationLane'), text: this.t('informationLaneText'), count: this.decisionCount(current, 'INFORMATION') }
    ];
  });

  readonly filteredFindings = computed(() => {
    const current = this.report();
    if (!current) {
      return [];
    }

    this.filterVersion();
    const term = this.search.trim().toLowerCase();

    return current.findings.filter((finding) => {
      const activeLane = this.activeDecisionLane();
      const advisor = this.isSpringAdvisorFinding(finding);
      const includeAdvisor = activeLane === 'ADVISOR' || this.activeTab() === 'advisor';
      if (advisor && !includeAdvisor) {
        return false;
      }
      const matchesLane = activeLane === null || this.inDecisionLane(finding, activeLane);
      const matchesSeverity = this.severityFilter === 'ALL' || finding.severity === this.severityFilter;
      const matchesCategory = this.categoryFilter === 'ALL' || finding.category === this.categoryFilter;
      const matchesType = this.typeFilter === 'ALL' || this.findingType(finding) === this.typeFilter;
      const searchable = [
        finding.ruleId,
        this.ruleCode(finding.ruleId),
        finding.title,
        finding.category,
        finding.whyItMatters,
        finding.suggestedFix,
        ...finding.affectedComponents.flatMap((component) => [
          component.name,
          component.filePath,
          component.evidence,
          component.codeSnippet
        ])
      ].join(' ').toLowerCase();

      return matchesLane && matchesSeverity && matchesCategory && matchesType && (!term || searchable.includes(term));
    });
  });


  readonly totalFindingOccurrences = computed(() => {
    const current = this.report();
    if (!current) {
      return 0;
    }
    return current.findings.reduce((total, finding) => total + finding.occurrences, 0);
  });

  readonly rawJson = computed(() => JSON.stringify(this.report(), null, 2));

  constructor(private readonly api: SpringGuardianApiService) {}

  t(key: TranslationKey): string {
    return TRANSLATIONS[this.selectedLanguage()][key];
  }

  selectLanguage(language: ReportLanguage): void {
    this.selectedLanguage.set(language);
    this.error.set(null);
    this.report.set(null);
    this.resetFilters();
  }

  selectMode(mode: UploadMode): void {
    this.uploadMode.set(mode);
    this.error.set(null);
  }

  selectTab(tab: Tab): void {
    this.activeTab.set(tab);
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedFile.set(input.files?.[0] ?? null);
    this.error.set(null);
  }

  onFolderSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const files = Array.from(input.files ?? []);
    this.selectedFolderFiles.set(files);
    this.selectedFolderName.set(this.resolveFolderName(files));
    this.error.set(null);
  }

  scan(): void {
    this.error.set(null);
    const language = this.selectedLanguage();
    if (this.uploadMode() === 'zip') {
      const file = this.selectedFile();
      if (!file) {
        this.error.set(this.t('selectZipError'));
        return;
      }
      this.currentScanSource.set(file.name);
      this.executeScan(this.api.scanZip(file, language));
      return;
    }

    if (this.uploadMode() === 'folder') {
      const files = this.selectedFolderFiles();
      if (files.length === 0) {
        this.error.set(this.t('selectFolderError'));
        return;
      }
      this.currentScanSource.set(this.selectedFolderName() ?? this.t('selectedFolderFallback'));
      this.executeScan(this.api.scanFolder(files, language));
      return;
    }

    const path = this.localPath.trim();
    if (!path) {
      this.error.set(this.t('selectPathError'));
      return;
    }
    this.currentScanSource.set(path);
    this.executeScan(this.api.scanLocalPath(path, language));
  }

  resetFilters(): void {
    this.search = '';
    this.severityFilter = 'ALL';
    this.categoryFilter = 'ALL';
    this.typeFilter = 'ALL';
    this.activeDecisionLane.set(null);
    this.touchFilters();
  }

  touchFilters(): void {
    this.filterVersion.update((version) => version + 1);
  }


  severityPercent(report: ArchitectureReviewReport, severity: Severity): number {
    const total = report.findings.reduce((sum, finding) => sum + finding.occurrences, 0);
    if (total === 0) {
      return 0;
    }
    return Math.round((this.severityCount(severity) / total) * 100);
  }

  severityTone(severity: Severity): string {
    return severity.toLowerCase();
  }

  shortPath(value: string | null | undefined): string {
    if (!value) {
      return '-';
    }
    const normalized = value.replace(/\\/g, '/');
    const parts = normalized.split('/').filter(Boolean);
    return parts.slice(-3).join('/');
  }

  decisionCount(report: ArchitectureReviewReport, lane: DecisionLane): number {
    return report.findings
      .filter((finding) => this.inDecisionLane(finding, lane))
      .reduce((total, finding) => total + finding.occurrences, 0);
  }

  focusDecisionLane(lane: DecisionLane): void {
    this.resetFilters();
    this.activeDecisionLane.set(lane);
    if (lane === 'ADVISOR') {
      this.activeTab.set('advisor');
      this.touchFilters();
      return;
    }
    this.activeTab.set('findings');
    if (lane === 'BLOCKERS') {
      this.severityFilter = 'CRITICAL';
      this.touchFilters();
      return;
    }
    if (lane === 'IMPORTANT') {
      this.severityFilter = 'MAJOR';
      this.touchFilters();
      return;
    }
    if (lane === 'IMPROVEMENTS') {
      this.severityFilter = 'MINOR';
      this.touchFilters();
      return;
    }
    this.severityFilter = 'INFO';
    this.touchFilters();
  }

  private inDecisionLane(finding: FindingGroup, lane: DecisionLane): boolean {
    const advisor = this.isSpringAdvisorFinding(finding);
    if (lane === 'ADVISOR') {
      return advisor;
    }
    if (advisor) {
      return false;
    }
    if (lane === 'BLOCKERS') {
      return finding.severity === 'CRITICAL';
    }
    if (lane === 'IMPORTANT') {
      return finding.severity === 'MAJOR';
    }
    if (lane === 'IMPROVEMENTS') {
      return finding.severity === 'MINOR';
    }
    return finding.severity === 'INFO';
  }

  setTypeFilter(type: string): void {
    this.search = '';
    this.severityFilter = 'ALL';
    this.categoryFilter = 'ALL';
    this.typeFilter = type;
    this.activeDecisionLane.set(null);
    this.activeTab.set('findings');
    this.touchFilters();
  }

  firstComponent(finding: FindingGroup): AffectedComponent | null {
    return finding.affectedComponents.length > 0 ? finding.affectedComponents[0] : null;
  }

  visibleComponents(finding: FindingGroup): AffectedComponent[] {
    return finding.affectedComponents.slice(0, 12);
  }

  remainingComponents(finding: FindingGroup): number {
    return Math.max(0, finding.affectedComponents.length - 12);
  }

  private problemTypeRank(type: string): number {
    return ({
      SECURITY: 1,
      WEB_LAYER: 2,
      ARCHITECTURE: 3,
      JPA: 4,
      SPRING_BATCH: 5,
      CLOUD_READINESS: 6,
      OBSERVABILITY: 7,
      POM: 8,
      DEPENDENCIES: 9,
      DEPENDENCY_INJECTION: 10,
      RUNTIME_CODE: 11,
      CONFIGURATION: 12,
      TEST: 13,
      CODE: 14
    } as Record<string, number>)[type] ?? 99;
  }

  severityCount(severity: Severity): number {
    const current = this.report();
    return Number(current?.findingsBySeverity?.[severity] ?? 0);
  }

  scoreClass(score: number): string {
    if (score >= 85) return 'score-good';
    if (score >= 65) return 'score-warning';
    return 'score-danger';
  }

  exportJson(): void {
    const current = this.report();
    if (!current) {
      return;
    }
    const blob = new Blob([JSON.stringify(current, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const anchor = document.createElement('a');
    anchor.href = url;
    anchor.download = `spring-guardian-${current.projectName || 'report'}.json`;
    anchor.click();
    URL.revokeObjectURL(url);
  }

  trackFinding(_: number, finding: FindingGroup): string {
    return finding.ruleId;
  }

  severityLabel(severity: Severity | string): string {
    return ({
      CRITICAL: this.t('critical'),
      MAJOR: this.t('major'),
      MINOR: this.t('minor'),
      INFO: this.t('info')
    } as Record<string, string>)[severity] ?? this.humanize(severity);
  }

  findingType(finding: FindingGroup): string {
    if (finding.findingType) {
      return finding.findingType;
    }
    if (this.isSpringAdvisorFinding(finding)) {
      return 'SPRING_ALTERNATIVE';
    }
    if (finding.affectedComponents.some((component) => component.type === 'MAVEN_POM')) {
      return 'POM';
    }
    if (finding.affectedComponents.some((component) => component.type === 'CONFIG_FILE')) {
      return 'CONFIGURATION';
    }
    if (finding.affectedComponents.some((component) => component.type === 'TEST_CLASS')) {
      return 'TEST';
    }
    return 'CODE';
  }

  findingTypeLabel(type: string): string {
    return ({
      CODE: this.t('typeCode'),
      POM: this.t('typePom'),
      DEPENDENCIES: this.t('typeDependencies'),
      CONFIGURATION: this.t('typeConfiguration'),
      TEST: this.t('typeTest'),
      SECURITY: this.t('typeSecurity'),
      JPA: this.t('typeJpa'),
      WEB_LAYER: this.t('typeWebLayer'),
      DEPENDENCY_INJECTION: this.t('typeDependencyInjection'),
      RUNTIME_CODE: this.t('typeRuntimeCode'),
      SPRING_ALTERNATIVE: this.t('typeSpringAlternative'),
      SPRING_BATCH: this.t('typeSpringBatch'),
      SPRING_CAPABILITY_GAP: this.t('springModules'),
      ARCHITECTURE: this.t('typeArchitecture'),
      CLOUD_READINESS: this.t('typeCloudReadiness'),
      OBSERVABILITY: this.t('typeObservability')
    } as Record<string, string>)[type] ?? this.humanize(type);
  }

  isSpringAdvisorFinding(finding: FindingGroup): boolean {
    return finding.findingType === 'SPRING_ALTERNATIVE' || finding.category === 'Spring Alternative Advisor' || finding.ruleId.startsWith('ADV') || /^SPR(06[4-9]|07[0-9]|08[0-9]|090)/.test(finding.ruleId);
  }

  advisorCount(current: ArchitectureReviewReport): number {
    return current.findings.filter((finding) => this.isSpringAdvisorFinding(finding)).reduce((total, finding) => total + finding.occurrences, 0);
  }

  typeOccurrenceCount(current: ArchitectureReviewReport, type: string): number {
    return current.findings
      .filter((finding) => this.findingType(finding) === type)
      .reduce((total, finding) => total + finding.occurrences, 0);
  }

  riskLabel(riskLevel: string): string {
    return ({
      HIGH: this.t('riskHigh'),
      MEDIUM: this.t('riskMedium'),
      LOW: this.t('riskLow'),
      HEALTHY: this.t('riskHealthy')
    } as Record<string, string>)[riskLevel] ?? this.humanize(riskLevel);
  }

  statusLabel(status: string): string {
    return ({
      INTERVENTO_RICHIESTO: this.t('actionRequired'),
      MIGLIORAMENTO_RICHIESTO: this.t('improvementRequired'),
      NESSUN_PROBLEMA_RILEVATO: this.t('noFindings'),
      REVISIONE_CONSIGLIATA: this.t('reviewRecommended'),
      ACTION_REQUIRED: this.t('actionRequired'),
      IMPROVEMENT_REQUIRED: this.t('improvementRequired'),
      NO_FINDINGS: this.t('noFindings'),
      REVIEW_RECOMMENDED: this.t('reviewRecommended')
    } as Record<string, string>)[status] ?? this.humanize(status);
  }

  componentTypeLabel(type: string): string {
    return ({
      PROJECT: this.t('projectComponent'),
      JAVA_CLASS: this.t('javaClass'),
      TEST_CLASS: this.t('testClass'),
      MAVEN_POM: this.t('mavenPom'),
      CONFIG_FILE: this.t('configFile'),
      FILE: this.t('file')
    } as Record<string, string>)[type] ?? this.humanize(type);
  }

  gateStatusLabel(status: string): string {
    return ({
      PASS: this.t('pass'),
      FAIL: this.t('fail'),
      WARNING: this.t('warning'),
      OK: this.t('ok'),
      REVIEW: this.t('review'),
      BLOCKED: this.t('blocked'),
      ATTENTION: this.t('attention')
    } as Record<string, string>)[status] ?? this.humanize(status);
  }

  projectTypeLabel(value: ProjectType | string): string {
    return ({
      WEB_API: this.t('webApi'),
      BATCH: this.t('batch'),
      LIBRARY: this.t('library'),
      UNKNOWN: this.t('autoDetected')
    } as Record<string, string>)[value] ?? this.humanize(value);
  }

  architectureStyleLabel(value: ArchitectureStyle | string): string {
    return ({
      AUTO_DETECTED: this.t('autoDetected')
    } as Record<string, string>)[value] ?? this.humanize(value);
  }

  releaseTargetLabel(value: ReleaseTarget | string): string {
    return ({
      PRODUCTION: this.t('production'),
      INTERNAL: this.t('internal'),
      LEGACY_BASELINE: this.t('legacyBaseline')
    } as Record<string, string>)[value] ?? this.humanize(value);
  }

  ruleCode(ruleId: string): string {
    return ruleId.match(/^(SPR|SEC|WEB|BAT|CLD|OBS|POM|ADV|ARCH|CAP)\d+/)?.[0] ?? ruleId;
  }

  occurrenceLabel(count: number): string {
    const label = count === 1 ? this.t('occurrence') : this.t('occurrences');
    return `${count} ${label}`;
  }

  folderFilesLabel(): string {
    const count = this.selectedFolderFiles().length;
    if (count === 0) {
      return '';
    }
    const folder = this.selectedFolderName() ?? this.t('selectedFolderFallback');
    return `${folder} · ${count} ${this.t('file').toLowerCase()}`;
  }

  componentTitle(component: AffectedComponent): string {
    return component.name || component.filePath || this.componentTypeLabel(component.type);
  }

  detectedScopeLabel(current: ArchitectureReviewReport): string {
    const modules = [];
    if (current.capabilities.usesSpringWeb) modules.push('Web/API');
    if (current.capabilities.usesSpringSecurity) modules.push('Security');
    if (current.capabilities.usesSpringBatch) modules.push('Batch');
    if (current.capabilities.usesJpa) modules.push('JPA');
    if (current.capabilities.usesActuator) modules.push('Actuator');
    return modules.length > 0 ? modules.join(' · ') : this.t('autoDetected');
  }

  capabilityItems(current: ArchitectureReviewReport): string[] {
    const capabilities = current.capabilities;
    const values = [
      capabilities.usesSpringWeb ? 'Spring Web' : '',
      capabilities.usesSpringSecurity ? 'Spring Security' : '',
      capabilities.usesJpa ? 'JPA' : '',
      capabilities.usesActuator ? 'Actuator' : '',
      capabilities.usesValidation ? 'Bean Validation' : '',
      capabilities.usesOpenApi ? 'OpenAPI' : '',
      capabilities.usesLombok ? 'Lombok' : '',
      capabilities.usesSpringBatch ? 'Spring Batch' : ''
    ].filter(Boolean);
    return values.length > 0 ? values : [this.t('noFindings')];
  }


  moduleCard(name: string, active: boolean, evidence: string, description: string): { name: string; active: boolean; evidence: string; description: string; status: string } {
    return {
      name,
      active,
      evidence,
      description,
      status: active ? this.t('capabilityDetected') : this.t('capabilityMissing')
    };
  }

  isProductionFinding(finding: FindingGroup): boolean {
    const type = this.findingType(finding);
    return ['CLOUD_READINESS', 'OBSERVABILITY', 'DEPENDENCIES', 'POM', 'CONFIGURATION'].includes(type)
      || finding.ruleId.startsWith('CLD')
      || finding.ruleId.startsWith('OBS')
      || finding.ruleId.startsWith('POM');
  }

  isSuggestionFinding(finding: FindingGroup): boolean {
    if (this.isSpringAdvisorFinding(finding) || this.isProductionFinding(finding)) {
      return false;
    }
    return finding.severity === 'INFO' || finding.findingType === 'CODE';
  }

  confidenceLabel(finding: FindingGroup): string {
    if (finding.ruleId.startsWith('CAP') || this.isSpringAdvisorFinding(finding)) {
      return this.t('highConfidenceLabel');
    }
    if (finding.severity === 'INFO') {
      return this.t('verifyConfidenceLabel');
    }
    return this.t('mediumConfidenceLabel');
  }

  advisorArea(finding: FindingGroup): string {
    const code = this.ruleCode(finding.ruleId);
    const numeric = Number(code.replace(/\D/g, ''));
    const italian = this.selectedLanguage() === 'it';
    if (['ADV003','ADV004','ADV005','ADV006','ADV007','ADV048','ADV084','ADV085','ADV089'].includes(code)) return italian ? 'Client HTTP e integrazioni' : 'HTTP clients and integrations';
    if (['ADV012','ADV013','ADV049','ADV079','ADV080','ADV097','ADV098'].includes(code)) return italian ? 'Configurazione e proprietà' : 'Configuration and properties';
    if (['ADV008','ADV009','ADV010','ADV011','ADV045','ADV046','ADV057','ADV066','ADV067','ADV086','ADV100'].includes(code)) return italian ? 'Thread, asincronia e schedulazione' : 'Threads, async and scheduling';
    if (['ADV001','ADV002','ADV041','ADV062','ADV063','ADV083'].includes(code) || code === 'SPR064') return italian ? 'JSON e serializzazione' : 'JSON and serialization';
    if (['ADV020','ADV021','ADV076'].includes(code)) return italian ? 'Validazione' : 'Validation';
    if (['ADV037','ADV038','ADV073','ADV074','ADV075','ADV095','ADV096'].includes(code)) return italian ? 'Persistenza e database' : 'Persistence and databases';
    if (['ADV016','ADV069','ADV070','ADV087'].includes(code)) return italian ? 'Cache e idempotenza' : 'Caching and idempotency';
    if (['ADV028','ADV029','ADV056','ADV072'].includes(code)) return italian ? 'Eventi e audit' : 'Events and audit';
    if (['ADV026','ADV027','ADV065'].includes(code)) return italian ? 'Osservabilità' : 'Observability';
    if (['ADV036','ADV058','ADV059','ADV061','ADV090'].includes(code)) return italian ? 'Sicurezza e filtri' : 'Security and filters';
    if (['ADV042','ADV043','ADV044','ADV060','ADV064','ADV077','ADV078','ADV091','ADV092'].includes(code)) return italian ? 'Web/API' : 'Web/API';
    if (['ADV030','ADV031','ADV051','ADV052','ADV053','ADV093','ADV094','ADV099'].includes(code)) return italian ? 'Lifecycle e bean' : 'Lifecycle and beans';
    if (['ADV014','ADV015','ADV081','ADV082','ADV071'].includes(code)) return italian ? 'File, CSV e Batch' : 'Files, CSV and Batch';
    if (numeric >= 1 && numeric <= 100) return italian ? 'Modernizzazione Spring' : 'Spring modernization';
    return finding.findingTypeLabel || this.t('typeSpringAlternative');
  }

  private executeScan(request$: Observable<ArchitectureReviewReport>): void {
    const scanToken = ++this.scanSequence;
    this.loading.set(true);
    this.error.set(null);
    this.report.set(null);
    this.activeTab.set('overview');
    this.resetFilters();
    request$.subscribe({
      next: (result) => {
        if (scanToken !== this.scanSequence) {
          return;
        }
        this.report.set(result);
        this.activeTab.set('overview');
        this.loading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        if (scanToken !== this.scanSequence) {
          return;
        }
        const detail = error.error?.detail || error.error?.message || error.message;
        this.error.set(detail || this.t('scanError'));
        this.loading.set(false);
      }
    });
  }

  private resolveFolderName(files: File[]): string | null {
    const first = files[0] as File & { webkitRelativePath?: string };
    const relativePath = first?.webkitRelativePath;
    if (!relativePath) {
      return null;
    }
    return relativePath.split('/')[0] || null;
  }

  private humanize(value: string): string {
    const lower = value.replaceAll('_', ' ').toLowerCase();
    return lower.charAt(0).toUpperCase() + lower.slice(1);
  }
}

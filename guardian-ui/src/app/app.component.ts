import { CommonModule } from '@angular/common';
import { Component, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SpringGuardianApiService } from './spring-guardian-api.service';
import { ArchitectureReviewReport, AffectedComponent, FindingGroup, ReportLanguage, Severity } from './spring-guardian.model';

type Tab = 'overview' | 'findings' | 'actions' | 'json';
type UploadMode = 'zip' | 'folder' | 'local';
type TranslationKey = keyof typeof TRANSLATIONS.it;

const TRANSLATIONS = {
  it: {
    eyebrow: 'Scanner architetturale Spring',
    heroText: 'Analizza progetti Spring Boot, raggruppa i problemi per area tecnica e mostra in modo leggibile cosa correggere, perché conta e quali classi o file sono coinvolti.',
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
    scanRunning: 'Scansione in corso...',
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
    problems: 'Problemi',
    actions: 'Azioni',
    technicalJson: 'JSON tecnico',
    executiveSummary: 'Riepilogo esecutivo',
    project: 'Progetto',
    scan: 'Scansione',
    impactedAreas: 'Aree più impattate',
    howToRead: 'Come leggere il report',
    searchPlaceholder: 'Cerca per codice, classe, file o testo...',
    allSeverities: 'Tutte le severità',
    allAreas: 'Tutte le aree',
    clearFilters: 'Pulisci filtri',
    technicalCode: 'Codice tecnico',
    whyItMatters: 'Perché conta',
    recommendedFix: 'Correzione consigliata',
    involvedComponents: 'Classi e file coinvolti',
    technicalEvidence: 'Evidenza tecnica',
    recommendedActions: 'Azioni consigliate',
    priorities: 'priorità',
    exportJson: 'Esporta JSON',
    jsonNote: 'Questa sezione serve a sviluppatori e pipeline CI. La lettura funzionale è nelle sezioni Riepilogo, Problemi e Azioni.',
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
    selectedFolderFallback: 'cartella selezionata'
  },
  en: {
    eyebrow: 'Spring architecture scanner',
    heroText: 'Analyze Spring Boot projects, group findings by technical area and clearly show what should be fixed, why it matters and which classes or files are involved.',
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
    folderHelp: 'Select the main project folder, for example the one containing the pom.xml file.',
    backendFolderPath: 'Backend folder path',
    backendPathHelp: 'Use this when the backend and the project are on the same machine or when Docker mounts a folder under /scan.',
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
    problems: 'Findings',
    actions: 'Actions',
    technicalJson: 'Technical JSON',
    executiveSummary: 'Executive summary',
    project: 'Project',
    scan: 'Scan',
    impactedAreas: 'Most impacted areas',
    howToRead: 'How to read the report',
    searchPlaceholder: 'Search by code, class, file or text...',
    allSeverities: 'All severities',
    allAreas: 'All areas',
    clearFilters: 'Clear filters',
    technicalCode: 'Technical code',
    whyItMatters: 'Why it matters',
    recommendedFix: 'Recommended fix',
    involvedComponents: 'Involved classes and files',
    technicalEvidence: 'Technical evidence',
    recommendedActions: 'Recommended actions',
    priorities: 'priorities',
    exportJson: 'Export JSON',
    jsonNote: 'This section is meant for developers and CI pipelines. Functional reading is available in Overview, Findings and Actions.',
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
    selectedFolderFallback: 'selected folder'
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
  readonly selectedLanguage = signal<ReportLanguage>('it');

  localPath = '';
  search = '';
  severityFilter: Severity | 'ALL' = 'ALL';
  categoryFilter = 'ALL';

  readonly categories = computed(() => {
    const current = this.report();
    if (!current) {
      return [];
    }
    return [...new Set(current.findings.map((finding) => finding.category))].sort();
  });

  readonly filteredFindings = computed(() => {
    const current = this.report();
    if (!current) {
      return [];
    }

    const term = this.search.trim().toLowerCase();

    return current.findings.filter((finding) => {
      const matchesSeverity = this.severityFilter === 'ALL' || finding.severity === this.severityFilter;
      const matchesCategory = this.categoryFilter === 'ALL' || finding.category === this.categoryFilter;
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
          component.evidence
        ])
      ].join(' ').toLowerCase();

      return matchesSeverity && matchesCategory && (!term || searchable.includes(term));
    });
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
      this.executeScan(this.api.scanZip(file, language));
      return;
    }

    if (this.uploadMode() === 'folder') {
      const files = this.selectedFolderFiles();
      if (files.length === 0) {
        this.error.set(this.t('selectFolderError'));
        return;
      }
      this.executeScan(this.api.scanFolder(files, language));
      return;
    }

    if (!this.localPath.trim()) {
      this.error.set(this.t('selectPathError'));
      return;
    }
    this.executeScan(this.api.scanLocalPath(this.localPath.trim(), language));
  }

  resetFilters(): void {
    this.search = '';
    this.severityFilter = 'ALL';
    this.categoryFilter = 'ALL';
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

  ruleCode(ruleId: string): string {
    return ruleId.match(/^SPR\d+/)?.[0] ?? ruleId;
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

  private executeScan(request$: Observable<ArchitectureReviewReport>): void {
    this.loading.set(true);
    request$.subscribe({
      next: (result) => {
        this.report.set(result);
        this.activeTab.set('overview');
        this.loading.set(false);
      },
      error: (error: HttpErrorResponse) => {
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

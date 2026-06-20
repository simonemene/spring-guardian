import { Injectable, computed, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { finalize } from 'rxjs';
import {
  ArchitectureReviewReport,
  ModernizationChecklistItem,
  ReportLanguage,
} from '../spring-guardian.model';
import { SpringGuardianApiService } from '../spring-guardian-api.service';

export type UploadMode = 'zip' | 'folder' | 'local';

@Injectable({ providedIn: 'root' })
export class ReportStateService {
  readonly report = signal<ArchitectureReviewReport | null>(null);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  readonly language = signal<ReportLanguage>('it');
  readonly uploadMode = signal<UploadMode>('zip');
  readonly selectedZip = signal<File | null>(null);
  readonly selectedFolderFiles = signal<File[]>([]);
  readonly localPath = signal('/scan');
  readonly checklistFilter = signal<'all' | 'todo' | 'progress' | 'done' | 'ignored'>('all');
  readonly checklistStatus = signal<Record<string, ChecklistStatus>>({});
  readonly checklistMeta = signal<Record<string, ChecklistItemState>>({});
  readonly globalQuery = signal('');

  readonly hasReport = computed(() => this.report() !== null);
  readonly architectMode = computed(() => this.report()?.architectMode ?? null);
  readonly checklist = computed(() => this.mergeChecklistState(this.architectMode()?.modernizationPlan.checklist ?? []));
  readonly checklistProgress = computed(() => {
    const items = this.checklist();
    if (items.length === 0) {
      return 0;
    }
    const completed = items.filter((item) => this.statusOf(item.id) === 'done').length;
    return Math.round((completed / items.length) * 100);
  });
  readonly visibleChecklist = computed(() => {
    const filter = this.checklistFilter();
    const items = this.checklist();
    if (filter === 'all') {
      return items;
    }
    return items.filter((item) => this.statusOf(item.id) === filter);
  });

  readonly globalSearchResults = computed<SearchResult[]>(() => {
    const query = this.globalQuery().trim().toLowerCase();
    const report = this.report();
    if (!report || query.length < 2) {
      return [];
    }
    const results: SearchResult[] = [];

    for (const finding of report.findings ?? []) {
      const text = [
        finding.ruleId,
        finding.title,
        finding.category,
        finding.suggestedFix,
        finding.guidance?.springAlternative ?? '',
        ...(finding.affectedComponents ?? []).map((component) => `${component.name} ${component.filePath}`),
      ].join(' ').toLowerCase();
      if (text.includes(query)) {
        results.push({
          type: 'Finding',
          title: `${finding.ruleId} · ${finding.title}`,
          detail: finding.affectedComponents?.[0]?.filePath || finding.category,
          route: '/findings',
        });
      }
    }

    for (const item of this.checklist()) {
      const text = [
        item.id,
        item.title,
        item.springAlternative,
        item.suggestedChange,
        item.files.join(' '),
        item.relatedFindings.join(' '),
      ].join(' ').toLowerCase();
      if (text.includes(query)) {
        results.push({
          type: 'Checklist',
          title: item.title,
          detail: `${item.effort} effort · ${item.businessImpact} impact`,
          route: '/checklist',
        });
      }
    }

    for (const area of report.architectMode?.maturityScore.areas ?? []) {
      const text = [area.name, area.code, area.drivers.join(' '), area.recommendations.join(' ')].join(' ').toLowerCase();
      if (text.includes(query)) {
        results.push({
          type: 'Score',
          title: `${area.name} · ${area.score}/100`,
          detail: area.recommendations[0] || 'Spring Maturity Score area',
          route: '/architect',
        });
      }
    }

    return results.slice(0, 12);
  });

  constructor(private readonly api: SpringGuardianApiService) {
    try {
      const storedFilter = localStorage.getItem('spring-guardian.checklist.filter');
      if (this.isChecklistFilter(storedFilter)) {
        this.checklistFilter.set(storedFilter);
      }
    } catch {
      // localStorage may be unavailable.
    }
  }

  setLanguage(language: ReportLanguage): void {
    this.language.set(language);
  }

  isEnglish(): boolean {
    return this.language() === 'en';
  }

  text(it: string, en: string): string {
    return this.language() === 'en' ? en : it;
  }

  setGlobalQuery(query: string): void {
    this.globalQuery.set(query);
  }

  selectUploadMode(mode: UploadMode): void {
    this.uploadMode.set(mode);
    this.error.set(null);
  }

  selectZip(file: File | null): void {
    this.selectedZip.set(file);
    this.error.set(null);
  }

  selectFolderFiles(files: File[]): void {
    this.selectedFolderFiles.set(files);
    this.error.set(null);
  }

  setLocalPath(path: string): void {
    this.localPath.set(path);
  }

  scan(): void {
    this.error.set(null);
    const language = this.language();
    const mode = this.uploadMode();

    if (mode === 'zip') {
      const file = this.selectedZip();
      if (!file) {
        this.error.set(this.text('Seleziona un file ZIP da analizzare.', 'Select a ZIP file to analyze.'));
        return;
      }
      this.executeScan(this.api.scanZip(file, language));
      return;
    }

    if (mode === 'folder') {
      const files = this.selectedFolderFiles();
      if (files.length === 0) {
        this.error.set(this.text('Seleziona una cartella progetto da analizzare.', 'Select a project folder to analyze.'));
        return;
      }
      this.executeScan(this.api.scanFolder(files, language));
      return;
    }

    const path = this.localPath().trim();
    if (path.length === 0) {
      this.error.set(this.text('Inserisci un percorso backend valido.', 'Enter a valid backend path.'));
      return;
    }
    this.executeScan(this.api.scanLocalPath(path, language));
  }

  updateChecklistStatus(id: string, status: ChecklistStatus): void {
    const next = { ...this.checklistStatus(), [id]: status };
    this.checklistStatus.set(next);
    this.persistChecklistState();
  }

  updateChecklistMeta(id: string, patch: Partial<ChecklistItemState>): void {
    const current = this.checklistMeta()[id] ?? {};
    const next = { ...this.checklistMeta(), [id]: { ...current, ...patch } };
    this.checklistMeta.set(next);
    this.persistChecklistState();
  }

  metaOf(id: string): ChecklistItemState {
    return this.checklistMeta()[id] ?? {};
  }

  setChecklistFilter(filter: ChecklistFilter): void {
    this.checklistFilter.set(filter);
    try {
      localStorage.setItem('spring-guardian.checklist.filter', filter);
    } catch {
      // localStorage may be unavailable.
    }
  }


  resetChecklistState(): void {
    this.checklistStatus.set({});
    this.checklistMeta.set({});
    const report = this.report();
    if (!report) {
      return;
    }
    try {
      localStorage.removeItem(this.storageKey(report));
    } catch {
      // localStorage may be unavailable. In-memory state is already reset.
    }
  }

  importChecklistState(file: File | null): void {
    if (!file) {
      return;
    }
    file.text()
      .then((content) => {
        const parsed = JSON.parse(content);
        const nextStatus: Record<string, ChecklistStatus> = {};
        const nextMeta: Record<string, ChecklistItemState> = {};

        const applyItem = (item: any) => {
          if (!item?.id) {
            return;
          }
          if (this.isChecklistStatus(item.status)) {
            nextStatus[item.id] = item.status;
          }
          const meta = item.meta ?? item;
          nextMeta[item.id] = {
            owner: typeof meta.owner === 'string' ? meta.owner : '',
            dueDate: typeof meta.dueDate === 'string' ? meta.dueDate : '',
            note: typeof meta.note === 'string' ? meta.note : '',
            decision: this.isChecklistDecision(meta.decision) ? meta.decision : 'fix',
          };
        };

        if (Array.isArray(parsed)) {
          parsed.forEach(applyItem);
        } else if (Array.isArray(parsed?.items)) {
          parsed.items.forEach(applyItem);
        } else if (typeof parsed === 'object' && parsed !== null) {
          const rawStatus = parsed.statuses ?? parsed.status ?? parsed;
          for (const [id, status] of Object.entries(rawStatus)) {
            if (this.isChecklistStatus(status)) {
              nextStatus[id] = status;
            }
          }
          const rawMeta = parsed.meta ?? parsed.metadata ?? {};
          for (const [id, meta] of Object.entries(rawMeta)) {
            if (typeof meta === 'object' && meta !== null) {
              nextMeta[id] = {
                owner: typeof (meta as any).owner === 'string' ? (meta as any).owner : '',
                dueDate: typeof (meta as any).dueDate === 'string' ? (meta as any).dueDate : '',
                note: typeof (meta as any).note === 'string' ? (meta as any).note : '',
                decision: this.isChecklistDecision((meta as any).decision) ? (meta as any).decision : 'fix',
              };
            }
          }
        }

        this.checklistStatus.set(nextStatus);
        this.checklistMeta.set(nextMeta);
        this.persistChecklistState();
      })
      .catch(() => this.error.set(this.text('Import checklist non valido: carica un JSON esportato da Spring Guardian.', 'Invalid checklist import: upload a JSON exported by Spring Guardian.')));
  }

  checklistItemsForFinding(ruleId: string): ModernizationChecklistItem[] {
    return this.checklist().filter((item) => item.relatedFindings?.includes(ruleId));
  }

  statusOf(id: string): ChecklistStatus {
    return this.checklistStatus()[id] ?? 'todo';
  }

  downloadChecklistJson(): void {
    const report = this.report();
    const payload = {
      projectName: report?.projectName ?? 'unknown',
      fingerprint: report?.architectMode?.fingerprint?.summary ?? 'unknown',
      exportedAt: new Date().toISOString(),
      items: this.checklist().map((item) => ({
        ...item,
        status: this.statusOf(item.id),
        meta: this.metaOf(item.id),
      })),
      statuses: this.checklistStatus(),
      meta: this.checklistMeta(),
    };
    this.download('spring-guardian-checklist.json', JSON.stringify(payload, null, 2), 'application/json');
  }

  downloadChecklistMarkdown(): void {
    const lines = ['# Spring Guardian improvement checklist', ''];
    for (const item of this.checklist()) {
      const checked = this.statusOf(item.id) === 'done' ? 'x' : ' ';
      lines.push(`- [${checked}] ${item.title}`);
      const meta = this.metaOf(item.id);
      lines.push(`  - Status: ${this.statusOf(item.id)}`);
      lines.push(`  - Decision: ${meta.decision || 'fix'}`);
      if (meta.owner) lines.push(`  - Owner: ${meta.owner}`);
      if (meta.dueDate) lines.push(`  - Due date: ${meta.dueDate}`);
      if (meta.note) lines.push(`  - Note: ${meta.note}`);
      lines.push(`  - Priority: ${item.priority}`);
      lines.push(`  - Severity: ${item.severity}`);
      lines.push(`  - Effort: ${item.effort}`);
      lines.push(`  - Business impact: ${item.businessImpact}`);
      if (item.files.length > 0) {
        lines.push(`  - Files/classes: ${item.files.join(', ')}`);
      }
      lines.push(`  - Spring alternative: ${item.springAlternative}`);
      lines.push(`  - Suggested change: ${item.suggestedChange}`);
      lines.push('');
    }
    this.download('spring-guardian-checklist.md', lines.join('\n'), 'text/markdown');
  }

  downloadMermaid(): void {
    const mermaid = this.architectMode()?.architectureMap.mermaidDiagram ?? '';
    this.download('spring-guardian-module-map.mmd', mermaid, 'text/plain');
  }

  downloadOpenRewrite(): void {
    const yaml = this.architectMode()?.openRewritePlan.yaml ?? '';
    this.download('spring-guardian-openrewrite-suggestions.yml', yaml, 'application/yaml');
  }


  downloadExecutiveSummaryMarkdown(): void {
    const report = this.report();
    if (!report) {
      return;
    }
    const architect = report.architectMode;
    const lines: string[] = [
      '# Spring Guardian Executive Summary',
      '',
      `Project: ${report.projectName || 'unknown'}`,
      `Generated at: ${report.scannedAt || new Date().toISOString()}`,
      '',
      '## Spring fingerprint',
      '',
    ];

    if (architect) {
      lines.push(`- ${architect.fingerprint.summary}`);
      lines.push(`- Build: ${architect.fingerprint.buildTool}`);
      lines.push(`- Java: ${architect.fingerprint.javaVersion}`);
      lines.push(`- Spring Boot: ${architect.fingerprint.springBootVersion}`);
      lines.push('');
      lines.push('## Spring maturity');
      lines.push('');
      lines.push(`- Overall score: ${architect.maturityScore.overallScore}/100 (${architect.maturityScore.status})`);
    } else {
      lines.push('- Architect Mode data not available.');
      lines.push('');
      lines.push('## Spring maturity');
      lines.push('');
      lines.push(`- Architecture score: ${report.architectureScore}/100 (${report.riskLevel})`);
    }

    lines.push('');
    lines.push('## Top Spring risks');
    lines.push('');
    for (const risk of this.topExecutiveRisks(report)) {
      lines.push(`- ${risk}`);
    }

    lines.push('');
    lines.push('## Top Spring modernization actions');
    lines.push('');
    this.topExecutiveActions(report).forEach((action, index) => lines.push(`${index + 1}. ${action}`));

    lines.push('');
    lines.push('## Spring Boot production readiness');
    lines.push('');
    if (architect) {
      lines.push(`- ${architect.productionReadiness.status}: ${architect.productionReadiness.score}/100`);
    } else {
      lines.push('- Production readiness details not available.');
    }

    if (architect) {
      lines.push('');
      lines.push('## Spring Boot Upgrade Path');
      lines.push('');
      for (const step of architect.upgradePath.steps.slice(0, 5)) {
        lines.push(`- ${step.title} (${step.risk}, ${step.effort})`);
      }
    }

    lines.push('');
    lines.push('## Recommended next step');
    lines.push('');
    lines.push('Open the Spring Modernization Checklist and assign owner, decision and due date to the first high-priority items.');

    this.download('spring-guardian-executive-summary.md', lines.join('\n'), 'text/markdown');
  }

  private topExecutiveRisks(report: ArchitectureReviewReport): string[] {
    const risks = [
      ...(report.architectMode?.productionReadiness.risks ?? []),
      ...(report.architectMode?.architectureMap.globalRisks ?? []),
      ...report.findings
        .filter((finding) => finding.severity === 'CRITICAL' || finding.severity === 'MAJOR')
        .slice(0, 5)
        .map((finding) => `${finding.ruleId}: ${finding.title}`),
    ];
    return Array.from(new Set(risks)).slice(0, 7);
  }

  private topExecutiveActions(report: ArchitectureReviewReport): string[] {
    const checklist = report.architectMode?.modernizationPlan.checklist ?? [];
    const fromChecklist = checklist
      .slice()
      .sort((a, b) => a.priority - b.priority)
      .slice(0, 7)
      .map((item) => item.title);
    if (fromChecklist.length > 0) {
      return fromChecklist;
    }
    return report.recommendedActions.slice(0, 7).map((action) => action.title);
  }

  downloadJsonReport(): void {
    const report = this.report();
    if (!report) {
      return;
    }
    this.download(`${report.projectName || 'spring-guardian'}-report.json`, JSON.stringify(report, null, 2), 'application/json');
  }

  private executeScan(scan$: import('rxjs').Observable<ArchitectureReviewReport>): void {
    this.loading.set(true);
    scan$
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (report) => {
          this.report.set(report);
          this.loadChecklistState(report);
        },
        error: (error: unknown) => this.error.set(this.describeError(error)),
      });
  }

  private mergeChecklistState(items: ModernizationChecklistItem[]): ModernizationChecklistItem[] {
    const statuses = this.checklistStatus();
    return items.map((item) => ({
      ...item,
      completed: (statuses[item.id] ?? 'todo') === 'done',
    }));
  }

  private loadChecklistState(report: ArchitectureReviewReport): void {
    const key = this.storageKey(report);
    try {
      const raw = localStorage.getItem(key);
      if (!raw) {
        this.checklistStatus.set({});
        this.checklistMeta.set({});
        return;
      }
      const parsed = JSON.parse(raw);
      if (parsed?.statuses || parsed?.meta) {
        this.checklistStatus.set(parsed.statuses ?? {});
        this.checklistMeta.set(parsed.meta ?? {});
      } else {
        this.checklistStatus.set(parsed);
        this.checklistMeta.set({});
      }
    } catch {
      this.checklistStatus.set({});
      this.checklistMeta.set({});
    }
  }

  private persistChecklistState(): void {
    const report = this.report();
    if (!report) {
      return;
    }
    try {
      localStorage.setItem(this.storageKey(report), JSON.stringify({
        statuses: this.checklistStatus(),
        meta: this.checklistMeta(),
      }));
    } catch {
      // localStorage may be unavailable in private mode. UI still works in memory.
    }
  }

  private storageKey(report: ArchitectureReviewReport): string {
    const fingerprint = report.architectMode?.fingerprint;
    const source = [
      report.projectName || 'unknown',
      report.projectRootPath || '',
      fingerprint?.summary || '',
      fingerprint?.buildTool || '',
      fingerprint?.javaVersion || '',
      fingerprint?.springBootVersion || '',
    ].join('|');
    return `spring-guardian.checklist.${this.hash(source)}`;
  }

  private hash(value: string): string {
    let hash = 0;
    for (let index = 0; index < value.length; index += 1) {
      hash = ((hash << 5) - hash + value.charCodeAt(index)) | 0;
    }
    return Math.abs(hash).toString(36);
  }

  private isChecklistFilter(value: unknown): value is ChecklistFilter {
    return value === 'all' || value === 'todo' || value === 'progress' || value === 'done' || value === 'ignored';
  }

  private isChecklistStatus(value: unknown): value is ChecklistStatus {
    return value === 'todo' || value === 'progress' || value === 'done' || value === 'ignored';
  }

  private isChecklistDecision(value: unknown): value is ChecklistDecision {
    return value === 'fix' || value === 'accepted-risk' || value === 'ignore';
  }

  private describeError(error: unknown): string {
    if (error instanceof HttpErrorResponse) {
      const message = typeof error.error === 'string' ? error.error : error.error?.message;
      return message || this.text(`Errore HTTP ${error.status}: ${error.statusText}`, `HTTP error ${error.status}: ${error.statusText}`);
    }
    return this.text('Errore durante la scansione.', 'Error during scan.');
  }

  private download(filename: string, content: string, type: string): void {
    const blob = new Blob([content], { type });
    const url = URL.createObjectURL(blob);
    const anchor = document.createElement('a');
    anchor.href = url;
    anchor.download = filename;
    anchor.click();
    URL.revokeObjectURL(url);
  }
}

export type ChecklistFilter = 'all' | 'todo' | 'progress' | 'done' | 'ignored';
export type ChecklistStatus = 'todo' | 'progress' | 'done' | 'ignored';
export type ChecklistDecision = 'fix' | 'accepted-risk' | 'ignore';

export interface ChecklistItemState {
  owner?: string;
  dueDate?: string;
  note?: string;
  decision?: ChecklistDecision;
}


export interface SearchResult {
  type: 'Finding' | 'Checklist' | 'Score';
  title: string;
  detail: string;
  route: string;
}

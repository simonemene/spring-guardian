import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AfterViewInit, Component, ElementRef, EventEmitter, HostListener, Input, Output, ViewChild, computed, signal } from '@angular/core';
import { FindingGroup, SpringArchitectureMap, SpringModuleDependency, SpringModuleSummary } from '../../../spring-guardian.model';

interface ClassGraphNode {
  id: string;
  label: string;
  kind: 'controller' | 'service' | 'repository' | 'entity' | 'config' | 'client' | 'other';
  filePath: string;
}

interface ClassGraphEdge {
  id: string;
  from: ClassGraphNode;
  to: ClassGraphNode;
  label: string;
  severity: 'ok' | 'warning' | 'violation';
  finding?: FindingGroup;
  fix: string[];
}

interface SelectedArchitectureIssue {
  title: string;
  severity: 'warning' | 'violation' | 'cycle' | 'dependency';
  description: string;
  module?: string;
  evidence: string[];
  fix: string[];
}

@Component({
  selector: 'sg-architecture-graph-dialog',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './architecture-graph-dialog.component.html',
  styleUrl: './architecture-graph-dialog.component.scss',
})
export class ArchitectureGraphDialogComponent implements AfterViewInit {
  @ViewChild('dialogRoot') private readonly dialogRoot?: ElementRef<HTMLElement>;
  @Input({ required: true }) map!: SpringArchitectureMap;
  @Input() findings: FindingGroup[] = [];
  @Output() closed = new EventEmitter<void>();

  readonly selectedIssue = signal<SelectedArchitectureIssue | null>(null);
  readonly issueCount = computed(() => this.buildIssues().length);

  ngAfterViewInit(): void {
    setTimeout(() => this.dialogRoot?.nativeElement.focus());
  }

  @HostListener('document:keydown', ['$event'])
  onDialogKeydown(event: KeyboardEvent): void {
    if (event.key === 'Escape') {
      this.close();
      return;
    }
    if (event.key !== 'Tab') {
      return;
    }
    const root = this.dialogRoot?.nativeElement;
    if (!root) {
      return;
    }
    const focusable = Array.from(root.querySelectorAll<HTMLElement>(
      'a[href], button:not([disabled]), textarea, input, select, [tabindex]:not([tabindex="-1"])',
    )).filter((element) => !element.hasAttribute('disabled') && !element.getAttribute('aria-hidden'));
    if (focusable.length === 0) {
      event.preventDefault();
      root.focus();
      return;
    }
    const first = focusable[0];
    const last = focusable[focusable.length - 1];
    const active = document.activeElement;
    if (event.shiftKey && active === first) {
      event.preventDefault();
      last.focus();
    } else if (!event.shiftKey && active === last) {
      event.preventDefault();
      first.focus();
    }
  }

  close(): void {
    this.closed.emit();
  }


  hasViolations(): boolean {
    return this.classGraphEdges().length > 0 || this.map.globalRisks.length > 0 || this.map.modules.some((module) => module.risks.length > 0) || this.map.cycles.length > 0;
  }


  classGraphEdges(): ClassGraphEdge[] {
    const edges: ClassGraphEdge[] = [];
    const architectureFindings = this.findings.filter((finding) => this.isArchitectureFinding(finding));

    for (const finding of architectureFindings) {
      const components = finding.affectedComponents ?? [];
      const controller = this.firstComponentNode(components, ['controller', 'restcontroller', '/controller', 'controller.java']);
      const service = this.firstComponentNode(components, ['service', '/service', 'service.java']);
      const repository = this.firstComponentNode(components, ['repository', '/repository', 'repository.java']);
      const entity = this.firstComponentNode(components, ['entity', '/entity', 'entity.java', '/model']);
      const any = this.firstComponentNode(components, []);

      if (controller && repository) {
        edges.push({
          id: `${finding.ruleId}-controller-repository`,
          from: controller,
          to: repository,
          label: 'Controller → Repository',
          severity: 'violation',
          finding,
          fix: this.fixForRisk('controller repository'),
        });
        continue;
      }

      if (controller && entity) {
        edges.push({
          id: `${finding.ruleId}-controller-entity`,
          from: controller,
          to: entity,
          label: 'Controller → Entity',
          severity: 'violation',
          finding,
          fix: [
            'Introdurre DTO di request/response.',
            'Mappare entity JPA nel service o in un mapper dedicato.',
            'Non esporre @Entity come contratto REST.',
          ],
        });
        continue;
      }

      if (service && controller) {
        edges.push({
          id: `${finding.ruleId}-service-web`,
          from: service,
          to: controller,
          label: 'Service → Web layer',
          severity: 'violation',
          finding,
          fix: this.fixForRisk('service web controller'),
        });
        continue;
      }

      if (any) {
        const target = repository || service || entity || this.syntheticNode('Spring boundary', 'other');
        edges.push({
          id: `${finding.ruleId}-single`,
          from: any,
          to: target.id === any.id ? this.syntheticNode('Spring boundary', 'other') : target,
          label: finding.ruleId,
          severity: this.isViolationFinding(finding) ? 'violation' : 'warning',
          finding,
          fix: this.fixForFinding(finding),
        });
      }
    }

    for (const module of this.map.modules) {
      if ((module.controllers ?? 0) > 0 && (module.services ?? 0) === 0) {
        const controller = this.syntheticNode(`${module.name} controllers`, 'controller');
        const service = this.syntheticNode(`${module.name} @Service mancante`, 'service');
        edges.push({
          id: `${module.name}-missing-service`,
          from: controller,
          to: service,
          label: 'Service boundary missing',
          severity: 'violation',
          fix: [
            `Creare un @Service applicativo nel modulo ${module.name}.`,
            'Spostare use case, accesso dati e @Transactional fuori dai controller.',
            'Usare DTO/command come input del service.',
          ],
        });
      }
    }

    return this.uniqueEdges(edges).slice(0, 12);
  }

  classGraphNodes(): ClassGraphNode[] {
    const nodes = new Map<string, ClassGraphNode>();
    for (const edge of this.classGraphEdges()) {
      nodes.set(edge.from.id, edge.from);
      nodes.set(edge.to.id, edge.to);
    }
    return Array.from(nodes.values()).slice(0, 16);
  }

  edgeIssue(edge: ClassGraphEdge): SelectedArchitectureIssue {
    return {
      title: edge.label,
      severity: edge.severity === 'violation' ? 'violation' : 'warning',
      description: edge.finding?.title ?? 'Gap rilevato nella mappa Spring dei layer.',
      module: undefined,
      evidence: edge.finding?.affectedComponents?.map((component) => `${component.name || component.type} · ${component.filePath}${component.line ? ':' + component.line : ''}`) ?? [edge.from.label, edge.to.label],
      fix: edge.fix,
    };
  }


  moduleIssues(module: SpringModuleSummary): SelectedArchitectureIssue[] {
    return module.risks.map((risk) => ({
      title: this.riskTitle(risk),
      severity: this.riskSeverity(risk),
      description: risk,
      module: module.name,
      evidence: [
        `${module.controllers} controller · ${module.services} service · ${module.repositories} repository · ${module.entities} entity`,
        module.basePackage,
      ],
      fix: this.fixForRisk(risk),
    }));
  }

  dependencyIssue(dep: SpringModuleDependency): SelectedArchitectureIssue {
    return {
      title: `${dep.fromModule} dipende da ${dep.toModule}`,
      severity: dep.weight > 3 ? 'warning' : 'dependency',
      description: `Sono stati rilevati ${dep.weight} import tra i due moduli. Valuta se è una dipendenza intenzionale o un coupling da ridurre.`,
      evidence: dep.examples?.length ? dep.examples : [`${dep.fromModule} → ${dep.toModule}`],
      fix: [
        'Mantieni repository e persistence nel modulo proprietario.',
        'Esporre use case tramite service applicativi o eventi, non via repository cross-module.',
        'Valuta ApplicationEventPublisher o boundary Spring Modulith-style se la dipendenza è di dominio.',
      ],
    };
  }

  cycleIssue(modules: string[], remediation: string): SelectedArchitectureIssue {
    return {
      title: `Ciclo rilevato: ${modules.join(' → ')}`,
      severity: 'cycle',
      description: 'Un ciclo tra moduli rende difficile testare, evolvere e rilasciare parti del sistema in modo indipendente.',
      evidence: [modules.join(' → ')],
      fix: [
        remediation,
        'Spezzare il ciclo introducendo eventi applicativi, adapter o un modulo shared esplicito.',
        'Evitare accessi repository cross-module e dipendenze dal web layer.',
      ],
    };
  }

  select(issue: SelectedArchitectureIssue): void {
    this.selectedIssue.set(issue);
  }

  private buildIssues(): SelectedArchitectureIssue[] {
    return [
      ...this.map.modules.flatMap((module) => this.moduleIssues(module)),
      ...this.map.dependencies.map((dep) => this.dependencyIssue(dep)),
      ...this.map.cycles.map((cycle) => this.cycleIssue(cycle.modules, cycle.remediation)),
    ];
  }


  private isArchitectureFinding(finding: FindingGroup): boolean {
    const haystack = this.findingText(finding);
    return [
      'controller',
      'repository',
      'entity',
      'service',
      'transactional',
      'requestbody',
      'layer',
      'package',
      'module',
      'architecture',
      'jpa',
      'dto',
    ].some((keyword) => haystack.includes(keyword));
  }

  private isViolationFinding(finding: FindingGroup): boolean {
    const text = this.findingText(finding);
    return finding.severity === 'CRITICAL' || finding.severity === 'MAJOR' || text.includes('violation') || text.includes('direct');
  }

  private firstComponentNode(components: any[], keywords: string[]): ClassGraphNode | null {
    for (const component of components) {
      const text = `${component.type ?? ''} ${component.name ?? ''} ${component.filePath ?? ''} ${component.evidence ?? ''}`.toLowerCase();
      if (keywords.length === 0 || keywords.some((keyword) => text.includes(keyword))) {
        return {
          id: `${component.filePath || component.name || text}`.replace(/[^a-zA-Z0-9_.-]+/g, '-'),
          label: this.shortName(component.name || component.filePath || component.type || 'Component'),
          kind: this.kindOf(text),
          filePath: component.filePath || component.evidence || '',
        };
      }
    }
    return null;
  }

  private syntheticNode(label: string, kind: ClassGraphNode['kind']): ClassGraphNode {
    return {
      id: label.replace(/[^a-zA-Z0-9_.-]+/g, '-').toLowerCase(),
      label,
      kind,
      filePath: '',
    };
  }

  private uniqueEdges(edges: ClassGraphEdge[]): ClassGraphEdge[] {
    const seen = new Set<string>();
    return edges.filter((edge) => {
      const key = `${edge.from.id}->${edge.to.id}:${edge.label}`;
      if (seen.has(key)) {
        return false;
      }
      seen.add(key);
      return true;
    });
  }

  private shortName(value: string): string {
    const clean = value.split(/[\\/]/).pop() || value;
    return clean.replace(/\.java$/i, '');
  }

  private kindOf(text: string): ClassGraphNode['kind'] {
    if (text.includes('controller')) return 'controller';
    if (text.includes('service')) return 'service';
    if (text.includes('repository')) return 'repository';
    if (text.includes('entity') || text.includes('/model') || text.includes('/domain')) return 'entity';
    if (text.includes('config')) return 'config';
    if (text.includes('client')) return 'client';
    return 'other';
  }

  private findingText(finding: FindingGroup): string {
    return [
      finding.ruleId,
      finding.title,
      finding.category,
      finding.findingType,
      finding.suggestedFix,
      finding.whyItMatters,
      finding.guidance?.recommendedApproach ?? '',
      finding.guidance?.springAlternative ?? '',
      ...(finding.affectedComponents ?? []).map((component) => `${component.type} ${component.name} ${component.filePath} ${component.evidence}`),
    ].join(' ').toLowerCase();
  }

  private fixForFinding(finding: FindingGroup): string[] {
    const alternative = finding.guidance?.springAlternative;
    const recommended = finding.guidance?.recommendedApproach || finding.suggestedFix;
    return [
      recommended || 'Applica il pattern Spring indicato dal finding.',
      alternative ? `Alternativa Spring: ${alternative}` : 'Collega la modifica alla checklist finale.',
    ];
  }


  private riskTitle(risk: string): string {
    const normalized = risk.toLowerCase();
    if (normalized.includes('controller') && normalized.includes('repository')) {
      return 'Controller accede direttamente al repository';
    }
    if (normalized.includes('service') && (normalized.includes('web') || normalized.includes('controller'))) {
      return 'Service dipende dal web layer';
    }
    if (normalized.includes('missing') || normalized.includes('service layer')) {
      return 'Service layer mancante o debole';
    }
    if (normalized.includes('shared')) {
      return 'Package shared usato come dumping ground';
    }
    return 'Rischio architetturale rilevato';
  }

  private riskSeverity(risk: string): 'warning' | 'violation' {
    const normalized = risk.toLowerCase();
    if (normalized.includes('controller') || normalized.includes('repository') || normalized.includes('cycle')) {
      return 'violation';
    }
    return 'warning';
  }

  private fixForRisk(risk: string): string[] {
    const normalized = risk.toLowerCase();
    if (normalized.includes('controller') && normalized.includes('repository')) {
      return [
        'Creare un @Service applicativo dedicato, ad esempio OrderService / CustomerService.',
        'Spostare nel @Service l’accesso a Spring Data repository e le transazioni.',
        'Lasciare al @RestController solo HTTP boundary, DTO, Bean Validation e status code.',
      ];
    }
    if (normalized.includes('service') && (normalized.includes('web') || normalized.includes('controller'))) {
      return [
        'Rimuovere dipendenze da controller/request/response nel service.',
        'Passare DTO o command object indipendenti dal web layer.',
        'Tenere il service testabile senza Spring MVC.',
      ];
    }
    if (normalized.includes('service layer') || normalized.includes('missing')) {
      return [
        'Introdurre @Service/use case per orchestrare repository e @Transactional.',
        'Usare @Transactional nel service, non nel controller.',
        'Mappare entity verso DTO prima di uscire dal boundary applicativo.',
      ];
    }
    return [
      'Ridurre coupling e hard dependency tra package.',
      'Rendere esplicito il boundary con service, eventi o adapter.',
      'Aggiungere test di regressione sui confini architetturali.',
    ];
  }
}

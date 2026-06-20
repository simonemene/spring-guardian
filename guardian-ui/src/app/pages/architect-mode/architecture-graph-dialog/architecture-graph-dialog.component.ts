import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AfterViewInit, Component, ElementRef, EventEmitter, HostListener, Input, Output, ViewChild, computed, signal } from '@angular/core';
import { SpringArchitectureMap, SpringModuleDependency, SpringModuleSummary } from '../../../spring-guardian.model';

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
    return this.map.globalRisks.length > 0 || this.map.modules.some((module) => module.risks.length > 0) || this.map.cycles.length > 0;
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

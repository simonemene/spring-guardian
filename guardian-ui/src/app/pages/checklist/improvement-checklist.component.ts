import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ChecklistDecision, ChecklistStatus, ReportStateService } from '../../core/report-state.service';
import { PageHeaderComponent } from '../../shared/page-header/page-header.component';
import { ModernizationChecklistItem } from '../../spring-guardian.model';

@Component({
  selector: 'sg-improvement-checklist',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, PageHeaderComponent],
  templateUrl: './improvement-checklist.component.html',
  styleUrl: './improvement-checklist.component.scss',
})
export class ImprovementChecklistComponent {
  readonly statuses: ChecklistStatus[] = ['todo', 'progress', 'done', 'ignored'];
  readonly decisions: ChecklistDecision[] = ['fix', 'accepted-risk', 'ignore'];

  readonly releasePhases = ['Stabilization', 'Architecture/API', 'Security', 'Production readiness', 'Upgrade path'];

  itemsForPhase(phase: string): ModernizationChecklistItem[] {
    return this.state.checklist().filter((item) => this.phaseOf(item) === phase);
  }

  completedForPhase(phase: string): number {
    return this.itemsForPhase(phase).filter((item) => this.state.statusOf(item.id) === 'done').length;
  }


  constructor(readonly state: ReportStateService) {}

  phaseOf(item: ModernizationChecklistItem): string {
    const id = item.id.toLowerCase();
    const title = item.title.toLowerCase();
    if (id.includes('prod') || title.includes('actuator') || title.includes('production') || title.includes('logging')) return 'Production readiness';
    if (id.includes('upgrade') || title.includes('boot') || title.includes('java') || title.includes('jakarta')) return 'Upgrade path';
    if (title.includes('controller') || title.includes('service') || title.includes('repository') || title.includes('dto')) return 'Architecture/API';
    if (title.includes('security') || title.includes('csrf') || title.includes('principal') || title.includes('role')) return 'Security';
    return 'Stabilization';
  }

  importState(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.state.importChecklistState(input.files?.[0] ?? null);
    input.value = '';
  }

  resetState(): void {
    if (confirm('Vuoi resettare lo stato della checklist per questo progetto/report?')) {
      this.state.resetChecklistState();
    }
  }

  statusLabel(status: ChecklistStatus): string {
    return {
      todo: 'Da fare',
      progress: 'In corso',
      done: 'Completato',
      ignored: 'Ignorato',
    }[status];
  }

  decisionLabel(decision: ChecklistDecision): string {
    return {
      fix: 'Fix',
      'accepted-risk': 'Rischio accettato',
      ignore: 'Ignora',
    }[decision];
  }
}

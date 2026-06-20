import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ReportStateService } from '../../core/report-state.service';
import { MetricCardComponent } from '../../shared/metric-card/metric-card.component';
import { PageHeaderComponent } from '../../shared/page-header/page-header.component';
import { ScoreBadgeComponent } from '../../shared/score-badge/score-badge.component';
import { FindingGroup, Severity } from '../../spring-guardian.model';

@Component({
  selector: 'sg-dashboard-page',
  standalone: true,
  imports: [CommonModule, RouterLink, MetricCardComponent, PageHeaderComponent, ScoreBadgeComponent],
  templateUrl: './dashboard-page.component.html',
  styleUrl: './dashboard-page.component.scss',
})
export class DashboardPageComponent {
  readonly severities: Severity[] = ['CRITICAL', 'MAJOR', 'MINOR', 'INFO'];

  constructor(readonly state: ReportStateService) {}

  oneLineDiagnosis(): string {
    const report = this.state.report();
    const architect = report?.architectMode;
    if (!report) return '';
    const type = architect?.fingerprint.summary || report.profile.projectType;
    const weak = architect?.maturityScore.areas
      .filter((area) => area.score < 75)
      .sort((a, b) => a.score - b.score)
      .slice(0, 2)
      .map((area) => area.name)
      .join(' + ');
    return this.state.text(
      `${type}. Priorità: ${weak || 'mantenere regressioni sotto controllo'}.`,
      `${type}. Priority: ${weak || 'keep regressions under control'}.`
    );
  }

  topPriorities(): FindingGroup[] {
    return this.state.report()?.findings
      .filter((finding) => finding.severity === 'CRITICAL' || finding.severity === 'MAJOR')
      .slice(0, 3) ?? [];
  }

  weakAreas(): { name: string; score: number }[] {
    return this.state.report()?.architectMode?.maturityScore.areas
      .slice()
      .sort((a, b) => a.score - b.score)
      .slice(0, 4)
      .map((area) => ({ name: area.name, score: area.score })) ?? [];
  }

  severityCount(severity: Severity): number {
    return this.state.report()?.findingsBySeverity?.[severity] ?? 0;
  }

  severityClass(severity: Severity): string {
    return severity === 'CRITICAL' || severity === 'MAJOR' ? 'bad' : severity === 'MINOR' ? 'warn' : 'info';
  }
}

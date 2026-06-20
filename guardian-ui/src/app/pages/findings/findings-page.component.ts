import { CommonModule } from '@angular/common';
import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ReportStateService } from '../../core/report-state.service';
import { PageHeaderComponent } from '../../shared/page-header/page-header.component';
import { FindingGroup, Severity } from '../../spring-guardian.model';

@Component({
  selector: 'sg-findings-page',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, PageHeaderComponent],
  templateUrl: './findings-page.component.html',
  styleUrl: './findings-page.component.scss',
})
export class FindingsPageComponent {
  readonly selectedFinding = signal<FindingGroup | null>(null);
  readonly severity = signal<'ALL' | Severity>(this.loadSeverityFilter());
  readonly query = signal(this.loadQueryFilter());

  constructor(readonly state: ReportStateService, private readonly route: ActivatedRoute) {
    this.route.queryParamMap.subscribe((params) => {
      const rule = params.get('rule');
      const query = params.get('query');
      const severity = params.get('severity') as 'ALL' | Severity | null;
      if (query) {
        this.query.set(query);
      }
      if (severity && ['ALL', 'CRITICAL', 'MAJOR', 'MINOR', 'INFO'].includes(severity)) {
        this.severity.set(severity);
      }
      if (rule) {
        this.query.set(rule);
        setTimeout(() => {
          const match = this.findings().find((finding) => finding.ruleId === rule);
          if (match) {
            this.selectedFinding.set(match);
          }
        });
      }
      this.persistFilters();
    });
  }

  updateSeverity(value: 'ALL' | Severity): void {
    this.severity.set(value);
    this.persistFilters();
  }

  updateQuery(value: string): void {
    this.query.set(value);
    this.persistFilters();
  }

  private persistFilters(): void {
    try {
      localStorage.setItem('spring-guardian.findings.filters', JSON.stringify({
        severity: this.severity(),
        query: this.query(),
      }));
    } catch {
      // localStorage may be unavailable.
    }
  }

  private loadSeverityFilter(): 'ALL' | Severity {
    try {
      const parsed = JSON.parse(localStorage.getItem('spring-guardian.findings.filters') || '{}');
      if (['ALL', 'CRITICAL', 'MAJOR', 'MINOR', 'INFO'].includes(parsed.severity)) {
        return parsed.severity;
      }
    } catch {
      // localStorage may be unavailable.
    }
    return 'ALL';
  }

  private loadQueryFilter(): string {
    try {
      const parsed = JSON.parse(localStorage.getItem('spring-guardian.findings.filters') || '{}');
      return typeof parsed.query === 'string' ? parsed.query : '';
    } catch {
      return '';
    }
  }

  findings(): FindingGroup[] {
    const query = this.query().trim().toLowerCase();
    const severity = this.severity();
    return (this.state.report()?.findings ?? []).filter((finding) => {
      const matchesSeverity = severity === 'ALL' || finding.severity === severity;
      const haystack = `${finding.ruleId} ${finding.title} ${finding.category} ${finding.suggestedFix}`.toLowerCase();
      return matchesSeverity && (!query || haystack.includes(query));
    });
  }

  relatedChecklistCount(finding: FindingGroup): number {
    return this.state.checklistItemsForFinding(finding.ruleId).length;
  }

  firstLocation(finding: FindingGroup): string {
    const first = finding.affectedComponents?.[0];
    if (!first) return 'Nessuna location disponibile';
    return `${first.filePath}${first.line ? ':' + first.line : ''}`;
  }

  openFinding(finding: FindingGroup): void {
    this.selectedFinding.set(finding);
  }

  closeFinding(): void {
    this.selectedFinding.set(null);
  }
}

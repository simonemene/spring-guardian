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
      if (query !== null) {
        this.query.set(query);
      }
      if (severity && ['ALL', 'CRITICAL', 'MAJOR', 'MINOR', 'INFO'].includes(severity)) {
        this.severity.set(severity);
      }
      if (rule) {
        this.query.set(rule);
        setTimeout(() => {
          const match = this.allFindings().find((finding) => finding.ruleId === rule);
          if (match) {
            this.selectedFinding.set(match);
          }
        });
      }
      this.persistFilters();
    });
  }

  allFindings(): FindingGroup[] {
    const report = this.state.report() as any;
    const findings = report?.findings ?? report?.findingGroups ?? report?.groups ?? [];
    return Array.isArray(findings) ? findings : [];
  }

  findings(): FindingGroup[] {
    const query = this.normalize(this.query());
    const severity = this.severity();
    return this.allFindings().filter((finding) => {
      const matchesSeverity = severity === 'ALL' || finding.severity === severity;
      const haystack = this.findingText(finding);
      return matchesSeverity && (!query || haystack.includes(query));
    });
  }

  hasActiveFilters(): boolean {
    return this.severity() !== 'ALL' || this.query().trim().length > 0;
  }

  clearFilters(): void {
    this.query.set('');
    this.severity.set('ALL');
    this.persistFilters();
  }

  updateSeverity(value: 'ALL' | Severity): void {
    this.severity.set(value);
    this.persistFilters();
  }

  updateQuery(value: string): void {
    this.query.set(value);
    this.persistFilters();
  }

  relatedChecklistCount(finding: FindingGroup): number {
    return this.state.checklistItemsForFinding(finding.ruleId).length;
  }

  firstLocation(finding: FindingGroup): string {
    const first = finding.affectedComponents?.[0];
    if (!first) {
      return this.state.text('Nessuna location disponibile', 'No location available');
    }
    return `${first.filePath}${first.line ? ':' + first.line : ''}`;
  }

  compactImpact(finding: FindingGroup): string {
    return finding.guidance?.riskImpact || finding.whyItMatters || finding.explanation || finding.category;
  }

  springPattern(finding: FindingGroup): string {
    return finding.guidance?.recommendedApproach || finding.suggestedFix || this.state.text('Segui il boundary Spring indicato dalla rule.', 'Follow the Spring boundary suggested by the rule.');
  }

  openFinding(finding: FindingGroup): void {
    this.selectedFinding.set(finding);
  }

  closeFinding(): void {
    this.selectedFinding.set(null);
  }

  private findingText(finding: FindingGroup): string {
    return this.normalize([
      finding.ruleId,
      finding.title,
      finding.category,
      finding.findingType,
      finding.findingTypeLabel,
      finding.suggestedFix,
      finding.whyItMatters,
      finding.explanation,
      finding.guidance?.detectedProblem,
      finding.guidance?.riskImpact,
      finding.guidance?.recommendedApproach,
      finding.guidance?.springAlternative,
      finding.guidance?.documentationUrl,
      ...(finding.affectedComponents ?? []).map((component) => [
        component.type,
        component.name,
        component.filePath,
        component.evidence,
        component.codeSnippet,
      ].join(' ')),
    ].join(' '));
  }

  private normalize(value: string): string {
    return (value || '')
      .toLowerCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .replace(/[^a-z0-9_@./:-]+/g, ' ')
      .trim();
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
}

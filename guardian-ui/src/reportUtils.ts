import type { ArchitectureReviewReport, FindingGroup, Severity } from './types';

export const severityOrder: Severity[] = ['CRITICAL', 'MAJOR', 'MINOR', 'INFO'];

export function formatNumber(value: number | undefined | null): string {
  return new Intl.NumberFormat('en-US').format(value ?? 0);
}

export function formatDate(value: string): string {
  if (!value) {
    return '-';
  }

  try {
    return new Intl.DateTimeFormat('en-US', {
      dateStyle: 'medium',
      timeStyle: 'short'
    }).format(new Date(value));
  } catch {
    return value;
  }
}

export function scoreLabel(score: number): string {
  if (score >= 85) return 'Strong';
  if (score >= 70) return 'Good with improvements';
  if (score >= 50) return 'Needs attention';
  return 'High refactoring risk';
}

export function getSeverityCount(report: ArchitectureReviewReport, severity: Severity): number {
  return report.findingsBySeverity?.[severity] ?? 0;
}

export function filterFindings(
  findings: FindingGroup[],
  severity: Severity | 'ALL',
  category: string,
  query: string
): FindingGroup[] {
  const normalizedQuery = query.trim().toLowerCase();

  return findings.filter((finding) => {
    const severityMatches = severity === 'ALL' || finding.severity === severity;
    const categoryMatches = category === 'ALL' || finding.category === category;
    const queryMatches =
      normalizedQuery.length === 0 ||
      finding.ruleId.toLowerCase().includes(normalizedQuery) ||
      finding.title.toLowerCase().includes(normalizedQuery) ||
      finding.whyItMatters.toLowerCase().includes(normalizedQuery) ||
      finding.suggestedFix.toLowerCase().includes(normalizedQuery) ||
      finding.affectedComponents.some((component) =>
        `${component.name} ${component.filePath} ${component.evidence}`.toLowerCase().includes(normalizedQuery)
      );

    return severityMatches && categoryMatches && queryMatches;
  });
}

export function collectCategories(report: ArchitectureReviewReport | null): string[] {
  if (!report) {
    return [];
  }

  return Array.from(new Set(report.findings.map((finding) => finding.category))).sort();
}

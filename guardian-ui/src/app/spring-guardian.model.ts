export type Severity = 'CRITICAL' | 'MAJOR' | 'MINOR' | 'INFO';
export type ReportLanguage = 'it' | 'en';

export interface ArchitectureReviewReport {
  projectName: string;
  scannedAt: string;
  summary: ReportSummary;
  architectureScore: number;
  riskLevel: string;
  scannedJavaFiles: number;
  scannedPomFiles: number;
  rulesExecuted: number;
  findingsBySeverity: Partial<Record<Severity, number>>;
  findingsByCategory: CategorySummary[];
  recommendedActions: RecommendedAction[];
  explanation: ReportExplanation;
  findings: FindingGroup[];
}

export interface ReportSummary {
  totalFindings: number;
  criticalFindings: number;
  majorFindings: number;
  minorFindings: number;
  infoFindings: number;
  riskLevel: string;
  status: string;
  executiveSummary: string;
}

export interface CategorySummary {
  category: string;
  findings: number;
  criticalFindings: number;
  majorFindings: number;
  minorFindings: number;
  infoFindings: number;
  explanation: string;
}

export interface RecommendedAction {
  priority: number;
  severity: Severity;
  ruleId: string;
  title: string;
  location: string;
  reason: string;
  action: string;
}

export interface ReportExplanation {
  scoreMeaning: string;
  severityMeaning: string;
  howToUseThisReport: string;
  nextSteps: string[];
}

export interface FindingGroup {
  ruleId: string;
  severity: Severity;
  category: string;
  title: string;
  occurrences: number;
  affectedComponents: AffectedComponent[];
  whyItMatters: string;
  suggestedFix: string;
  explanation: string;
}

export interface AffectedComponent {
  type: string;
  name: string;
  filePath: string;
  line: number | null;
  evidence: string;
}

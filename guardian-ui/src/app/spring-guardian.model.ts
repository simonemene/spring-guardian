export type Severity = 'CRITICAL' | 'MAJOR' | 'MINOR' | 'INFO';
export type ReportLanguage = 'it' | 'en';
export type ProjectType = 'WEB_API' | 'BATCH' | 'LIBRARY' | 'UNKNOWN';
export type ArchitectureStyle = 'AUTO_DETECTED' | 'LAYERED' | 'DOMAIN_DRIVEN_DESIGN' | 'HEXAGONAL' | 'LEGACY_LAYERED';
export type ReleaseTarget = 'PRODUCTION' | 'INTERNAL' | 'LEGACY_BASELINE';

export interface ScanProfile {
  projectType: ProjectType;
  architectureStyle: ArchitectureStyle;
  releaseTarget: ReleaseTarget;
  knownIssuesAccepted: boolean;
}

export interface ArchitectureReviewReport {
  projectName: string;
  scannedAt: string;
  profile: ScanProfile;
  capabilities: ProjectCapabilities;
  summary: ReportSummary;
  releaseReadiness: ReleaseReadiness;
  architectureScore: number;
  riskLevel: string;
  scannedJavaFiles: number;
  scannedPomFiles: number;
  rulesExecuted: number;
  findingsBySeverity: Partial<Record<Severity, number>>;
  findingsByCategory: CategorySummary[];
  findingsByType: CategorySummary[];
  architectureAreas: ArchitectureAreaReport[];
  qualityGates: QualityGate[];
  recommendedActions: RecommendedAction[];
  explanation: ReportExplanation;
  findings: FindingGroup[];
}

export interface ProjectCapabilities {
  usesSpringWeb: boolean;
  usesSpringSecurity: boolean;
  usesJpa: boolean;
  usesActuator: boolean;
  usesValidation: boolean;
  usesOpenApi: boolean;
  usesLombok: boolean;
  usesSpringBatch: boolean;
  hasControllerLayer: boolean;
  hasServiceLayer: boolean;
  hasRepositoryLayer: boolean;
  hasDomainLayer: boolean;
  hasApplicationLayer: boolean;
  hasInfrastructureLayer: boolean;
  detectedArchitecturalStyles: string[];
}

export interface ReleaseReadiness {
  status: string;
  label: string;
  explanation: string;
  releasable: boolean;
  blockers: string[];
  warnings: string[];
}

export interface QualityGate {
  code: string;
  name: string;
  status: string;
  explanation: string;
  required: boolean;
  failingFindings: number;
}

export interface ArchitectureAreaReport {
  code: string;
  name: string;
  description: string;
  findings: number;
  criticalFindings: number;
  majorFindings: number;
  readinessStatus: string;
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
  findingType: string;
  findingTypeLabel: string;
  title: string;
  occurrences: number;
  affectedComponents: AffectedComponent[];
  whyItMatters: string;
  suggestedFix: string;
  explanation: string;
  guidance: RuleGuidance;
}

export interface RuleGuidance {
  detectedProblem: string;
  riskImpact: string;
  recommendedApproach: string;
  springAlternative: string;
  documentationUrl: string;
  beforeExample: string;
  afterExample: string;
}

export interface AffectedComponent {
  type: string;
  name: string;
  filePath: string;
  line: number | null;
  evidence: string;
  codeSnippet?: string;
}
